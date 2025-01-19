<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	<head>
		<title>Select CFP Teams and Create Games</title>
	</head>
	<body>
		<h2>Select CFP Teams and Create Games</h2>
		
		<br>
		- Use CFTeam.ShortName<br>
		- 11 CFPGame will be created<br>
		Round 1: 1. 12v5 2. 8v9 3. 6v11 4. 7v10<br>
		Round 2: 1. 4vR1G1 2. 1vR1G2 3. 3vR1G3 4. 2vR1G4<br>
		Round 3: 1. R2G1vR2G2 2. R2G3vR2G4<br>
		Round 4: 1. R3G1vR3G2 (Championship game)<br><br>
		<form action="createCFPGames">
      		<table>
      			<tr><th>Seed</th><th>Team</th></tr>
      			<c:forEach var = "i" begin = "1" end = "12">
         			<tr><td><c:out value = "${i}"/>.</td><td><input type="text" name="seed" size=6/></td><td>
      			</c:forEach>
      		</table>
      		<input type="submit" name="createCFPGamesButton" value="Create CFP Games"/>
      	</form>
	</body>
</html>