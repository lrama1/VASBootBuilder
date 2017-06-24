
define([
  'jquery',      
  'underscore',
  'backbone',
  'backgrid',
  'models/${domainClassName}Model'
], function($, _, Backbone, backgrid ,${domainClassName}Model) {
	var ${domainClassName}Collection = Backbone.PageableCollection.extend({
        model: ${domainClassName}Model,
        url : '/${projectName}/${domainClassName.toLowerCase()}s',
        state: {
            pageSize: 10
        },
        parse : function(response){
  	    	//this.state.totalRecords = response.totalRecords;
  	    	if(response.totalRecords == 0){
  	    		this.state.totalRecords = null;
  	    	}else{
  	    		this.state.totalRecords = response.totalRecords;
  	    	}
  	    	//this.state.totalPages = "2";
  	    	this.state.lastPage = response.lastPage;
  	    	console.log("Last Page: " + this.state.lastPage);
  	    	//return response.${domainClassName.substring(0,1).toLowerCase()}${domainClassName.substring(1)}s;
  	    	return response.rows;
  	    },
  	    
        mode: "server"        
    });
	return ${domainClassName}Collection;
});