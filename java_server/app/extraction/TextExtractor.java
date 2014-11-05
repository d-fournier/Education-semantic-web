package extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class TextExtractor {

	public static final String JSON_SAMPLE = "{ \"searchEngine\":\"google.fr\", \"results\":[ { \"title\":\"Speedtest.net by Ookla - The Global Broadband Speed Test\", \"url\":\"www.speedtest.net/\", \"description\":\"Test your Internet connection bandwidth to locations around the world with this \\ninteractive broadband speed test from Ookla.\" }, { \"title\":\"Create Tests for Organizational Training and Certification Programs ...\", \"url\":\"www.test.com/\", \"description\":\"Test.com provides a complete software solution for creating online tests and \\nmanaging enterprise and specialist certification programs, in up to 22 languages.\" }, { \"title\":\"Tested\", \"url\":\"www.tested.com/\", \"description\":\"This week\'s Show and Tell is another awesome project shared by our 3D printing \\ncolumnist Sean Charlesworth. Norm visits Sean while in New York to check out ...\" } ], \"searchTerms\":\"Test\" }";
	public static final String EXTENSION_FILE = ".resultsearch";
	public static final String REGEX = "(.*?)\\/.*";


	public static void main(String[] args){
		WebSearch ws = extractSearchResultFromJson(JSON_SAMPLE);
		downloadWebsiteContent(ws);
	}

	public static WebSearch extractSearchResultFromJson (String json){
		JsonReader reader = new JsonReader(new StringReader(json));
		JsonParser parser = new JsonParser();
		WebSearch webSearch = null;
		try {
			reader.setLenient(true);

			if(reader.hasNext()){
				webSearch = new Gson().fromJson(parser.parse(reader), WebSearch.class);
			}
		} catch (IOException e) {
		} catch (IllegalStateException e){
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
				}
			}
		}
		int i = 0;
		for(WebSearch.WebPagesItem item : webSearch.results){
			item.id = i;
			i++;
		}
		return webSearch;
	}

	/**
	 * Parse Json and save website content into files
	 * @param element JSonElement
	 */
	public static void downloadWebsiteContent(WebSearch webSearch) {
		for(WebSearch.WebPagesItem webPagesListItem : webSearch.results){
			try{

				String bodyFromUrl = extractBodyTextFromUrl(webPagesListItem.url);
				if(bodyFromUrl != null && bodyFromUrl != ""){
					String text = webPagesListItem.description.replace("\n", " ") + bodyFromUrl;
					saveTextIntoFile(webSearch,webPagesListItem, text, EXTENSION_FILE);       				
				}
			}catch(Exception e){}
		}
	}

	/**
	 * Extract body text form an url
	 * @param url
	 * @return body
	 */
	public static String extractBodyTextFromUrl (String url){

		Document doc = null;
		Elements elementBody = null;

		// Ajout pour connexion http
		url = "http://" + url;

		try {
			doc = Jsoup.connect(url).get();
			elementBody = doc.select("body");
		} catch (IOException e) {
			// An error occurs whilst fetching the URL
			return null;
		}

		return elementBody.text();
	}

	public static void saveTextIntoFile(WebSearch webSearch ,WebSearch.WebPagesItem webSearchItem, String text, String extension) {
		String filenameFinal = getStringFromUrl(webSearchItem);

		File folder = new File("./tmp/"+webSearch.searchTerms+"/"+webSearch.searchEngine);
		folder.mkdirs();
		// un dossier par search
		// extension tmp pour gitignore
		String pathAndFileName = "./tmp/"+webSearch.searchTerms+"/"+webSearch.searchEngine+"/" + filenameFinal+extension;
		try {
			PrintWriter out = new PrintWriter(pathAndFileName);
			out.println(text);	
			out.close();
		} catch (FileNotFoundException e) {
			// Error during PrintWriter
			e.printStackTrace();
		}
	}

	public static String getStringFromUrl(WebSearch.WebPagesItem item){
		String result = ""+item.id;
		return result;
	}

}
