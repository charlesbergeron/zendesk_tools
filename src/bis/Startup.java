package bis;

import java.util.Map.Entry;

import bis.Zendesk_api_v2.*;

public class Startup
{
  public static String mstrUrl;
  public static String mLogin;
  public static String mstrCurlPath;
  public static String mstrOutputFile;

  private static java.util.Map<Integer, ZENcustomField> mFields;
  private static java.util.Map<Long, ZENorganisation> mOrganisations;
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
      java.util.Map<Integer, ZENticket> mTickets = ZENticket.get(mapSearch);

      if(mTickets!=null && mTickets.size()>0)
      {
        mFields = ZENcustomField.get();
        mOrganisations = ZENorganisation.get();
        mGroups = ZENgroup.get();
  			mUsers = ZENuser.get();
        exportTicketsToCSV(mTickets);
      }
    }
    else if(mParams.getProperty("get","").equals("translations") )
    {
    	mCategories = ZENcategory.get();
    	mSections = ZENsection.get(false);
    	mArticles = ZENarticle.get();
    	mUsers = ZENuser.get();
    	exportCategoriesToCSV();
    }
    else if(mParams.getProperty("get","").equals("access") )
    {
    	mCategories = ZENcategory.get();
      mOrganisations = ZENorganisation.get();
      mGroups = ZENgroup.get();
			mSections = ZENsection.get(true);
			exportAccessesToCSV();
    }

  }

  private static void exportTicketsToCSV(java.util.Map<Integer, ZENticket> pTickets)
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

      //TODO: Fixme
      String strGroups="";
/*      for(Integer intGroup=0; intGroup<mUsers.get(ztic.mintAssignee).mlstGroups.size(); intGroup++)
      {
        strGroups= mGroups.get(mUsers.get(ztic.mintAssignee).mlstGroups.get(intGroup)).mstrName;
        if(intGroup+1 < mUsers.get(ztic.mintAssignee).mlstGroups.size())
        {
          strGroups+=",";
        }
      }
*/
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

  private static void exportAccessesToCSV()
  {
    java.util.List<KeyValue> lstHeaders = new java.util.ArrayList<KeyValue>();
    lstHeaders.add( new KeyValue("category_en","Category en"));
    lstHeaders.add( new KeyValue("category_fr","Category fr"));
    lstHeaders.add( new KeyValue("category_es","Category es"));

    lstHeaders.add( new KeyValue("section_en","Section en"));
    lstHeaders.add( new KeyValue("section_fr","Section fr"));
    lstHeaders.add( new KeyValue("section_es","Section es"));
    
    lstHeaders.add( new KeyValue("viewable_by","viewable_by"));
    lstHeaders.add( new KeyValue("manageable_by","manageable_by"));
    lstHeaders.add( new KeyValue("restricted_to_groups","restricted_to_groups"));
    lstHeaders.add( new KeyValue("restricted_to_organization","restricted_to_organization"));
    lstHeaders.add( new KeyValue("required_user_tags","required_user_tags"));

    CSVOutput csvOut = new CSVOutput(lstHeaders, mstrOutputFile);

    java.util.Properties proMap;
    for(Entry<Integer, ZENsection> ite : mSections.entrySet())
    {
      proMap = new java.util.Properties();

      ZENsection curSection=ite.getValue();
      ZENcategory curCategory= mCategories.get(curSection.mintCategoryId);

      proMap.put("section_en", curSection.mproTranslations.getProperty("title_en-us","") );
      proMap.put("category_en", curCategory.mproTranslations.getProperty("title_en-us","") );
      proMap.put("section_fr", curSection.mproTranslations.getProperty("title_fr-ca","") );
      proMap.put("category_fr", curCategory.mproTranslations.getProperty("title_fr-ca","") );
      proMap.put("section_es", curSection.mproTranslations.getProperty("title_es","") );
      proMap.put("category_es", curCategory.mproTranslations.getProperty("title_es","") );

      proMap.put("viewable_by", curSection.mZENaccess.viewable_by);
      proMap.put("manageable_by", curSection.mZENaccess.manageable_by);

      String strRestricted="";
      for(Long intGroup : curSection.mZENaccess.restricted_to_group_ids)
      {
      	strRestricted+= mGroups.get(intGroup).mstrName+"\n";
      }
      proMap.put("restricted_to_groups", strRestricted);

      strRestricted="";
      for(Long intOrg : curSection.mZENaccess.restricted_to_organization_ids)
      {
      	strRestricted+= mOrganisations.get(intOrg).mstrName+"\n";
      }
      proMap.put("restricted_to_organization", strRestricted);

      strRestricted="";
      for(String strTag : curSection.mZENaccess.required_tags)
      {
      	strRestricted+= strTag+"\n";
      }
      proMap.put("required_user_tags", strRestricted);

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



}
