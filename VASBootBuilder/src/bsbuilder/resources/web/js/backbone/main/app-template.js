define([           
  'jquery', 
  'underscore',
  'backbone',
  'router'], function($,_,Backbone, Router){
  var initialize = function(){
    // Pass in our Router module and call it's initialize function
    Router.initialize();
    
    $('#myTabs a').click(function (e) {
		  e.preventDefault();
		  window.location.href = $(this).attr("href");
	});
  };

  return { 
    initialize: initialize
  };
});