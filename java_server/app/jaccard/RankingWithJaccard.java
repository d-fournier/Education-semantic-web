package jaccard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class RankingWithJaccard {

	private static String DBPEDIA_EXTENSION = "dbpedia";
	private static String CONCEPT_EXTENSION = "concept";
	private static String TMP_PATH = "tmp\\";

	public static void main(String[] args) {
		Map<String, Double> ranking = attributeAJaccardMark("berlin");
		
		System.out.println("############## Classement classé ##############");
		for (Map.Entry<String, Double> entry : ranking.entrySet()) {
			System.out.println("[Key] : " + entry.getKey() 
					+ " [Value] : " + entry.getValue());
		}
		System.out.println("################# Fin Classement #############");
	}

	/**
	 * Return the ranking with a map containing the websites and their Jaccard values between 
	 * their concepts and the concepts of the user request
	 */
	public static Map<String, Double> attributeAJaccardMark(String userRequest){

		Map <String, Double> jaccardMap = new HashMap<>();

		TMP_PATH += userRequest;
		String [] listOfSearchEngineForOneSearch;
		List <String> list_dbpediaPathName = new ArrayList<>();

		// Retrieve all Browsers folder
		listOfSearchEngineForOneSearch = retrieveSearchEngineFolderForOneRequest(TMP_PATH);

		// Retrieve all Sites folders in each Browser folder
		list_dbpediaPathName = getAllDbpediaPathName(TMP_PATH, listOfSearchEngineForOneSearch);

		// Retrieve file .concept
		String conceptPathName = TMP_PATH;
		conceptPathName = retrieveConceptForTheSearch(TMP_PATH);

		// Read .concept
		File file = new File(conceptPathName);
		FileInputStream fisConcept;
		byte [] buffer = new byte[(int) file.length()];
		try {
			fisConcept = new FileInputStream(file);
			fisConcept.read(buffer);
			fisConcept.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String concepts = new String(buffer);

		// Read all .dbpedia
		for ( String db : list_dbpediaPathName )	{
			File fileDB = new File(db);
			FileInputStream fisDB;
			byte [] bufferDB = new byte[(int) fileDB.length()];
			try {
				fisDB = new FileInputStream(fileDB);
				fisDB.read(bufferDB);
				fisDB.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String conceptsDB = new String(bufferDB);
			String website = db.substring(db.lastIndexOf("\\")+1, db.lastIndexOf("."));

			// Jaccard
			double res = SimilarityWithJaccard.calculateSimilarity(conceptsDB.split("\r\n"), concepts.split("\r\n"));
			Double resultat = new Double(res);
			jaccardMap.put(website, resultat);
		}
	
		// Ranking
		return sortMapByValues(jaccardMap);
	}

	private static Map<String, Double> sortMapByValues(Map<String, Double> unsortMap) {
		 
		// Convert Map to List
		List<Map.Entry<String, Double>> list = 
			new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
                                           Map.Entry<String, Double> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	private static String[] retrieveSearchEngineFolderForOneRequest(String pathIntoRequestFolder){
		File actual = new File(pathIntoRequestFolder);

		String [] listOfSearchEngine_String = actual.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});

		return listOfSearchEngine_String;
	}

	private static List<String> getAllDbpediaPathName (String actualP, String [] listOfSearchEngine){

		List <String> list_dbpediaPathName = new ArrayList<>();

		// For each SearchEngine
		for( String searchEngine : listOfSearchEngine){

			String actualPathInEachSearchEngine = actualP+"\\"+searchEngine;
			File actualFileInEachBrowser = new File(actualPathInEachSearchEngine);

			// For each Sites
			for( File f : actualFileInEachBrowser.listFiles()){
				String fileName = f.getName();

				String pathFileNameDbpedia = new String(actualPathInEachSearchEngine);
				String extensionFiles = fileName.substring(fileName.lastIndexOf('.') + 1);

				if (extensionFiles.equals(DBPEDIA_EXTENSION)){
					pathFileNameDbpedia += "\\" + fileName;
					list_dbpediaPathName.add(pathFileNameDbpedia);
				}
			}
		}
		return list_dbpediaPathName;
	}

	private static String retrieveConceptForTheSearch (String actualP){

		File actualFilesInSearchFolder = new File(actualP);
		for( File f : actualFilesInSearchFolder.listFiles()){
			String fileName = f.getName();
			String pathFileNameConcept = new String(actualP);

			String extensionFiles = fileName.substring(fileName.lastIndexOf('.') + 1);

			if (extensionFiles.equals(CONCEPT_EXTENSION)){
				pathFileNameConcept += "\\" + fileName;
				return pathFileNameConcept;
			}
		}
		return null;
	}
}
