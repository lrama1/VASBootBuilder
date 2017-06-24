define([ 
  'jquery',
  'underscore',
  'backbone', 'models/${domainClassName}Model',
  #if($templateType == "JSP")'text!templates/${domainClassName}EditTemplate'#else 'text!templates/${domainClassName}EditTemplate.htm'#end, /* the request for this template actually goes thru the MainController*/
  'bootstrap-datepicker','backbone.global','bootstrap-wizard'
], function($, _, Backbone, ${domainClassName}Model, editTemplate, datePicker, BackboneGlobal,BootstrapWizard){
	
	var ${domainClassName}EditView = Backbone.View.extend({
		defaults : {model : {}},
		//standard backbone function called when a view is constructed
	    initialize: function(){    
	    	#set( $dropDownCounter = 0 )
	    	#set( $promiseExpr = "" )
	    	#set( $promiseResultExpr = "" )
	    	#set( $promiseAssignExpr = "" )
	    	var view = this;
	        #foreach($key in $attrs.keySet() )
	        	#if ($attrs.get($key) == "java.util.Date")
	        		$('#${key}').datepicker({
	    	    		format: 'mm-dd-yyyy',
	    	    		setTime : 'true'
	    	    	});
	        	#end
	        	
	        	#if ($fieldTypes.get($key) == "DropDown")	        		
	        		var ${key}s = $.getJSON( "${key.toLowerCase()}s");
	        		#if($dropDownCounter == 0)
	        			#set($promiseExpr = ${key} + "s")
	        			#set($promiseResultExpr = "result" + $dropDownCounter)	        			
	        		#else
	        			#set($promiseExpr = $promiseExpr + ", " + ${key} + "s")
	        			#set($promiseResultExpr = $promiseResultExpr + ", result" + $dropDownCounter)
	        		#end	
	        		#set( $promiseAssignExpr = $promiseAssignExpr + "data." + ${key} + "s = result" + $dropDownCounter + "[0];" )
	        		#set( $dropDownCounter = $dropDownCounter + 1 )
	        	#end	
	        #end
	        
	        if(!this.model){
				this.model = new ${domainClassName}Model();
			}
	        		
	        $.when(${promiseExpr}).done(function($promiseResultExpr) {
	        	var data = view.model.toJSON();
	        	${promiseAssignExpr}
				view.render(view.$el, data);	        	
	        });
	        
	        this.model.on("invalid", function(model, error) {
	        	  alert("Error: " + error);
	        });
	    },
	    render: function(myEl, data){    	
//			$.get("resources/templates/EditTemplate.htm", function(templateText){			
//				 myEl.html( _.template(templateText, modelToRender.toJSON()) );
//			});
			myEl.html(_.template(editTemplate, data));
	    },
	    events: {
	        "change input": "change",   //binding change of any input field to the change function below
	        "click #save${domainClassName}": "save${domainClassName}",  //binding the saveButton of template using id attr as selector  
	     	"click #saveNew${domainClassName}": "saveNew${domainClassName}"  //binding the saveNewButton of template using id attr as selector
	    },
	    save${domainClassName} : function(){
	    	this.model.save(null,{
	    		success : function(model, response, options){
	    			#set( $attributeNameIterator =  $attrs.keySet().iterator())	    			
	    			#set( $firstAttr = $attributeNameIterator.next().toString())
	    			#set( $secondAttr = $attributeNameIterator.next().toString())
					$("#operationMessage").html(model.get("${secondAttr}") + " was saved.");
	    			Backbone.trigger("testEvent");
				},
				error : function(model, response, options){
					alert("Error: " + response.statusText);
				}
	    		#set( $dateFieldCount = 0 )
	    		#foreach($key in $attrs.keySet() )
	    			#if ($attrs.get($key) == "java.util.Date")
	    				,	
                        ${key} : Date.parse(this.model.get("${key}"))
                        #set( $dateFieldCount = $dateFieldCount + 1 )
	    			#end	
	    		#end
	    	});
	    },
	    saveNew${domainClassName} : function(){
	    	this.model.set("${domainClassIdAttributeName}", null);
	    	this.model.save(null,{
	    		success : function(model, response, options){
	    			#set( $attributeNameIterator =  $attrs.keySet().iterator())	    			
	    			#set( $firstAttr = $attributeNameIterator.next().toString())
	    			#set( $secondAttr = $attributeNameIterator.next().toString())
					$("#operationMessage").html(model.get("${secondAttr}") + " was saved.");
	    			Backbone.trigger("testEvent");
				},
				error : function(model, response, options){
					alert("Error: " + response.statusText);
				}
	    		#set( $dateFieldCount = 0 )
	    		#foreach($key in $attrs.keySet() )
	    			#if ($attrs.get($key) == "java.util.Date")
	    				,	
                        ${key} : Date.parse(this.model.get("${key}"))
                        #set( $dateFieldCount = $dateFieldCount + 1 )
	    			#end	
	    		#end
	    	});
	    },
	    change : function(evt){  //we are relying that the id of the template elements are the same as the model attrs
	    	var target = evt.target;        	        
	        var payLoad = {};
			payLoad[target.id] = target.value;
			//this.model.set(payLoad, {silent : false});
			this.model.set(payLoad, {});
	    }
	    
	});

  return ${domainClassName}EditView;
});