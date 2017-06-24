define([ 
  'jquery',
  'underscore',  
  'backbone',
  'globals/global',  
  #foreach(${key} in ${childViews.keySet()} )
  '${key}',
  #end
  'text!templates/${wizardPresenterName}Template.htm'
  #if($injectMessages),'localizedmessages'#end  
], function($, _, Backbone, Global,#foreach(${key} in ${childViews.keySet()})${childViews.get(${key})}, #end presenterTemplate #if($injectMessages),messages #end){
		
	var ${wizardPresenterName} = Backbone.View.extend({
		//standard backbone function called when a view is constructed
	    initialize: function(){    	
	        this.render();	        
	    },
	    render: function(){	    	
	    	this.$el.html(presenterTemplate);	 
	    	//Global.showView(new SomeView({el: $("#someSection") }));
	    	#foreach(${key} in ${childViews.keySet()} )
	    	  Global.showView(new ${childViews.get(${key})}({el: $("#${wizardPresenterName}tab${foreach.count}") }));
	    	#end
	    	$('#rootwizard').bootstrapWizard({
	    		'onNext': function(tab, navigation, index) {
	    			//alert('next tab');
	    		}
	    	});
	    }    
	});

  return ${wizardPresenterName};
});