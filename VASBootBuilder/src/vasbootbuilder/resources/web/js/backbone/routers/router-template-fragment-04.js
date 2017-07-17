		
	    #if($compositePresenterName)
    	app_router.on('route:show${compositePresenterName}', function() {
				console.log("showing ${compositePresenterName}.");
				require([ 		'presenters/${compositePresenterName}' ], function(
						${compositePresenterName}) {
					Global.showView(new ${compositePresenterName}({
						el : $("#bodyContainer")
					}));
	
				});
		});	
	    #end	
	    
	    #if($wizardPresenterName)
	    	app_router.on('route:show${wizardPresenterName}', function() {
					console.log("showing ${wizardPresenterName}.");
					require([ 		'presenters/${wizardPresenterName}' ], function(
							${wizardPresenterName}) {
						Global.showView(new ${wizardPresenterName}({
							el : $("#bodyContainer")
						}));
		
					});
			});	
		#end
	    