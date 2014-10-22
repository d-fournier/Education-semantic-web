   $(document).ready(function() {

       $('#searchForm').submit(function(event) {
           event.preventDefault();
           var apiKey = "AIzaSyDZjrXVfbGRsUIZpOpB_I9BkIkIhQWoJ_Y";
           var cx = '016813502462276054558:2encdk-x_ka';
           var query = $('#query').val();
           //for testing
           var nbResults = 10;
           var resultsPerPage = 10;
           var nbPages = nbResults / resultsPerPage;
           for (var i = 0; i < nbPages; i++) {
               var getQueryResultJsonUrl = "https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=" + cx + "&q=" + query + "&num=" + resultsPerPage + "&start=" + (i * resultsPerPage + 1);

               $.getJSON(getQueryResultJsonUrl, function(data) {
                   formatJSON(data);
               });

               var jqxhr = $.getJSON(getQueryResultJsonUrl, function() {
                       console.log("success");
                   })
                   .done(function() {
                       console.log("second success");
                   })
                   .fail(function() {
                       console.log("error");
                   });
           }

       });
   });

   function formatJSON(rawResponseJSON) {
       console.log("This is your shit : ");
       var searchTerms = rawResponseJSON.queries.request[0].searchTerms;
       var rawResult = rawResponseJSON.items;
       var formattedJSON = {
           search: searchTerms,
           //TODO : find a way to get the search engine dynamically
           searchEngine: "google.fr",
           results: []
       };
       for (var item in rawResult) {
           var formattedItem = {
               title: rawResult[item].title,
               url: rawResult[item].formattedUrl,
               description: rawResult[item].snippet
           };
           formattedJSON.results.push(formattedItem);
       }
       console.log(formattedJSON);



   }
