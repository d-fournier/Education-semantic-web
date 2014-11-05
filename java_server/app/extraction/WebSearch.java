package extraction;

public class WebSearch{

	    public String searchTerms;
	    public String searchEngine;
	    public WebPagesItem[] results;
	
		public static class WebPagesItem {
	    public String title;
	    public String url;
	    public String description;
	    /**
	     * Does not exist in JSON, just for output
	     */
	    public int id;
	    public int[] idSimilarWebsite;
	    public String img;
	}
	
}
