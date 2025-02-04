<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
<title>Bowl Pool</title>
</head>
<body>
	<h1>Bowl Pool</h1>
   	<form action="getStandings">
      	<table>
      		<tr><td>User Name</td><td><input type="text" name="userName" size="10"/></td></tr>
			<tr><td>League</td><td>
		<select name="poolName">
      	     <option value="Jacobus">Jacobus</option>
      	     <option value="Sculley">Sculley</option>
		</select></td></tr>
		<tr><td>Year</td><td><input type="number" name="year" min=14 max=2075 size="6"/></td></tr>
      	<tr><td><input type="submit" value="Login"/></td></tr>
		</table>
  	 </form>
</body>
</html>