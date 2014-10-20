package semantic_web.extraction;

public class JsonModel {

	public class WebSearch{
	    public String search;
	    public String searchEngine;
	    public WebPagesItem[] result;
	}
	
	public class WebPagesItem {
	    public String title;
	    public String url;
	    public String description;
	}
	
}
