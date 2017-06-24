#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
(
	function(){
		angular.module('${projectName}').controller('${domainClassName}ListController', function(#[[$scope]]#, ${domainObjectName}Service) {
			
			$scope.pagination = {
			        current: 1
			 };
			
			$scope.pageChanged = function(newPage) {
		        getResultsPage(newPage);
		    };
		    
		    $scope.pageSize = 5;
		    
		    getResultsPage(1);
		    
		    function getResultsPage(pageNumber) {
				${domainObjectName}Service.get${domainClassName}s(pageNumber, #[[$scope]]#.pageSize)
						.success(function(value){
					console.log(value);
					#[[$scope]]#.${domainClassName}s = value.rows;
					#[[$scope]]#.totalRows = value.totalRecords;
					});				
			}

	});	
	})();
