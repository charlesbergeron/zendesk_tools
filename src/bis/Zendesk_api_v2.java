package bis;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Zendesk Help Center utility class
 */
public class Zendesk_api_v2
{

	/**
	 * Access policies
	 */
	public static class ZENaccess
	{
		Integer mintSectionId;

		String viewable_by;  //The set of users who can view content
		String manageable_by;  //The set of users who can manage content
		java.util.List<Long> restricted_to_group_ids; //The ids of the groups that have access
		java.util.List<Long> restricted_to_organization_ids; //The ids of the organizations that have access
		java.util.List<String> required_tags; //The tags a user must have to have access

		//TODO: Error management
	  private static ZENaccess get(Integer pintSectionId)
	  {
	    String strNextPage= Startup.mstrUrl+"help_center/sections/"+pintSectionId+"/access_policy.json";
      String[] strResult=new String[2];
      Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);
	    return parse(strResult[0]);
	  }

	  private static ZENaccess parse(String pstrJSON)
	  {
      ZENaccess zenEntity = new ZENaccess();

      Object obj=JSONValue.parse(pstrJSON);
	    JSONObject JSTop=(JSONObject)obj;

	    obj = JSTop.get("error");
	    if(obj!=null)
	    {
	      obj = JSTop.get("description");
	      System.out.println(obj.toString());
	    }

	    obj = JSTop.get("access_policy");
	    JSONObject JSAccess=(JSONObject)obj;

      zenEntity.viewable_by=JSAccess.get("viewable_by").toString();
      zenEntity.manageable_by=JSAccess.get("manageable_by").toString();

      obj = JSAccess.get("restricted_to_group_ids");
      JSONArray obj3=(JSONArray)obj;
      zenEntity.restricted_to_group_ids=new ArrayList<Long>();
      for(int ite2=0; ite2< obj3.size(); ite2++)
      {
        obj=obj3.get(ite2);
	      zenEntity.restricted_to_group_ids.add((Long)obj);
      }

      obj = JSAccess.get("restricted_to_organization_ids");
      obj3=(JSONArray)obj;
      zenEntity.restricted_to_organization_ids=new ArrayList<Long>();
      for(int ite2=0; ite2< obj3.size(); ite2++)
      {
      	obj=obj3.get(ite2);
	      zenEntity.restricted_to_organization_ids.add((Long)obj);
      }

      obj = JSAccess.get("required_tags");
      obj3=(JSONArray)obj;
      zenEntity.required_tags=new ArrayList<String>();
      for(int ite2=0; ite2< obj3.size(); ite2++)
      {
        obj=obj3.get(ite2);
	      zenEntity.required_tags.add(obj.toString() );
      }
	    return zenEntity;
	  }

	}

  public static class ZENarticle
  {
    Integer mintId;
		Integer mintSectionId;
		Integer mintAuthorId;
		java.util.Properties mproTranslations = new java.util.Properties();

	  public static Map<Integer, ZENarticle> get()
	  {
	    Map<Integer, ZENarticle> mapZenEntities = new java.util.HashMap<Integer, ZENarticle>();
	    String strNextPage= Startup.mstrUrl+"help_center/articles.json?include=translations";
	    while(strNextPage!=null)
	    {
	      String[] strResult=new String[2];
	      Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);
	      strNextPage=parseArticlesPage(strResult[0], mapZenEntities);
	    }
	    return mapZenEntities;
	  }

	  private static String parseArticlesPage(String pstrJSON, java.util.Map<Integer, ZENarticle> pmapZenEntities)
	  {
	    Object obj=JSONValue.parse(pstrJSON);
	    JSONObject JSTop=(JSONObject)obj;

	    obj = JSTop.get("error");
	    if(obj!=null)
	    {
	      obj = JSTop.get("description");
	      System.out.println(obj.toString());
	    }

	    obj = JSTop.get("articles");
	    JSONArray JSTickets=(JSONArray)obj;

	    for(int ite=0; ite< JSTickets.size(); ite++)
	    {
	      obj=JSTickets.get(ite);
	      JSONObject obj2=(JSONObject)obj;

	      ZENarticle zenEntity = new ZENarticle();

	      zenEntity.mintId=Integer.valueOf(obj2.get("id").toString());
	      zenEntity.mintSectionId=Integer.valueOf(obj2.get("section_id").toString());
	      zenEntity.mintAuthorId=Integer.valueOf(obj2.get("author_id").toString());

	      obj = obj2.get("translations");
	      JSONArray JSTranslations=(JSONArray)obj;
	      
	      for(int ite2=0; ite2< JSTranslations.size(); ite2++)
	      {
	        obj=JSTranslations.get(ite2);
	        JSONObject objTranslation=(JSONObject)obj;
	        
	        String strLocale=objTranslation.get("locale").toString();
	        zenEntity.mproTranslations.setProperty("title_"+strLocale, objTranslation.get("title").toString() );
	        zenEntity.mproTranslations.setProperty("date_"+strLocale, objTranslation.get("updated_at").toString() );
	      }
	      pmapZenEntities.put(zenEntity.mintId, zenEntity);
	    }

	    obj = JSTop.get("next_page");
	    if(obj!=null)
	    {
	      return obj.toString();
	    }
	    return null;
	  }
  }

  public static class ZENaudit
  {
//    Integer mintId;
    //Integer intTicketId;
    java.util.List<String> mlstAttachments =new java.util.ArrayList<String>();

    public static ZENaudit get(Integer pintTicket)
    {
      String strNextPage= Startup.mstrUrl+"tickets/"+pintTicket+"/audits.json";
      String[] strResult=new String[2];
      Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);

      ZENaudit zenEntity=parseAuditPage(strResult[0]);

      return zenEntity;
    }

    private static ZENaudit parseAuditPage(String pstrJSON)
    {
      //todo: pas tout à fait clean, il peut y avoir plusieurs audits.
      ZENaudit zenEntity = new ZENaudit();
      zenEntity.mlstAttachments= new java.util.ArrayList<String>();
      
      Object obj=JSONValue.parse(pstrJSON);
      JSONObject JSTop=(JSONObject)obj;

      obj = JSTop.get("audits");
      JSONArray JSAudits=(JSONArray)obj;

      for(int ite=0; ite< JSAudits.size(); ite++)
      {
        obj=JSAudits.get(ite);
        JSONObject obj2=(JSONObject)obj;

        //Get Audit id
//        obj = obj2.get("id");
//        Integer intId=Integer.valueOf(obj.toString());
//        zenEntity.id=intId;

        //Get audit events
        obj = obj2.get("events");
        JSONArray JSEvents=(JSONArray)obj;

        for(int ite2=0; ite2< JSEvents.size(); ite2++)
        {
          obj=JSEvents.get(ite2);
          JSONObject obj3=(JSONObject)obj;

          obj = obj3.get("type");
          if(obj.toString().equals("VoiceComment"))
          {
            obj=obj3.get("data");
            JSONObject obj4=(JSONObject)obj;

            obj = obj4.get("recording_url");
            zenEntity.mlstAttachments.add(obj.toString());
          }
        }
      }
      return zenEntity;
    }
  }

  public static class ZENcategory
  {
    Integer mintId;
    java.util.Properties mproTranslations = new java.util.Properties();

    public static java.util.Map<Integer, ZENcategory> get()
    {
      java.util.Map<Integer, ZENcategory> mapZenEntities = new java.util.HashMap<Integer, ZENcategory>();
      String strNextPage= Startup.mstrUrl+"help_center/categories.json?include=translations";
      while(strNextPage!=null)
      {
        String[] strResult=new String[2];
        Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);
        strNextPage=parseCategoriesPage(strResult[0], mapZenEntities);
      }
      return mapZenEntities;
    }

    private static String parseCategoriesPage(String pstrJSON, java.util.Map<Integer, ZENcategory> pmapZenEntities)
    {
      Object obj=JSONValue.parse(pstrJSON);
      JSONObject JSTop=(JSONObject)obj;

      obj = JSTop.get("error");
      if(obj!=null)
      {
        obj = JSTop.get("description");
        System.out.println(obj.toString());
      }

      obj = JSTop.get("categories");
      JSONArray JSTickets=(JSONArray)obj;

      for(int ite=0; ite< JSTickets.size(); ite++)
      {
        obj=JSTickets.get(ite);
        JSONObject obj2=(JSONObject)obj;

        ZENcategory zenEntity = new ZENcategory();

        zenEntity.mintId=Integer.valueOf(obj2.get("id").toString());

        obj = obj2.get("translations");
        JSONArray JSTranslations=(JSONArray)obj;
        
        for(int ite2=0; ite2< JSTranslations.size(); ite2++)
        {
          obj=JSTranslations.get(ite2);
          JSONObject objTranslation=(JSONObject)obj;

          String strLocale=objTranslation.get("locale").toString();
          zenEntity.mproTranslations.setProperty("title_"+strLocale, objTranslation.get("title").toString() );
          zenEntity.mproTranslations.setProperty("date_"+strLocale, objTranslation.get("updated_at").toString() );        
        }
        pmapZenEntities.put(zenEntity.mintId, zenEntity);
      }
      obj = JSTop.get("next_page");
      if(obj!=null)
      {
        return obj.toString();
      }
      return null;
    }
  }

  public static class ZENcustomField
  {
    Integer mintId;
    String mstrTitle;
    String mstrType;
    java.util.Properties mlstCat = new java.util.Properties();
    
    public static java.util.Map<Integer, ZENcustomField> get()
    {
      java.util.Map<Integer, ZENcustomField> mapZenEntities = new java.util.HashMap<Integer, ZENcustomField>();

      String strNextPage= Startup.mstrUrl+"ticket_fields.json";
      while(strNextPage!=null)
      {
        String[] strResult=new String[2];
        Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);
        strNextPage=parseCustomFieldsPage(strResult[0], mapZenEntities);
      }
      return mapZenEntities;
    }

    private static String parseCustomFieldsPage(String pstrJSON, java.util.Map<Integer, ZENcustomField> pmapZenEntities)
    {
      Object obj=JSONValue.parse(pstrJSON);
      JSONObject JSTop=(JSONObject)obj;
      
      obj = JSTop.get("ticket_fields");
      JSONArray JSTickets=(JSONArray)obj;

      for(int ite=0; ite< JSTickets.size(); ite++)
      {
        obj=JSTickets.get(ite);
        JSONObject obj2=(JSONObject)obj;

        ZENcustomField zenEntity = new ZENcustomField();
        obj = obj2.get("id");
        zenEntity.mintId=Integer.valueOf(obj.toString());

        obj = obj2.get("title");
        zenEntity.mstrTitle=obj.toString();

        obj = obj2.get("type");
        zenEntity.mstrType=obj.toString();
        
        //get value from option id 
        obj = obj2.get("custom_field_options");
        if(obj!=null)
        {
          JSONArray JSCustomFields=(JSONArray)obj;
          for(int ite2=0; ite2< JSCustomFields.size(); ite2++)
          {
            obj=JSCustomFields.get(ite2);
            JSONObject obj3=(JSONObject)obj;
            zenEntity.mlstCat.put(obj3.get("value"), obj3.get("name"));
          }
        }

        pmapZenEntities.put(zenEntity.mintId, zenEntity);
      }

      obj = JSTop.get("next_page");
      if(obj!=null)
      {
        return obj.toString();
      }
      return null;
    }

  }

  public static class ZENgroup
  {
    Integer mintId;
    String mstrName;
    public static java.util.Map<Integer, ZENgroup> get()
    {
      java.util.Map<Integer, ZENgroup> mapZenEntities = new java.util.HashMap<Integer, ZENgroup>();

      String strNextPage= Startup.mstrUrl+"groups.json?include=users";
      while(strNextPage!=null)
      {
        String[] strResult=new String[2];
        Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);
        strNextPage=parseGroupsPage(strResult[0], mapZenEntities);
      }
      return mapZenEntities;
    }

    private static String parseGroupsPage(String pstrJSON, java.util.Map<Integer, ZENgroup> pmapZenEntities)
    {
      Object obj=JSONValue.parse(pstrJSON);
      JSONObject JSTop=(JSONObject)obj;

      obj = JSTop.get("groups");
      JSONArray JSTickets=(JSONArray)obj;

      for(int ite=0; ite< JSTickets.size(); ite++)
      {
        obj=JSTickets.get(ite);
        JSONObject obj2=(JSONObject)obj;

        ZENgroup zenEntity = new ZENgroup();
        obj = obj2.get("id");
        zenEntity.mintId=Integer.valueOf(obj.toString());

        obj = obj2.get("name");
        zenEntity.mstrName=obj.toString();
        
        pmapZenEntities.put(zenEntity.mintId, zenEntity);
        
        obj = obj2.get("user_ids");
        if(obj!=null)
        {
          JSONArray JSArr2=(JSONArray)obj;
          for(int ite2=0; ite2< JSArr2.size(); ite2++)
          {
            obj=JSArr2.get(ite2);
  //TODO: Remove this from here
//            mUsers.get(Integer.valueOf(obj.toString())).mlstGroups.add(zenEntity.mintId);
          }
        }
      }

      obj = JSTop.get("next_page");
      if(obj!=null)
      {
        return obj.toString();
      }
      return null;
    }
  }

  public static class ZENmetrics
  {
    Integer mintGroupStations;
    Integer mintAssigneeStations;
    Integer mintReopens;
    Integer mintReplies;
    
//    String mstrAssigneeUpdatedAt;
//    String mstrRequesterUpdatedAt;
    String mstrInitiallyAssignedAt;
    String mstrAssignedAt;
    String mstrSolvedAt;
    
    String[] mstrFirstResolutionTime = new String[2];
    String[] mstrReplyTime = new String[2];
    String[] mstrFullResolutionTime = new String[2];
    String[] mstrAgentWaitTime = new String[2];
    String[] mstrRequesterWaitTime = new String[2];
    String[] mstrHoldTime = new String[2];
    
    public static ZENmetrics get(Integer pintTicket)
    {
      String strNextPage= Startup.mstrUrl+"tickets/"+pintTicket+"/metrics.json";
      String[] strResult=new String[2];
      Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);
      ZENmetrics mapZenEntities=parseMetricPage(strResult[0]);
      return mapZenEntities;
    }

    private static ZENmetrics parseMetricPage(String pstrJSON)
    {
      ZENmetrics zenEntity = new ZENmetrics();
      
      Object obj=JSONValue.parse(pstrJSON);
      JSONObject JSTop=(JSONObject)obj;
      
      obj = JSTop.get("ticket_metric");
      JSONObject obj2=(JSONObject)obj;

      obj = obj2.get("group_stations");
      zenEntity.mintGroupStations = Integer.valueOf(obj.toString());

      obj = obj2.get("assignee_stations");
      zenEntity.mintAssigneeStations = Integer.valueOf(obj.toString());

      obj = obj2.get("reopens");
      zenEntity.mintReopens = Integer.valueOf(obj.toString());

      obj = obj2.get("replies");
      zenEntity.mintReplies = Integer.valueOf(obj.toString());
      
  /*    obj = obj2.get("assignee_updated_at");
      zenEntity.mstrAssigneeUpdatedAt = (obj!=null? obj.toString():"");

      obj = obj2.get("requester_updated_at");
      zenEntity.mstrRequesterUpdatedAt = (obj!=null? obj.toString():"");
  */
      obj = obj2.get("initially_assigned_at");
      zenEntity.mstrInitiallyAssignedAt = (obj!=null? obj.toString():"");

      obj = obj2.get("assigned_at");
      zenEntity.mstrAssignedAt = (obj!=null? obj.toString():"");

      obj = obj2.get("solved_at");
      zenEntity.mstrSolvedAt = (obj!=null? obj.toString():"");

      obj = obj2.get("reply_time_in_minutes");
      JSONObject obj3 = (JSONObject) obj;
      obj = obj3.get("calendar");
      zenEntity.mstrReplyTime[0] = (obj!=null? obj.toString():"");
      obj = obj3.get("business");
      zenEntity.mstrFirstResolutionTime[1] = (obj!=null? obj.toString():"");

      obj = obj2.get("first_resolution_time_in_minutes");
      obj3 = (JSONObject) obj;
      obj = obj3.get("calendar");
      zenEntity.mstrFirstResolutionTime[0] = (obj!=null? obj.toString():"");
      obj = obj3.get("business");
      zenEntity.mstrFirstResolutionTime[1] = (obj!=null? obj.toString():"");
      
      obj = obj2.get("full_resolution_time_in_minutes");
      obj3 = (JSONObject) obj;
      obj = obj3.get("calendar");
      zenEntity.mstrFullResolutionTime[0] = (obj!=null? obj.toString():"");
      obj = obj3.get("business");
      zenEntity.mstrFullResolutionTime[1] = (obj!=null? obj.toString():"");

      obj = obj2.get("agent_wait_time_in_minutes");
      obj3 = (JSONObject) obj;
      obj = obj3.get("calendar");
      zenEntity.mstrAgentWaitTime[0] = (obj!=null? obj.toString():"");
      obj = obj3.get("business");
      zenEntity.mstrAgentWaitTime[1] = (obj!=null? obj.toString():"");

      obj = obj2.get("requester_wait_time_in_minutes");
      obj3 = (JSONObject) obj;
      obj = obj3.get("calendar");
      zenEntity.mstrRequesterWaitTime[0] = (obj!=null? obj.toString():"");
      obj = obj3.get("business");
      zenEntity.mstrRequesterWaitTime[1] = (obj!=null? obj.toString():"");

      obj = obj2.get("on_hold_time_in_minutes");
      obj3 = (JSONObject) obj;
      obj = obj3.get("calendar");
      zenEntity.mstrHoldTime[0] = (obj!=null? obj.toString():"");
      obj = obj3.get("business");
      zenEntity.mstrHoldTime[1] = (obj!=null? obj.toString():"");

      return zenEntity;
    }
  }

  public static class ZENorganisation
  {
  	Long mintId;
    String mstrName;
    String mstrDomains="";

    public static java.util.Map<Long, ZENorganisation> get()
    {
      java.util.Map<Long, ZENorganisation> mapZenEntities = new java.util.HashMap<Long, ZENorganisation>();

      String strNextPage= Startup.mstrUrl+"organizations.json";
      while(strNextPage!=null)
      {
        String[] strResult=new String[2];
        Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);
        strNextPage=parse(strResult[0], mapZenEntities);
      }
      return mapZenEntities;
    }

    private static String parse(String pstrJSON, java.util.Map<Long, ZENorganisation> pmapZenEntities)
    {
      Object obj=JSONValue.parse(pstrJSON);
      JSONObject JSTop=(JSONObject)obj;

      obj = JSTop.get("organizations");
      JSONArray JSTickets=(JSONArray)obj;

      for(int ite=0; ite< JSTickets.size(); ite++)
      {
        obj=JSTickets.get(ite);
        JSONObject obj2=(JSONObject)obj;

        ZENorganisation zenEntity = new ZENorganisation();
        obj = obj2.get("id");
        zenEntity.mintId=Long.valueOf(obj.toString());

        obj = obj2.get("name");
        zenEntity.mstrName=obj.toString();

        obj = obj2.get("domain_names");
        JSONArray JSEvents=(JSONArray)obj;
        for(int ite2=0; ite2< JSEvents.size(); ite2++)
        {
          obj=JSEvents.get(ite2);
          zenEntity.mstrDomains+=obj.toString();
          if(ite2+1<JSEvents.size())
          {
            zenEntity.mstrDomains+=",";
          }
        }
        
        pmapZenEntities.put(zenEntity.mintId, zenEntity);
      }

      obj = JSTop.get("next_page");
      if(obj!=null)
      {
        return obj.toString();
      }
      return null;
    }
  }

  /**
   *
   */
  public static class ZENsection
  {
    Integer mintId;
    Integer mintCategoryId;
    ZENaccess mZENaccess;
    java.util.Properties mproTranslations = new java.util.Properties();

    public static Map<Integer, ZENsection> get(Boolean pblnGetAccess)
    {
      java.util.Map<Integer, ZENsection> mapZenEntities = new java.util.HashMap<Integer, ZENsection>();
      String strNextPage= Startup.mstrUrl+"help_center/sections.json?include=translations";
      while(strNextPage!=null)
      {
        String[] strResult=new String[2];
        Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);
        strNextPage=parseSectionsPage(strResult[0], mapZenEntities);
      }
      
      //Get the section access
      if(pblnGetAccess)
      {
      	for( Entry<Integer, ZENsection> ite : mapZenEntities.entrySet()  )
      	{
      		ite.getValue().mZENaccess= ZENaccess.get(ite.getKey());
      	}
      }
      
      return mapZenEntities;
    }

    private static String parseSectionsPage(String pstrJSON, java.util.Map<Integer, ZENsection> pmapZenEntities)
    {
      Object obj=JSONValue.parse(pstrJSON);
      JSONObject JSTop=(JSONObject)obj;

      obj = JSTop.get("error");
      if(obj!=null)
      {
        obj = JSTop.get("description");
        System.out.println(obj.toString());
      }

      obj = JSTop.get("sections");
      JSONArray JSTickets=(JSONArray)obj;

      for(int ite=0; ite< JSTickets.size(); ite++)
      {
        obj=JSTickets.get(ite);
        JSONObject obj2=(JSONObject)obj;

        ZENsection zenEntity = new ZENsection();

        zenEntity.mintId=Integer.valueOf(obj2.get("id").toString());
        zenEntity.mintCategoryId=Integer.valueOf(obj2.get("category_id").toString());
        obj = obj2.get("translations");
        JSONArray JSTranslations=(JSONArray)obj;
        
        for(int ite2=0; ite2< JSTranslations.size(); ite2++)
        {
          obj=JSTranslations.get(ite2);
          JSONObject objTranslation=(JSONObject)obj;
          
          String strLocale=objTranslation.get("locale").toString();
          zenEntity.mproTranslations.setProperty("title_"+strLocale, objTranslation.get("title").toString() );
          zenEntity.mproTranslations.setProperty("date_"+strLocale, objTranslation.get("updated_at").toString() );
        }

        pmapZenEntities.put(zenEntity.mintId, zenEntity);
      }
      
      obj = JSTop.get("next_page");
      if(obj!=null)
      {
        return obj.toString();
      }
      return null;
    }
  }

  public static class ZENticket
  {
    Integer mintId;
    java.util.Map<Integer, String> mcustomFields = new java.util.HashMap<Integer, String>();
    Integer mintRequester;
    Integer mintSubmitter;
    Integer mintAssignee;
    Integer mintOrganisation;

    String mstrSubject;
    String mstrTags;
    String mstrStatus;
    String mstrPriority;
    String mstrVia;
    String mstrType;
    
    String mstrCreated;
    String mstrUpdated;
//    String mstrAssigned;
    String mstrDueDate;
    String mstrSatisfaction;
    
    ZENmetrics mzenMetrics;
    ZENaudit mzenAudit;

    public static java.util.Map<Integer, ZENticket> get(java.util.Properties pmapSearch)
    {
      java.util.Map<Integer, ZENticket> mapZenEntities = new java.util.HashMap<Integer, ZENticket>();

      String strAdditionalParams="";
      for(Entry<Object, Object> es : pmapSearch.entrySet())
      {
        //TODO: Faire une structure avec un 3e champ, pour avoir 1 champ recherché, 2 opération, 3 valeur
        strAdditionalParams+="+"+es.getKey().toString()+es.getValue().toString();
      }

//      String strTicketsPage= mstrUrl+"tickets.json";
      String strNextPage= Startup.mstrUrl+"search.json?query=\"type:ticket"+strAdditionalParams+"\"";
      while(strNextPage!=null)
      {
        String[] strResult=new String[2];
        Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);
        strNextPage=parseTicketsPage(strResult[0], mapZenEntities);
      }
      
      return mapZenEntities;
    }

    private static String parseTicketsPage(String pstrJSON, java.util.Map<Integer, ZENticket> pmapZenEntities)
    {
      Object obj=JSONValue.parse(pstrJSON);
      JSONObject JSTop=(JSONObject)obj;

      obj = JSTop.get("error");
      if(obj!=null)
      {
        obj = JSTop.get("description");
        System.out.println(obj.toString());
      }

      obj = JSTop.get("results");
      JSONArray JSTickets=(JSONArray)obj;

      for(int ite=0; ite< JSTickets.size(); ite++)
      {
        obj=JSTickets.get(ite);
        JSONObject obj2=(JSONObject)obj;

        ZENticket zenEntity = new ZENticket();
        zenEntity.mintId=Integer.valueOf(obj2.get("id").toString());

        obj=obj2.get("requester_id");
        if(obj != null)
        {
          zenEntity.mintRequester=Integer.valueOf(obj.toString());
        }

        obj=obj2.get("submitter_id");
        if(obj != null)
        {
          zenEntity.mintSubmitter=Integer.valueOf(obj.toString());
        }

        obj=obj2.get("assignee_id");
        zenEntity.mintAssignee = (obj != null ? Integer.valueOf(obj.toString()) : null);

        obj=obj2.get("subject");
        zenEntity.mstrSubject = (obj != null ? obj.toString() : "");

        obj=obj2.get("organization_id");
        zenEntity.mintOrganisation = (obj != null ? Integer.valueOf(obj.toString()) : null);
        
        obj = obj2.get("custom_fields");
        JSONArray JSCustomFields=(JSONArray)obj;
        for(int ite2=0; ite2< JSCustomFields.size(); ite2++)
        {
          obj=JSCustomFields.get(ite2);
          JSONObject obj3=(JSONObject)obj;
          
          if(obj3.get("value")!=null)
          {
            zenEntity.mcustomFields.put(Integer.valueOf(obj3.get("id").toString()), obj3.get("value").toString());
          }
        }
        
        obj = obj2.get("tags");
        JSCustomFields=(JSONArray)obj;
        zenEntity.mstrTags="";
        for(int ite2=0; ite2< JSCustomFields.size(); ite2++)
        {
          obj=JSCustomFields.get(ite2);
          zenEntity.mstrTags+=obj.toString();
          if(ite2+1<JSCustomFields.size())
          {
            zenEntity.mstrTags+=",";
          }
        }

        obj=obj2.get("status");
        zenEntity.mstrStatus = (obj != null ? obj.toString() : "");

        obj=obj2.get("priority");
        zenEntity.mstrPriority = (obj != null ? obj.toString() : "");

        obj=obj2.get("via");
        JSONObject obj3=(JSONObject)obj;
        obj=obj3.get("channel");
        zenEntity.mstrVia = (obj != null ? obj.toString() : "");
        
        obj=obj2.get("type");
        zenEntity.mstrType = (obj != null ? obj.toString() : "");

        obj=obj2.get("created_at");
        zenEntity.mstrCreated = (obj != null ? obj.toString() : "");

        obj=obj2.get("updated_at");
        zenEntity.mstrUpdated = (obj != null ? obj.toString() : "");

        obj=obj2.get("due_at");
        zenEntity.mstrDueDate = (obj != null ? obj.toString() : "");
        
        obj=obj2.get("satisfaction_rating");
        if(obj!=null)
        {
          obj3=(JSONObject)obj;
          obj=obj3.get("score");
          zenEntity.mstrSatisfaction = (obj3 != null ? obj3.toString() : "");
        }
        else
        {
          zenEntity.mstrSatisfaction="";
        }

        zenEntity.mzenAudit=ZENaudit.get(zenEntity.mintId);
        zenEntity.mzenMetrics=ZENmetrics.get(zenEntity.mintId);
        pmapZenEntities.put(zenEntity.mintId, zenEntity);
      }
      
      obj = JSTop.get("next_page");
      if(obj!=null)
      {
        return obj.toString();
      }
      return null;
    }
  }
  
  public static class ZENuser
  {
    Integer mintId;
    String mstrName;
    String mstrExternalId;
    String mstrEmail;
//    java.util.List<Integer> mlstGroups = new java.util.ArrayList<Integer>();

    public static java.util.Map<Integer, ZENuser> get()
    {
      java.util.Map<Integer, ZENuser> mapZenEntities = new java.util.HashMap<Integer, ZENuser>();

      String strNextPage= Startup.mstrUrl+"users.json";
      while(strNextPage!=null)
      {
        String[] strResult=new String[2];
        Utils.runBat(Startup.mstrCurlPath+" -u "+Startup.mLogin+" -k "+strNextPage, strResult);
        strNextPage=parseUsersPage(strResult[0], mapZenEntities);
      }
      return mapZenEntities;
    }

    private static String parseUsersPage(String pstrJSON, java.util.Map<Integer, ZENuser> pmapZenEntities)
    {
      Object obj=JSONValue.parse(pstrJSON);
      JSONObject JSTop=(JSONObject)obj;

      obj = JSTop.get("users");
      JSONArray JSTickets=(JSONArray)obj;

      for(int ite=0; ite< JSTickets.size(); ite++)
      {
        obj=JSTickets.get(ite);
        JSONObject obj2=(JSONObject)obj;

        ZENuser zenEntity = new ZENuser();
        obj = obj2.get("id");
        zenEntity.mintId=Integer.valueOf(obj.toString());

        obj = obj2.get("name");
        zenEntity.mstrName=obj.toString();

        obj = obj2.get("external_id");
        zenEntity.mstrExternalId = (obj!=null ? obj.toString() : "");
        
        obj = obj2.get("email");
        zenEntity.mstrEmail = (obj!=null ? obj.toString() : "");

        pmapZenEntities.put(zenEntity.mintId, zenEntity);
      }

      obj = JSTop.get("next_page");
      if(obj!=null)
      {
        return obj.toString();
      }
      return null;
    }
  }
  
}
