define([
  'jquery',      
  'underscore',
  'backbone'
], function($, _, Backbone) {
  
  var ${domainClassName}Model = Backbone.Model.extend({
	  idAttribute : '${domainClassIdAttributeName}',	
	  urlRoot : '/${projectName}/${domainClassName.toLowerCase()}',	  
	  validate : function(attributes, options){
		  //sample validation. uncomment if u want to use it
		  //if(attributes.<ATTRBITE_NAME_HERE> == ''){				
		  //	return '<ATTR_NAME> cant be blank.';
		  //}		
	  },
	  defaults : {
		  #foreach($key in $attrs.keySet() )
			  #if($foreach.count == 1)			  
				  "$key" : ""
			  #else
				  ,"$key" : ""
			  #end
		  #end	  
	  },
	  parse : function(response){		  
		  #foreach($key in $attrs.keySet() )
		  	#if($attrs.get($key) == 'java.util.Date')
		  		//response.$key = new Date(response.$key);		  		
		  		var convertedDate = new Date(response.${key});
		  		response.${key} = (convertedDate.getMonth()+1) + "-" + convertedDate.getDate() +
								"-" + convertedDate.getFullYear();		  		
		  	#end	
		  #end
		  return response;
	  }
  });

  return ${domainClassName}Model;

});