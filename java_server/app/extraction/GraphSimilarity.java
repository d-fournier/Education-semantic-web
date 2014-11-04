package extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.riot.RiotException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class GraphSimilarity {

	public static Map<Long,String> sortGraphs()
	{
		Map<Long,String> map=new HashMap<Long, String>();
		File folder = new File("tmp");
		File[] queries = folder.listFiles();

		for( int i=0; i<queries.length; i++)
		{
			if(queries[i].isDirectory())
			{
				File[] queryFileToParse=queries[i].listFiles(new FilenameFilter() {									
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

				File[] searchEngines=queries[i].listFiles();

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
							long rank=compareModels(queryModel, websiteModel);
							map.put(rank, filesToParse[k].getName().replace(".rdf",""));
						}
					}
				}
			}
		}

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

		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		// lire le fichier RDF
		boolean finished = false;
		while(!finished){
			try{
				model.read(br,null,"N-TRIPLE");
				finished = true;
			} catch(RiotException e)
			{
				return null;
			}		
		}

		return model;
		// l'écrire sur la sortie standard
		//		model.write(System.out, "N-TRIPLE");
	}
	private static long compareModels(Model m1, Model m2)
	{
		return m2.intersection(m1).size()/m2.union(m1).size();
	}

	public static void main(String[] args)
	{
		DBpediaSparqlClient.writeAllRdfFiles("berlin");
		GraphSimilarity.sortGraphs();
	}
}
