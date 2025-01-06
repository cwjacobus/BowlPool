<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<title>Bowl Pool - Update Scores</title>
</head>
<body>
	Update Bowl Game Scores<br><br>
	<s:iterator value="bowlGamesList" var="bowlGame">
  		<form action="updateScore">
  		<table><tr>
      		<td width=200 style="color: white; background: #5D7B9D;"><s:property value="#bowlGame.bowlName"/></td>
      		<c:choose>
      		<c:when test="${bowlGame.bowlName != 'Championship'}">
      			<td width=200><s:property value="#bowlGame.favorite"/></td>
      			<input type="hidden" name="favorite" value=""/>
      		</c:when>
      		<c:otherwise>
      			<td><input type="text" name="favorite" value="<s:property value='#bowlGame.favorite'/>" size=10/></td>
      		</c:otherwise>
      		</c:choose>
      		<td><input type="text" name="favoriteScore" value="<s:property value="#bowlGame.favoriteScore"/>" size=2/></td>
      		<c:choose>
      		<c:when test="${bowlGame.bowlName != 'Championship'}">
      			<td width=200><s:property value="#bowlGame.underdog"/></td>
      			<input type="hidden" name="underdog" value=""/>
      		</c:when>
      		<c:otherwise>
      			<td><input type="text" name="underdog" value="<s:property value='#bowlGame.underdog'/>" size=10/></td>
      		</c:otherwise>
      		</c:choose>
      		<td><input type="text" name="underDogScore" value="<s:property value="#bowlGame.underDogScore"/>" size=2/></td>
      		<td><input type="submit" value="Set Score"/></td>
      	</tr></table>
      	<input type="hidden" name="gameId" value="<s:property value="#bowlGame.gameId"/>"/>
      	<input type="hidden" name="year" value="${year}"/>
      	<input type="hidden" name="champGame" value="${bowlGame.bowlName == 'Championship'}"/>
  		</form>
  	</s:iterator>
  	<s:iterator value="cfpGamesList" var="cfpGame">
  		<form action="updateCfpScore">
  		<table><tr>
      		<td width=200 style="color: white; background: #5D7B9D;"><s:property value="#cfpGame.description"/></td>
      		<c:choose>
      		<c:when test="${cfpGame.round == 1}">
      			<td width=100><s:property value="#cfpGame.visitor"/><input type="hidden" name="visitor" value=""/></td>
      		</c:when>
      		<c:otherwise>
      			<td width=100>
      			<select name="visitor" id="visitor">	
      				<option value=''></option>
      				<c:forEach var="cfpTeam" items="${sessionScope.cfpTeamsList}">
      					<c:set var="selected" value="" />
      					<c:if test="${cfpTeam == cfpGame.visitor}">
      						<c:set var="selected" value="selected" />
      					</c:if>
      					<option value="${cfpTeam}"${selected}>${cfpTeam}</option>
      				</c:forEach>
      			</select>
      			</td>
      		</c:otherwise>
      		</c:choose>
      		<td><input type="text" name="visScore" value="<s:property value="#cfpGame.visScore"/>" size=2/></td>
      		<c:choose>
      		<c:when test="${cfpGame.round == 1}">
      			<td width=100><s:property value="#cfpGame.home"/><input type="hidden" name="home" value=""/></td>
      		</c:when>
      		<c:otherwise>
      			<td width=100>
      			<select name="home" id="home">	
      				<option value=''></option>
      				<c:forEach var="cfpTeam" items="${sessionScope.cfpTeamsList}">
      					<c:set var="selected" value="" />
      					<c:if test="${cfpTeam == cfpGame.home}">
      						<c:set var="selected" value="selected" />
      					</c:if>
      					<option value="${cfpTeam}"${selected}>${cfpTeam}</option>
      				</c:forEach>
      			</select>
      			</td>
      		</c:otherwise>
      		</c:choose>
      		<td><input type="text" name="homeScore" value="<s:property value="#cfpGame.homeScore"/>" size=2/></td>
      		<td><input type="submit" value="Set Score"/></td>
      	</tr></table>
      	<input type="hidden" name="cfpGameId" value="<s:property value="#cfpGame.cfpGameId"/>"/>
      	<input type="hidden" name="year" value="${year}"/>
  		</form>
  	</s:iterator>
	</body>
</html>