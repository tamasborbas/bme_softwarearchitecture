/******************************* Load Datas *******************************/
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

function loadGameDatas() {
	loadingActivate();
	var playerMail = getQueryVariable("email");
	var gameId = getQueryVariable("gameid");
	sessionStorage.happygames_game_email = playerMail;
	sessionStorage.happygames_game_gameid = gameId;
	$.ajax({
		type : "POST",
		data: '[{"gameId":'+gameId+',"email":"'+playerMail+'"}]',
		dataType : "json",
		url : "/Monopoly/rest/gameapi/OpenGame"
	}).success(function(data) {
		console.log(data);
		var gameData = JSON.parse(data);
		if(gameData.gameStatus=="init") {
			alert("Sorry, this game is under initialization.");
			window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
		} else if(gameData.gameStatus=="finished") {
			alert("Sorry, this game has been finished.");
			window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
		} else {
			sessionStorage.happygames_game_name = gameData.name;
			sessionStorage.happygames_game_ownerOfGame = gameData.ownerOfGame;
			sessionStorage.happygames_game_actualPlayer = gameData.actualPlayer;
			sessionStorage.happygames_game_players = JSON.stringify(gameData.players);
			sessionStorage.happygames_game_places = JSON.stringify(gameData.places);
			// TODO folyt
			
			loadingDeactivate();
		}
	});
//	createMiniPlayers();
//	createGameBoard();
}

/******************************* Game Board *******************************/
var gameboardData = 
	'{"places":[{"placeSequenceNumber":1,"type":"start","placeId":0,"name":"Start","playersOnPlace":[{"id":0,"name":"Valaki"},{"id":1,"name":"Valaki Más"}]}]}';
function getPlaceData(id) {
	alert(id);
}
function createGameBoard() {
	var jsonobject = JSON.parse(gameboardData);
	for (var pi in jsonobject.places) {
		var place = jsonobject.places[pi];
		var basesec = document.getElementById(("place" + place.placeSequenceNumber));
		if (place.type == "BuildingPlace") {
			basesec.onclick = getPlaceData.bind(this, place.id); // TODO nem build.id kéne?
		}

		// basic place data
		var lblpname = document.createElement('label');
		lblpname.className = "placedata placedata-name";
		lblpname.textContent = place.name;
		basesec.appendChild(lblpname);

		var lblpid = document.createElement('label');
		lblpid.className = "placedata placedata-number";
		lblpid.textContent = place.placeSequenceNumber;
		basesec.appendChild(lblpid);

		var brsep = document.createElement('br');
		basesec.appendChild(brsep);


		// table for players tokens
		var sec = document.createElement('section');
		sec.className = "playertokenstable";
		basesec.appendChild(sec);
		var table = document.createElement('table');
		table.className = "playertokens";
		sec.appendChild(table);

		var tr1 = document.createElement('tr');
		table.appendChild(tr1);
		var tr2 = document.createElement('tr');
		table.appendChild(tr2);

		for (var i = 0; i < 4; i++) {
			//console.log("Placeid:" + place.id + " | i:" + i);
			var td1 = document.createElement('td');
			td1.id = "place" + place.placeSequenceNumber + "player" + i;
			td1.className = "transparentcolor";
			tr1.appendChild(td1);
			var td2 = document.createElement('td');
			td2.id = "place" + place.placeSequenceNumber + "player" + (i+4);
			td2.className = "transparentcolor";
			tr2.appendChild(td2);
			if (place.players.length > 0) {
				for (var playeri in place.players) {
					var player = place.players[playeri];
					//alert(player + "\n id:" + player.id + "\n name:" + player.name);
					//alert(i + " " + (i == player.id));
					//console.log("Playerid:" + player.id + " | Playername:" + player.name);
					if (player.id == i) {
						//console.log("OK"+i);
						td1.className = ("token playercolor"+i);
						td1.title = player.name;
					} else if (player.id == (i + 4)) {
						//console.log("OK"+(i+4));
						td2.className = ("token playercolor"+(i+4));
						td2.title = player.name;
					}
				}
			}
		}
	}
}

