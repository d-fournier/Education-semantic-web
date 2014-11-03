package dbpedia;

import java.io.IOException;
import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import extraction.JsonModel;

public class DbpediaParser {

	public static final String JSON_SAMPLE = "{ \"head\": { \"link\": [], \"vars\": [ \"s\", \"p\", \"o\" ] }, \"results\": { \"distinct\": false, \"ordered\": true, \"bindings\": [ { \"s\": { \"type\": \"uri\", \"value\": \"http://dbpedia.org/resource/Student\" }, \"p\": { \"type\": \"uri\", \"value\": \"http://www.w3.org/2002/07/owl#sameAs\" }, \"o\": { \"type\": \"uri\", \"value\": \"http://rdf.freebase.com/ns/m.014cnc\" } }, { \"s\": { \"type\": \"uri\", \"value\": \"http://dbpedia.org/resource/Student\" }, \"p\": { \"type\": \"uri\", \"value\": \"http://www.w3.org/2002/07/owl#sameAs\" }, \"o\": { \"type\": \"uri\", \"value\": \"http://fr.dbpedia.org/resource/\u00c9tudiant\" } }, { \"s\": { \"type\": \"uri\", \"value\": \"http://dbpedia.org/resource/Student\" }, \"p\": { \"type\": \"uri\", \"value\": \"http://www.w3.org/2002/07/owl#sameAs\" }, \"o\": { \"type\": \"uri\", \"value\": \"http://de.dbpedia.org/resource/Student\" } }, { \"s\": { \"type\": \"uri\", \"value\": \"http://dbpedia.org/resource/Student\" }, \"p\": { \"type\": \"uri\", \"value\": \"http://www.w3.org/2002/07/owl#sameAs\" }, \"o\": { \"type\": \"uri\", \"value\": \"http://wikidata.dbpedia.org/resource/Q48282\" } }, { \"s\": { \"type\": \"uri\", \"value\": \"http://dbpedia.org/resource/Student\" }, \"p\": { \"type\": \"uri\", \"value\": \"http://www.w3.org/2002/07/owl#sameAs\" }, \"o\": { \"type\": \"uri\", \"value\": \"http://cs.dbpedia.org/resource/Student\" } } ] } }";

	public Head head;
	public Results results;

	public static Dbpedia_sparql parseText(String json){
		JsonReader reader = new JsonReader(new StringReader(json));
		JsonParser parser = new JsonParser();
		Dbpedia_sparql result = null;
		try {
			reader.setLenient(true);
			if(reader.hasNext()){
				result = new Gson().fromJson(parser.parse(reader), Dbpedia_sparql.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					// Error closing stream
				}
			}
		}
		return result;
	}


	public class Dbpedia_sparql {
		public Head head;
		public Results results;
	}

	public class Head {
		public String[] link;
		public String[] vars;
	}

	public class Ressource {
		public String type;
		public String value;
	}

	public class Results {
		public boolean distinct;
		public boolean ordered;
		public Triplet[] bindings;
	}

	public class Triplet {
		public Ressource s;
		public Ressource p;
		public Ressource o;
	}

	
	public static void main(String[] args){
		Dbpedia_sparql result = parseText(JSON_SAMPLE);
		if(result != null){
			for(Triplet t : result.results.bindings){
				System.out.print("<"+t.s.value+"> ");
				System.out.print("<"+t.p.value+"> ");
				System.out.println("<"+t.o.value+">");
			}
		} else {
			System.out.println("Error");
		}
	}
}
