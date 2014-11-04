package extraction;

public class InformationExtractor {

	public static WebSearch.WebPagesItem findInfoFromFile(WebSearch request, String filename){
		int i = 0;
		for(WebSearch.WebPagesItem item : request.results){
			String convertedURL = TextExtractor.getStringFromUrl(item.url);
			if(convertedURL.equals(filename)){
				item.id = i;
				return item;				
			}
			i++;
		}
		WebSearch.WebPagesItem item = new WebSearch.WebPagesItem();
		item.title = "";
		item.url = "";
		item.description = "";	
		item.id = 0;
		return item;
	}
	
}
