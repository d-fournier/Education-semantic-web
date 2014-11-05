package extraction;

import jaccard.RankingWithJaccard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;


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

	public static class Couple
	{
		String s1;
		String s2;
		public Couple()
		{

		}
	}

	public static Map<String, List<String>> compareAllSites(double minSimValue, String request)
	{
		Map<Couple, Double> mapCompareSiteToSite = new HashMap<Couple, Double>();
		Map<String, List<String>> mapTotal = new HashMap<String, List<String>>();
		String[] filesName = null;

		File folder = new File("tmp/"+request);
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
					if(filesName==null)
					{
						filesName = new String[filesToParse.length];
					}
					Model websiteModel=createGraph(filesToParse[k].getPath());
					Couple couple = new Couple();
					couple.s1 = filesToParse[k].getName();
					filesName[k] = filesToParse[k].getName();
					for(int m=0;m<filesToParse.length;m++)
					{
						double rank=compareModels(createGraph(filesToParse[m].getPath()), websiteModel);
						couple.s2 = filesToParse[m].getName();
						mapCompareSiteToSite.put(couple, rank);
					}	
				}
			}
		}
		//Créer la map de similarité, on a accès au score des sites deux à deux
		for(int i=0; i<filesName.length; i++)
		{
			String siteName = filesName[i];
			for (Map.Entry<Couple,Double> entry : mapCompareSiteToSite.entrySet()) {
				Couple key = entry.getKey();
				Double rank = entry.getValue();

				//TODO Or ?????????
				if((siteName.equals(key.s1) || siteName.equals(key.s2)) && rank>minSimValue)
				{
					List<String> values = mapTotal.get(siteName);
					if(values==null)
					{
						values = new ArrayList<String>();
					}
					if(siteName.equals(key.s1))
					{
						values.add(key.s2);
					}else 
					{
						values.add(key.s1);
					}
					mapTotal.put(siteName, values);
				}
			}
		}

		return mapTotal;

	}

	private static Model createGraph(String inputFileName)
	{
		// créer un modèle vide
		Model model = ModelFactory.createDefaultModel();

		// utiliser le FileManager pour trouver le fichier d'entrée
		InputStream in = FileManager.get().open( inputFileName );
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		try{
			model.read(br,null,"N-TRIPLE");
		} catch (Exception e){
		}

		return model;
	}
	private static double compareModels(Model m1, Model m2)
	{
		double intersection = m2.intersection(m1).size();
		double union = m2.union(m1).size();
		return intersection/union;
	}

	public static void main(String[] args)
	{
		//DBpediaSparqlClient.writeAllRdfFiles("Test");
		//GraphSimilarity.sortGraphs("Test");
		GraphSimilarity.compareAllSites(0.2, "Test");
	}
}
