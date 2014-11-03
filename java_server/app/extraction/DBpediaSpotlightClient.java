package extraction;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Web service-based annotation client for DBpedia Spotlight.
 */
public class DBpediaSpotlightClient extends AnnotationClient {
	//private final static String API_URL = "http://jodaiber.dyndns.org:2222/";
	private final static String API_URL = "http://spotlight.dbpedia.org/";
	private static final double CONFIDENCE = 0.2;
	private static final int SUPPORT = 20;

	@Override
	public List<String> extract(String text) {
		String spotlightResponse;
		try {
			System.out.println("Launch Spotlight WS");
			GetMethod getMethod = new GetMethod(API_URL + "rest/annotate/?" +
					"confidence=" + CONFIDENCE
					+ "&support=" + SUPPORT
					+ "&text=" + URLEncoder.encode(text, "utf-8"));
			getMethod.addRequestHeader(new Header("Accept", "application/json"));
			spotlightResponse = request(getMethod);
			System.out.println("Finish Spotlight WS");
		} catch (Exception e) {
			return null;
		}
		assert spotlightResponse != null;
		JSONObject resultJSON = null;
		JSONArray entities = null;
		try {
			resultJSON = new JSONObject(spotlightResponse);
			entities = resultJSON.getJSONArray("Resources");
		} catch (JSONException e) {
			return null;
		}
		LinkedList<String> resources = new LinkedList<String>();
		for(int i = 0; i < entities.length(); i++) {
			try {
				JSONObject entity = entities.getJSONObject(i);
				resources.add(new String(entity.getString("@URI")));
			} catch (JSONException e) {
			}
		}
		return resources;
	}

	public void writeTextConcepts()
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
								return name.endsWith(".resultsearch");
							}
						});
						for(int k=0; k<filesToParse.length; k++)
						{
							input = filesToParse[k];
							output = new File(filesToParse[k].getPath().replace(".resultsearch",".dbpedia"));
							try {
								evaluate(input, output);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}


			}
		} 
	}
	
    public static void main(String[] args) throws Exception {
        DBpediaSpotlightClient sc = new DBpediaSpotlightClient ();
        
        //Research of concepts linked to Berlin
        DBpediaLookupClient lc= new DBpediaLookupClient("berlin"); 
        
        //Writing of concepts linked to the results of search motors queries
         sc.writeTextConcepts();
         
         //Writing of concepts linked to Berlin (found just before)
         lc.writeConceptFromQuery("berlin");
         
         DBpediaSparqlClient sparql=new DBpediaSparqlClient();
         sparql.writeAllJsonFiles(); 
    }
}
