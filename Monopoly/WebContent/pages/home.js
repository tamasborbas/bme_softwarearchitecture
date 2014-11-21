function openGame(id) {
	alert(id);
	var jsonObject = [];
	jsonObject.push({
		"gameid": id
	});
	//$.ajax({
	//	type: "POST",
	//	data: JSON.stringify(jsonObject),
	//	url: "/Monopoly/rest/userapi/openGame"
	//});
}
function startGame(id) {
	if (confirm('Do you want to start the game? Somebody does not accept the invitation yet.')) {
		alert(id);
		var jsonObject = [];
		jsonObject.push({
			"gameid": id
		});
		//$.ajax({
		//	type: "POST",
		//	data: JSON.stringify(jsonObject),
		//	url: "/Monopoly/rest/userapi/startGame"
		//});
	}
}
function refuseInvitation(id) {
	alert("refuse "+id);
	var jsonObject = [];
	jsonObject.push({
		"gameid": id
	});
	//$.ajax({
	//	type: "POST",
	//	data: JSON.stringify(jsonObject),
	//	url: "/Monopoly/rest/userapi/refuseInvitation"
	//});
}
function acceptInvitation(id) {
	alert("accept "+id);
	var jsonObject = [];
	jsonObject.push({
		"gameid": id
	});
	//$.ajax({
	//	type: "POST",
	//	data: JSON.stringify(jsonObject),
	//	url: "/Monopoly/rest/userapi/acceptInvitation"
	//});
}

var actGameData = '{"activeGames":[{"id":3,"actualPlayer":"admin","players":[{"name":"anna"}],"name":"TEST2"}]}';
var manageGameData = '{"myGames":[{"id":2,"notAcceptedYetPlayers":[],"refusedPlayers":[],"name":"TEST3","acceptedPlayers":[{"placeId":130,"status":"accepted","playerId":3,"name":"admin"},{"placeId":130,"status":"accepted","playerId":4,"name":"anna"}]}]}';
var invGameData = '{"nayGames":[{"id":1,"notAcceptedYetPlayers":[{"placeId":130,"status":"notAcceptedYet","playerId":1,"name":"admin"}],"refusedPlayers":[],"name":"TEST2","acceptedPlayers":[{"placeId":130,"status":"accepted","playerId":2,"name":"anna"}]},{"id":2,"notAcceptedYetPlayers":[{"placeId":130,"status":"notAcceptedYet","playerId":3,"name":"admin"}],"refusedPlayers":[],"name":"TEST3","acceptedPlayers":[{"placeId":130,"status":"accepted","playerId":4,"name":"anna"}]}]}';

function deleteAllSubNode(basesec) {
	while(basesec.firstChild) {
		basesec.removeChild(basesec.firstChild);
	}
}

function getActiveGames() {
	//$.getJSON("/Monopoly/rest/userapi/getActiveGames", function (data, status) {
	var basesec = document.getElementById("actgamesec");
	deleteAllSubNode(basesec);
	
	var jsonobject = JSON.parse(actGameData);
	for (var gi in jsonobject.activeGames) {
		var game = jsonobject.activeGames[gi];
		var sec = document.createElement('section');
		sec.className = "actgame";
		sec.id = game.id;
		sec.onclick = openGame.bind(this, game.id);
		basesec.appendChild(sec);

		var aghead = document.createElement('h3');
		aghead.textContent = game.name;
		sec.appendChild(aghead);

		var aghr = document.createElement('hr');
		aghr.className = "act";
		sec.appendChild(aghr);

		var spanuact = document.createElement('section');
		spanuact.className = "user user-act";
		spanuact.textContent = game.actualPlayer + "|";
		sec.appendChild(spanuact);

		for (var ui in game.players) {
			var suser = game.players[ui];
			var spanu = document.createElement('section');
			spanu.className = "user";
			if (ui != game.players.length - 1) {
				spanu.textContent = suser.name + "|";
			} else {
				spanu.textContent = suser.name;
			}
			sec.appendChild(spanu);
		}
	}
	//});
}


function createPlayersBlock(sec, game) {
	var divac = document.createElement('section');
	divac.className = "accepts";
	sec.appendChild(divac);

	for (var ui in game.acceptedPlayers) {
		var suser = game.acceptedPlayers[ui];
		var spanu = document.createElement('section');
		spanu.className = "accept";
		if (ui != game.acceptedPlayers.length - 1) {
			spanu.textContent = suser.name + "|";
		} else {
			spanu.textContent = suser.name;
		}
		divac.appendChild(spanu);
	}

	var divny = document.createElement('section');
	divny.className = "notyets";
	sec.appendChild(divny);

	for (var uiny in game.notAcceptedYetPlayers) {
		var suserny = game.notAcceptedYetPlayers[uiny];
		var spanuny = document.createElement('section');
		spanuny.className = "notyet";
		if (uiny != game.notAcceptedYetPlayers.length - 1) {
			spanuny.textContent = suserny.name + "|";
		} else {
			spanuny.textContent = suserny.name;
		}
		divny.appendChild(spanuny);
	}

	var divref = document.createElement('section');
	divref.className = "refuseds";
	sec.appendChild(divref);

	for (var uir in game.refusedPlayers) {
		var suserr = game.refusedPlayers[uir];
		var spanur = document.createElement('section');
		spanur.className = "refused";
		if (uir != game.refusedPlayers.length - 1) {
			spanur.textContent = suserr.name + "|";
		} else {
			spanur.textContent = suserr.name;
		}
		divref.appendChild(spanur);
	}
}

