package controllers;

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
		String json = request().body().asText();
		// Create file with website content
		// Return search
		JsonModel.WebSearch request = TextExtractor.extractDataFromJson(json);

		DBpediaSpotlightClient sc = new DBpediaSpotlightClient ();
		DBpediaLookupClient lc;
		try {
			lc = new DBpediaLookupClient(request.search);
			// Explore concepts thanks to search
			lc.writeConceptFromQuery(request.search);
		} catch (Exception e) {
			e.printStackTrace();
		} 

		// Writing of concepts linked to the results of search motors queries
		// Spotlight Text
		sc.writeTextConcepts();

		DBpediaSparqlClient sparql=new DBpediaSparqlClient();
		sparql.writeAllRdfFiles(); 

		return ok(json);
	}

}
