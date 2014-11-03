package extraction;

public class JsonModel {

	public class WebSearch{
	    public String searchTerms;
	    public String searchEngine;
	    public WebPagesItem[] results;
	}
	
	public class WebPagesItem {
	    public String title;
	    public String url;
	    public String description;
	}
	
}
