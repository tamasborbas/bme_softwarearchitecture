/**************************************************************************************************************************/
/***************************************************** Main functions *****************************************************/
/**************************************************************************************************************************/

/**
 * Send a post message to the server for get user profile data, and if the
 * request is success then it will load data into the fields.
 */
function getProfil() {
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "/Monopoly/rest/userapi/GetProfil"
	}).success(function(data) {
		// console.log(data);
		var profilData = JSON.parse(data);
		var userMail = document.getElementById("mail");
		userMail.value = profilData.email;
		sessionStorage.happygames_basic_email = profilData.email;
		var userName = document.getElementById("name");
		userName.value = profilData.name;
	});
}

/**
 * Send a post request to the server to create game if all of the fields contain
 * valid data. When there is a validation error it will return false. If there
 * are some problem with game creation we inform the user.
 * 
 * @returns {Boolean}
 */
function createGame() {
	loadingActivate();
	var gname = document.getElementById("gname");
	if (gname.checkValidity()) {
		// Create JSON for post (game name and players list).
		var jsonString = '[{"gameName":"' + gname.value
				+ '","players":[{"player":"'+ sessionStorage.happygames_basic_username + '"}';
		var playersNum = 0;
		for (var i = 1; i < 8; i++) {
			var iname = document.getElementById("iname" + i);
			if (iname.checkValidity()) {
				if (iname.value != "" && iname.value!=sessionStorage.happygames_basic_username
						&& iname.value!=sessionStorage.happygames_basic_email) {
					jsonString = jsonString + ',{"player":"' + iname.value + '"}';
					playersNum++;
				}
			} else {
				return false;
			}
		}
		jsonString = jsonString + ']}]';
		// Send post message to the server.
		$.ajax({
			type : "POST",
			data : jsonString,
			url : "/Monopoly/rest/gamemanagementapi/CreateGame"
		}).success(function(data) {
			// If we get a response...
			var responseObject = JSON.parse(data);
			loadingDeactivate();
			if (responseObject.code == 0) {
				// ... we show that everything is okay if there is no error...
				alert("You successfully create the game");
				var gname = document.getElementById("gname");
				gname.value = "";
				for (var i = 1; i < 8; i++) {
					var iname = document.getElementById("iname"
							+ i);
					if (iname.value != "") {
						iname.value = "";
					}
				}
			} else {
				// ... or we show the users with which we have problems.
				var stringbuffer = "Sorry, there are some problem:\n - invalid usernames:";
				for ( var iui in responseObject.invalidUserNames) {
					stringbuffer = stringbuffer
							+ " "
							+ responseObject.invalidUserNames[iui]
							+ ",";
				}
				stringbuffer = stringbuffer
						+ "\n - not registered who has too much game:";
				for ( var nri in responseObject.notRegisteredUserEmails) {
					stringbuffer = stringbuffer
							+ " "
							+ responseObject.notRegisteredUserEmails[nri]
							+ ",";
				}
				alert(stringbuffer);
			}
		}).fail(function() {
			// We inform user that we have server problems.
			alert("Sorry, we have problems. Try again later.");
			loadingDeactivate();
		});
	}
}

/**
 * Send a post request to get the active games and show these to the user.
 */
function getActiveGames() {
	var basesec = document.getElementById("actgamesec");
	// But before the request we clear the section.
	deleteAllSubNode(basesec);
	loadingActivate();
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "/Monopoly/rest/gamemanagementapi/GetActiveGames"
	}).success(function(data) {
		// If the request call back successfully we create sections for the active games.
		var responseObject = JSON.parse(data);
		for ( var agi in responseObject.activeGames) {
			// The section built from ...
			var game = responseObject.activeGames[agi];
			var sec = document.createElement('section');
			sec.className = "actgame";
			sec.id = game.id;
			// (if the user click on the game section we start the game)
			sec.onclick = openGame.bind(this, game.id);
			basesec.appendChild(sec);

			// ... a header which show the game name, ...
			var aghead = document.createElement('h3');
			aghead.textContent = game.name;
			sec.appendChild(aghead);

			// ... a separator line which separate the players from the header, ...
			var aghr = document.createElement('hr');
			aghr.className = "act";
			sec.appendChild(aghr);

			// ... create a highlighted section for the actual player, ...
			var spanuact = document.createElement('section');
			spanuact.className = "accept";
			spanuact.textContent = "Actual player: "+game.actualPlayer;
			sec.appendChild(spanuact);
			sec.appendChild(document.createElement('br'));

			// ... and simple sections for the others.
			for ( var ui in game.players) {
				var suser = game.players[ui];
				var spanu = document.createElement('section');
				spanu.className = "user";
				if (ui != 0) {
					spanu.textContent = "|"+suser.name;
				} else {
					spanu.textContent = suser.name;
				}
				sec.appendChild(spanu);
			}
		}
	}).always(loadingDeactivate());
}

