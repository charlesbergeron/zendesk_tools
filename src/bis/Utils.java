package bis;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Utils
{
  public static void runBat(String pstrBatPath, final String[] pstrArr)
  {
    Integer intTry=0;
    while(intTry<3)
    {
      try
      {
        //TODO: Fixer le problème de < > du filtre de date en command line...
        String strCommand = "cmd.exe /c \"" + pstrBatPath.replace("&", "^&") +"\"";
        System.out.println(strCommand);
        pstrArr[0]="";
        
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
                	pstrArr[0] += line;
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
        Object obj=JSONValue.parse(pstrArr[0]);
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
