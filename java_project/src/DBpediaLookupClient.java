

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Client that looks for concepts binded to a query
 */
public class DBpediaLookupClient extends DefaultHandler {
	private final static int NBRESULTS=10;
	private String query = "";
	private List<Map<String, String>> variableBindings = new ArrayList<Map<String, String>>();
	private Map<String, String> tempBinding = null;
	private String lastElementName = null;

	public DBpediaLookupClient(String query) throws Exception {
		this.query = query;
		HttpClient client = new HttpClient();
		String query2 = query.replaceAll(" ", "+"); // URLEncoder.encode(query, "utf-8");
		HttpMethod method =
				new GetMethod("http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryString=" +
						query2+"&MaxHits="+NBRESULTS);
		try {
			client.executeMethod(method);
			System.out.println(method);
			InputStream ins = method.getResponseBodyAsStream();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser sax = factory.newSAXParser();
			sax.parse(ins, this);
		} catch (HttpException he) {
			System.err.println("Http error connecting to lookup.dbpedia.org");
		} catch (IOException ioe) {
			System.err.println("Unable to connect to lookup.dbpedia.org");
		}
		method.releaseConnection();
	}



	/**
	 * Writes the concepts linked to the query in a file
	 * @param q query which we want to look for the linked concepts (used for the name of the file)
	 */
	public void writeConceptFromQuery(String q)
	{
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("tmp/"+q+"/"+q+".concept");
			DBpediaLookupClient dbLookup= new DBpediaLookupClient(q);
			List<Map<String, String>> resultList=dbLookup.variableBindings();
			for(Map<String,String> map : resultList)
			{
				fos.write((map.get("URI")+"\n").getBytes());
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**Methods to parse the results of the query to DbPedia
	 * 
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//System.out.println("startElement " + qName);
		if (qName.equalsIgnoreCase("result")) {
			tempBinding = new HashMap<String, String>();
		}
		lastElementName = qName;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		//System.out.println("endElement " + qName);
		if (qName.equalsIgnoreCase("result")) {
			if (!variableBindings.contains(tempBinding) && containsSearchTerms(tempBinding))
				variableBindings.add(tempBinding);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		String s = new String(ch, start, length).trim();
		//System.out.println("characters (lastElementName='" + lastElementName + "'): " + s);
		if (s.length() > 0) {
			if ("Description".equals(lastElementName)) {
				if (tempBinding.get("Description") == null) {
					tempBinding.put("Description", s);
				}
				tempBinding.put("Description", "" + tempBinding.get("Description") + " " + s);
			}
			if ("URI".equals(lastElementName) && s.indexOf("Category")==-1
					&& tempBinding.get("URI") == null) {
				tempBinding.put("URI", s);
			}
			if ("Label".equals(lastElementName)) tempBinding.put("Label", s);
		}
	}

	public List<Map<String, String>> variableBindings() {
		return variableBindings;
	}
	private boolean containsSearchTerms(Map<String, String> bindings) {
		StringBuilder sb = new StringBuilder();
		for (String value : bindings.values()) sb.append(value);  // do not need white space
		String text = sb.toString().toLowerCase();
		StringTokenizer st = new StringTokenizer(this.query);
		while (st.hasMoreTokens()) {
			if (text.indexOf(st.nextToken().toLowerCase()) == -1) {
				return false;
			}
		}
		return true;
	}

}

