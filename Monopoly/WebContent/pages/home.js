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

var data1 = '{"actGames":[{"name":"valami","id":1100,"users":[{"name":"uasdasdasd1"},{"name":"uasasdasdd2"},{"name":"uasasdasdd2"},{"name":"uasasdasdd2"},{"name":"uasdasdasd3"}]},{"name":"valami","id":1100,"users":[{"name":"uasdasdasd1"}]},{"name":"valami","id":1100,"users":[{"name":"uasdasdasd1"},{"name":"uasasdasdd2"},{"name":"uasdasdasd3"},{"name":"uasasdasdd2"},{"name":"uasdasdasd3"}]}]}';
var data = '{"myGames":[{"name":"valami","id":1100,"accept":[{"name":"uasdasdasd1"}],"notyet":[{"name":"uasasdasdd2"},{"name":"uasasdasdd2"},{"name":"uasasdasdd2"},{"name":"uasdasdasd3"}],"refused":[{"name":"uasasdasdd2"},{"name":"uasdasdasd3"}]},{"name":"valami","id":1100,"accept":[{"name":"uasdasdasd1"}],"notyet":[{"name":"uasasdasdd2"},{"name":"uasdasdasd3"}],"refused":[{"name":"uasasdasdd2"},{"name":"uasdasdasd3"}]},{"name":"valami","id":1100,"accept":[{"name":"uasdasdasd1"}],"notyet":[{"name":"uasasdasdd2"},{"name":"uasdasdasd3"}],"refused":[{"name":"uasasdasdd2"},{"name":"uasdasdasd3"}]}]}';

function getActiveGames() {
	//$.getJSON("/Monopoly/rest/userapi/getActiveGames", function (data, status) {
	var basesec = document.getElementById("actgamesec");
	var jsonobject = JSON.parse(data1);
	for (var gi in jsonobject.actGames) {
		var game = jsonobject.actGames[gi];
		var sec = document.createElement('section');
		sec.className = "actgame";
		sec.id = game.id;
		sec.onclick = function () { openGame(game.id + (Math.floor(Math.random() * 100))) };
		basesec.appendChild(sec);

		var aghead = document.createElement('h3');
		aghead.textContent = game.name;
		sec.appendChild(aghead);

		var aghr = document.createElement('hr');
		aghr.className = "act";
		sec.appendChild(aghr);

		//var divs = document.createElement('section');
		//divs.className = "users";
		//sec.appendChild(divs);

		for (var ui in game.users) {
			var suser = game.users[ui];
			var spanu = document.createElement('section');
			spanu.className = "user";
			spanu.textContent = suser.name + "|";
			sec.appendChild(spanu);
		}
	}
	//});
}
function getMyGames() {
	//$.getJSON("/Monopoly/rest/userapi/getMyGames", function (data, status) {
	//alert(event.srcElement);
	var basesec = document.getElementById("managesec");
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

		var divac = document.createElement('section');
		divac.className = "accepts";
		sec.appendChild(divac);

		for (var ui in game.accept) {
			var suser = game.accept[ui];
			var spanu = document.createElement('section');
			spanu.className = "accept";
			spanu.textContent = suser.name + "|";
			divac.appendChild(spanu);
		}

		var divny = document.createElement('section');
		divny.className = "notyets";
		sec.appendChild(divny);

		for (var ui in game.notyet) {
			var suser = game.notyet[ui];
			var spanu = document.createElement('section');
			spanu.className = "notyet";
			spanu.textContent = suser.name + "|";
			divny.appendChild(spanu);
		}

		var divref = document.createElement('section');
		divref.className = "refuseds";
		sec.appendChild(divref);

		for (var ui in game.refused) {
			var suser = game.refused[ui];
			var spanu = document.createElement('section');
			spanu.className = "refused";
			spanu.textContent = suser.name + "|";
			divref.appendChild(spanu);
		}

		var downhr = document.createElement('hr');
		downhr.className = "act";
		sec.appendChild(downhr);

		var boxdiv = document.createElement('div');
		sec.appendChild(boxdiv);
		var cl = document.createElement('i');
		cl.className = "fa fa-play-circle-o";
		cl.id = "start";
		cl.title = "Start the game.";
		cl.onclick = function () { startGame(game.id + (Math.floor(Math.random() * 100))) };
		boxdiv.appendChild(cl);
	}
	//});
}

