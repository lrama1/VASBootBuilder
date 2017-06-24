#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
(
	function(){
		angular.module('${projectName}').controller('${domainClassName}EditController', function(#[[$scope, $routeParams, $http]]#,${domainObjectName}Service){
			console.log(#[[$routeParams]]#.id);			
			
			${domainObjectName}Service.get${domainClassName}(#[[$routeParams]]#.id)
			.success(function(value){
				console.log(value);
				#[[$scope]]#.${domainClassName} = value;
				
			});
			
			#[[$scope]]#.doSave = function(){
				alert('Saving:' + JSON.stringify(#[[$scope]]#.${domainClassName}));			
				${domainObjectName}Service.save${domainClassName}(#[[$scope]]#.${domainClassName});
			}	
			
		});
	}	
)();
