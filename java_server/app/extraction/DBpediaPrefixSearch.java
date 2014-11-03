package extraction;

import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Exception;


public class DBpediaPrefixSearch extends AnnotationClient{
	
	private static HttpClient client = new HttpClient();
	private final static String API_URL = "http://lookup.dbpedia.org/api/search/PrefixSearch?";
	private static int maxHits;
	
	public DBpediaPrefixSearch(int maxHits){
		this.maxHits= maxHits;
	}
	
	@Override
	public List<String> extract(String text) throws Exception{
		throw new Exception("not implemented");
	}
	
	public String getAutoCompletionList(String prefix){
		String spotlightResponse;
		try {
			GetMethod getMethod = new GetMethod(API_URL 
					+"QueryClass=" 
					+ "&MaxHits=" + maxHits
					+ "&QueryString=" + URLEncoder.encode(prefix, "utf-8"));
			getMethod.addRequestHeader(new Header("Accept", "application/json"));
			spotlightResponse = request(getMethod);
			
		} catch (Exception e) {
			return null;
		}
		
		assert spotlightResponse != null;
		JSONObject resultJSON = null;
		JSONArray entities = null;
		JSONArray suggestions = new JSONArray();
		try {
			resultJSON = new JSONObject(spotlightResponse);
			entities = resultJSON.getJSONArray("results");
			for(int i = 0; i < entities.length(); i++) {
				try {
					suggestions.put(entities.getJSONObject(i).get("label"));
					
				} catch (JSONException e) {
				}
			}
		
			
		} catch (JSONException e) {
			return null;
		}
		
		
		return suggestions.toString();
	}
	
	

}
