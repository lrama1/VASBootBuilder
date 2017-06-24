define([
  'jquery'
], function($) {
  
	if (typeof console  != "object")
		console = {log : function(message){}};
	if (typeof allViews  != "object")
		allViews = {};
	
	
	var Global = ({	  
	  showView: function (view) {
	        //destroy current view -- we need this to address the 'ghost'/'zombie' view problem
			//of inherent to apps using Backbone
		  var viewContainer = view.el.id;
		  if(viewContainer in allViews){
			  allViews[viewContainer].unbind();
			  allViews[viewContainer].undelegateEvents();
			  allViews[viewContainer] = {};
		  }
		  
		  allViews[viewContainer] = view;
		  allViews[viewContainer].delegateEvents();
		  return allViews[viewContainer];
	  }	 
  });

  return Global;

});