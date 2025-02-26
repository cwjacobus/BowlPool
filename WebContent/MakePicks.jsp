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
	//var cfpTeams = {1: "OREGON", 2: "UGA", 3: "BOISE", 4: "ASU", 5: "UT", 6: "PSU", 7: "ND", 8: "OSU", 9: "TENN", 10: "IU", 11: "SMU", 12: "CLEM"};
	var year = "${sessionScope.year}"; // No need if before 2025
	var cfpTeams = ${sessionScope.cfpTeamsJSON};
	
	function confirmPicks(form) {
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
		var errorMsg = "";
		if ((favsChecked + dogsChecked) != favorites.length) {
			errorMsg += "Not All Games Picked\n";
		}
		var missingCfpPicks = 0;
		var missingCfpChampTotPts = 0;
		if (document.getElementById("cfp1").value.trim().length < 4 || document.getElementById("cfp2").value.trim().length < 4 ||
			document.getElementById("cfp3").value.trim().length < 4 || document.getElementById("cfp4").value.trim().length < 4 || 
			document.getElementById("cfp5").value.trim().length < 4 || document.getElementById("cfp6").value.trim().length < 4 ||
			document.getElementById("cfp7").value.trim().length < 4 || document.getElementById("cfp8").value.trim().length < 4 ||
			document.getElementById("cfpSemi1").value.trim().length < 4 || document.getElementById("cfpSemi2").value.trim().length < 4 ||
			document.getElementById("cfpChamp").value.trim().length < 4) {
				missingCfpPicks = 1;
		}
		if (document.getElementById("cfpChampTotPts").value.trim().length == 0) {
			missingCfpChampTotPts = 1;
		}
		if (missingCfpPicks) {
			alert("Missing CFP Bracket Selections");
			return false;
		}
		if (missingCfpChampTotPts) {
			alert("Missing Championship Game Total Points");
			return false;
		}
		if (errorMsg.length > 0) {
			errorMsg = "Are you sure you want to save your picks?  The following are missing: \n" + errorMsg;
			return confirm(errorMsg);
		}
		else {
			return true;
		}
	}
	
	function addDropDown(team, round, gameIndex, ddElement) {
		var option = document.createElement('option');
		option.text = team;
		option.value = team + ':' + round + ':' + gameIndex;
		ddElement.add(option);
	}

    function initializeCFPTeams() {	
    	var round = 1;
    	if (!(year > 24)) {
    		return;
    	}
		addDropDown('', 0, '', cfp1);
		addDropDown(cfpTeams[5], round, 1, cfp1);
		addDropDown(cfpTeams[12], round, 1, cfp1);
		
		addDropDown('', 0, '', cfp2);
		addDropDown(cfpTeams[8], round, 2, cfp2);
		addDropDown(cfpTeams[9], round, 2, cfp2);
		
		addDropDown('', 0, '', cfp3);
		addDropDown(cfpTeams[6], round, 3, cfp3);
		addDropDown(cfpTeams[11], round, 3, cfp3);

		addDropDown('', 0, '', cfp4);
		addDropDown(cfpTeams[7], round, 4, cfp4);
		addDropDown(cfpTeams[10], round, 4, cfp4);

		addDropDown('', 0, '', cfp5);
		addDropDown(cfpTeams[4], round, 1, cfp5);
		
		addDropDown('', 0, '', cfp6);
		addDropDown(cfpTeams[1], round, 2, cfp6);
		
		addDropDown('', 0, '', cfp7);
		addDropDown(cfpTeams[3], round, 3, cfp7);
		
		addDropDown('', 0, '', cfp8);
		addDropDown(cfpTeams[2], round, 4, cfp8);
    }
    
    function getQtrValues(qtrGameIndex) {
    	var round = 2;
    	var topSeedOption = document.createElement('option');
    	var cfpChamp = document.getElementById("cfpChamp");
    	var cfpSemi1 = document.getElementById("cfpSemi1");
    	var cfpSemi2 = document.getElementById("cfpSemi2");
		removeAllFromDropDown(cfpSemi1);
		removeAllFromDropDown(cfpSemi2);
		removeAllFromDropDown(cfpChamp);
		var emptyOption = document.createElement('option');
		emptyOption.text = "";
		emptyOption.value = 0;
		if (qtrGameIndex == 1) {
			cfp5 = document.getElementById("cfp5");
			cfp1 = document.getElementById("cfp1");
			removeAllFromDropDown(cfp5);
			topSeedOption.text = cfpTeams[4];
			topSeedOption.value = cfpTeams[4] + ':' + round + ':' + qtrGameIndex;
			cfp5.add(emptyOption);
			cfp5.add(topSeedOption);
			addDropDown(cfp1.options[cfp1.selectedIndex].text, round, qtrGameIndex, cfp5);
		}
		else if (qtrGameIndex == 2) {
			cfp6 = document.getElementById("cfp6");
			removeAllFromDropDown(cfp6);
			topSeedOption.text = cfpTeams[1];
			topSeedOption.value = cfpTeams[1] + ':' + round + ':' + qtrGameIndex;
			cfp6.add(emptyOption);
			cfp6.add(topSeedOption);
			addDropDown(cfp2.options[cfp2.selectedIndex].text, round, qtrGameIndex, cfp6);
		}
		else if (qtrGameIndex == 3) {
			cfp7 = document.getElementById("cfp7");
			removeAllFromDropDown(cfp7);
			topSeedOption.text = cfpTeams[3];
			topSeedOption.value = cfpTeams[3] + ':' + round + ':' + qtrGameIndex;
			cfp7.add(emptyOption);
			cfp7.add(topSeedOption);
			addDropDown(cfp3.options[cfp3.selectedIndex].text, round, qtrGameIndex, cfp7);
		}
		else {
			cfp8 = document.getElementById("cfp8");
			removeAllFromDropDown(cfp8);
			topSeedOption.text = cfpTeams[2];
			topSeedOption.value = cfpTeams[2] + ':' + round + ':' + qtrGameIndex;
			cfp8.add(emptyOption);
			cfp8.add(topSeedOption);
			addDropDown(cfp4.options[cfp4.selectedIndex].text, round, qtrGameIndex, cfp8);
		}	
    }
    
    function getSemiValues(semiGameIndex) {
    	var round = 3;
    	var cfpChamp = document.getElementById("cfpChamp");
    	removeAllFromDropDown(cfpChamp);
    	var emptyOption = document.createElement('option');
		emptyOption.text = "";
		emptyOption.value = 0;
		var cfp5 = document.getElementById("cfp5");
		var cfp6 = document.getElementById("cfp6");
		var cfp7 = document.getElementById("cfp7");
		var cfp8 = document.getElementById("cfp8");
		if (semiGameIndex == 1 && cfp5.selectedIndex != 0 && cfp6.selectedIndex != 0) {
			cfpSemi1 = document.getElementById("cfpSemi1");
			removeAllFromDropDown(cfpSemi1);
			cfpSemi1.add(emptyOption);
			addDropDown(cfp5.options[cfp5.selectedIndex].text, round, semiGameIndex, cfpSemi1);
			addDropDown(cfp6.options[cfp6.selectedIndex].text, round, semiGameIndex, cfpSemi1);
		}
		else if (semiGameIndex == 2 && cfp7.selectedIndex != 0 && cfp8.selectedIndex != 0) {
			cfpSemi2 = document.getElementById("cfpSemi2");
			removeAllFromDropDown(cfpSemi2);
			cfpSemi2.add(emptyOption);
			addDropDown(cfp7.options[cfp7.selectedIndex].text, round, semiGameIndex, cfpSemi2);
			addDropDown(cfp8.options[cfp8.selectedIndex].text, round, semiGameIndex, cfpSemi2);
		}
    }
    
    function getChampValues() {
    	var round = 4;
    	var cfpChamp = document.getElementById("cfpChamp");
    	emptyOption = document.createElement('option');
		emptyOption.text = "";
		emptyOption.value = 0;
		var cfpSemi1 = document.getElementById("cfpSemi1");
		var cfpSemi2 = document.getElementById("cfpSemi2");
		if (cfpSemi1.selectedIndex != 0 && cfpSemi2.selectedIndex != 0) {
			removeAllFromDropDown(cfpChamp);
			cfpChamp.add(emptyOption);
			addDropDown(cfpSemi1.options[cfpSemi1.selectedIndex].text, round, 1, cfpChamp);
			addDropDown(cfpSemi2.options[cfpSemi2.selectedIndex].text, round, 1, cfpChamp);
		}
    }
    
    function removeAllFromDropDown(ddElement) {
		var len = ddElement.length;
		for (i=0; i < len;  i++) {
			ddElement.remove(0);
		}
	}
	</script>
