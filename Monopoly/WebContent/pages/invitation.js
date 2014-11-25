function getQueryVariable(variable) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == variable) {
			return pair[1];
		}
	}
	return(false);
}
function onLoad() {
	var email = getQueryVariable("email");
	getInvitation(email);
}

function getInvitation(email) {
	var basesec = document.getElementById("invitationsec");
	deleteAllSubNode(basesec);

	loadingActivate();
	$.ajax({
		type : "POST",
		data: '{"email":"'+email+'"}',
		dataType : "json",
		url : "/Monopoly/rest/gameapi/GetInvitation"
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


/*********************** Concrete Game Actions ***********************/
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