/******************************* Game Steps *******************************/
function backToHome() {
	window.location = "/Monopoly/pages/home.html";
}
function rollDie(sides) {
	if (!sides) {
		sides = 6;
	}
	with (Math) return 1 + floor(random() * sides);
}
function rollDice(number, sides) {
	var total = 0;
	while (number-- > 0) {
		total += rollDie(sides);
	}
	return total;
}
function roll() {
	var rollresult = rollDice(1, 6);
	var rb = document.getElementById("rollsection");
	rb.className = "steph1-collapsed";
	var rr = document.getElementById("rollresults");
	rr.className = "steph1";
	var rt = document.getElementById("roll_text");
	rt.value = rt.value.concat(" " + rollresult);
	if (Math.random() > 0.0) {
		var ps = document.getElementById("paysection");
		ps.className = "steph1 steph1-withbutton";
	} else {
		var bs = document.getElementById("buysection");
		bs.className = "steph1 steph1-withbutton";
	}
}
var sellablebuildings = '{"buildings":[{"id":0,"name":"BCE","price":750,"houseprice":150,"maxhouses":4},{"id":1,"name":"BME","price":500,"houseprice":50,"maxhouses":4},{"id":2,"name":"BGF","price":250,"houseprice":25,"maxhouses":5}]}';
function getSellableBuildings() {
	var tbody = document.getElementById("sellbuildingsbody");
	
	var jsonobject = JSON.parse(sellablebuildings);
	for (var bi in jsonobject.buildings) {
		var building = jsonobject.buildings[bi];
		var tr = document.createElement('tr');
		tbody.appendChild(tr);

		var td1 = document.createElement('td');
		td1.className = "scolumn";
		td1.textContent = building.name;
		tbody.appendChild(td1);

		var td2 = document.createElement('td');
		td2.className = "scolumn";
		td2.textContent = building.price;
		tbody.appendChild(td2);

		var td3 = document.createElement('td');
		td3.className = "ticky";
		tbody.appendChild(td3);

		var tickb = document.createElement('input');
		tickb.type = "checkbox";
		tickb.id = "ticky" + building.id;
		td3.appendChild(tickb);
	}
}
function pay() {
	var rb = document.getElementById("paysection");
	rb.className = "steph1-collapsed";
	if (Math.random() > 0.0) {
		var ps = document.getElementById("sellbuildingsection");
		ps.className = "steph1 steph1-withbutton";
		getSellableBuildings();
		var soldbuildings = [];
		sessionStorage.setItem("soldbuildings", JSON.stringify(soldbuildings));
	} else {
		var bs = document.getElementById("buyhousesection");
		bs.className = "steph1 steph1-withbutton";
	}
}
function decreaseNumOfH(id) {
	var textNum = document.getElementById(("boughtNumOfH" + id));
	var num = parseInt(textNum.value);
	if (num > 0) {
		textNum.value = (num - 1);
	}
}
function inreaseNumOfH(id, max) {
	var textNum = document.getElementById(("boughtNumOfH" + id));
	var num = parseInt(textNum.value);
	if (num < max) {
		textNum.value = (num + 1);
	}
}
function sellb() {
	var rb = document.getElementById("sellbuildingsection");
	rb.className = "steph1-collapsed";
	var jsonobjects = JSON.parse(sellablebuildings);
	for (var bi in jsonobjects.buildings) {
		var building = jsonobjects.buildings[bi];
		var tickyb = document.getElementById(("ticky" + building.id));
		if (tickyb.checked) {
			var soldbuildingsAsString = sessionStorage.getItem("soldbuildings");
			if (soldbuildingsAsString) {
				var soldbuildings = JSON.parse(soldbuildingsAsString);
				soldbuildings[soldbuildings.length] = building.id;
				sessionStorage.setItem("soldbuildings", JSON.stringify(soldbuildings));
				alert("Sold:" + (soldbuildings.lastIndexOf(building.id)) + " - " + soldbuildings);
			}
		}
	}
	//alert(sessionStorage.getItem("soldbuildings"));
	var soldbuildingsAsString = sessionStorage.getItem("soldbuildings");
	var soldbuildings = [];
	if (soldbuildingsAsString) {
		soldbuildings = JSON.parse(soldbuildingsAsString);
	}
	var tbody = document.getElementById("buybuildingsbody");

	var jsonobject = JSON.parse(sellablebuildings);
	for (var bi in jsonobject.buildings) {
		var building = jsonobject.buildings[bi];
		if (soldbuildings.lastIndexOf(building.id) == (-1)) {
			var tr = document.createElement('tr');
			tbody.appendChild(tr);

			var td1 = document.createElement('td');
			td1.className = "hcolumn";
			td1.textContent = building.name;
			tbody.appendChild(td1);

			var td2 = document.createElement('td');
			td2.className = "hcolumn";
			td2.textContent = building.houseprice;
			tbody.appendChild(td2);

			var td3 = document.createElement('td');
			td3.className = "plusmin";
			tbody.appendChild(td3);

			var minus = document.createElement('i');
			minus.className = "fa fa-minus-circle";
			minus.onclick = decreaseNumOfH.bind(this, building.id);
			td3.appendChild(minus);

			var num = document.createElement('input');
			num.className = "mini";
			num.type = "text";
			num.id = "boughtNumOfH" + building.id;
			num.value = 0;
			num.disabled = true;
			td3.appendChild(num);

			var plus = document.createElement('i');
			plus.className = "fa fa-plus-circle";
			plus.onclick = inreaseNumOfH.bind(this, building.id, building.maxhouses);
			td3.appendChild(plus);
		}
	}
	var rr = document.getElementById("boughtsection");
	rr.className = "steph1";
	var bb = document.getElementById("buildingbuy_text");
	bb.value = "Sold house(s): ";
	var bs = document.getElementById("buyhousesection");
	bs.className = "steph1 steph1-withbutton";
}
function buyb() {
	var rb = document.getElementById("buysection");
	rb.className = "steph1-collapsed";
	var rr = document.getElementById("boughtsection");
	rr.className = "steph1";
	var bs = document.getElementById("buyhousesection");
	bs.className = "steph1 steph1-withbutton";
}
function nbuyb() {
	var rb = document.getElementById("buysection");
	rb.className = "steph1-collapsed";
	var bs = document.getElementById("buyhousesection");
	bs.className = "steph1 steph1-withbutton";
}
function buyh() {
	var rb = document.getElementById("buyhousesection");
	rb.className = "steph1-collapsed";
	var bs = document.getElementById("finishsection");
	bs.className = "steph1 steph1-withbutton";
}
function finish() {
	alert("Bye-bye");
}

