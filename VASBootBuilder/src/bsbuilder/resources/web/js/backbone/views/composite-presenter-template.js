define([ 
  'jquery',
  'underscore',  
  'backbone',
  'globals/global',  
  #foreach(${key} in ${childViews.keySet()} )
  '${key}',
  #end
  'text!templates/${compositePresenterName}Template.htm'
  #if($injectMessages),'localizedmessages'#end  
], function($, _, Backbone, Global,#foreach(${key} in ${childViews.keySet()})${childViews.get(${key})}, #end presenterTemplate #if($injectMessages),messages #end){
		
	var ${compositePresenterName} = Backbone.View.extend({
		//standard backbone function called when a view is constructed
	    initialize: function(){    	
	        this.render();	        
	    },
	    render: function(){	    	
	    	this.$el.html(presenterTemplate);	 
	    	//Global.showView(new SomeView({el: $("#someSection") }));
	    	#foreach(${key} in ${childViews.keySet()} )
	    	  Global.showView(new ${childViews.get(${key})}({el: $("#${childViews.get(${key})}-presenterSection") }));
	    	#end
	    }    
	});

  return ${compositePresenterName};
});