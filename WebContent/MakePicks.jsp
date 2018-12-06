<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
<title>Bowl Pool - Make Picks</title>
</head>
<body>
	<form action="savePicks">
	<table border=1 cellspacing=0 cellpadding=0>
	<tr><th>Bowl</th><th>Time</th><th>Favorite</th><th>Underdog</th><th>Spread</th></tr>
	<tr><td>
	<s:iterator value="bowlGameList" var="bowlGame">
		<c:set var="favChecked" value = ""/>
		<c:set var="dogChecked" value = ""/>
		<c:forEach var="pick" items="${userPicks}">
 			<c:if test="${pick.gameId == bowlGame.gameId && pick.favorite}">
    			<c:set var="favChecked" value="checked" />
  			</c:if>
  			<c:if test="${pick.gameId == bowlGame.gameId && !pick.favorite}">
    			<c:set var="dogChecked" value="checked" />
  			</c:if>
		</c:forEach>
  		<tr>
      		<td width=200 style="color: white; background: #5D7B9D;"><s:property value="#bowlGame.bowlName"/></td>
      		<td width=130><fmt:formatDate type='both' dateStyle='short' timeStyle='short' value='${bowlGame.dateTime}'/></td>
      		<c:choose>
      		<c:when test="${bowlGame.bowlName != 'Championship'}">
      			<td width=200><input type="checkbox" name="favorite" value="<s:property value="#bowlGame.gameId"/>" ${favChecked}><s:property value="#bowlGame.favorite"/></td>
      			<!--  <input type="hidden" name="favorite" value=""/> -->
      		</c:when>
      		<c:otherwise>
      			<td><input type="text" name="champGame" value="${champPick.winner}" size=24/></td>
      			<td><input type="number" name="champTotPts" value="${champPick.totalPoints}" min="0" max="175" size=2/></td>
      			<input type="hidden" name="champGameId" value="<s:property value="#bowlGame.gameId"/>"/>
      		</c:otherwise>
      		</c:choose>
      		<c:choose>
      		<c:when test="${bowlGame.bowlName != 'Championship'}">
      			<td width=200><input type="checkbox" name="underdog" value="<s:property value="#bowlGame.gameId"/>"${dogChecked}><s:property value="#bowlGame.underdog"/></td>
      		</c:when>
      		<c:otherwise>
      			<td></td>
      		</c:otherwise>
      		</c:choose>
      		<c:if test="${bowlGame.bowlName != 'Championship'}">
      			<td width=50 align=center>
      			<c:choose>
      				<c:when test="${bowlGame.spread != ''}">
      					<s:property value='#bowlGame.Spread'/>
      				</c:when>
      				<c:otherwise>
      					N/L
      				</c:otherwise>
      			</c:choose>
      			</td>
      		</c:if>
      	</tr>
  	</s:iterator>
  	</td></tr></table>
  	<br>
  	<input type="submit" value="Make Picks"/>
  	</form>
	</body>
</html>