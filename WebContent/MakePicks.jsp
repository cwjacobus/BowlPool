<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
	<title>Bowl Pool - Make Picks</title>
	<style type="text/css">
	.win {
		color: green;
		font-weight: bold;
	}
	.lose {
		color: red;
		font-weight: bold;
	}
	</style>
	<script type="text/javascript">
		function confirmPicks(form) {
			var champTeam = document.getElementById("champGame").value.trim();
			var champTotPts = document.getElementById("champTotPts").value.trim();
			var favorites = document.getElementsByName("favorite");
			var underdogs = document.getElementsByName("underdog");
			var favsChecked = 0;
			var dogsChecked = 0;
			for (var i = 0; i < favorites.length; i++) {
				if (favorites[i].checked == true) {
					favsChecked++;
				}
				if (underdogs[i].checked == true) {
					dogsChecked++;
				}	
			}
			//var numOfBowlGames = "${sessionScope.bowlGamesList}";
			var errorMsg = "";
			if (champTeam.length == 0) {
				errorMsg += "No Championship Game Winner\n"
			}
			if (champTotPts.length == 0) {
				errorMsg += "No Championship Game Total Points\n"
			}
			if ((favsChecked + dogsChecked) != favorites.length) {
				errorMsg += "Not All Games Picked"
			}
			if (errorMsg.length > 0) {
				errorMsg = "Are you sure you want to save your picks?  The following are missing: \n" + errorMsg;
				return confirm(errorMsg);
			}
			else {
				return true;
			}
		}
	</script>
</head>
<body>
	<form action="savePicks">
	<table border=1 cellspacing=0 cellpadding=0>
	<tr><th>Bowl</th><th>Time(EST)</th><th>Favorite</th><th>Underdog</th><th>Spread</th></tr>
	<tr><td>
	<c:forEach var="bowlGame" items="${sessionScope.bowlGamesList}">
		<c:set var="favChecked" value = ""/>
		<c:set var="dogChecked" value = ""/>
		<c:set var="winLoseClass" value=""/>
		<c:set var="spread" value="0"/>
		<c:if test="${sessionScope.pool.usePointSpreads}">
 			<c:set var="spread" value="${bowlGame.spread}"/>
 		</c:if>
		<c:forEach var="pick" items="${userPicks}">
 			<c:if test="${pick.gameId == bowlGame.gameId && pick.favorite}">
 				<c:if test="${bowlGame.completed && (bowlGame.favoriteScore > (bowlGame.underDogScore + spread))}">
 					<c:set var="winLoseClass" value="class='win'" />
 				</c:if>
 				<c:if test="${bowlGame.completed && (bowlGame.favoriteScore <= (bowlGame.underDogScore + spread))}">
 					<c:set var="winLoseClass" value="class='lose'" />
 				</c:if>
    			<c:set var="favChecked" value="checked" />
  			</c:if>
  			<c:if test="${pick.gameId == bowlGame.gameId && !pick.favorite}">
  				<c:if test="${bowlGame.completed && (bowlGame.favoriteScore >= (bowlGame.underDogScore + spread))}">
 					<c:set var="winLoseClass" value="class='lose'" />
 				</c:if>
 				<c:if test="${bowlGame.completed && (bowlGame.favoriteScore < (bowlGame.underDogScore + spread))}">
 					<c:set var="winLoseClass" value="class='win'" />
 				</c:if>
    			<c:set var="dogChecked" value="checked" />
  			</c:if>
		</c:forEach>
		<c:set var="disabled" value="" />
		<c:if test="${sessionScope.readOnly}">
			<c:set var="disabled" value="disabled" />
		</c:if>
  		<tr ${winLoseClass}>
      		<td width=200 style="color: white; background: #5D7B9D;">${bowlGame.bowlName}</td>
      		<td width=130><fmt:formatDate type='both' dateStyle='short' timeStyle='short' value='${bowlGame.dateTime}'/></td>
      		<c:choose>
      		<c:when test="${bowlGame.bowlName != 'Championship'}">
      			<td width=200><input type="checkbox" name="favorite" value="${bowlGame.gameId}" ${favChecked} ${disabled}>${bowlGame.favorite}</td>
      		</c:when>
      		<c:otherwise>
      			<c:choose>
      			<c:when test = "${fn:length(sessionScope.potentialChampionsList) == 0}">
      				<td><input type="text" name="champGame" id="champGame" value="${champPick.winner}" size=24/></td>
      			</c:when>
      			<c:otherwise>
      				<td>
      					<select name="champGame" id="champGame">	
      						<c:forEach var="champTeam" items="${sessionScope.potentialChampionsList}">
      							<c:set var="selected" value="" />
      							<c:if test="${champTeam == champPick.winner}">
      								<c:set var="selected" value="selected" />
      							</c:if>
      							<option value="${champTeam}"${selected}>${champTeam}</option>
      						</c:forEach>
      					</select>
      				</td>
      			</c:otherwise>
      			</c:choose>
      			<td><input type="number" name="champTotPts" id="champTotPts" value="${champPick.totalPoints}" min="0" max="175" size=2/></td>
      			<input type="hidden" name="champGameId" value="${bowlGame.gameId}"/>
      		</c:otherwise>
      		</c:choose>
      		<c:choose>
      		<c:when test="${bowlGame.bowlName != 'Championship'}">
      			<td width=200><input type="checkbox" name="underdog" value="${bowlGame.gameId}"${dogChecked} ${disabled}>${bowlGame.underdog}</td>
      		</c:when>
      		<c:otherwise>
      			<td></td>
      		</c:otherwise>
      		</c:choose>
      		<c:if test="${bowlGame.bowlName != 'Championship'}">
      			<td width=50 align=center>
      			<c:choose>
      				<c:when test="${bowlGame.spread != ''}">
      					${bowlGame.spread}
      				</c:when>
      				<c:otherwise>
      					N/L
      				</c:otherwise>
      			</c:choose>
      			</td>
      		</c:if>
      	</tr>
  	</c:forEach>
  	</td></tr></table>
  	<br>
  	<c:if test="${!sessionScope.readOnly}">
  		<input type="submit" value="Make Picks" onclick="return confirmPicks(this)"/>
  	</c:if>
  	</form>
	</body>
</html>