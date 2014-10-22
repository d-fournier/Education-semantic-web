   $(document).ready(function() {

		$('#search-form').submit(function(event){
			event.preventDefault();
			var apiKey = "AIzaSyDZjrXVfbGRsUIZpOpB_I9BkIkIhQWoJ_Y";
			var cx = '016813502462276054558:2encdk-x_ka';
			var query = $('#query').val();
			var nbResults = 50;
			var resultsPerPage = 10;
			var nbPages = nbResults/resultsPerPage;
			for( var i=0 ; i< nbPages ; i++){
				var getQueryResultJsonUrl = "https://www.googleapis.com/customsearch/v1?key=" + apiKey 
											+ "&cx=" + cx 
											+ "&q=" + query 
											+ "&num=" + resultsPerPage
											+ "&start=" + (i*resultsPerPage+1); 
			
				$.getJSON( getQueryResultJsonUrl, function(data){
					formatJSON(data);
				});
				
				var jqxhr = $.getJSON( getQueryResultJsonUrl, function() {
					console.log( "success" );
				})
				.done(function() {
					console.log( "second success" );
				})
				.fail(function() {
					console.log( "error" );
				});
			}
			
			});
      });
	  
	function formatJSON(rawResponseJSON){
		console.log("This is your shit : " );
		var searchTerms = rawResponseJSON.queries.request[0].searchTerms;
		var result = rawResponseJSON.items;
		for(item in result){
			console.log(result[item]);
		}
	
	}