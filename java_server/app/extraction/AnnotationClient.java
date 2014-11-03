package extraction;

/**
 * Copyright 2011 Pablo Mendes, Max Jakob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class has been translate to scala. Please use the AnnotationClientScala.scala for new External Clients!
 * (AnnotationClientScala.scala is at eval/src/main/scala/org/dbpedia/spotlight/evaluation/external/)
 *
 * @author pablomendes
 */

public abstract class AnnotationClient {

	// Create an instance of HttpClient.
	private static HttpClient client = new HttpClient();


	public String request(HttpMethod method) throws Exception {

		String response = null;

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {

			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody(); //TODO Going to buffer response body of large or unknown size. Using getResponseBodyAsStream instead is recommended.

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			response = new String(responseBody);

		} catch (HttpException e) {
		} catch (IOException e) {
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
		return response;

	}

	protected static String readFileAsString(String filePath) throws java.io.IOException{
		return readFileAsString(new File(filePath));
	}

	protected static String readFileAsString(File file) throws IOException {
		byte[] buffer = new byte[(int) file.length()];
		BufferedInputStream f = new BufferedInputStream(new FileInputStream(file));
		f.read(buffer);
		return new String(buffer);
	}

	static abstract class LineParser {

		public abstract String parse(String s) throws ParseException;

		static class ManualDatasetLineParser extends LineParser {
			public String parse(String s) throws ParseException {
				return s.trim();
			}
		}

		static class OccTSVLineParser extends LineParser {
			public String parse(String s) throws ParseException {
				String result = s;
				try {
					result = s.trim().split("\t")[3];
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new ParseException(e.getMessage(), 3);
				}
				return result; 
			}
		}
	}

	public void saveExtractedEntitiesSet(File inputFile, File outputFile, LineParser parser, int restartFrom) throws Exception {
		PrintWriter out = new PrintWriter(outputFile);
		String text = readFileAsString(inputFile);
		int i=0;
		int correct =0 ;
		int error = 0;
		
		String s = parser.parse(text);
		if (s!= null && !s.equals("")) {
			i++;

			List<String> entities = new ArrayList<String>();
			try {
				//   final long startTime = System.nanoTime();
				entities = extract(text.replaceAll("\\s+"," "));
				//   final long endTime = System.nanoTime();
				// sum += endTime - startTime;
				// LOG.info(String.format("(%s) Extraction ran in %s ns.", i, endTime - startTime));
				correct++;
			} catch (Exception e) {
				error++;
				e.printStackTrace();
			}
			if(entities != null){
				for (String e: entities) {
					out.println(e);
				}					
			}
			out.println();
			out.flush();
		}
		out.close();
		//   double avg = (new Double(sum) / i);
		//  LOG.info(String.format("Average extraction time: %s ms", avg * 1000000));
	}


	public void evaluate(File inputFile, File outputFile) throws Exception {
		evaluateManual(inputFile,outputFile,0);
	}

	public void evaluateManual(File inputFile, File outputFile, int restartFrom) throws Exception {
		saveExtractedEntitiesSet(inputFile, outputFile, new LineParser.ManualDatasetLineParser(), restartFrom);
	}

	//    public void evaluateCurcerzan(File inputFile, File outputFile) throws Exception {
	//         saveExtractedEntitiesSet(inputFile, outputFile, new LineParser.OccTSVLineParser());
	//    }

	/**
	 * Entity extraction code.
	 * @param text
	 * @return
	 */
	public abstract List<String> extract(String text);
}
