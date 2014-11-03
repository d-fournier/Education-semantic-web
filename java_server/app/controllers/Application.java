package controllers;

import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import extraction.DBpediaLookupClient;
import extraction.DBpediaSparqlClient;
import extraction.DBpediaSpotlightClient;
import extraction.JsonModel;
import extraction.TextExtractor;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result formatResults() {
		String json = request().body().asJson().toString();

		System.out.println("Parse JSON");
		// Return search
		JsonModel.WebSearch request = TextExtractor.extractSearchResultFromJson(json);
		
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
			
			System.out.println("End for query : "+request.searchTerms);
			return ok("{\"test\":\"coucou\"}");

		}
		System.out.println("Error Parsing JSON");
		return ok(json);
	}

}
