package extraction;

public class InformationExtractor {

	private static final String PATH = "tmp";
	
	public static void createInfoFiles(WebSearch request){
		for(int i = 0; i<request.results.length;i++){
			String text = request.results[i].title +"\n"+ request.results[i].url + "\n"+request.results[i].description;
			TextExtractor.saveTextIntoFile(request.searchTerms, request.searchEngine, request.results[i].url, text, ".info");			
		}
	}
	
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
