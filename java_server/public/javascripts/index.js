   $(document).ready(function() {

       var postGoogleResultsURL = $(".radio-class:checked").val(); // Determined according to the checked radio button
       $("#rdfCompare,#simpleCompare").change(function() {
           // Listening for a click on one of the radio buttons.
           //Update the post URL accordingly
           postGoogleResultsURL = "/" + this.value;
       });


       $(".header").hide(); 
       $(".spinner").hide();

       $("#searchTerms").autocomplete({
           appendTo: ".input-group",
           delay: 0,
           autoFocus: true,
           source: function(request, response) {
               $.ajax({
                   url: "http://lookup.dbpedia.org/api/search/PrefixSearch",
                   dataType: "json",
                   data: {
                       QueryClass: "",
                       MaxHits: 10,
                       QueryString: request.term
                   },
                   success: function(data) {
                       var suggestions = [];
                       for (i in data.results)
                           suggestions.push(data.results[i].label);
                       response(suggestions);
                   }
               });

               $("#searchTerms").autocomplete({
                   select: function(event, ui) {
                       $("#submitButton").prop("disabled", false).css("cursor", "pointer");


                   }
               });


           },
           minLength: 2,
       });


       $('#searchForm').submit(function(event) {

           event.preventDefault();
           $("#google-results-div").html("");
           $("#processed-results-div").html("");

           //can't do anything now

           
             $("#searchTerms").prop("disabled",true);
             $("#submitButton").prop("disabled",true).css("cursor","not-allowed");
             $(".spinner").show();
             
              $(".header").hide();
              
          
           //We do need to find away to remove these from here
           var apiKey2 = "AIzaSyDZjrXVfbGRsUIZpOpB_I9BkIkIhQWoJ_Y";
           var apiKey = "AIzaSyBOeLl5E9RSrKA0QpWFuF3F91n4rmcPz8o";
           var apiKey3 = "AIzaSyC1ykvZWHr4EHY29MXjPoMo2_3g34liVEQ";

           var apiKey4 = "AIzaSyCbU-dPDGLG3lBZC6q8M81mwJJLAMptCXE";

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
           }

           //because Google's API wouldn't have it any other way
           var getRequests = [];
           for (var i = 0; i < nbPages; i++) {


               var getQueryResultJsonUrl = "https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=" + cx + "&q=" + searchTerms + "&num=" + resultsPerPage + "&start=" + (i * resultsPerPage + 1);


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
                   url: postGoogleResultsURL,

               });

               postRequest.success(function(formattedResults) {
                   console.log(formattedResults);
                   renderResults(formattedJSON, "google-results-div");
                   renderResults(formattedResults, "processed-results-div");
                   $("#searchTerms").prop("disabled", false);
                   $(".spinner").hide();

               });

               postRequest.error(function(jqXHR, textStatus, errorThrown) {
                   $("#searchTerms").prop("disabled", false);
                   $(".spinner").hide();
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
               url: rawResult[item].link.replace(/.*?:\/\//g, ""),
               description: rawResult[item].snippet
           };
           formattedJSON.results.push(formattedItem);
       }

       return formattedJSON;

   }

   function result(r, processedResults) {
       //Class definition for a processed result
       var arr;
  //r.idSimilarWebsite = [1, 3, 2, 13, 15]; //for test purpose
  if (r.id == null) { // Google Results
      arr = [
          '<div class="webResult">',
          '<h2><a href="http://', r.url, '">', r.title, '</a></h2>',
          '<p>', r.description, '</p>',
          '<a href="http://', r.url, '">', r.url, '</a>',
          //Could add the relevant concepts discovered when processing.
          '<p>&zwnj</p>',
          '<p>&zwnj</p>',
          '</div>'
      ]

  } else { // Processed results


      if (!r.img) {
          if (r.idSimilarWebsite == undefined) {
              arr = [
                  '<div class="webResult">',
                  '<h2><a name=', r.id, ' href="http://', r.url, '">', r.title, '</a></h2>',
                  '<p>', r.description, '</p>',
                  '<a href="http://', r.url, '">', r.url, '</a>',
                  '<p> Rank of this result in the Google Search :', r.id + 1, '</p>',
                  '</div>'
              ];
          } else {
              arr = [
                  '<div class="webResult">',
                  '<h2><a name=', r.id, ' href="http://', r.url, '">', r.title, '</a></h2>',
                  '<p>', r.description, '</p>',
                  '<a href="http://', r.url, '">', r.url, '</a>',
                  '<p> Rank of this result in the Google Search :', r.id + 1, '</p>',
                  '<p> Related results : '
              ];
              for (var i = 0; i < r.idSimilarWebsite.length; i++) {
                  arr.push('<a href="#' + r.idSimilarWebsite[i] + '">' + (r.idSimilarWebsite[i] + 1) + ', </a>');

              }

              arr.push('</p></div>');
              console.log(arr);

          }
      } else {
          if (r.idSimilarWebsite == undefined) {
              arr = [
                  '<div class="webResult">',
                  '<h2 data-container="body" data-toggle="popover" data-placement="bottom" att="', r.img, '" class="pop-image"><a name=', r.id, ' href="http://', r.url, '">', r.title, '</a></h2>',
                  '<p>', r.description, '</p>',
                  '<a href="http://', r.url, '">', r.url, '</a>',
                  '<p> Rank of this result in the Google Search :', r.id + 1, '</p>',
                  '</div>'
              ];
          } else {
              arr = [
                  '<div class="webResult">',
                  '<h2 data-container="body" data-toggle="popover" data-placement="bottom" att="', r.img, '" class="pop-image"><a name=', r.id, ' href="http://', r.url, '">', r.title, '</a></h2>',
                  '<p>', r.description, '</p>',
                  '<a href="http://', r.url, '">', r.url, '</a>',
                  '<p> Rank of this result in the Google Search :', r.id + 1, '</p>',
                  '<p> Related results : '
              ];
              for (var i = 0; i < r.idSimilarWebsite.length; i++) {
                  arr.push('<a href="#' + r.idSimilarWebsite[i] + '">' + (r.idSimilarWebsite[i] + 1) + ', </a>');

              }

              arr.push('</p></div>');
              console.log(arr);

          }
      }

}
       


       this.toString = function() {
            console.log('join');
           console.log(arr.join(''));
           return arr.join('');
       }
   }

   function renderResults(resultSet, divID) {

      $(".header").show();
      
       var pageContainer = $('<div>', {
           class: 'pageContainer'
       });
       var resultsDiv = $("#" + divID);
       for (var i = 0; i < resultSet.results.length; i++) {
           // Creating a new result object and firing its toString method:
           pageContainer.append(new result(resultSet.results[i], resultSet.results) + '');
       }
       pageContainer.append('<div class="clear"></div>')
           .hide().appendTo(resultsDiv)
           .fadeIn('slow');
           $('.pop-image').popover({
  html: true,
  trigger: 'hover',
  content: function () {
    return '<img class="pop-img" src="'+$(this).attr("att") + '" />';
  }
});

   }

