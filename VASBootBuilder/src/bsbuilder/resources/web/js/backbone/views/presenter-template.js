define([ 
  'jquery',
  'underscore',  
  'backbone',
  'globals/global',  
  'text!templates/${domainClassName}PresenterTemplate.htm'
  #if($injectMessages),'localizedmessages'#end  
], function($, _, Backbone, Global, presenterTemplate #if($injectMessages),messages #end){
		
	var ${domainClassName}Presenter = Backbone.View.extend({
		//standard backbone function called when a view is constructed
	    initialize: function(){    	
	        this.render();	        
	    },
	    render: function(){
	    	//Global.showView(new SomeView({el: $("#someSection") }));
	    	this.$el.html(presenterTemplate);
	    }    
	});

  return ${domainClassName}Presenter;
});