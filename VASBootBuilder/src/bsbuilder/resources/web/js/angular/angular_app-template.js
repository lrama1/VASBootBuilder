#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
(
	function(){
		angular.module('${projectName}', ['ngRoute','angularUtils.directives.dirPagination']).config(function($routeProvider){
			$routeProvider
					.when('/${domainClassName.toLowerCase()}s', 
						{controller : '${domainClassName}ListController',
						templateUrl : '/${projectName}/resources/js/angular_templates/${domainClassName}List.html'})					
					.when('/${domainClassName.toLowerCase()}/:id', {
						controller : '${domainClassName}EditController',
							templateUrl : '/${projectName}/resources/js/angular_templates/${domainClassName}Edit.html'
					});
		});
	}
)();