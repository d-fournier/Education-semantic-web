package semantic_web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Extractor {

	public static void main(String[] args){
		String urlTest = new String("http://www.bordeaux.fr/");
		String bodyFromUrl;
		
		bodyFromUrl = extractRawTextFromUrl(urlTest);
		saveTextIntoAFile("testVilleBordeaux", bodyFromUrl);
	}
	/**
	 * Extract body text form an url
	 * @param url
	 * @return body
	 */
	public static String extractRawTextFromUrl (String url){

		Document doc = null;
		Elements elementBody = null;

		try {
			doc = Jsoup.connect(url).get();
			elementBody = doc.select("body");
		} catch (IOException e) {
			// An error occurs whilst fetching the URL
			e.printStackTrace();
			return new String("Erreur. \nVoilà je suis désolé je ne peux rien faire... Si vous vous ennuyez, allez faire un tour dans la forêt.");
		}
		
		return elementBody.text();
	}
	
	public static void saveTextIntoAFile(String filename, String text) {
		
		String realFileName = filename+".txt";
		try {
			PrintWriter out = new PrintWriter(realFileName);
			out.println(text);	
			out.close();
		} catch (FileNotFoundException e) {
			// Error during PrintWriter
			e.printStackTrace();
		}
	}

}
