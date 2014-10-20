   $(document).ready(function() {
        $('#search').click(function()
		{
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
					console.log(data);
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