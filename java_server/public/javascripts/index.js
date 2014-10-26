   $(document).ready(function() {
       $('#searchForm').submit(function(event) {
           event.preventDefault();
           var apiKey2 = "AIzaSyDZjrXVfbGRsUIZpOpB_I9BkIkIhQWoJ_Y";
           var apiKey = "AIzaSyBOeLl5E9RSrKA0QpWFuF3F91n4rmcPz8o"
           var cx = '016813502462276054558:2encdk-x_ka';
           var searchTerms = $('#searchTerms').val();
           //for testing
           var nbResults = 10;
           var resultsPerPage = 10;
           var nbPages = nbResults / resultsPerPage;

           var formattedJSON = {

               //TODO : find a way to get the search engine dynamically
               searchEngine: "google.fr",
               results: []
           };
           for (var i = 0; i < nbPages; i++) {
               var getQueryResultJsonUrl = "https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=" + cx + "&q=" + searchTerms + "&num=" + resultsPerPage + "&start=" + (i * resultsPerPage + 1);
               $.getJSON(getQueryResultJsonUrl, function(data) {
                   formattedJSON = formatJSON(data, formattedJSON);

               });
           }
           console.log(formattedJSON);
       });
   });

   function formatJSON(rawResponseJSON, formattedJSON) {
       if (!formattedJSON.searchTerms)
           formattedJSON.searchTerms = rawResponseJSON.queries.request[0].searchTerms;

       var rawResult = rawResponseJSON.items;
       for (var item in rawResult) {
           var formattedItem = {
               title: rawResult[item].title,
               url: rawResult[item].formattedUrl,
               description: rawResult[item].snippet
           };
           formattedJSON.results.push(formattedItem);
       }

       return formattedJSON;


   }

