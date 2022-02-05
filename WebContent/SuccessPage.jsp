<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	<head>
		<style>
			.successText {
				color: green;
				font-weight: bold;
			}
		</style>
		<title>Error</title>
	</head>
	<body>
		<h1>Success</h1>
		<div class="successText">${successMsg}<br><br>
		return to <a href="/BowlPoolWeb">Login</a></div>
	</body>
</html>