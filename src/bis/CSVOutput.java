package bis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CSVOutput
{
  java.util.List<String> mmapHeaders = new java.util.ArrayList<String>();
  String mstrTextDelimiter="\"";
  String mstrFieldSeparator=";";
  String mstrRowSeparator="\n";

  public BufferedWriter bwOut;

  public CSVOutput(java.util.List<KeyValue> plstHeaders, String pstrFilePath)
  {
    java.util.Properties strValues = new java.util.Properties();
    for(KeyValue kv : plstHeaders)
    {
      mmapHeaders.add(kv.strKey);
      strValues.put(kv.strKey, kv.strValue);
    }

    try
    {
      bwOut = new BufferedWriter(new FileWriter(pstrFilePath,true));
      this.outputRow(strValues);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

  }
  
  public void close()
  {
    try
    {
      bwOut.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public void outputRow(java.util.Properties pstrValues)
  {
    String strValue;
    Integer ite=0;

    //For each column
    for(String strCol : mmapHeaders)
    {
      //Get if a value is defined
      if(pstrValues.containsKey(strCol) )
      {
        strValue = pstrValues.get(strCol).toString();
      }
      else
      {
        strValue = "";
      }
      
      if(strValue.contains(mstrFieldSeparator) || strValue.contains(mstrRowSeparator) )
      {
        //Encase value between text delimiters if either field separator or row separator chars are present in value.
        strValue= mstrTextDelimiter + strValue.replace(mstrTextDelimiter, mstrTextDelimiter+mstrTextDelimiter) + mstrTextDelimiter;
      }
      this.output(strValue);
      ite++;
      if( ite < mmapHeaders.size() )
      {
        this.output(mstrFieldSeparator);
      }
    }
    this.output(mstrRowSeparator);
  }
  
  private void output(String pstrValue)
  {
    try
    {
      bwOut.write(pstrValue);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

}