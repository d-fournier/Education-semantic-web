package jaccard;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SimilarityWithJaccard {
	
	public static void main(String[] args) {
		testCalculateSimilarity();
	}
	
	/**
	 * calculate similarity between two list of String
	 */
	private static void testCalculateSimilarity (){
		String [] text1 = {"au revoir", "lol", "habile"};
		String [] text2 = {"au revoir", "habile"};
		double jaccardResult = calc(text1,text2);
		
		System.out.println("Résultat similarité avec Jaccard : "+ jaccardResult);
	}
	
	public static double calculateSimilarity(String[] text1, String[] text2) {
		return calc(text1,text2);
	}

	/**
	 *
	 * @param a 
	 * @param b 
	 * @return 
	 */
	public static double calc(Object[] a, Object[] b){
		int alen = a.length;
		int blen = b.length;
		Set<Object> set = new HashSet<Object>(alen + blen);
		set.addAll(Arrays.asList(a));
		set.addAll(Arrays.asList(b));

		return innerCalc(alen, blen, set.size());
	}

	/**
	 *
	 * @param a 
	 * @param b 
	 * @return 
	 */
	public double calc(List<? extends Object> a, List<? extends Object> b){
		int alen = a.size();
		int blen = b.size();
		Set<Object> set = new HashSet<Object>(alen + blen);
		set.addAll(a);
		set.addAll(b);
		return innerCalc(alen, blen, set.size());
	}

	/**
	 *
	 * @param alen
	 * @param blen
	 * @param union
	 * @return Jaccard similarity between a and b
	 */
	 private static double innerCalc(int alen, int blen, int union){
		double overlap = alen +  blen - union;
		if( overlap <= 0 )
			return 0.0;
		return overlap / union;
	 }
}