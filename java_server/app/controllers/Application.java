package controllers;

import org.json.JSONObject;

import play.api.mvc.Content;
import play.libs.Json;

import jaccard.RankingWithJaccard;

import java.util.Map;

import akka.serialization.Serialization.Information;

import com.google.gson.Gson;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import extraction.DBpediaLookupClient;
import extraction.DBpediaSparqlClient;
import extraction.DBpediaSpotlightClient;
import extraction.InformationExtractor;
import extraction.TextExtractor;
import extraction.WebSearch;

import extraction.DBpediaPrefixSearch;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result formatResults() {
		String json = request().body().asJson().toString();

		System.out.println("Parse JSON");
		// Return search
		WebSearch request = TextExtractor.extractSearchResultFromJson(json);
		
		if(request != null){			
			System.out.println("Download Website for query : "+request.searchTerms);
			// Create files with website content
			TextExtractor.downloadWebsiteContent(request);
			
			System.out.println("Search related concept to query : "+request.searchTerms);
			// Create file with ressources related to the query
			DBpediaLookupClient.writeConceptFromQuery(request.searchTerms);

			System.out.println("Searching for concept into website content for query : "+request.searchTerms);
			// Writing of concepts linked to the results of search motors queries
			// Spotlight Text
			DBpediaSpotlightClient.writeTextConcepts(request.searchTerms);
			
			System.out.println("Create n-triplets for Websites for query : "+request.searchTerms);
			DBpediaSparqlClient.writeAllRdfFiles(request.searchTerms); 
			
			Map<String, Double> ranking = RankingWithJaccard.attributeAJaccardMark(request.searchTerms);

			WebSearch result = new WebSearch();
			result.searchEngine="SwaggySearchEngine";
			result.searchTerms=request.searchTerms;
			result.results = new WebSearch.WebPagesItem[ranking.size()];
			int i = 0;
			for(String s : ranking.keySet()){
				result.results[i] = InformationExtractor.findInfoFromFile(request, s);
				i++;
			}	
			String webServiceResult = new Gson().toJson(result);
			
			return ok(webServiceResult);

		}
		System.out.println("Error Parsing JSON");
		return ok(json);
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result suggestions(String prefix) {
		
		DBpediaPrefixSearch dp = new DBpediaPrefixSearch(5);
		return ok(dp.getAutoCompletionList(prefix));
		
		
	}
	
	

}
