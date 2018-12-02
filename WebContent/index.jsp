<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
   pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
   <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Bowl Pool</title>
</head>
<body>
	<h1>Bowl Pool</h1>
   	<form action="getStandings">
      	<table>
      	<tr><td>User</td><td><input type="text" name="name"/></td></tr>
		<tr><td>Year</td><td><select name="year">
             <option value="14">2014</option>
             <option value="15">2015</option>
             <option value="16">2016</option>
             <option value="17">2017</option>
             <option value="18">2018</option>
		</select>
		<tr><td>Pool</td>
      	<td><select name="poolId">
      	     <option value="0"></option>
      	     <option value="1">Sculley 2014</option>
             <option value="2">Sculley 2015</option>
			 <option value="3">Jacobus 2016</option>
             <option value="4">Sculley 2017</option>
             <option value="5">Sculley 2018</option>
			 <option value="6">Jacobus 2018</option>
		</select></td></tr>
      	<tr><td><input type="submit" value="Login"/></td></tr>
		</table>
  	 </form>
</body>
</html>