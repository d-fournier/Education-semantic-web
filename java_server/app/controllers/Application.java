package controllers;

import org.json.JSONObject;

import play.api.mvc.Content;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import extraction.DBpediaPrefixSearch;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }
    
	@BodyParser.Of(BodyParser.Json.class)
	public static Result formatResults() {
	  	JsonNode json = request().body().asJson();
	 	//ObjectNode result = Json.newObject();
	  	//String searchEngine = json.findPath("results").textValue();
	  	//if(searchEngine == null) {
		 // return badRequest("Missing parameter [name]");
	  	//} else {
	    	//result=(ObjectNode)json;
    		return ok(json);
	  	//}
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result suggestions(String prefix) {
		
		DBpediaPrefixSearch dp = new DBpediaPrefixSearch(5);
		return ok(dp.getAutoCompletionList(prefix));
		
		
	}
	
	

}
