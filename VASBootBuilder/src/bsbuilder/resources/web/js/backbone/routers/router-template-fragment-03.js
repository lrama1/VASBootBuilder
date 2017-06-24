		//FROM a Fragment
		//handler for getting a ${domainClassName} record
//	    app_router.on('route:get${domainClassName}', function (idToFetch) {
//	        // Note the variable in the route definition being passed in here
//	    	require(['views/${domainClassName}EditView', 'models/${domainClassName}Model'], 
//	    		function(${domainClassName}EditView, ${domainClassName}Model){
//			        var ${domainClassName.toLowerCase()}  = new ${domainClassName}Model({${domainClassIdAttributeName} : idToFetch});	
//					var result = ${domainClassName.toLowerCase()}.fetch({
//						success : function(){
//							//render the view when ${domainClassName} is fetched successfully	
//							//Use this if you want to Edit in the same page
//							app_router.showView(new ${domainClassName}EditView({ el: $("#bodyContainer"), model : ${domainClassName.toLowerCase()} }));
//						},
//						error : function(){
//							alert("problem");
//						},
//						silent : true
//					});
//	    		});
//	    });
//		app_router.on('route:show${domainClassName}Presenter', function() {
//			console.log("showing ${domainClassName}Presenter.");
//			require([ 		'presenters/${domainClassName}Presenter' ], function(
//					${domainClassName}Presenter) {
//				Global.showView(new ${domainClassName}Presenter({
//					el : $("#bodyContainer")
//				}));
//
//			});
//		});


	    app_router.on('route:get${domainClassName}List', function () {
	    	console.log("getting the list of ${domainClassName}s.");
	    	require(['collections/${domainClassName}Collection','views/${domainClassName}CollectionView'], 
	    	    function(${domainClassName}Collection, ${domainClassName}CollectionView){
	    			var collectionToFetch = new ${domainClassName}Collection();
	    			collectionToFetch.fetch({
	    				success : function(collectionData){
	    					Global.showView(new ${domainClassName}CollectionView({ el: $("#bodyContainer"), collection : collectionData}));
	    				},
	    				error : function(){
	    					alert("Failed to obtain data for ${domainClassName}Collection()");
	    				}
	    			});
	    	        
	    	    });
	    });
	    