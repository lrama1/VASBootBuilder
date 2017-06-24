define([ 
  'jquery',
  'underscore',
  'backbone',
  'backgrid',  
  'collections/${domainClassName}Collection',
  'globals/global',
  'backbone.global',
  #if($injectMessages)'localizedmessages',#end
  #if($templateType == "JSP")'text!templates/${domainClassName}ListTemplate'#else 'text!templates/${domainClassName}ListTemplate.htm'#end /* the request for this template actually goes thru the MainController if its JSP*/
], function($, _, Backbone, Backgrid, ${domainClassName}Collection, Global, BackboneGlobal, #if($injectMessages)messages ,#end collectionTemplate){
	
	var EditDeleteCell = Backgrid.Cell.extend({
	    template: _.template('<a href="#" id="editRow"><span class="glyphicon glyphicon-pencil"></span></a>' +
	    		'<a href="#" id="deleteRow"><span class="glyphicon glyphicon-remove"></span></a>'),
	    events: {
	      "click #deleteRow": "deleteRow",
	      "click #editRow": "editRow"
	    },	    
	    editRow : function(){
	    	var idToFetch = this.model.get("${domainClassIdAttributeName}");
			require(['views/${domainClassName}EditView', 'models/${domainClassName}Model'], 
	    		function(${domainClassName}EditView, ${domainClassName}Model){
			        var ${domainClassName.toLowerCase()}  = new ${domainClassName}Model({${domainClassIdAttributeName} : idToFetch});	
					var result = ${domainClassName.toLowerCase()}.fetch({
						success : function(){
							//render the view when ${domainClassName} is fetched successfully	
							//Use this if you want to Edit in a Modal Dialog
							Global.showView(new ${domainClassName}EditView({ el: $("#modalEditBody"), model : ${domainClassName.toLowerCase()} }));
							$('#myModal').modal('show');
						},
						error : function(){
							alert("problem");
						},
						silent : true
					});
	    		});
	    },
	    deleteRow: function (e) {
	      console.log("model id: " + this.model.get("${domainClassIdAttributeName}"));
	      e.preventDefault();
	      this.model.collection.remove(this.model);
	    },
	    render: function () {
	      #[[this.$el.html(this.template());
	      this.delegateEvents();
	      return this;
	      ]]#
	    }
	});
	
	var ${domainClassName}CollectionView = Backbone.View.extend({
		//standard backbone function called when a view is constructed
	    initialize: function(){    	
	    	if(!this.collection){
				this.collection = new ${domainClassName}Collection();
			}
	        this.render();	        
	    },
	    render: function(){
	    	console.log("Models" + JSON.stringify(this.collection.models));	    	
	    	var columns = [
		    	#foreach($key in $attrs.keySet() )
		    		#if($foreach.count == 1)
		    			{
		    				name : "${domainClassName.toLowerCase()}Selector",
		    				cell : "select-row",
		    				headerCell: "select-all"
		    			},
			    		{
		    				name : "operation",
		    				cell : EditDeleteCell
			    		},
			    		{
			    			name : "${key}",
			    			#if($injectMessages)label : messages("${key.toUpperCase()}"),#end
			    			cell : "string"
			    		}
					#else
						,{
			    			name : "${key}",
			    			#if($injectMessages)label : messages("${key.toUpperCase()}"),#end
			    			cell : "string"
			    		}
					#end
		    	#end
	    	];

	        // Initialize a new Grid instance
	        var grid = new Backgrid.Grid({
	          columns: columns,
	          collection: this.collection
	          //,footer: Backgrid.Extension.Paginator
	        });
	        //lets expose the grid
	        this.grid = grid;

	       // Initialize the paginator
			var paginator = new Backgrid.Extension.Paginator({
			  collection: this.collection
			});

	        // Render the grid and attach the root to your HTML document
			#set( $attrVar = "" )
			#foreach($key in $attrs.keySet() )
				#if($foreach.count == 1)
					#set( $attrVar = $key + ':' + "''" )
				#else
					#set( $attrVar = $attrVar + ', ' + $key + ':' + "''" )
				#end	
			#end
			
			#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
	        #[[
	        this.$el.html(_.template(collectionTemplate, ]]#  {$attrVar}  ));   
	        $("#${domainObjectName}ListContainer").html(grid.render().$el);
	        $("#${domainObjectName}ListContainer").append(paginator.render().$el);
	        //this.collection.fetch({reset : true});
	    },
	    events : {
	    	"click #edit${domainClassName}Button" : "edit${domainClassName}",
	    	"click #filterButton" : "filter",
	    	"click #addFilterFor${domainClassName}Link" : "addFilterFor${domainClassName}",
			"click #addNew${domainClassName}" : "addNew${domainClassName}",
     	    "global testEvent" : "testEvent",
     	    "global ${domainClassName}s:fetch" : "fetchMyCollection"
				
	    },
	    fetchMyCollection : function(){
	    	this.collection.state.currentPage = 1;
	    	this.collection.fetch();
	    },
	    testEvent : function(){
	    	alert("Event Fired Listener[${domainClassName}CollectionView]");
	    },
	    addFilterFor${domainClassName} : function(){
	    	$('#myFilterModal').modal('show');
	    },
	    addNew${domainClassName} : function(){
	    	var idToFetch = 'default';
			require([ 'views/${domainClassName}EditView',
						'models/${domainClassName}Model' ], function(
								${domainClassName}EditView, ${domainClassName}Model) {
				var ${domainClassName.toLowerCase()}  = new ${domainClassName}Model({${domainClassIdAttributeName} : idToFetch});
				var result = ${domainClassName.toLowerCase()}.fetch({
					success : function() {
						//render the view when SentimentAspect is fetched successfully	
						//Use this if you want to Edit in a Modal Dialog
						Global.showView(new ${domainClassName}EditView({ el: $("#modalEditBody"), model : ${domainClassName.toLowerCase()} }));
						$('#myModal').modal('show');
					},
					error : function() {
						alert("problem");
					},
					silent : true
				});
			});
	    },
	    edit${domainClassName} : function(){
	    	if(this.grid.getSelectedModels().length > 0){
				//location.hash = "${domainClassName.toLowerCase()}/" + this.grid.getSelectedModels()[0].get("${domainClassIdAttributeName}");
				var idToFetch = this.grid.getSelectedModels()[0].get("${domainClassIdAttributeName}");
				require(['views/${domainClassName}EditView', 'models/${domainClassName}Model'], 
		    		function(${domainClassName}EditView, ${domainClassName}Model){
				        var ${domainClassName.toLowerCase()}  = new ${domainClassName}Model({${domainClassIdAttributeName} : idToFetch});	
						var result = ${domainClassName.toLowerCase()}.fetch({
							success : function(){
								//render the view when ${domainClassName} is fetched successfully	
								//Use this if you want to Edit in a Modal Dialog
								Global.showView(new ${domainClassName}EditView({ el: $("#modalEditBody"), model : ${domainClassName.toLowerCase()} }));
								$('#myModal').modal('show');
							},
							error : function(){
								alert("problem");
							},
							silent : true
						});
		    		});
				
			}else{
				alert("Please select a ${domainClassName} to edit.");
			}
	    },
		filter : function(){
			#set( $attrSelectors = "" )
			#foreach($key2 in $attrs.keySet() )
				#if($foreach.count == 1)
					#set( $attrSelectors = "'" + $key2 + "' + ':' + " + 'this.$("#' + ${key2} + '").val()' )
				#else
					#set( $attrSelectors = $attrSelectors + " + ';' + '" + $key2 + "' + ':' + " + 'this.$("#' + ${key2} + '").val()' )
				#end	
			#end
			this.collection.queryParams.filter=$attrSelectors;
			alert('filtering the list');
			this.collection.fetch();
		}	    
	});

  return ${domainClassName}CollectionView;
});