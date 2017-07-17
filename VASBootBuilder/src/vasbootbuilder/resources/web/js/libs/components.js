

${className} = Backbone.Model.extend({	
	//defaults : {firstName : "", lastName : ""},
	idAttribute : '${domainClassIdAttributeName}'
	,	
	urlRoot : '/${projectName}/${className.toLowerCase()}'
	,
	validate : function(attributes){
		
	}	
});


${className}EditView = Backbone.View.extend({
	defaults : {model : {}},
    initialize: function(){    	
        this.render(this.$el, this.model);
    },
    render: function(myEl, modelToRender){    	
		$.get("resources/templates/EditTemplate.htm", function(templateText){			
			 myEl.html( _.template(templateText, modelToRender.toJSON()) );
		});

    },
    events: {
        "change input": "change",   //binding change of any input field to the change function below
        "click #save${className}": "save${className}",  //binding the saveButton of template using id attr as selector  
     	"click #saveNew${className}": "saveNew${className}"  //binding the saveNewButton of template using id attr as selector
    },
    save${className} : function(){
    	console.log(JSON.stringify(this.model));
    	this.model.save();
    },
    saveNew${className} : function(){
    	this.model.set("${domainClassIdAttributeName}", null);
    	this.model.save();
    },
    change : function(){
    	var target = event.target;        
        this.model.set(target.id, target.value);
    }
    
});