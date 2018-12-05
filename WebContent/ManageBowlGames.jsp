<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<title>Bowl Pool - Update Scores</title>
</head>
<body>
	Update Bowl Game Scores<br><br>
	<s:iterator value="bowlGameList" var="bowlGame">
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
	</body>
</html>