/**
 * Send a post request to get the manageable games for the user.
 */
function getMyGames() {
	var basesec = document.getElementById("managesec");
	// But before the request we clear the section.
	deleteAllSubNode(basesec);
	loadingActivate();
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "/Monopoly/rest/gamemanagementapi/GetMyGames"
	}).success(function(data) {
		var jsonobject = JSON.parse(data);
		// Create game sections which contain ...
		for ( var gi in jsonobject.myGames) {
			var game = jsonobject.myGames[gi];
			var sec = document.createElement('section');
			sec.className = "mygame";
			sec.id = game.id;
			basesec.appendChild(sec);

			// ... a header which show the game's name ...
			var aghead = document.createElement('h3');
			aghead.textContent = game.name;
			sec.appendChild(aghead);

			// ... a separator line ...
			var aghr = document.createElement('hr');
			aghr.className = "act";
			sec.appendChild(aghr);

			// ... players block which show separatly the users who accepted, 
			// who not response yet and who refused the invitation for the game ...
			createPlayersBlock(sec, game);

			// ... an other separator line ...
			var downhr = document.createElement('hr');
			downhr.className = "act";
			sec.appendChild(downhr);

			// ... and a clickable element with which the owner can start the game.
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

/**
 * Send a post request to get the game invitations.
 */
function getInvitations() {
	var basesec = document.getElementById("invitationsec");
	// But before the request we clear the section.
	deleteAllSubNode(basesec);
	loadingActivate();
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "/Monopoly/rest/gamemanagementapi/GetInvitations"
	}).success(function(data) {
		var jsonobject = JSON.parse(data);
		// If we get the response we create game sections like in the management view...
		for ( var gi in jsonobject.nayGames) {
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

			// ... with the same players block ...
			createPlayersBlock(sec, game);

			var downhr = document.createElement('hr');
			downhr.className = "act";
			sec.appendChild(downhr);

			// ... the only difference is that we have two button ...
			var boxdiv = document.createElement('div');
			sec.appendChild(boxdiv);
			// ... one to refuse the invitation ...
			var cl = document.createElement('i');
			cl.className = "fa fa-close";
			cl.id = "close";
			cl.title = "Refuse invitation.";
			cl.onclick = refuseInvitation.bind(this, game.id);
			boxdiv.appendChild(cl);
			// ... and one to accept it.
			var tick = document.createElement('i');
			tick.className = "fa fa-check";
			tick.id = "tick";
			tick.title = "Accept invitation.";
			tick.onclick = acceptInvitation.bind(this, game.id);
			boxdiv.appendChild(tick);
		}
	}).always(loadingDeactivate());
}

/**
 * Logout the user:
 *  - send server that we want to log out
 *  - then we clear session data and redirect user.
 */
function logout() {
	$.ajax({
		type : "POST",
		url : "/Monopoly/rest/userapi/Logout",
		success : function() {
			sessionStorage.clear();
			window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
		}
	});
}




/**************************************************************************************************************************/
/************************************************  Concrete Game Functions ************************************************/
/**************************************************************************************************************************/

/**
 * We open the selected game.
 * 
 * @param id The id of the selected game.
 */
function openGame(id) {
	window.location.href = "https://localhost:8443/Monopoly/pages/game.html?email="
			+ sessionStorage.happygames_basic_email + "&gameid=" + id;
}

/**
 * We can start manually the selected game.
 * If there is not enough players we refuse the start request.
 * 
 * @param id The selected game id.
 */
function startGame(id) {
	if (confirm('Do you want to start the game? Somebody does not accept the invitation yet.')) {
		loadingActivate();
		var jsonObject = [];
		jsonObject.push({
			"gameid" : id
		});
		$.ajax({
			type : "POST",
			data : JSON.stringify(jsonObject),
			url : "/Monopoly/rest/gamemanagementapi/StartGame"
		}).success(function(data) {
			var responseObject = JSON.parse(data);
			loadingDeactivate();
			if (responseObject.success) {
				alert("The game is started.");
			} else {
				alert("Sorry, you can not start the game.");
			}
			getMyGames();
		}).fail(loadingDeactivate());
	}
}

/**
 * We refuse the selected game invitation.
 * 
 * @param id The selected game id.
 */
function refuseInvitation(id) {
	$.ajax({
		type : "POST",
		data : '{"gameId":' + id + "}",
		dataType : "json",
		url : "/Monopoly/rest/gamemanagementapi/RefuseInvitation"
	}).success(function(data) {
		var responseObject = JSON.parse(data);
		if (responseObject.success) {
			alert("You refused the invitation");
		} else {
			alert("Sorry, something went wrong");
		}
	}).always(function() {
		loadingDeactivate();
		getInvitations();
	});
}

/**
 * We accept the selected game invitation.
 * 
 * @param id The selected game id.
 */
function acceptInvitation(id) {
	$.ajax({
		type : "POST",
		data : '{"gameId":' + id + "}",
		dataType : "json",
		url : "/Monopoly/rest/gamemanagementapi/AcceptInvitation"
	}).success(function(data) {
		var responseObject = JSON.parse(data);
		if (responseObject.success) {
			alert("You accepted the invitation");
		} else {
			alert("Sorry, something went wrong");
		}
	}).always(function() {
		loadingDeactivate();
		getInvitations();
	});
}




/**************************************************************************************************************************/
/****************************************************** UI modifiers ******************************************************/
/**************************************************************************************************************************/

/**
 * Remove all children of the selected section.
 * 
 * @param basesec The selected section.
 */
function deleteAllSubNode(basesec) {
	while (basesec.firstChild) {
		basesec.removeChild(basesec.firstChild);
	}
}

/**
 * Create players block into the selected section.
 * 
 * @param sec The selected section.
 * @param game The source of the players data.
 */
function createPlayersBlock(sec, game) {
	// Separatly create a block for the accepted users ...
	var divac = document.createElement('section');
	divac.className = "accepts";
	sec.appendChild(divac);

	for ( var ui in game.acceptedPlayers) {
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

	// ... another for who have not answer yet ...
	var divny = document.createElement('section');
	divny.className = "notyets";
	sec.appendChild(divny);

	for ( var uiny in game.notAcceptedYetPlayers) {
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

	// ... and one for who refused the invitation.
	var divref = document.createElement('section');
	divref.className = "refuseds";
	sec.appendChild(divref);

	for ( var uir in game.refusedPlayers) {
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

/**
 * Activate the loading image.
 */
function loadingActivate() {
	var loading = document.getElementById("loading");
	loading.className = "loading";
}

/**
 * Deactivate the loading image.
 */
function loadingDeactivate() {
	var loading = document.getElementById("loading");
	loading.className = "loading invisible";
}

/**
 * Set all visible section to unvisible.
 */
function resetsecs() {
	var visibleSecs = document.getElementsByClassName("visiblesec");
	for (var i = 0; i < visibleSecs.length; i++) {
		visibleSecs[i].className = "";
	}
}

/**
 * Set all icons to default.
 */
function reseticons() {
	var selectedHeaders = document.getElementsByClassName("selectedHeader");
	for (var i = 0; i < selectedHeaders.length; i++) {
		selectedHeaders[i].className = "header";
	}
}

/**
 * Set the caught parameter section visible (others unvisible) and highlight the
 * caught icon (set others to default).
 * 
 * @param sec The selected section
 * @param icon The highlighted icon
 */
function selectSection(sec, icon) {
	resetsecs();
	reseticons();
	sec.className = "visiblesec";
	icon.className = "selectedHeader";
}

// These are the selectable views' selection methods.
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