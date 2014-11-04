package extraction;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import jaccard.RankingWithJaccard;


public class GraphSimilarity {

	public static Map<String, Double> sortGraphs(String request)
	{
		Map<String, Double> map=new HashMap<String, Double>();
		File folder = new File("tmp/"+request);

		File[] queryFileToParse=folder.listFiles(new FilenameFilter() {									
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".rdf");
			}
		});
		Model queryModel=null;
		for(int l=0; l<queryFileToParse.length; l++)
		{
			queryModel=createGraph(queryFileToParse[l].getPath());

		}

		File[] searchEngines=folder.listFiles();

		for(int j=0; j< searchEngines.length; j++)
		{
			if(searchEngines[j].isDirectory())
			{
				File[] filesToParse=searchEngines[j].listFiles(new FilenameFilter() {									
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".rdf");
					}
				});
				for(int k=0; k<filesToParse.length; k++)
				{
					Model websiteModel=createGraph(filesToParse[k].getPath());
					double rank=compareModels(queryModel, websiteModel);
					map.put( filesToParse[k].getName().replace(".rdf",""), rank);
				}
			}
		}
		RankingWithJaccard.sortMapByValues(map);
		return map;	
	}

	private static Model createGraph(String inputFileName)
	{
		// créer un modèle vide
		Model model = ModelFactory.createDefaultModel();

		// utiliser le FileManager pour trouver le fichier d'entrée
		InputStream in = FileManager.get().open( inputFileName );
		if (in == null) {
			throw new IllegalArgumentException(
					"Fichier: " + inputFileName + " non trouvé");
		}

		model.read(in,null,"N-TRIPLE");

		return model;
	}
	private static double compareModels(Model m1, Model m2)
	{
		return (double)m2.intersection(m1).size()/m2.union(m1).size();
	}

	public static void main(String[] args)
	{
		DBpediaSparqlClient.writeAllRdfFiles("machin");
		GraphSimilarity.sortGraphs("machin");
	}
}
