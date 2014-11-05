package controllers;

import jaccard.RankingWithJaccard;

import java.util.Map;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.google.gson.Gson;

import extraction.DBpediaLookupClient;
import extraction.DBpediaPrefixSearch;
import extraction.DBpediaSparqlClient;
import extraction.DBpediaSparqlImageClient;
import extraction.DBpediaSpotlightClient;
import extraction.GraphSimilarity;
import extraction.InformationExtractor;
import extraction.TextExtractor;
import extraction.WebSearch;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result simpleListCompare() {
		String json = request().body().asJson().toString();

		System.out.println("Parse JSON");
		// Return search
		WebSearch request = TextExtractor.extractSearchResultFromJson(json);
		
		if(request != null){			
			System.out.println("Download Website for query : "+request.searchTerms);
			// Create files with website content
			TextExtractor.downloadWebsiteContent(request);

			// Create file with ressources related to the query
			System.out.println("Search related concept to query : "+request.searchTerms);
			DBpediaLookupClient.writeConceptFromQuery(request.searchTerms);

			// Writing of concepts linked to the results of search motors queries
			// Spotlight Text
			System.out.println("Searching for concept into website content for query : "+request.searchTerms);
			DBpediaSpotlightClient.writeTextConcepts(request.searchTerms);
			
			System.out.println("Rank for query : "+request.searchTerms);			
			Map<String, Double> ranking = RankingWithJaccard.attributeAJaccardMark(request.searchTerms);

			System.out.println("Search Images for query : "+request.searchTerms);
			Map<String, String> image = DBpediaSparqlImageClient.getImage(request.searchTerms);
			
			WebSearch webServiceResult = createReturnWebsearch(ranking, request, image);
			System.out.println("End for query : "+request.searchTerms);
			return ok(new Gson().toJson(webServiceResult));

		}
		System.out.println("Error Parsing JSON");
		return ok(json);
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result rdfGraphCompare() {
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
			System.out.println("Rank for query : "+request.searchTerms);			
			Map<String, Double> ranking = GraphSimilarity.sortGraphs(request.searchTerms);

			System.out.println("Search Images for query : "+request.searchTerms);
			Map<String, String> image = DBpediaSparqlImageClient.getImage(request.searchTerms);
			WebSearch webServiceResult = createReturnWebsearch(ranking, request,image);
			
			System.out.println("End for query : "+request.searchTerms);
			return ok(new Gson().toJson(webServiceResult));

		}
		System.out.println("Error Parsing JSON");
		return ok(json);
	}	
	
	private static WebSearch createReturnWebsearch(Map<String, Double> ranking, WebSearch originalRequest, Map<String, String> imageMap){
		WebSearch result = new WebSearch();
		result.searchEngine="SwaggySearchEngine";
		result.searchTerms=originalRequest.searchTerms;
		result.results = new WebSearch.WebPagesItem[ranking.size()];
		int i = 0;
		for(String s : ranking.keySet()){
//			System.out.println(s +" : "+ranking.get(s));
			result.results[i] = InformationExtractor.findInfoFromFile(originalRequest, s);
			result.results[i].img = imageMap.get(s);
			i++;
		}	
		
		return result;
	}
	
}
