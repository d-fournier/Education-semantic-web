package extraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import dbpedia.DbpediaParser;
import dbpedia.DbpediaParser.Dbpedia_sparql;
import dbpedia.DbpediaParser.Triplet;


public class DBpediaSparqlClient {

	private HttpClient client;
	public DBpediaSparqlClient()
	{
		client = new HttpClient();
	}	
	
	public void writeAllJsonFiles()
	{
		File input = new File(""); //Fichier non annote
        File output = new File(""); //Fichier annote
        
        File folder = new File("tmp");
        File[] queries = folder.listFiles();
        
        for( int i=0; i<queries.length; i++)
        {
        	if(queries[i].isDirectory())
        	{
        		File[] searchEngines=queries[i].listFiles();
        		for(int j=0; j< searchEngines.length; j++)
        		{
        			if(searchEngines[j].isDirectory())
        			{
        				File[] filesToParse=searchEngines[j].listFiles(new FilenameFilter() {									
							@Override
							public boolean accept(File dir, String name) {
								return name.endsWith(".dbpedia");
							}
						});
        				 for(int k=0; k<filesToParse.length; k++)
        	                {
        	                        input = filesToParse[k];
        	                       
        	                        output = new File(filesToParse[k].getPath().replace(".dbpedia",".rdf"));
        	                        try {
        	                        	String response=executeQuery(splitFile(input));
        	                    		Dbpedia_sparql result = DbpediaParser.parseText(response);
        	                        	FileOutputStream fos=new FileOutputStream(output);
        	                    		if(result != null){
        	                    			for(Triplet t : result.results.bindings){
        	                    				fos.write(("<"+t.s.value+"> ").getBytes());
        	                    				fos.write(("<"+t.p.value+"> ").getBytes());
        	                    				fos.write(("<"+t.o.value+"> \n").getBytes());
        	                    			}
        	                    		}
        	                        	fos.close();
        	                        	
									} catch (Exception e) {
										e.printStackTrace();
									}
        	                }
        			}
        		}
        		
        		
        	}
        } 
	}
	
	private List<String> splitFile(File file)
	{
		List<String> list=new ArrayList<String>();
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			
			byte[] buffer=new byte[(int) file.length()];
			fis.read(buffer);
			String tmp = new String(buffer);
			String [] tab = tmp.split("\r\n");
			for(String s : tab)
			{
				if(s != null && !s.equals("")){
					list.add(s);
				}
			}
			
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private String executeQuery(List<String> resources)
	{
		String query="SELECT * WHERE { ?s ?p ?o. FILTER(?s in (";
		if(resources.size()>0)
			query+="<"+resources.get(0)+">";
		
		for(int i=1; i<resources.size(); i++)
		{
			query+=", <"+resources.get(i)+">";
		}
		query+=")) }"; 
		
		try {
			query=URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		HttpMethod method =
				new GetMethod("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query="+query+"+&format=json&timeout=30000");
		
		try {
			client.executeMethod(method);
			return method.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