function getMyGames() {
	//$.getJSON("/Monopoly/rest/userapi/getMyGames", function (data, status) {
	//alert(event.srcElement);
	var basesec = document.getElementById("managesec");
	deleteAllSubNode(basesec);
	
	var jsonobject = JSON.parse(manageGameData);
	for (var gi in jsonobject.myGames) {
		var game = jsonobject.myGames[gi];
		var sec = document.createElement('section');
		sec.className = "mygame";
		sec.id = game.id;
		basesec.appendChild(sec);

		var aghead = document.createElement('h3');
		aghead.textContent = game.name;
		sec.appendChild(aghead);

		var aghr = document.createElement('hr');
		aghr.className = "act";
		sec.appendChild(aghr);

		createPlayersBlock(sec, game);

		var downhr = document.createElement('hr');
		downhr.className = "act";
		sec.appendChild(downhr);

		var boxdiv = document.createElement('div');
		sec.appendChild(boxdiv);
		var cl = document.createElement('i');
		cl.className = "fa fa-play-circle-o";
		cl.id = "start";
		cl.title = "Start the game.";
		cl.onclick = startGame.bind(this, game.id);
		boxdiv.appendChild(cl);
	}
}

function getInvitations() {
	//$.getJSON("/Monopoly/rest/userapi/getInvitations", function (data, status) {
	//alert(event.srcElement);
	var basesec = document.getElementById("invitationsec");
	deleteAllSubNode(basesec);
	
	var jsonobject = JSON.parse(invGameData);
	for (var gi in jsonobject.nayGames) {
		var game = jsonobject.nayGames[gi];
		var sec = document.createElement('section');
		sec.className = "invgame";
		sec.id = game.id;
		basesec.appendChild(sec);

		var aghead = document.createElement('h3');
		aghead.textContent = game.name;
		sec.appendChild(aghead);

		var uphr = document.createElement('hr');
		uphr.className = "act";
		sec.appendChild(uphr);

		createPlayersBlock(sec, game);

		var downhr = document.createElement('hr');
		downhr.className = "act";
		sec.appendChild(downhr);

		var boxdiv = document.createElement('div');
		sec.appendChild(boxdiv);
		var cl = document.createElement('i');
		cl.className = "fa fa-close";
		cl.id = "close";
		cl.title = "Refuse invitation.";
		cl.onclick = refuseInvitation.bind(this, game.id);
		boxdiv.appendChild(cl);
		var tick = document.createElement('i');
		tick.className = "fa fa-check";
		tick.id = "tick";
		tick.title = "Accept invitation.";
		tick.onclick = acceptInvitation.bind(this, game.id);
		boxdiv.appendChild(tick);
	}
}


function resetsecs(e) {
	var visibleSecs = document.getElementsByClassName("visiblesec");
	for (var i = 0; i < visibleSecs.length; i++) {
		visibleSecs[i].className = "";
	}
}
function reseticons(icon) {
	var selectedHeaders = document.getElementsByClassName("selectedHeader");
	for (var i = 0; i < selectedHeaders.length; i++) {
		selectedHeaders[i].className = "header";
	}

}
function selectSection(sec, icon) {
	resetsecs(sec);
	reseticons(icon);
	sec.className = "visiblesec";
	icon.className = "selectedHeader";
}
function logoutSel() {
	var e = document.getElementById("logoutsec");
	var icon = document.getElementById("logouticonb");
	selectSection(e, icon);
}
function profilSel() {
	var e = document.getElementById("profilsec");
	var icon = document.getElementById("profiliconb");
	selectSection(e, icon);
}
function activeGamesSel() {
	var e = document.getElementById("actgamesec");
	var icon = document.getElementById("actgameiconb");
	selectSection(e, icon);
	getActiveGames();
}
function createGameSel() {
	var e = document.getElementById("newgamesec");
	var icon = document.getElementById("newgameiconb");
	selectSection(e, icon);
}
function myGamesSel() {
	var e = document.getElementById("managesec");
	var icon = document.getElementById("mygameiconb");
	selectSection(e, icon);
	getMyGames();
}
function invitationsSel() {
	var e = document.getElementById("invitationsec");
	var icon = document.getElementById("invitationsiconb");
	selectSection(e, icon);
	getInvitations();
}