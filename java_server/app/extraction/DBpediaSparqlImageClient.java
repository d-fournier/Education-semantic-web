package extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import dbpedia.DbpediaParser;
import dbpedia.DbpediaParser.Dbpedia_sparql;
import dbpedia.DbpediaParser.Triplet;


public class DBpediaSparqlImageClient {

	public static final String LITTERAL = "literal";

	private static String executeQuery(String resources)
	{
		if(resources == null || resources.equals("")){
			return null;
		}
		String query="select ?o where { <"+resources+"> dbpedia-owl:thumbnail ?o . }"; 

		try {
			query=URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		HttpMethod method =
				new GetMethod("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query="+query+"+&format=json&timeout=30000");
		HttpClient client = new HttpClient();

		try {
			client.executeMethod(method);
			return method.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, String> getImage(String query)
	{
		File folder = new File("tmp/"+query);
		Map<String, String> result = new HashMap<String, String>();

		if(folder.isDirectory())
		{			
			File[] searchEngines=folder.listFiles();
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
						String id = filesToParse[k].getName().replace(".dbpedia", "");
						String value = convert(filesToParse[k]);
						result.put(id, value);
					}
				}
			}
		}
		return result;
	}

	private static String convert(File input){
		String img = null;
		String value = "";

		Map<String, Integer> map = new HashMap<>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(input));
			String line;
			while ((line = br.readLine()) != null) {
			   Integer val = map.get(line);
			   if(val == null){
				   map.put(line, new Integer(1));
			   } else {
				   map.put(line, ++val);
			   }
			}
			br.close();
		} catch (Exception e1) {
		}

		Integer max = 0;
		for(String s : map.keySet()){
			if(map.get(s)>max){
				value = s;
				max = map.get(s);
			}
		}
		if( max == 0){
			return "";
		}
		try {
			String response=executeQuery(value);
			if(response != null){
				Dbpedia_sparql result = DbpediaParser.parseText(response);
				if(result != null){
					for(Triplet t : result.results.bindings){
						img = t.o.value;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}


	public static void main(String[] args) {
		Map<String, String> ranking = getImage("Sea turtle");
		
		System.out.println("############## Classement classé ##############");
		for (Map.Entry<String, String> entry : ranking.entrySet()) {
			System.out.println("[Key] : " + entry.getKey() 
					+ " [Value] : " + entry.getValue());
		}
		System.out.println("################# Fin Classement #############");
	}

}
