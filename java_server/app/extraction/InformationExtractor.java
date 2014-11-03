package extraction;

public class InformationExtractor {

	public static WebSearch.WebPagesItem findInfoFromFile(WebSearch request, String filename){
		for(WebSearch.WebPagesItem item : request.results){
			String convertedURL = TextExtractor.getStringFromUrl(item.url);

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
