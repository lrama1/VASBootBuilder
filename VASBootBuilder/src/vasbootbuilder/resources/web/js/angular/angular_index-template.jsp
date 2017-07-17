<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html ng-app="${projectName}">
<head>
	<title>${projectName}</title>
	
	<script src="http://code.jquery.com/jquery-1.11.3.js"></script>
	<link rel="stylesheet" href="/${projectName}/resources/css/libs/bootstrap.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

	<script src="/${projectName}/resources/js/libs/bootstrap.min.js"></script>
	<script type="text/javascript" src="/${projectName}/resources/js/libs/angular.min.js"></script>
	<script type="text/javascript" src="/${projectName}/resources/js/libs/angular-route.min.js"></script>
	<script src="/${projectName}/resources/js/libs/dirPagination.js"></script>
	
	<script src="/${projectName}/resources/js/angular_app.js"></script>
	<script src="/${projectName}/resources/js/angular_controllers/HomeController.js"></script>
	<script src="/${projectName}/resources/js/angular_controllers/${domainClassName}ListController.js"></script>
	<script src="/${projectName}/resources/js/angular_controllers/${domainClassName}EditController.js"></script>
	<script src="/${projectName}/resources/js/angular_services/${domainClassName}Service.js"></script>
	
</head>
<body>
	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
					data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
					<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">Brand</a>
			</div>

			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1" ng-controller="HomeController">
				<ul class="nav navbar-nav">
					<li ng-class="{ active: isActive('/${domainClassName.toLowerCase()}s')}"><a href="#${domainClassName.toLowerCase()}s">${domainClassName}<span class="sr-only">(current)</span></a></li>				
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button"
						aria-haspopup="true" aria-expanded="false">Dropdown <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="#">Action</a></li>
							<li><a href="#">Another action</a></li>
							<li><a href="#">Something else here</a></li>
							<li role="separator" class="divider"></li>
							<li><a href="#">Separated link</a></li>
							<li role="separator" class="divider"></li>
							<li><a href="#">One more separated link</a></li>
						</ul></li>
				</ul>
				<form class="navbar-form navbar-left" role="search">
					<div class="form-group">
						<input type="text" class="form-control" placeholder="Search">
					</div>
					<button type="submit" class="btn btn-default">Submit</button>
				</form>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="#">LinkRight</a></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button"
						aria-haspopup="true" aria-expanded="false">Dropdown <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="#">Action</a></li>
							<li><a href="#">Another action</a></li>
							<li><a href="#">Something else here</a></li>
							<li role="separator" class="divider"></li>
							<li><a href="#">Separated link</a></li>
						</ul></li>
				</ul>
			</div>
			<!-- /.navbar-collapse -->
		</div>
		<!-- /.container-fluid -->
	</nav>

	<div class="container" ng-view>		
		
	</div>
</body>
</html>