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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class TextExtractor {

	public static final String JSON_SAMPLE = "{    \"search\":\"Bordeaux\",    \"searchEngine\":\"google.fr\",    \"result\":[        {            \"title\":\"Site officiel de la ville de Bordeaux - Bordeaux\",            \"url\":\"www.bordeaux.fr\",            \"description\":\"Le site officiel de la ville informe sur l'actualit\u00E9, l'agenda, les services, les d\u00E9marches et publie des guides pour sortir, \u00E9tudier, travailler et vivre \u00E0 Bordeaux.\"        },        {            \"title\":\"Bordeaux \u2014 Wikip\u00E9dia\",            \"url\":\"fr.wikipedia.org/wiki/Bordeaux\",            \"description\":\"Bordeaux (prononc\u00E9 [b??.'d?o ]) est une commune du Sud-Ouest de la France, pr\u00E9fecture du d\u00E9partement de la Gironde et chef-lieu de la r\u00E9gion d'Aquitaine.\"        }    ]}";
	public static final String extensionFile = ".resultsearch";

	public static void main(String[] args){
		extractDataFromJson(JSON_SAMPLE);
	}

	public static void extractDataFromJson (String json){
		JsonReader reader = new JsonReader(new StringReader(json));
		JsonParser parser = new JsonParser();
		try {
			reader.setLenient(true);

			if(reader.hasNext()){
				parse(parser.parse(reader));
			}
		} catch (IOException e) {
		} catch (IllegalStateException e){
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					// Error closing stream
				}
			}
		}
	}

	/**
	 * Parse Json and save website content into files
	 * @param element JSonElement
	 */
	public static void parse(JsonElement element) {
		JsonModel.WebSearch webSearch = new Gson().fromJson(element, JsonModel.WebSearch.class);
		for(JsonModel.WebPagesItem webPagesListItem : webSearch.result){
			String bodyFromUrl = extractBodyTextFromUrl(webPagesListItem.url);
			if(bodyFromUrl != null && bodyFromUrl != ""){
				saveTextIntoFile(webSearch.search,webSearch.searchEngine, webPagesListItem.url, bodyFromUrl);       				
			}
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
			e.printStackTrace();
			return null;
		}

		return elementBody.text();
	}

	/**
	 * Create a file into ./tmp/externFolder/internalFolder/filename.tmp
	 * @param externFolder
	 * @param internFolder
	 * @param fileName
	 * @param text
	 */
	public static void saveTextIntoFile(String externFolder,String internFolder, String fileName, String text) {
		String filenameFinal = fileName;
		filenameFinal = filenameFinal.replaceAll("\"", "_");
		filenameFinal = filenameFinal.replaceAll("/", "_");

		File folder = new File("./tmp/"+externFolder+"/"+internFolder);
		folder.mkdirs();
		// un dossier par search
		// extension tmp pour gitignore
		String pathAndFileName = "./tmp/"+externFolder+"/"+internFolder+"/" + filenameFinal+extensionFile;
		try {
			PrintWriter out = new PrintWriter(pathAndFileName);
			out.println(fileName);
			out.println(text);	
			out.close();
		} catch (FileNotFoundException e) {
			// Error during PrintWriter
			e.printStackTrace();
		}
	}

}
