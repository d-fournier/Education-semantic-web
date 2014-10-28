   $(document).ready(function() {
       $('#searchForm').submit(function(event) {

           event.preventDefault();
           //We do need to find away to remove these from here
           var apiKey2 = "AIzaSyDZjrXVfbGRsUIZpOpB_I9BkIkIhQWoJ_Y";
           var apiKey = "AIzaSyBOeLl5E9RSrKA0QpWFuF3F91n4rmcPz8o"
           var cx = '016813502462276054558:2encdk-x_ka';
           var searchTerms = $('#searchTerms').val();

           //for testing
           var nbResults = 20;
           var resultsPerPage = 10;
           var nbPages = nbResults / resultsPerPage;

           var formattedJSON = {
               //TODO : find a way to get the search engine dynamically
               searchEngine: "google.fr",
               results: []
           }

           //because Google's API wouldn't have it any other way
           var getRequests = [];
           for (var i = 0; i < nbPages; i++) {
               var getQueryResultJsonUrl = "https://www.googleapis.com/customsearch/v1?key=" + apiKey2 + "&cx=" + cx + "&q=" + searchTerms + "&num=" + resultsPerPage + "&start=" + (i * resultsPerPage + 1);

               getRequests.
               push($.getJSON(getQueryResultJsonUrl, function(data) {
                   formattedJSON = formatJSON(data, formattedJSON);
               }));
           }

           //wait for the get request to be properly completed
           $.when.apply($, getRequests).then(function() {

               var postRequest = $.ajax({
                   contentType: 'application/json; charset=utf-8',
                   type: "POST",
                   dataType: 'json',
                   data: JSON.stringify(formattedJSON),
                   url: "/formatResults",

               });

               postRequest.success(function(formattedResults) {
                   console.log(formattedResults);
               });


           });


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
