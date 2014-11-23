var profil = '{"ownGamesNum":0,"activeGamesNum":0,"invitationsNum":0,"email":"borbastomi@gmail.com","name":"borbastomi","wonGamesNum":0,"participatedGamesNum":0}';
function getProfil() {
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "/Monopoly/rest/gameapi/GetProfil"
	}).success(function(data) {
		console.log(data);
		var profilData = JSON.parse(data);
		var userMail = document.getElementById("mail");
		userMail.value = profilData.email;
		var userName = document.getElementById("name");
		userName.value = profilData.name;
	});
}
function createGame() {
	loadingActivate();
	var gname = document.getElementById("gname");
	if(gname.checkValidity()) {
		var jsonString = '[{"gameName":"'+gname.value+'","players":[{"player":"'+sessionStorage.happygames_basic_username+'"}';
		for(var i=1;i<8;i++) {
			var iname = document.getElementById("iname"+i);
			if(iname.checkValidity()) {
				if(iname.value!="") {
					jsonString = jsonString+',{"player":"'+iname.value+'"}';
				}
			} else {
				return false;
			}
		}
		jsonString = jsonString + ']}]';
		$.ajax({
			type: "POST",
			data : jsonString,
			url : "/Monopoly/rest/gameapi/CreateGame"
		}).success(function(data) {
			alert(data.msg);
			var responseObject = JSON.parse(data);
			loadingDeactivate();
			if(responseObject.code==0) {
				alert("You successfully create the game");
				var gname = document.getElementById("gname");
				gname.value = "";
				for(var i=1;i<8;i++) {
					var iname = document.getElementById("iname"+i);
					if(iname.value!="") {
							iname.value="";
					}
				}
			} else {
				var stringbuffer = "Sorry, there are some problem:\n - invalid usernames:";
				for(var iui in responseObject.invalidUserNames) {
					stringbuffer = stringbuffer + " "+responseObject.invalidUserNames[iui]+",";
				}
				stringbuffer = stringbuffer + "\n - not registered who has too much game:";
				for(var nri in responseObject.notRegisteredUserEmails) {
					stringbuffer = stringbuffer + " "+responseObject.notRegisteredUserEmails[nri]+",";
				}
				alert(stringbuffer);
			}
		}).fail(function() {
			loadingDeactivate();
		});
	}
}
function getActiveGames() {
	var basesec = document.getElementById("actgamesec");
	deleteAllSubNode(basesec);
	loadingActivate();
	$.ajax({
		type : "GET",
		dataType : "json",
		url : "/Monopoly/rest/gameapi/GetActiveGames"
	}).success(function(data) {
		var responseObject = JSON.parse(data);
		for(var agi in responseObject.activeGames) {
			var game = responseObject.activeGames[agi];
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
	}).always(loadingDeactivate());
}
function getMyGames() {
	var basesec = document.getElementById("managesec");
	deleteAllSubNode(basesec);

	loadingActivate();
	$.ajax({
		type : "GET",
		dataType : "json",
		url : "/Monopoly/rest/gameapi/GetMyGames"
	}).success(function(data) {
		var jsonobject = JSON.parse(data);
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
	}).always(loadingDeactivate());
}
function getInvitations() {
	var basesec = document.getElementById("invitationsec");
	deleteAllSubNode(basesec);

	loadingActivate();
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "/Monopoly/rest/gameapi/GetInvitations"
	}).success(function(data) {
		var jsonobject = JSON.parse(data);
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
	}).always(loadingDeactivate());
}
function logout() {
	$.ajax({
		type: "GET",
		url : "/Monopoly/rest/userapi/Logout",
		success : function() {
			sessionStorage.clear();
			window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
		}
	});
}


/*********************** Concrete Game Actions ***********************/

function openGame(id) {
	alert(id);
	var jsonObject = [];
	jsonObject.push({
		"gameid": id
	});
	// TODO
	//$.ajax({
	//	type: "POST",
	//	data: JSON.stringify(jsonObject),
	//	url: "/Monopoly/rest/userapi/openGame"
	//});
}
function startGame(id) {
	if (confirm('Do you want to start the game? Somebody does not accept the invitation yet.')) {
		loadingActivate();
		var jsonObject = [];
		jsonObject.push({
			"gameid": id
		});
		$.ajax({
			type: "POST",
			data: JSON.stringify(jsonObject),
			url: "/Monopoly/rest/userapi/StartGame"
		}).success(function(data) {
			var responseObject = JSON.parse(data);
			loadingDeactivate();
			if(responseObject.success) {
				alert("The game is started.");
			} else {
				alert("Sorry, something wen wrong.");
			}
			getMyGames();
		}).fail(loadingDeactivate());
	}
}
function refuseInvitation(id) {
	var jsonObject = [];
	jsonObject.push({
		"gameid": id
	});
	
	$.ajax({
		type : "POST",
		data : JSON.stringify(jsonObject),
		dataType : "json",
		url : "/Monopoly/rest/gameapi/RefuseInvitation"
	}).success(function(data) {
		var responseObject = JSON.parse(data);
		if(responseObject.success) {
			alert("You refused the invitation");
		} else {
			alert("Sorry, something went wrong");
		}
	}).always(function() {
		loadingDeactivate();
		getInvitations();
	});
}
function acceptInvitation(id) {
	loadingActivate();
	var jsonObject = [];
	jsonObject.push({
		"gameid": id
	});
	
	$.ajax({
		type : "POST",
		data : JSON.stringify(jsonObject),
		dataType : "json",
		url : "/Monopoly/rest/gameapi/AcceptInvitation"
	}).success(function(data) {
		var responseObject = JSON.parse(data);
		if(responseObject.success) {
			alert("You accepted the invitation");
		} else {
			alert("Sorry, something went wrong");
		}
	}).always(function() {
		loadingDeactivate();
		getInvitations();
	});
}


/*********************** UI modifiers ***********************/

function deleteAllSubNode(basesec) {
	while(basesec.firstChild) {
		basesec.removeChild(basesec.firstChild);
	}
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

function loadingActivate() {
	var loading=document.getElementById("loading");
	loading.className = "loading";
}
function loadingDeactivate() {
	var loading=document.getElementById("loading");
	loading.className = "loading invisible";
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