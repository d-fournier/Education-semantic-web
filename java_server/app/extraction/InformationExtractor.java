package extraction;

public class InformationExtractor {

	public static WebSearch.WebPagesItem findInfoFromFile(WebSearch request, String filename){
		for(WebSearch.WebPagesItem item : request.results){
			String convertedURL = item.url.replaceAll("\"", "_");
			convertedURL = convertedURL.replaceAll("/", "_");

			if(convertedURL.equals(filename)){
				return item;				
			}
		}
		WebSearch.WebPagesItem item = new WebSearch.WebPagesItem();
		item.title = "";
		item.url = "";
		item.description = "";		
		return item;
	}
	
}
