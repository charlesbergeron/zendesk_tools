package bis;

import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Startup
{
  
  private static class ZENticket
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
  }
  
  private static class ZENmetrics
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
  }
  
  private static class ZENcustomField
  {
    Integer mintId;
    String mstrTitle;
    String mstrType;
    java.util.Properties mlstCat = new java.util.Properties();
  }

  private static class ZENuser
  {
    Integer mintId;
    String mstrName;
    String mstrExternalId;
    String mstrEmail;
    java.util.List<Integer> mlstGroups = new java.util.ArrayList<Integer>();
  }

  private static class ZENorganisation
  {
    Integer mintId;
    String mstrName;
    String mstrDomains="";
  }

  private static class ZENgroup
  {
    Integer mintId;
    String mstrName;
  }

  private static class ZENaudit
  {
//    Integer mintId;
    //Integer intTicketId;
    java.util.List<String> mlstAttachments =new java.util.ArrayList<String>();
  }

  private static class ZENcategory
  {
    Integer mintId;
    java.util.Properties mproTranslations = new java.util.Properties();
  }

  private static class ZENsection
  {
    Integer mintId;
    Integer mintCategoryId;
    java.util.Properties mproTranslations = new java.util.Properties();
  }
  
  private static class ZENarticle
  {
    Integer mintId;
		public Integer mintSectionId;
		public Integer mintAuthorId;
		java.util.Properties mproTranslations = new java.util.Properties();
  }

  private static String mstrUrl;
  private static String mLogin;
  private static String mstrCurlPath;
  private static String mstrOutputFile;
  
  private static String[] pstrRes;

  private static java.util.Map<Integer, ZENcustomField> mFields;
  private static java.util.Map<Integer, ZENorganisation> mOrganisations;
  private static java.util.Map<Integer, ZENuser> mUsers;
  private static java.util.Map<Integer, ZENgroup> mGroups;
  
  private static java.util.Map<Integer, ZENcategory> mCategories;
  private static java.util.Map<Integer, ZENsection> mSections;
  private static java.util.Map<Integer, ZENarticle> mArticles;

  private static java.util.Properties mParams = new java.util.Properties();

  public static void main(String[] args)
  {
    for (String arg : args)
    {
      String keyValue = arg.substring(1);
      int index = -1;
      if (keyValue != null && (index = keyValue.indexOf('=')) > -1)
      {
        mParams.put(keyValue.substring(0, index), keyValue.substring(index + 1));
      }
    }

    int intErrors=0;
    if(mParams.getProperty("o","").equals("") )
    {
    	System.err.println("You must define the file output path.");
    	intErrors++;
    }
    else
    {
    	mstrOutputFile=mParams.getProperty("o","");
    }
    if(mParams.getProperty("u","").equals("") || mParams.getProperty("p","").equals("") )
    {
    	System.err.println("You must specify the username and password.");
    	intErrors++;
    }
    else
    {
    	mLogin=mParams.getProperty("u","")+"/token:"+mParams.getProperty("p","");
    }
    if(mParams.getProperty("d","").equals("") )
    {
    	System.err.println("You must define the zendesk domain. ( https://changeme.zendesk.com/api/v2/ )");
    	intErrors++;
    }
    else
    {
    	mstrUrl="https://"+mParams.getProperty("d")+".zendesk.com/api/v2/";
    }
    if(mParams.getProperty("curl","").equals("") )
    {
    	System.err.println("You must provide the path to curl executable file.");
    	intErrors++;
    }
    else
    {
    	mstrCurlPath=mParams.getProperty("curl");
    }
    
    if(intErrors++ >0)
    {
    	return;
    }

    if(mParams.getProperty("get","").equals("tickets") )
    {
      java.util.Properties mapSearch = new java.util.Properties();
    	//Can be set to filter which tickets are returned
//      mapSearch.put("created", ">2013-05-10");
      java.util.Map<Integer, ZENticket> mTickets = getTicketsMap(mapSearch);

      if(mTickets!=null && mTickets.size()>0)
      {
        mFields = getCustomFieldsMap();
        mOrganisations = getOrganisationsMap();
        mGroups = getGroupsMap();
  			mUsers = getUsersMap();
        exportTicketsToCSV(mTickets);
      }
    }
    else if(mParams.getProperty("get","").equals("translations") )
    {
			mCategories = getCategoriesMap();
			mSections = getSectionsMap();
			mArticles = getArticlesMap();
			mUsers = getUsersMap();
			exportCategoriesToCSV();
    }

  }

  public static void exportTicketsToCSV(java.util.Map<Integer, ZENticket> pTickets)
  {
    java.util.List<KeyValue> lstHeaders = new java.util.ArrayList<KeyValue>();
    lstHeaders.add( new KeyValue("summation","Summation column"));
    lstHeaders.add( new KeyValue("ticket_id","Id"));
    lstHeaders.add( new KeyValue("requester","Requester"));
    lstHeaders.add( new KeyValue("requester_id","Requester id"));
    lstHeaders.add( new KeyValue("requester_external_id","Requester external id"));
    lstHeaders.add( new KeyValue("requester_email","Requester email"));
    lstHeaders.add( new KeyValue("requester_domain","Requester domain"));
    lstHeaders.add( new KeyValue("submitter","Submitter"));
    lstHeaders.add( new KeyValue("assignee","Assignee"));
    lstHeaders.add( new KeyValue("group","Group"));
    lstHeaders.add( new KeyValue("subject","Subject"));
    lstHeaders.add( new KeyValue("tags","Tags"));
    lstHeaders.add( new KeyValue("status","Status"));
    lstHeaders.add( new KeyValue("priority","Priority"));
    lstHeaders.add( new KeyValue("via","Via"));
    lstHeaders.add( new KeyValue("ticket_type","Ticket type"));
    lstHeaders.add( new KeyValue("created_at","Created at"));
    lstHeaders.add( new KeyValue("updated_at","Updated at"));
    lstHeaders.add( new KeyValue("assigned_at","Assigned at"));
    lstHeaders.add( new KeyValue("organisation","Organization"));
    lstHeaders.add( new KeyValue("due_date","Due date"));
    lstHeaders.add( new KeyValue("initially_assigned_at","Initially assigned at"));
    lstHeaders.add( new KeyValue("solved_at","Solved at"));
    lstHeaders.add( new KeyValue("resolution_time","Resolution time"));
    lstHeaders.add( new KeyValue("satisfaction_score","Satisfaction Score"));
    lstHeaders.add( new KeyValue("group_stations","Group stations"));
    lstHeaders.add( new KeyValue("assigne_stations","Assignee stations"));
    lstHeaders.add( new KeyValue("reopens","Reopens"));
    lstHeaders.add( new KeyValue("replies","Replies"));
    lstHeaders.add( new KeyValue("first_reply_time","First reply time in minutes"));
    lstHeaders.add( new KeyValue("first_reply_time_business","First reply time in minutes within business hours"));
    lstHeaders.add( new KeyValue("first_resolution_time","First resolution time in minutes"));
    lstHeaders.add( new KeyValue("first_resolution_time_business","First resolution time in minutes within business hours"));
    lstHeaders.add( new KeyValue("full_resolution_time","Full resolution time in minutes"));
    lstHeaders.add( new KeyValue("full_resolution_time_business","Full resolution time in minutes within business hours"));
    lstHeaders.add( new KeyValue("agent_wait_time","Agent wait time in minutes"));
    lstHeaders.add( new KeyValue("agent_wait_time_business","Agent wait time in minutes within business hours"));
    lstHeaders.add( new KeyValue("requester_wait_time","Requester wait time in minutes"));
    lstHeaders.add( new KeyValue("requester_wait_time_business","Requester wait time in minutes within business hours"));
    lstHeaders.add( new KeyValue("hold_time","On hold time in minutes"));
    lstHeaders.add( new KeyValue("hold_time_business","On hold time in minutes within business hours"));
    lstHeaders.add( new KeyValue("client_request_type","Client request Type [list]"));
    lstHeaders.add( new KeyValue("progress_status","Progress Status [list]"));
    lstHeaders.add( new KeyValue("urgency","Urgency [list]"));
    lstHeaders.add( new KeyValue("impact","Impact [list]"));
    lstHeaders.add( new KeyValue("custom_priority","Priority [list]"));
    lstHeaders.add( new KeyValue("project","Project [list]"));
    lstHeaders.add( new KeyValue("effort_estimated","Effort Estimated [dec]"));
    lstHeaders.add( new KeyValue("effort_logged","Effort Logged [dec]"));
    lstHeaders.add( new KeyValue("effort_cost","Effort Cost [dec]"));
    lstHeaders.add( new KeyValue("sla_type","SLA Type [list]"));
    lstHeaders.add( new KeyValue("non_billable","Non-billable [flag]"));
    lstHeaders.add( new KeyValue("jira_id","JIRA ID [txt]"));

    CSVOutput csvOut = new CSVOutput(lstHeaders, mstrOutputFile);

    java.util.Properties proMap;
    for(Entry<Integer, ZENticket> ite : pTickets.entrySet())
    {
      proMap = new java.util.Properties();
      
      //Dummy column
      proMap.put("summation",1);
      
      ZENticket ztic = pTickets.get(ite.getKey());
      proMap.put("ticket_id", ztic.mintId.toString() );

      if(ztic.mintRequester!=null)
      {
        proMap.put("requester_id", ztic.mintRequester);
        proMap.put("requester", mUsers.get(ztic.mintRequester).mstrName);
        proMap.put("requester_external_id", mUsers.get(ztic.mintRequester).mstrExternalId);
        proMap.put("requester_email", mUsers.get(ztic.mintRequester).mstrEmail);
      }

      if(ztic.mintSubmitter!=null)
      {
        proMap.put("submitter", mUsers.get(ztic.mintAssignee).mstrName);
      }

      if(ztic.mintAssignee!=null)
      {
        proMap.put("assignee", mUsers.get(ztic.mintAssignee).mstrName);
      }

      String strGroups="";
      for(Integer intGroup=0; intGroup<mUsers.get(ztic.mintAssignee).mlstGroups.size(); intGroup++)
      {
        strGroups= mGroups.get(mUsers.get(ztic.mintAssignee).mlstGroups.get(intGroup)).mstrName;
        if(intGroup+1 < mUsers.get(ztic.mintAssignee).mlstGroups.size())
        {
          strGroups+=",";
        }
      }
      proMap.put("group", strGroups);
      
      if(ztic.mintOrganisation!=null)
      {
        proMap.put("organisation", mOrganisations.get(ztic.mintOrganisation).mstrName);
        proMap.put("requester_domain", mOrganisations.get(ztic.mintOrganisation).mstrDomains);
      }

      proMap.put("subject", ztic.mstrSubject);
      proMap.put("tags", ztic.mstrTags);
      proMap.put("status", ztic.mstrStatus);
      proMap.put("priority", ztic.mstrPriority);
      
      for(Entry<Integer, String> ite2 : ztic.mcustomFields.entrySet() )
      {
        String strDisplay = ztic.mcustomFields.get(ite2.getKey());
        //Obtient la valeur associé une key (pour les champs selection list.)
        if( mFields.get(ite2.getKey()).mlstCat.size()>0 )
        {
          strDisplay = mFields.get(ite2.getKey()).mlstCat.getProperty(ztic.mcustomFields.get(ite2.getKey()));
        }
        
        if(mFields.get(ite2.getKey()).mstrTitle.equals("Priority"))
        {
          proMap.put("custom_priority", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("SLA Type"))
        {
          proMap.put("sla_type", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("Non-billable"))
        {
          if(strDisplay.equals("TRUE"))
          {
            strDisplay="Yes";
          }
          else if(strDisplay.equals("FALSE"))
          {
            strDisplay="No";
          }
          
          proMap.put("non_billable", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("JIRA ID"))
        {
          proMap.put("jira_id", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("Effort Estimated") && mFields.get(ite2.getKey()).mstrType.equals("decimal"))
        {
          proMap.put("effort_estimated", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("Effort Logged") && mFields.get(ite2.getKey()).mstrType.equals("decimal"))
        {
          proMap.put("effort_logged", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("Effort Cost") && mFields.get(ite2.getKey()).mstrType.equals("decimal") )
        {
          proMap.put("effort_cost", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("Project") )
        {
          proMap.put("project", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("Client request Type") )
        {
          proMap.put("client_request_type", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("Progress Status") )
        {
          proMap.put("progress_status", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("Urgency") )
        {
          proMap.put("urgency", strDisplay );
        }
        else if(mFields.get(ite2.getKey()).mstrTitle.equals("Impact") )
        {
          proMap.put("impact", strDisplay );
        }
      }

      proMap.put("via", ztic.mstrVia);
      proMap.put("ticket_type", ztic.mstrType);
      
      proMap.put("created_at", ztic.mstrCreated);
      proMap.put("updated_at", ztic.mstrUpdated);
      proMap.put("assigned_at", ztic.mzenMetrics.mstrAssignedAt);
      proMap.put("initially_assigned_at",ztic.mzenMetrics.mstrInitiallyAssignedAt);
      proMap.put("solved_at",ztic.mzenMetrics.mstrSolvedAt);
//      proMap.put("resolution_time","");
      proMap.put("due_date", ztic.mstrDueDate);
      proMap.put("satisfaction_score", ztic.mstrSatisfaction);
      proMap.put("group_stations",ztic.mzenMetrics.mintGroupStations);
      proMap.put("assigne_stations",ztic.mzenMetrics.mintAssigneeStations);
      proMap.put("reopens",ztic.mzenMetrics.mintReopens);
      proMap.put("replies",ztic.mzenMetrics.mintReplies);
      proMap.put("first_reply_time",ztic.mzenMetrics.mstrReplyTime[0]);
      proMap.put("first_reply_time_business",ztic.mzenMetrics.mstrReplyTime[0]);
      proMap.put("first_resolution_time",ztic.mzenMetrics.mstrFirstResolutionTime[0]);
      proMap.put("first_resolution_time_business",ztic.mzenMetrics.mstrFirstResolutionTime[1]);
      proMap.put("full_resolution_time",ztic.mzenMetrics.mstrFullResolutionTime[0]);
      proMap.put("full_resolution_time_business",ztic.mzenMetrics.mstrFullResolutionTime[1]);
      proMap.put("agent_wait_time",ztic.mzenMetrics.mstrAgentWaitTime[0]);
      proMap.put("agent_wait_time_business",ztic.mzenMetrics.mstrAgentWaitTime[1]);
      proMap.put("requester_wait_time",ztic.mzenMetrics.mstrRequesterWaitTime[0]);
      proMap.put("requester_wait_time_business",ztic.mzenMetrics.mstrRequesterWaitTime[1]);
      proMap.put("hold_time",ztic.mzenMetrics.mstrHoldTime[0]);
      proMap.put("hold_time_business",ztic.mzenMetrics.mstrHoldTime[1]);
      
      String strOut = "[";
      for(String strAttachment : ztic.mzenAudit.mlstAttachments)
      {
        strOut += strAttachment+",";
      }
      strOut += "]";
      proMap.put("attachments", strOut );
      
      csvOut.outputRow(proMap);
    }
    csvOut.close();
  }

  private static void exportCategoriesToCSV()
  {
    java.util.List<KeyValue> lstHeaders = new java.util.ArrayList<KeyValue>();
    lstHeaders.add( new KeyValue("category_en","Category en"));
    lstHeaders.add( new KeyValue("category_fr","Category fr"));
    lstHeaders.add( new KeyValue("category_es","Category es"));

    lstHeaders.add( new KeyValue("section_en","Section en"));
    lstHeaders.add( new KeyValue("section_fr","Section fr"));
    lstHeaders.add( new KeyValue("section_es","Section es"));

    lstHeaders.add( new KeyValue("article_en","Article en"));
    lstHeaders.add( new KeyValue("article_fr","Article fr"));
    lstHeaders.add( new KeyValue("article_es","Article es"));

    lstHeaders.add( new KeyValue("article_updated_en","Article en Last updated"));
    lstHeaders.add( new KeyValue("article_updated_fr","Article fr Last updated"));
    lstHeaders.add( new KeyValue("article_updated_es","Article es Last updated"));

    lstHeaders.add( new KeyValue("author","Author"));

    CSVOutput csvOut = new CSVOutput(lstHeaders, mstrOutputFile);

    java.util.Properties proMap;
    for(Entry<Integer, ZENarticle> ite : mArticles.entrySet())
    {
      proMap = new java.util.Properties();

      ZENarticle curArticle=ite.getValue();
      ZENsection curSection=mSections.get(curArticle.mintSectionId);
      ZENcategory curCategory= mCategories.get(curSection.mintCategoryId);
      ZENuser curAuthor= mUsers.get(curArticle.mintAuthorId);
      
      proMap.put("article_en", curArticle.mproTranslations.getProperty("title_en-us","") );
      proMap.put("section_en", curSection.mproTranslations.getProperty("title_en-us","") );
      proMap.put("category_en", curCategory.mproTranslations.getProperty("title_en-us","") );

      proMap.put("article_fr", curArticle.mproTranslations.getProperty("title_fr-ca","") );
      proMap.put("section_fr", curSection.mproTranslations.getProperty("title_fr-ca","") );
      proMap.put("category_fr", curCategory.mproTranslations.getProperty("title_fr-ca","") );

      proMap.put("article_es", curArticle.mproTranslations.getProperty("title_es","") );
      proMap.put("section_es", curSection.mproTranslations.getProperty("title_es","") );
      proMap.put("category_es", curCategory.mproTranslations.getProperty("title_es","") );

      proMap.put("article_updated_en", curArticle.mproTranslations.getProperty("date_en-us","") );
      proMap.put("article_updated_fr", curArticle.mproTranslations.getProperty("date_fr-ca","") );
      proMap.put("article_updated_es", curArticle.mproTranslations.getProperty("date_es","") );

      if(curAuthor!=null)
      {
        proMap.put("author", curAuthor.mstrName );
      }
      else
      {
    	proMap.put("author", "UNKNOWN USER ID" );
      }

      csvOut.outputRow(proMap);
    }
    csvOut.close();
  }

  public static java.util.Map<Integer, ZENticket> getTicketsMap(java.util.Properties pmapSearch)
  {
    java.util.Map<Integer, ZENticket> mapZenEntities = new java.util.HashMap<Integer, ZENticket>();

    String strAdditionalParams="";
    for(Entry<Object, Object> es : pmapSearch.entrySet())
    {
      //TODO: Faire une structure avec un 3e champ, pour avoir 1 champ recherché, 2 opération, 3 valeur
      strAdditionalParams+="+"+es.getKey().toString()+es.getValue().toString();
    }
    
//    String strTicketsPage= mstrUrl+"tickets.json";
    String strNextPage= mstrUrl+"search.json?query=\"type:ticket"+strAdditionalParams+"\"";
    while(strNextPage!=null)
    {
      pstrRes= new String[2];
      runBat(mstrCurlPath+" -u "+mLogin+" -k "+strNextPage);
      strNextPage=parseTicketsPage(pstrRes[0], mapZenEntities);
    }
    
    return mapZenEntities;
  }

  public static String parseTicketsPage(String pstrJSON, java.util.Map<Integer, ZENticket> pmapZenEntities)
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

      zenEntity.mzenAudit=getAudit(zenEntity.mintId);
      zenEntity.mzenMetrics=getMetric(zenEntity.mintId);
      pmapZenEntities.put(zenEntity.mintId, zenEntity);
    }
    
    obj = JSTop.get("next_page");
    if(obj!=null)
    {
      return obj.toString();
    }
    return null;
  }
  
  public static ZENmetrics getMetric(Integer pintTicket)
  {
    String strNextPage= mstrUrl+"tickets/"+pintTicket+"/metrics.json";
    pstrRes= new String[2];
    runBat(mstrCurlPath+" -u "+mLogin+" -k \""+strNextPage+"\"");
    ZENmetrics mapZenEntities=parseMetricPage(pstrRes[0]);
    return mapZenEntities;
  }

  public static ZENmetrics parseMetricPage(String pstrJSON)
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

  public static java.util.Map<Integer, ZENcustomField> getCustomFieldsMap()
  {
    java.util.Map<Integer, ZENcustomField> mapZenEntities = new java.util.HashMap<Integer, ZENcustomField>();

    String strNextPage= mstrUrl+"ticket_fields.json";
    while(strNextPage!=null)
    {
      pstrRes= new String[2];
      runBat(mstrCurlPath+" -u "+mLogin+" -k \""+strNextPage+"\"");
      strNextPage=parseCustomFieldsPage(pstrRes[0], mapZenEntities);
    }
    return mapZenEntities;
  }

  public static String parseCustomFieldsPage(String pstrJSON, java.util.Map<Integer, ZENcustomField> pmapZenEntities)
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

  public static java.util.Map<Integer, ZENuser> getUsersMap()
  {
    java.util.Map<Integer, ZENuser> mapZenEntities = new java.util.HashMap<Integer, ZENuser>();

    String strNextPage= mstrUrl+"users.json";
    while(strNextPage!=null)
    {
      pstrRes= new String[2];
      runBat(mstrCurlPath+" -u "+mLogin+" -k "+strNextPage);
      strNextPage=parseUsersPage(pstrRes[0], mapZenEntities);
    }
    return mapZenEntities;
  }

  public static String parseUsersPage(String pstrJSON, java.util.Map<Integer, ZENuser> pmapZenEntities)
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

  public static java.util.Map<Integer, ZENgroup> getGroupsMap()
  {
    java.util.Map<Integer, ZENgroup> mapZenEntities = new java.util.HashMap<Integer, ZENgroup>();

    String strNextPage= mstrUrl+"groups.json?include=users";
    while(strNextPage!=null)
    {
      pstrRes= new String[2];
      runBat(mstrCurlPath+" -u "+mLogin+" -k \""+strNextPage+"\"");
      strNextPage=parseGroupsPage(pstrRes[0], mapZenEntities);
    }
    return mapZenEntities;
  }

  public static String parseGroupsPage(String pstrJSON, java.util.Map<Integer, ZENgroup> pmapZenEntities)
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
      
      //Je pogne les memberships ici parce que ca sauve un call à l'api de membership.
      obj = obj2.get("user_ids");
      if(obj!=null)
      {
        JSONArray JSArr2=(JSONArray)obj;
        for(int ite2=0; ite2< JSArr2.size(); ite2++)
        {
          obj=JSArr2.get(ite2);
          mUsers.get(Integer.valueOf(obj.toString())).mlstGroups.add(zenEntity.mintId);
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

  
  public static java.util.Map<Integer, ZENorganisation> getOrganisationsMap()
  {
    java.util.Map<Integer, ZENorganisation> mapZenEntities = new java.util.HashMap<Integer, ZENorganisation>();

    String strNextPage= mstrUrl+"organizations.json";
    while(strNextPage!=null)
    {
      pstrRes= new String[2];
      runBat(mstrCurlPath+" -u "+mLogin+" -k \""+strNextPage+"\"");
      strNextPage=parseOrganisationsPage(pstrRes[0], mapZenEntities);
    }
    return mapZenEntities;
  }

  public static String parseOrganisationsPage(String pstrJSON, java.util.Map<Integer, ZENorganisation> pmapZenEntities)
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
      zenEntity.mintId=Integer.valueOf(obj.toString());

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

  public static ZENaudit getAudit(Integer pintTicket)
  {
    String strNextPage= mstrUrl+"tickets/"+pintTicket+"/audits.json";
    pstrRes= new String[2];
    runBat(mstrCurlPath+" -u "+mLogin+" -k \""+strNextPage+"\"");

    ZENaudit zenEntity=parseAuditPage(pstrRes[0]);

    return zenEntity;
  }

  public static ZENaudit parseAuditPage(String pstrJSON)
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
//      obj = obj2.get("id");
//      Integer intId=Integer.valueOf(obj.toString());
//      zenEntity.id=intId;

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

  public static java.util.Map<Integer, ZENcategory> getCategoriesMap()
  {
    java.util.Map<Integer, ZENcategory> mapZenEntities = new java.util.HashMap<Integer, ZENcategory>();
    String strNextPage= mstrUrl+"help_center/categories.json?include=translations";
    while(strNextPage!=null)
    {
      pstrRes= new String[2];
      runBat(mstrCurlPath+" -u "+mLogin+" -k "+strNextPage);
      strNextPage=parseCategoriesPage(pstrRes[0], mapZenEntities);
    }
    return mapZenEntities;
  }

  public static String parseCategoriesPage(String pstrJSON, java.util.Map<Integer, ZENcategory> pmapZenEntities)
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

  public static Map<Integer, ZENsection> getSectionsMap()
  {
    java.util.Map<Integer, ZENsection> mapZenEntities = new java.util.HashMap<Integer, ZENsection>();
    String strNextPage= mstrUrl+"help_center/sections.json?include=translations";
    while(strNextPage!=null)
    {
      pstrRes= new String[2];
      runBat(mstrCurlPath+" -u "+mLogin+" -k "+strNextPage);
      strNextPage=parseSectionsPage(pstrRes[0], mapZenEntities);
    }
    return mapZenEntities;
  }

  public static String parseSectionsPage(String pstrJSON, java.util.Map<Integer, ZENsection> pmapZenEntities)
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

  public static Map<Integer, ZENarticle> getArticlesMap()
  {
    Map<Integer, ZENarticle> mapZenEntities = new java.util.HashMap<Integer, ZENarticle>();
    String strNextPage= mstrUrl+"help_center/articles.json?include=translations";
    while(strNextPage!=null)
    {
      pstrRes= new String[2];
      runBat(mstrCurlPath+" -u "+mLogin+" -k "+strNextPage);
      strNextPage=parseArticlesPage(pstrRes[0], mapZenEntities);
    }
    return mapZenEntities;
  }

  public static String parseArticlesPage(String pstrJSON, java.util.Map<Integer, ZENarticle> pmapZenEntities)
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

  public static void runBat(String pstrBatPath)
  {
    Integer intTry=0;
    while(intTry<3)
    {
      try
      {
        //TODO: Fixer le problème de < > du filtre de date en command line...
        String strCommand = "cmd.exe /c \"" + pstrBatPath.replace("&", "^&") +"\"";
        System.out.println(strCommand);
        pstrRes[0]="";
        
        //Run the command line then wait for its completion, since it is started in another process(or something).
        final Process procRun = Runtime.getRuntime().exec(strCommand);
        //Start threads to catch if the shell shows messages
        Thread normal = new Thread()
        {
          public void run()
          {
            try
            {
              java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(procRun.getInputStream()));
              String line = "";
              try
              {
                while ((line = reader.readLine()) != null)
                {
                  pstrRes[0] += line;
                }
              }
              finally
              {
                reader.close();
              }
            }
            catch (java.io.IOException ioe)
            {
              ioe.printStackTrace();
            }
          }
        };
        normal.start();
  
        Thread error = new Thread()
        {
          public void run()
          {
            try
            {
              java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(procRun.getErrorStream()));
              String line = "";
              try
              {
                while ((line = reader.readLine()) != null)
                {
                  System.out.println(line);
                }
              }
              finally
              {
                reader.close();
              }
            }
            catch (java.io.IOException ioe)
            {
              ioe.printStackTrace();
            }
          }
        };
        error.start();
        
        procRun.waitFor();
        normal.join(10000);
        error.join(10000);
  
        procRun.destroy();

        //TODO: Pas clean que ca soit ici... changer le return de la fonction, genre
        //TODO: À tester...
        Object obj=JSONValue.parse(pstrRes[0]);
        JSONObject JSTop=(JSONObject)obj;
        //Si Retry-After dans la réponse, fait un wait
        obj = JSTop.get("Retry-After");
        if(obj==null)
        {
          intTry=99;
        }
        else
        {
          try
          {
            Thread.sleep(60000);
            intTry++;
          }
          catch(InterruptedException e)
          {
            e.printStackTrace();
          }
        }
        
      }
      catch(Exception e)
      {
  //      System.err.println("failed."+ e.getMessage());
        intTry=99;
        e.printStackTrace();
      }
    }

  }
  
}
