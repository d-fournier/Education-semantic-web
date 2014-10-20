
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dbpedia.spotlight.exceptions.AnnotationException;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
/**
 * Simple web service-based annotation client for DBpedia Spotlight.
 *
 * @author pablomendes, Joachim Daiber
 */
public class DBpediaSpotlightClient extends AnnotationClient {
        //private final static String API_URL = "http://jodaiber.dyndns.org:2222/";
    private final static String API_URL = "http://spotlight.dbpedia.org/";
        private static final double CONFIDENCE = 0.0;
        private static final int SUPPORT = 0;
        @Override
        public List<DBpediaResource> extract(Text text) throws AnnotationException {
                String spotlightResponse;
                try {
                        GetMethod getMethod = new GetMethod(API_URL + "rest/annotate/?" +
                                        "confidence=" + CONFIDENCE
                                        + "&support=" + SUPPORT
                                        + "&text=" + URLEncoder.encode(text.text(), "utf-8"));
                        // TODO : request for xhtml+xml type and parse it has RDF
                        // getMethod.addRequestHeader(new Header("Accept", "application/xhtml+xml"))
                        getMethod.addRequestHeader(new Header("Accept", "application/json"));
                        spotlightResponse = request(getMethod);
                } catch (UnsupportedEncodingException e) {
                        throw new AnnotationException("Could not encode text.", e);
                }
                assert spotlightResponse != null;
                JSONObject resultJSON = null;
                JSONArray entities = null;
                try {
                        resultJSON = new JSONObject(spotlightResponse);
                        entities = resultJSON.getJSONArray("Resources");
                } catch (JSONException e) {
                        throw new AnnotationException("Received invalid response from DBpedia Spotlight API.");
                }
                LinkedList<DBpediaResource> resources = new LinkedList<DBpediaResource>();
                for(int i = 0; i < entities.length(); i++) {
                        try {
                                JSONObject entity = entities.getJSONObject(i);
                                resources.add(
                                                new DBpediaResource(entity.getString("@URI"),
                                                                Integer.parseInt(entity.getString("@support"))));
                        } catch (JSONException e) {
                LOG.error("JSON exception "+e);
            }
                }
                return resources;
        }
        public static void main(String[] args) throws Exception {
        DBpediaSpotlightClient c = new DBpediaSpotlightClient ();
                
                File input = new File(""); //Fichier non annote
                File output = new File(""); //Fichier annote
                
                File folder = new File("files/input");
                File[] listOfFiles = folder.listFiles();
                for(int i=0; i<listOfFiles.length;i++)
                {
                        input = listOfFiles[i];
                        output = new File("files/output/output_"+i);
                        c.evaluate(input, output);
                }
    }
}