/******************************* Playerboard *******************************/
var playersboardData = '{"actualPlayer":[{"playerId":1,"name":"Valaki Neve","placeSequenceNumber":15,"money":800},{"id":2,"name":"Valaki Más Neve","place":10,"money":700}],"losers":[{"id":3,"name":"Nagyon-nagyon Senki Neve"}]}';
function getPlayerData(id) {
	alert(id);
}
function createMiniPlayers() {
	var basesec = document.getElementById("playersboardcontainer");
	var jsonobject = JSON.parse(sessionStorage.happygames_game_players);
	for (var pi in jsonobject.activePlayers) {
		var player = jsonobject.activePlayers[pi];
		var sec = document.createElement('section');
		sec.className = "miniplayer";
		sec.id = player.playerId;
		sec.onclick = getPlayerData.bind(this, player.playerId);
		basesec.appendChild(sec);


		// player name section
		var secpname = document.createElement('section');
		secpname.className = "playerid";
		sec.appendChild(secpname);

		var lblpname = document.createElement('label');
		lblpname.className = "playerid";
		lblpname.textContent = player.name;
		secpname.appendChild(lblpname);


		// player data section
		var secpdata = document.createElement('section');
		secpdata.className = "playerdata";
		sec.appendChild(secpdata);

		var lblpmoney = document.createElement('label');
		lblpmoney.className = "playerdata";
		lblpmoney.textContent = "Money: " + player.money;
		secpdata.appendChild(lblpmoney);

		var brsep = document.createElement('br');
		secpdata.appendChild(brsep);

		var lblpplace = document.createElement('label');
		lblpplace.className = "playerdata";
		lblpplace.textContent = "Place: " + player.placeSequenceNumber;
		secpdata.appendChild(lblpplace);
	}
	for (var pli in jsonobject.losers) {
		var loser = jsonobject.losers[pli];
		var secl = document.createElement('section');
		secl.className = "miniplayer out";
		secl.id = loser.playerId;
		secl.onclick = function () { getPlayerData(loser.playerId); };
		basesec.appendChild(secl);


		// player name section
		var seclpname = document.createElement('section');
		seclpname.className = "playerid";
		secl.appendChild(seclpname);

		var lbllpname = document.createElement('label');
		lbllpname.className = "playerid";
		lbllpname.textContent = loser.name;
		seclpname.appendChild(lbllpname);


		// player data section
		var seclpdata = document.createElement('section');
		seclpdata.className = "playerdata";
		secl.appendChild(seclpdata);

		var lbllpmoney = document.createElement('label');
		lbllpmoney.className = "playerdata";
		lbllpmoney.textContent = "Money: -";
		seclpdata.appendChild(lbllpmoney);

		var brlsep = document.createElement('br');
		seclpdata.appendChild(brlsep);

		var lbllpplace = document.createElement('label');
		lbllpplace.className = "playerdata";
		lbllpplace.textContent = "Place: -";
		seclpdata.appendChild(lbllpplace);
	}
}

/********************************************* Loading *********************************************/
function loadingActivate() {
	var loading=document.getElementById("loading");
	loading.className = "loading";
}
function loadingDeactivate() {
	var loading=document.getElementById("loading");
	loading.className = "loading invisible";
}