<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<title>Bowl Pool - Make Picks</title>
</head>
<body>
	<form action="savePicks">
	<table>
	<tr><th>Bowl</th><th>Favorite</th><th>Underdog</th><th>Spread</th></tr>
	<tr><td>
	<s:iterator value="bowlGameList" var="bowlGame">
  		<tr>
      		<td width=100 style="color: white; background: #5D7B9D;"><s:property value="#bowlGame.bowlName"/></td>
      		<c:choose>
      		<c:when test="${bowlGame.bowlName != 'Championship'}">
      			<td width=150><input type="checkbox" name="favorite" value="<s:property value="#bowlGame.gameId"/>"><s:property value="#bowlGame.favorite"/></td>
      			<!--  <input type="hidden" name="favorite" value=""/> -->
      		</c:when>
      		<c:otherwise>
      			<td><input type="text" name="favorite" value="<s:property value='#bowlGame.favorite'/>" size=10/></td>
      		</c:otherwise>
      		</c:choose>
      		<c:choose>
      		<c:when test="${bowlGame.bowlName != 'Championship'}">
      			<td width=150><input type="checkbox" name="underdog" value="<s:property value="#bowlGame.gameId"/>"><s:property value="#bowlGame.underdog"/></td>
      			<!--  <input type="hidden" name="underdog" value=""/>-->
      		</c:when>
      		<c:otherwise>
      			<td></td>
      		</c:otherwise>
      		</c:choose>
      		<c:if test="${bowlGame.bowlName != 'Championship'}">
      			<td width=50 align=center><s:property value='#bowlGame.Spread'/></td>
      		</c:if>
      	</tr>
      	<!--  <input type="hidden" name="gameId" value="<s:property value="#bowlGame.gameId"/>"/>-->
      	<!--  <input type="hidden" name="champGame" value="${bowlGame.bowlName == 'Championship'}"/>-->
  	</s:iterator>
  	</td></tr></table>
  	<input type="submit" value="Make Picks"/>
  	</form>
	</body>
</html>