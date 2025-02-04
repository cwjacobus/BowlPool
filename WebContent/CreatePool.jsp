<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Create Bowl Pool</title>
</head>
<body>
	<h2>Create Bowl Pool</h2>
   	<form action="createPool">
   		<table>
      	<tr><td>League</td><td>
		<select name="poolName">
      	     <option value="Jacobus">Jacobus</option>
      	     <option value="Sculley">Sculley</option>
		</select></td></tr>
		<tr><td>Year</td><td><input type="number" name="year" min=14 max=2075 style="width: 5em"/></td></tr>
		<tr><td>Bowl Game Pts</td><td><input type="number" name="ptsBowlGame" value="1" min=0 max=1000 style="width: 3em"/></td></tr>
		<tr><td>R1 CFP Pts</td><td><input type="number" name="ptsRound1CFPGame" value="2" min=0 max=1000 style="width: 3em"/></td></tr>
		<tr><td>Qtr CFP Pts</td><td><input type="number" name="ptsQtrCFPGame" value="3" min=0 max=1000 style="width: 3em"/></td></tr>
		<tr><td>Semi CFP Pts</td><td><input type="number" name="ptsSemiCFPGame" value="5" min=0 max=1000 style="width: 3em"/></td></tr>
		<tr><td>Champ CFP Pts</td><td><input type="number" name="ptsChampCFPGame" value="10" min=0 max=1000 style="width: 3em"/></td></tr>
		<tr><td></td><td><input type="checkbox" name="usePointSpreads">Use Points Spreads for Bowl Games</td></tr>
		<tr><td></td><td><input type="checkbox" name="copyUsers" checked>Copy Users From Previous Year</td></tr>
      	<tr><td><input type="submit" value="Create Pool"/></td></tr>
      	</table>
  	 </form>
	</body>
</html>