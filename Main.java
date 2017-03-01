package cc.rowan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main 
{
	static final String TUMOR_SIZE_KEYWORD = "tumor size";
	
	static enum ParserState
	{
		FOR_PAT_ID,
		FOR_TUMOR_KEY,
	};
	
    public static String[] readLines(String filename) throws IOException 
    {
        FileReader fileReader = new FileReader(filename);
         
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String>lines = new ArrayList<String>();
        String line = null;
         
        while ((line = bufferedReader.readLine()) != null) 
        {
            lines.add(line);
        }
         
        bufferedReader.close();
         
        return lines.toArray(new String[lines.size()]);
    }   	
	
    static class TumorSize
    {
    	float dim[] = new float[3];
    	String unit;
    }
    
    public static void main(String[] args) throws Exception
    {
        String[] lines = readLines(args[0]);
        Map<String,TumorSize> tumorSizes = new TreeMap<String,TumorSize>();        
        String thisPatient = null;
        ParserState state = ParserState.FOR_PAT_ID;        
    	for(int i=0;i<lines.length;++i)
    	{
    		//each line
    		String line = lines[i];
    		//System.out.println(line);
    		
    		switch(state)
    		{
    			//read patient id
    			case FOR_PAT_ID:
    	    		if(line.trim().contains("\"PAT"))
    	    		{
    	    			Pattern pattern = Pattern.compile("PAT[0-9]{1,3}");//"");
    	    			Matcher matcher = pattern.matcher(line);
    	    			if(matcher.find())
    	    			{
    	    				thisPatient = matcher.group(0);
    	    				state = ParserState.FOR_TUMOR_KEY;
    	    			}
    	    		}
    			break;
    		
    			//read tumor size
    			case FOR_TUMOR_KEY:
    	    		if(line.trim().contains("\"PAT"))
    	    		{
    	    			Pattern pattern = Pattern.compile("PAT[0-9]{1,3}");//"");
    	    			Matcher matcher = pattern.matcher(line);
    	    			if(matcher.find())
    	    			{
    	    				thisPatient = matcher.group(0);
    	    				state = ParserState.FOR_TUMOR_KEY;
    	    				break;
    	    			}
    	    		}
    				
    				line = line.toLowerCase();
    				TumorSize ts = new TumorSize();
    				if(line.contains(TUMOR_SIZE_KEYWORD))
    				{
    					int idx = line.indexOf(TUMOR_SIZE_KEYWORD);
    					int unitofMeasureIdx =  line.indexOf("cm", idx+1);
    					if(unitofMeasureIdx == -1)
    					{
    						unitofMeasureIdx =  line.indexOf("mm", idx+1);
    						if(unitofMeasureIdx != -1)
    						{
    							ts.unit = "mm";
    						}
    					}
    					else
    					{
    						ts.unit = "cm";
    					}
    					
    					String subString = line;
    					if(unitofMeasureIdx != -1)
    					{
    						subString = line.substring(idx, unitofMeasureIdx);
    					}
    					
    					//match dimensions at most 3 times
						Pattern pattern = Pattern.compile("([0-9]+(\\.[0-9])?)");//"");
						Matcher matcher = pattern.matcher(subString);
   						int j = 0;
   					    // Find all matches
   					    while (matcher.find()) 
   					    {
   					    	// Get the matching string
    					    String match = matcher.group();
    					    ts.dim[j++] = Float.parseFloat(matcher.group());
    					    if(j >= 3) break;	
    					}
    					state = ParserState.FOR_PAT_ID;
    					tumorSizes.put(thisPatient, ts);
    				}
    			break;
    		} 		
    	}
    	
    	//display result
    	for(String patId : tumorSizes.keySet())
    	{
    		TumorSize ts = tumorSizes.get(patId);
    		System.out.println("PAT ID: " + patId);
    		System.out.println("Tumor Size: " + ts.dim[0] + " " + ts.dim[1] + " " + ts.dim[2]);
    		System.out.println("Tumor Measurement Unit: " + ts.unit + "\n");
    	}
    }
}
