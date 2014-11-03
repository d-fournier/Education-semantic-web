   $(document).ready(function() {

       $("#searchTerms").autocomplete({
              appendTo: ".input-group" ,
               delay: 0, 
              autoFocus: true,        
              source: function(request, response){
                 $.ajax({
                    url: "http://lookup.dbpedia.org/api/search/PrefixSearch",
                    dataType: "json",
                    data: {
                      QueryClass:"",
                      MaxHits: 5,
                      QueryString: request.term
                    },
                  success: function( data ) {
                    var suggestions = [];
                    for (i in data.results)
                      suggestions.push(data.results[i].label);
                    response( suggestions );
                  }
        });


              },
              minLength: 2,
       });


       $('#searchForm').submit(function(event) {

           event.preventDefault();
           $("#google-results-div").html("");
           $("#processed-results-div").html("");
           //We do need to find away to remove these from here
           var apiKey2 = "AIzaSyDZjrXVfbGRsUIZpOpB_I9BkIkIhQWoJ_Y";
           var apiKey = "AIzaSyBOeLl5E9RSrKA0QpWFuF3F91n4rmcPz8o";
           var apiKey3= "AIzaSyC1ykvZWHr4EHY29MXjPoMo2_3g34liVEQ";
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
               var getQueryResultJsonUrl = "https://www.googleapis.com/customsearch/v1?key=" + apiKey3 + "&cx=" + cx + "&q=" + searchTerms + "&num=" + resultsPerPage + "&start=" + (i * resultsPerPage + 1);

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
                   renderResults(formattedResults,"google-results-div");
                   renderResults(formattedResults,"processed-results-div");

               });

               //TODO : Ask server to send processed results. Display processed results.
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
               url: rawResult[item].formattedUrl.replace(/.*?:\/\//g, ""),
               description: rawResult[item].snippet
           };
           formattedJSON.results.push(formattedItem);
       }

       return formattedJSON;

   }

   function result(r) {
       //Class definition for a processed result

       var arr = [
           '<div class="webResult">',
           '<h2><a href="', r.url, '">', r.title, '</a></h2>',
           '<p>', r.description, '</p>',
           '<a href="', r.url, '">', r.url, '</a>',
           //Could add the relevant concepts discovered when processing.
           '</div>'
       ];
       this.toString = function() {
           return arr.join('');
       }
   }

   function renderResults(resultSet, divID) {
       var pageContainer = $('<div>', {
           class: 'pageContainer'
       });
       var resultsDiv = $("#"+divID);
       for (var i = 0; i < resultSet.results.length; i++) {
           // Creating a new result object and firing its toString method:
           pageContainer.append(new result(resultSet.results[i]) + '');
       }
       pageContainer.append('<div class="clear"></div>')
           .hide().appendTo(resultsDiv)
           .fadeIn('slow');
   }