</head>
<body onload="initializeCFPTeams()">
	<!--<c:out value="User: ${sessionScope.user.userName} ${sessionScope.user.admin}"/>-->
	<jsp:useBean id="now" class="java.util.Date"/>
	<c:set target='${now}' property='time' value='${now.time + 3600000}'/><!-- Add 1 hour for CT -> ET -->
	<form action="savePicks" onsubmit="makePicksButton.disabled = true; return true;">
	<table border=1 style="border-collapse: collapse; border-spacing: 0px;">
	<tr><th>Bowl</th><th>Time(EST)</th><th>Favorite</th><th>Underdog</th><th>Spread</th></tr>
	<tr><td>
	<c:forEach var="bowlGame" items="${sessionScope.bowlGamesList}">
		<c:if test="${!fn:contains(sessionScope.excludedGameList, bowlGame.gameId)}">
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
		<c:set var="gameStarted" value="false"/>
		<c:if test="${bowlGame.dateTime < now}">
			<c:set var="gameStarted" value="true" />
		</c:if>
		<c:set var="disabled" value="" />
		<c:if test="${gameStarted || sessionScope.readOnly}"> 
			<c:set var="disabled" value="disabled" />
		</c:if>
  		<tr ${winLoseClass}>
      		<td width=200 style="color: white; background: #5D7B9D;">${bowlGame.bowlName}</td>
      		<td width=130><fmt:formatDate type='both' dateStyle='short' timeStyle='short' value='${bowlGame.dateTime}'/></td>
      		<td width=200><input type="checkbox" name="favorite" value="${bowlGame.gameId}" ${favChecked} ${disabled}>${bowlGame.favorite}</td>
      		<td width=200><input type="checkbox" name="underdog" value="${bowlGame.gameId}"${dogChecked} ${disabled}>${bowlGame.underdog}</td>
      		<td width=50 align=center>
      		<c:choose>
      			<c:when test="${bowlGame.spread != null}">
      				${bowlGame.spread}
      			</c:when>
      			<c:otherwise>
      				N/L
      			</c:otherwise>
      		</c:choose>
      		</td>
      	</tr>
      	</c:if>
  	</c:forEach>
  	</td></tr></table>
  	<br>
  	<c:if test="${sessionScope.year > 24}">
  	CF Playoff Bracket
  	<table cellspacing=5 cellpadding=5>
		<tr><th>Round 1</th><th>Quarters</th><th>Semis</th><th>Championship</th><th>Champ Pts</th></tr>
		<tr>
			<td width=75> <select name="cfp1" id="cfp1" onchange="getQtrValues(1)" style="width: 75px;"></select></td>
      		<td width=75><select name="cfp5" id="cfp5" onchange="getSemiValues(1)" style="width: 75px;"></select></td>
			<td></td>
      		<td></td>
      		<td></td>
		</tr>
		<tr>
			<td width=75> <select name="cfp2" id="cfp2" onchange="getQtrValues(2)" style="width: 75px;"></select></td>
			<td width=75><select name="cfp6" id="cfp6" onchange="getSemiValues(1)" style="width: 75px;"></select></td>
			<td width=75><select name="cfpSemi1" id="cfpSemi1" onchange="getChampValues()" style="width: 75px;"></select></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td width=75> <select name="cfp3" id="cfp3" onchange="getQtrValues(3)" style="width: 75px;"></select></td>
      		<td width=75><select name="cfp7" id="cfp7" onchange="getSemiValues(2)" style="width: 75px;"></select></td>
      		<td width=75><select name="cfpSemi2" id="cfpSemi2" onchange="getChampValues()" style="width: 75px;"></select></td>
      		<td width=75><select name="cfpChamp" id="cfpChamp" style="width: 75px;"></select></td>
      		<td width=75><input type="number" name="cfpChampTotPts" id="cfpChampTotPts" min="0" max="175" size=2/></td>
		</tr>
		<tr>
			<td width=75> <select name="cfp4" id="cfp4" onchange="getQtrValues(4)" style="width: 75px;"></select></td>
      		<td width=75><select name="cfp8" id="cfp8" onchange="getSemiValues(2)" style="width: 75px;"></select></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
	</table>
	</c:if>
	<c:if test="${!sessionScope.readOnly}">
  		<input type="submit" name="makePicksButton" value="Make Picks" onclick="return confirmPicks(this)"/>
  	</c:if>
  	</form>
  	
  	<br><br>
  	<c:set var="cfpPicksMap" value="${sessionScope.cfpPicksMap}"/>
  	<c:choose>
  	<c:when test="${fn:length(cfpPicksMap[sessionScope.user.userId]) > 0}">
  		My CFP Bracket Picks:<br>
  		<table border=1>
  			<tr><th colspan=4>Round 1</th><th colspan=4>Quarters</th><th colspan=2>Semi</th><th align=left>Championship</th><th>Pts</th></tr>
  			<tr>
  			<c:forEach var="cfpPick" items="${cfpPicksMap[sessionScope.user.userId]}">
  				<c:set var="totPts" value="${cfpPick.totalPoints}"/>
  				<c:set var="winLoseClass" value="class='win'"/>
  				<c:if test="${!cfpGamesMap[cfpPick.cfpGameId].completed}">
  					<c:set var="winLoseClass" value=""/>
  				</c:if>
  				<c:if test="${(cfpGamesMap[cfpPick.cfpGameId].completed && 
  						((cfpGamesMap[cfpPick.cfpGameId].home == cfpPick.winner && cfpGamesMap[cfpPick.cfpGameId].homeScore < cfpGamesMap[cfpPick.cfpGameId].visScore) ||
  						 (cfpGamesMap[cfpPick.cfpGameId].visitor == cfpPick.winner && cfpGamesMap[cfpPick.cfpGameId].visScore < cfpGamesMap[cfpPick.cfpGameId].homeScore) ||
  						 (cfpGamesMap[cfpPick.cfpGameId].home != cfpPick.winner && cfpGamesMap[cfpPick.cfpGameId].visitor != cfpPick.winner))) ||
  						 (!cfpGamesMap[cfpPick.cfpGameId].completed && fn:contains(sessionScope.eliminatedCFPTeamsList, cfpPick.winner))}">
  					<c:set var="winLoseClass" value="class='lose'"/>
  				</c:if>
  				<td align=center ${winLoseClass}>${cfpPick.winner}</td>
  			</c:forEach>
  			<td>${totPts}</td>
  			</tr>
  		</table>
  	</c:when>
  	<c:otherwise>
  		No picks made
  	</c:otherwise>
  	</c:choose>
  	
  	<br><br>
  	<c:if test="${sessionScope.readOnly || sessionScope.user.admin}">
  		All CFP Bracket Picks<br>
  		<table border=1>
  		<tr><th>User</th><th colspan=4>Round 1</th><th colspan=4>Quarters</th><th colspan=2>Semi</th><th align=left>Championship</th><th>Pts</th></tr>
  		<c:forEach var="cfpPicks" items="${cfpPicksMap}">
  			<tr>
  			<td>${usersMap[cfpPicks.key].userName}</td>
  			<c:forEach var="cfpPick" items="${cfpPicks.value}">
  				<c:set var="totPts" value="${cfpPick.totalPoints}"/>
  				<c:set var="winLoseClass" value="class='win'"/>
  				<c:if test="${!cfpGamesMap[cfpPick.cfpGameId].completed}">
  					<c:set var="winLoseClass" value=""/>
  				</c:if>
  				<c:if test="${(cfpGamesMap[cfpPick.cfpGameId].completed && 
  						((cfpGamesMap[cfpPick.cfpGameId].home == cfpPick.winner && cfpGamesMap[cfpPick.cfpGameId].homeScore < cfpGamesMap[cfpPick.cfpGameId].visScore) ||
  						 (cfpGamesMap[cfpPick.cfpGameId].visitor == cfpPick.winner && cfpGamesMap[cfpPick.cfpGameId].visScore < cfpGamesMap[cfpPick.cfpGameId].homeScore) ||
  						 (cfpGamesMap[cfpPick.cfpGameId].home != cfpPick.winner && cfpGamesMap[cfpPick.cfpGameId].visitor != cfpPick.winner))) ||
  						 (!cfpGamesMap[cfpPick.cfpGameId].completed && fn:contains(sessionScope.eliminatedCFPTeamsList, cfpPick.winner))}">
  					<c:set var="winLoseClass" value="class='lose'"/>
  				</c:if>
  				<td align=center ${winLoseClass}>${cfpPick.winner}</td>
  			</c:forEach>
  			<td>${totPts}</td>
  			</tr>
  		</c:forEach>
  		</table>
  	</c:if>
	</body>
</html>