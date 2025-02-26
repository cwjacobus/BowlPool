<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
	<title>Bowl Pool</title>
</head>
<body>
	<h2>20${sessionScope.pool.year} Standings</h2>
	<!--<c:out value="User: ${sessionScope.user.userName} ${sessionScope.user.admin}"/>-->
	<table style="padding: 0; border-spacing: 0;">
	<c:set var="color" value="white" />
	<c:set var="index" value="0" />
	<tr style="color: white; background: black; font-weight: bold;"><th></th><th align="left">Name</th><th align="left">Pts</th><th align="left">Elim</th></tr>
  	<s:iterator value="standings" var="standingsLine">
  		<c:if test ="${standingsLine.value.userName != 'dummy'}">
  			<c:choose>
  				<c:when test = "${index % 2 == 0}">
  					<c:set var="color" value="tan" />
  				</c:when>
  				<c:otherwise>
  					<c:set var="color" value="#5D7B9D" />
  				</c:otherwise>
  			</c:choose>
  			<tr style="color: white; background: ${color}; font-weight: bold;">
    			<td width=25><s:property value="#standingsLine.value.rank"/>.</td>
    			<td width=150><s:property value='#standingsLine.value.userName'/></td>
    			<td width=35 align="center"><s:property value="#standingsLine.value.correct"/></td>
	    		<td width=20 align="center">${standingsLine.value.eliminatedBy >= 5 ? 'X' : standingsLine.value.eliminatedBy}</td>
    		</tr>
    		<c:set var="index" value="${index + 1}"/>
    	</c:if>
	</s:iterator> 
	</table>
	${numOfCompletedGames} completed games ${numOfRemainingGames} remaining
  	<br>
  	<br>
  	<a href="/BowlPoolWeb/makePicks">
  	<c:choose>
  	<c:when test = "${!sessionScope.readOnly}">
  		Make Picks
  	</c:when>
  	<c:otherwise>
  		View Picks
  	</c:otherwise>
  	</c:choose>
  	</a>
  	<br><br>
  	<c:if test = "${allowAdmin}">
  		<a href="/BowlPoolWeb/manageBowlGames">Manage Bowl Games</a><br>
  		<a href="/BowlPoolWeb/CreateCFPTeams.jsp">Create CFP Games</a>
  		<br>
  		<h3>Import Data</h3>
  		<form action="import">
  			<input type="file" name="inputFileName" accept=".xls" /><br>
  			<input type="checkbox" name="cfTeamsCB" value="CFTeams"> CF Teams from WS<br>
  			<input type="checkbox" name="usersCB" value="Users"> Users<br>
			<input type="checkbox" name="gamesCB" value="Games"> Games
			(<input type="checkbox" name="fromWS" value="FromWS"> from WS)<br>
			<input type="checkbox" name="picksCB" value="Picks"> Picks<br>
			<input type="submit" value="Import">
  		</form>
  		<h3>Cancel Game</h3>
  		<form action="cancelGame">
  			<select name="bowlGame">	
      			<c:forEach var="bowlGame" items="${sessionScope.bowlGamesList}">
      				<option value="${bowlGame.gameId}">${bowlGame.bowlName}</option>
      			</c:forEach>
      		</select><br>
  			<input type="submit" value="Cancel Game">
  		</form>
  		<h3>Exclude Game</h3>
  		<form action="excludeGame">
  			<select name="bowlGame">	
      			<c:forEach var="bowlGame" items="${sessionScope.bowlGamesList}">
      				<option value="${bowlGame.gameId}">${bowlGame.bowlName}</option>
      			</c:forEach>
      		</select><br>
  			<input type="submit" value="Exclude Game">
  		</form>
  		<br>
  	</c:if>
  	<!--<s:form action="import" method="post" enctype="multipart/form-data" >
        <s:file name="inputFile2" accept=".xls" />
        <s:submit />
    </s:form>-->
	</body>
</html>