function getInvitations() {
	//$.getJSON("/Monopoly/rest/userapi/getInvitations", function (data, status) {
	//alert(event.srcElement);
	var basesec = document.getElementById("invitationsec");
	var jsonobject = JSON.parse(data);
	for (var gi in jsonobject.myGames) {
		var game = jsonobject.myGames[gi];
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

		var divac = document.createElement('section');
		divac.className = "accepts";
		sec.appendChild(divac);

		for (var ui in game.accept) {
			var suser = game.accept[ui];
			var spanu = document.createElement('section');
			spanu.className = "accept";
			spanu.textContent = suser.name + "|";
			divac.appendChild(spanu);
		}

		var divny = document.createElement('section');
		divny.className = "notyets";
		sec.appendChild(divny);

		for (var ui in game.notyet) {
			var suser = game.notyet[ui];
			var spanu = document.createElement('section');
			spanu.className = "notyet";
			spanu.textContent = suser.name + "|";
			divny.appendChild(spanu);
		}

		var divref = document.createElement('section');
		divref.className = "refuseds";
		sec.appendChild(divref);

		for (var ui in game.refused) {
			var suser = game.refused[ui];
			var spanu = document.createElement('section');
			spanu.className = "refused";
			spanu.textContent = suser.name + "|";
			divref.appendChild(spanu);
		}

		var downhr = document.createElement('hr');
		downhr.className = "act";
		sec.appendChild(downhr);

		var boxdiv = document.createElement('div');
		sec.appendChild(boxdiv);
		var cl = document.createElement('i');
		cl.className="fa fa-close";
		cl.id = "close";
		cl.title = "Refuse invitation.";
		cl.onclick = function() { refuseInvitation(game.id)};
		boxdiv.appendChild(cl);
		var tick = document.createElement('i');
		tick.className="fa fa-check";
		tick.id = "tick";
		tick.title = "Accept invitation."
		tick.onclick = function() { acceptInvitation(game.id)};
		boxdiv.appendChild(tick);
	}
	//});
}


function resetsecs(e) {
	//var l = document.getElementById("loginsec");
	//if (l != e) {
	//	l.className = "";
	//	l.style.display = 'none';
	//}
	//var r = document.getElementById("registrationsec");
	//if (r != e) {
	//	r.className = "";
	//	r.style.display = 'none';
	//}
	//var rem = document.getElementById("remindersec");
	//if (rem != e) {
	//	rem.className = "";
	//	rem.style.display = 'none';
	//}
	var visibleSecs = document.getElementsByClassName("visiblesec");
	for (var i = 0; i < visibleSecs.length; i++) {
		visibleSecs[i].className = "";
	}
}
function reseticons(icon) {
	//var iconb1 = document.getElementById("profiliconb");
	//if (iconb1 != icon) {
	//	iconb1.className = "header";
	//}
	//var iconb2 = document.getElementById("actgameiconb");
	//if (iconb2 != icon) {
	//	iconb2.className = "header";
	//}
	//var iconb3 = document.getElementById("newgameiconb");
	//if (iconb3 != icon) {
	//	iconb3.className = "header";
	//}
	//var iconb3 = document.getElementById("newgameiconb");
	//if (iconb3 != icon) {
	//	iconb3.className = "header";
	//}
	//var iconb3 = document.getElementById("newgameiconb");
	//if (iconb3 != icon) {
	//	iconb3.className = "header";
	//}
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