#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
(
	function(){
	  var ${domainObjectName}Service = function(#[[$http, $routeParams]]#){
		  
		  //define service methods
		  this.get${domainClassName}s = function(pageNumber, pageSize){			  
			  return #[[$http]]#.get('/${projectName}/${domainClassName.toLowerCase()}s',{params : {
					page : pageNumber,
					per_page : pageSize
				}});
		  };
		  
		  this.get${domainClassName} = function(id){
			return #[[$http]]#.get('/${projectName}/${domainClassName.toLowerCase()}/' + #[[$routeParams]]#.id);
		  };
		  
		  this.save${domainClassName} = function(${domainObjectName}ToSave){				
			  #[[$http]]#.post('/${projectName}/${domainClassName.toLowerCase()}', ${domainObjectName}ToSave);
		  };
		  
	  };
	  
	  angular.module('${projectName}').service('${domainObjectName}Service', ${domainObjectName}Service);
	}
());