(
		function(){
			angular.module('${projectName}').controller('HomeController',function($scope, $location){
				$scope.isActive = function (viewLocation) { 
			        return viewLocation === $location.path();
			    };
			});
		}
)();