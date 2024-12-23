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
	var cfpTeams = ${sessionScope.cFPTeamsJSON};
	
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
		if (!(year > 24)) {
			var champTeam = document.getElementById("champGame").value.trim();
			var champTotPts = document.getElementById("champTotPts").value.trim();
			if (champTeam.length == 0) {
				errorMsg += "No Championship Game Winner\n"
			}
			if (champTotPts.length == 0) {
				errorMsg += "No Championship Game Total Points\n"
			}
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
	
	function addDropDown(team, seed, gameCode, ddElement) {
		var option = document.createElement('option');
		option.text = team;
		option.value = seed + ':' + team + ':' + gameCode;
		ddElement.add(option);
	}

    function initializeCFPTeams() {	
    	if (!(year > 24)) {
    		return;
    	}
		addDropDown('', 0, '', cfp1);
		addDropDown(cfpTeams[5], 5, 'cfp1', cfp1);
		addDropDown(cfpTeams[12], 12, 'cfp1', cfp1);
		
		addDropDown('', 0, '', cfp2);
		addDropDown(cfpTeams[8], 8, 'cfp2', cfp2);
		addDropDown(cfpTeams[9], 9, 'cfp2', cfp2);
		
		addDropDown('', 0, '', cfp3);
		addDropDown(cfpTeams[6], 6, 'cfp3', cfp3);
		addDropDown(cfpTeams[11], 11, 'cfp3', cfp3);

		addDropDown('', 0, '', cfp4);
		addDropDown(cfpTeams[7], 7, 'cfp4', cfp4);
		addDropDown(cfpTeams[10], 10, 'cfp4', cfp4);

		addDropDown('', 0, '', cfp5);
		addDropDown(cfpTeams[4], 4, 'cfp5', cfp5);
		
		addDropDown('', 0, '', cfp6);
		addDropDown(cfpTeams[1], 1, 'cfp6', cfp6);
		
		addDropDown('', 0, '', cfp7);
		addDropDown(cfpTeams[3], 3, 'cfp7', cfp7);
		
		addDropDown('', 0, '', cfp8);
		addDropDown(cfpTeams[2], 2, 'cfp8', cfp8);
    }
    
    function getQtrValues(qtrGameIndex) {
    	topSeedOption = document.createElement('option');
    	newOption = document.createElement('option');
    	cfpChamp = document.getElementById("cfpChamp");
    	cfpSemi1 = document.getElementById("cfpSemi1");
		cfpSemi2 = document.getElementById("cfpSemi2");
		topSeedOption.text = cfpTeams[qtrGameIndex];
		removeAllFromDropDown(cfpSemi1);
		removeAllFromDropDown(cfpSemi2);
		removeAllFromDropDown(cfpChamp);
		emptyOption = document.createElement('option');
		emptyOption.text = "";
		emptyOption.value = 0;
		if (qtrGameIndex == 4) {
			cfp5 = document.getElementById("cfp5");
			cfp1 = document.getElementById("cfp1");
			removeAllFromDropDown(cfp5);
			topSeedOption.value = qtrGameIndex + ':' + cfpTeams[qtrGameIndex] + ':cfp5';
			cfp5.add(emptyOption);
			cfp5.add(topSeedOption);
			seeding = cfp1.options[cfp1.selectedIndex].value.split(":")[0];
			newOption.text = cfp1.options[cfp1.selectedIndex].text;
			newOption.value = seeding + ":" + newOption.text + ":" + 'cfp5';
			cfp5.add(newOption);
		}
		else if (qtrGameIndex == 1) {
			cfp6 = document.getElementById("cfp6");
			removeAllFromDropDown(cfp6);
			topSeedOption.value = qtrGameIndex + ':' + cfpTeams[qtrGameIndex] + ':cfp6';
			cfp6.add(emptyOption);
			cfp6.add(topSeedOption);
			seeding = cfp2.options[cfp2.selectedIndex].value.split(":")[0];
			newOption.text = cfp2.options[cfp2.selectedIndex].text;
			newOption.value = seeding + ":" + newOption.text + ":" + 'cfp6';
			cfp6.add(newOption);
		}
		else if (qtrGameIndex == 3) {
			cfp7 = document.getElementById("cfp7");
			removeAllFromDropDown(cfp7);
			topSeedOption.value = qtrGameIndex + ':' + cfpTeams[qtrGameIndex] + ':cfp7';
			cfp7.add(emptyOption);
			cfp7.add(topSeedOption);
			seeding = cfp3.options[cfp3.selectedIndex].value.split(":")[0];
			newOption.text = cfp3.options[cfp3.selectedIndex].text;
			newOption.value = seeding + ":" + newOption.text + ":" + 'cfp7';
			cfp7.add(newOption);
		}
		else {
			cfp8 = document.getElementById("cfp8");
			removeAllFromDropDown(cfp8);
			topSeedOption.value = qtrGameIndex + ':' + cfpTeams[qtrGameIndex] + ':cfp8';
			cfp8.add(emptyOption);
			cfp8.add(topSeedOption);
			seeding = cfp4.options[cfp4.selectedIndex].value.split(":")[0];
			newOption.text = cfp4.options[cfp4.selectedIndex].text;
			newOption.value = seeding + ":" + newOption.text + ":" + 'cfp8';
			cfp8.add(newOption);
		}	
    }
    
    function getSemiValues(semiGameIndex) {
    	newOption1 = document.createElement('option');
    	newOption2 = document.createElement('option');
    	cfpChamp = document.getElementById("cfpChamp");
    	removeAllFromDropDown(cfpChamp);
    	emptyOption = document.createElement('option');
		emptyOption.text = "";
		emptyOption.value = 0;
		cfp5 = document.getElementById("cfp5");
		cfp6 = document.getElementById("cfp6");
		cfp7 = document.getElementById("cfp7");
		cfp8 = document.getElementById("cfp8");
		if (semiGameIndex == 1 && cfp5.selectedIndex != 0 && cfp6.selectedIndex != 0) {
			cfpSemi1 = document.getElementById("cfpSemi1");
			removeAllFromDropDown(cfpSemi1);
			cfpSemi1.add(emptyOption);
			seeding1 = cfp5.options[cfp5.selectedIndex].value.split(":")[0];
			newOption1.text = cfp5.options[cfp5.selectedIndex].text;
			newOption1.value = seeding1 + ":" + newOption1.text + ":" + 'cfpSemi1';
			cfpSemi1.add(newOption1);
			seeding2 = cfp6.options[cfp6.selectedIndex].value.split(":")[0];
			newOption2.text = cfp6.options[cfp6.selectedIndex].text;
			newOption2.value = seeding2 + ":" + newOption2.text + ":" + 'cfpSemi1';
			cfpSemi1.add(newOption2);
		}
		else if (semiGameIndex == 2 && cfp7.selectedIndex != 0 && cfp8.selectedIndex != 0) {
			cfpSemi2 = document.getElementById("cfpSemi2");
			removeAllFromDropDown(cfpSemi2);
			cfpSemi2.add(emptyOption);
			seeding1 = cfp7.options[cfp7.selectedIndex].value.split(":")[0];
			newOption1.text = cfp7.options[cfp7.selectedIndex].text;
			newOption1.value = seeding1 + ":" + newOption1.text + ":" + 'cfpSemi2';
			cfpSemi2.add(newOption1);
			seeding2 = cfp8.options[cfp8.selectedIndex].value.split(":")[0];
			newOption2.text = cfp8.options[cfp8.selectedIndex].text;
			newOption2.value = seeding2 + ":" + newOption2.text + ":" + 'cfpSemi2';
			cfpSemi2.add(newOption2);
		}
    }
    
    function getChampValues() {
    	newOption1 = document.createElement('option');
    	newOption2 = document.createElement('option');
    	cfpChamp = document.getElementById("cfpChamp");
    	emptyOption = document.createElement('option');
		emptyOption.text = "";
		emptyOption.value = 0;
		cfpSemi1 = document.getElementById("cfpSemi1");
		cfpSemi2 = document.getElementById("cfpSemi2");
		if (cfpSemi1.selectedIndex != 0 && cfpSemi2.selectedIndex != 0) {
			removeAllFromDropDown(cfpChamp);
			cfpChamp.add(emptyOption);
			seeding1 = cfpSemi1.options[cfpSemi1.selectedIndex].value.split(":")[0];
			newOption1.text = cfpSemi1.options[cfpSemi1.selectedIndex].text;
			newOption1.value = seeding1 + ":" + newOption1.text + ":" + 'cfpChamp';
			cfpChamp.add(newOption1);
			seeding2 = cfpSemi2.options[cfpSemi2.selectedIndex].value.split(":")[0];
			newOption2.text = cfpSemi2.options[cfpSemi2.selectedIndex].text;
			newOption2.value = seeding2 + ":" + newOption2.text + ":" + 'cfpChamp';
			cfpChamp.add(newOption2);
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
      				<c:when test="${bowlGame.spread != null}">
      					${bowlGame.spread}
      				</c:when>
      				<c:otherwise>
      					N/L
      				</c:otherwise>
      			</c:choose>
      			</td>
      		</c:if>
      	</tr>
      	</c:if>
  	</c:forEach>
  	</td></tr></table>
  	<br>
  	<c:if test="${sessionScope.year > 24}">
  	CF Playoff Bracket
  	<table cellspacing=10 cellpadding=10>
		<tr><th>Round 1</th><th>Quarters</th><th>Semis</th><th>Championship</th></tr>
		<tr>
		<td width=75> <select name="cfp1" id="cfp1" onchange="getQtrValues(4)" style="width: 75px;">
		</select></td>
      	<td width=75><select name="cfp5" id="cfp5" onchange="getSemiValues(1)" style="width: 75px;">
		</select></td>
		<td></td>
      	<td></td>
		</tr>
		<tr>
		<td width=75> <select name="cfp2" id="cfp2" onchange="getQtrValues(1)" style="width: 75px;">
		</select></td>
		<td width=75><select name="cfp6" id="cfp6" onchange="getSemiValues(1)" style="width: 75px;">
		</select></td>
		<td width=75><select name="cfpSemi1" id="cfpSemi1" onchange="getChampValues()" style="width: 75px;">
		</select></td>
		<td></td>
		</tr>
		<tr>
		<td width=75> <select name="cfp3" id="cfp3" onchange="getQtrValues(3)" style="width: 75px;">
		</select></td>
      	<td width=75><select name="cfp7" id="cfp7" onchange="getSemiValues(2)" style="width: 75px;">
		</select></td>
      	<td width=75><select name="cfpSemi2" id="cfpSemi2" onchange="getChampValues()" style="width: 75px;">
		</select></td>
      	<td width=75><select name="cfpChamp" id="cfpChamp" style="width: 75px;">
		</select></td>
		</tr>
		<tr>
		<td width=75> <select name="cfp4" id="cfp4" onchange="getQtrValues(2)" style="width: 75px;">
		</select></td>
      	<td width=75><select name="cfp8" id="cfp8" onchange="getSemiValues(2)" style="width: 75px;">
		</select></td>
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
	</body>
</html>