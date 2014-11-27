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
		if(gameData.playerStatus=="notAcceptedYet") {
			var baseGD="Game name: "+gameData.name+"\nOwner of the game: "+gameData.nameOfGameOwner;
			
			if(confirm(("Do you want to accept the invitation for this game?\n"+baseGD))) {
				var nrPlayerMail = getQueryVariable("email");
				var gameId = getQueryVariable("gameid");
				$.ajax({
					type : "POST",
					data: '{"email":"'+nrPlayerMail+'","gameId":'+gameId+'}',
					dataType : "json",
					url : "/Monopoly/rest/gamemanagementapi/AcceptInvitationFromEmail"
				}).success(function() {
					removeGameSessionData();
					alert("You accept the invitation.");
					location.reload();
				}).error(function() {
					window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
				});
			} else {
				var nrPlayerMail = getQueryVariable("email");
				var gameId = getQueryVariable("gameid");
				$.ajax({
					type : "POST",
					data: '{"email":"'+nrPlayerMail+'","gameId":'+gameId+'}',
					dataType : "json",
					url : "/Monopoly/rest/gamemanagementapi/RefuseInvitationFromEmail"
				}).success(function() {
					alert("You refused the invitation.");
				}).complete(function() {
					window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
				});
			}
		} else if(gameData.gameStatus=="init") {
			alert("Sorry, this game is under initialization.");
			window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
		} else if(gameData.gameStatus=="finished") {
			alert("Sorry, this game has been finished.");
			window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
		} else {
			// store data
			sessionStorage.happygames_game_name = gameData.name;
			sessionStorage.happygames_game_money = gameData.actualPlayer.money;
			sessionStorage.happygames_game_id = gameData.id;
			sessionStorage.happygames_game_ownerOfGame = gameData.nameOfGameOwner;
			sessionStorage.happygames_game_actualPlayer = JSON.stringify(gameData.actualPlayer);
			sessionStorage.happygames_game_actualPlayer_sellableBuildings = JSON.stringify(gameData.actualPlayer.ownedBuildings);
			sessionStorage.happygames_game_places = JSON.stringify(gameData.places);
			sessionStorage.happygames_game_activePlayers = JSON.stringify(gameData.acceptedPlayers);
			sessionStorage.happygames_game_losersPlayers = JSON.stringify(gameData.loserPlayers);
			// default data
			sessionStorage.happygames_game_roll = 0;
			sessionStorage.happygames_game_placeSN = 0;
			sessionStorage.happygames_game_isBuildingBought = false;
			sessionStorage.happygames_game_isPayed = false;
			sessionStorage.happygames_game_isSold = false;
			sessionStorage.happygames_game_actualPlayer_soldbuildings = "[]";
			sessionStorage.happygames_game_boughtHouseNumberForBuildings = "[]";
			
			console.log((parseInt(sessionStorage.happygames_game_money)+parseInt(sessionStorage.happygames_game_money)));
			var amt = document.getElementById("actmoney_text");
			amt.value = "Your money: "+ sessionStorage.happygames_game_money;
			
			if(gameData.isActualPlayer) {
				var rb = document.getElementById("rollsection");
				rb.className = "steph1 steph1-withbutton";
			}
			if(sessionStorage.getItem("happygames_basic_username")!=null && sessionStorage.getItem("happygames_basic_username")!="") {
				var rgh = document.getElementById("gohomesection");
				rgh.className = "steph1 steph1-withbutton";
			}
			
			createGameBoard();
			createMiniPlayers();
			loadingDeactivate();
		}
	});
}

/******************************* Game Board *******************************/
function getPlaceData(id) {
	alert(id);
}
function createGameBoard() {
	var jsonarray = JSON.parse(sessionStorage.happygames_game_places);
	for (var pi in jsonarray) {
		var place = jsonarray[pi];
		console.log(jsonarray);
		console.log(jsonarray[pi]);
		console.log(place.placeSequenceNumber);
		var basesec = document.getElementById(("place" + (place.placeSequenceNumber-1)));
		if (place.type == "BuildingPlace") {
			basesec.onclick = getPlaceData.bind(this, place.id); // TODO nem build.id kéne?
		}

		// basic place data
		var lblpname = document.createElement('label');
		lblpname.className = "placedata placedata-name";
		lblpname.textContent = place.placeName;
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
			console.log(place.placeSequenceNumber +" playersnum: "+place.playersOnPlace.length);
			if (place.playersOnPlace.length > 0) {
				for (var playeri in place.playersOnPlace) {
					var player = place.playersOnPlace[playeri];
					console.log(JSON.stringify(player));
					
					if (player.playerSequence == i) {
						//console.log("OK"+i);
						td1.className = ("token playercolor"+i);
						td1.title = player.userName;
					} else if (player.playerSequence == (i + 4)) {
						//console.log("OK"+(i+4));
						td2.className = ("token playercolor"+(i+4));
						td2.title = player.userName;
					}
				}
			}
		}
	}
}

/******************************* Game Steps *******************************/
function removeGameSessionData() {
	var removables = [];
	for(var i=0; i<sessionStorage.length; i++) {
		var key = sessionStorage.key(i);
		if(key.indexOf("happygames_game_")!=-1) {
			removables.push(key);
		}
	}
	for(var rkey in removables) {
		sessionStorage.removeItem(rkey);
	}
}
function backToHome() {
	removeGameSessionData();
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
function getPlaceBySN(sn) {
	var places = JSON.parse(sessionStorage.happygames_game_places);
	var newPlace = null;
	for(var pi in places) {
		var p = places[pi];
		if(p.placeSequenceNumber==sn) {
			newPlace = p;
		}
	}
	return newPlace;
}
function roll() {
	var rgh = document.getElementById("gohomesection");
	rgh.className = "steph1-collapsed";
	var rb = document.getElementById("rollsection");
	rb.className = "steph1-collapsed";
	
	var rollresult = rollDice(1, 6);
	
	sessionStorage.happygames_game_roll = rollresult;
	var player = JSON.parse(sessionStorage.happygames_game_actualPlayer);
	var oldPlaceSequenceNumber = player.placeSequenceNumber;
	var newPlaceSequenceNumber = ((oldPlaceSequenceNumber+rollresult-1)%16)+1;
	// TODO ha átment a starton akkor adjunk neki pénzt
	console.log("old: "+oldPlaceSequenceNumber);
	console.log("new: "+newPlaceSequenceNumber);
	sessionStorage.happygames_game_placeSN = newPlaceSequenceNumber;
	var oldplace = document.getElementById("place" + oldPlaceSequenceNumber + "player" + player.playerSequence);
	oldplace.className = "transparentcolor";
	var newplace = document.getElementById("place" + newPlaceSequenceNumber + "player" + player.playerSequence);
	newplace.className = ("token playercolor"+ player.playerSequence);
	
	var newPlace = getPlaceBySN(newPlaceSequenceNumber);
	
	var pt = document.getElementById("place_text");
	pt.value = pt.value.concat(" " + newPlace.placeSequenceNumber);
	var rt = document.getElementById("roll_text");
	rt.value = rt.value.concat(" " + rollresult);
	var rr = document.getElementById("rollresults");
	rr.className = "steph1";
	
	if (newPlace.type == "BuildingPlace") {
		if(newPlace.owner==0 && parseInt(sessionStorage.happygames_game_money)>newPlace.price) {
			var bs = document.getElementById("buysection");
			bs.className = "steph1 steph1-withbutton";
		} else if (newPlace.owner!=player.playerId && newPlace.owner!=0){
			var ps = document.getElementById("paysection");
			ps.className = "steph1 steph1-withbutton";
		} else {
			createBuyHouseTable();
			var bhs = document.getElementById("buyhousesection");
			bhs.className = "steph1 steph1-withbutton";
		}
	} else {
		createBuyHouseTable();
		var bhs2 = document.getElementById("buyhousesection");
		bhs2.className = "steph1 steph1-withbutton";
	}
}
function pay() {
	var rb = document.getElementById("paysection");
	rb.className = "steph1-collapsed";
	var building = getPlaceBySN(parseInt(sessionStorage.happygames_game_placeSN));
	var payment = building.totalPriceForNight;
	sessionStorage.happygames_game_money = (parseInt(sessionStorage.happygames_game_money)-payment);
	sessionStorage.happygames_game_isPayed = true;
	if (parseInt(sessionStorage.happygames_game_money)<0) {
		var ps = document.getElementById("sellbuildingsection");
		ps.className = "steph1 steph1-withbutton";
		createSellBuildingsTable();
		var soldbuildings = [];
		sessionStorage.setItem("happygames_game_actualPlayer_soldbuildings", JSON.stringify(soldbuildings));
	} else {
		var bs = document.getElementById("buyhousesection");
		bs.className = "steph1 steph1-withbutton";
	}
}
function sellb() {
	var rb = document.getElementById("sellbuildingsection");
	rb.className = "steph1-collapsed";
	var jsonarraySellable = JSON.parse(sessionStorage.happygames_game_actualPlayer_sellableBuildings);
	var soldBuildingsName = "";
	for (var bi in jsonarraySellable) {
		var building = jsonarraySellable[bi];
		var tickyb = document.getElementById(("ticky" + building.buildingId));
		if (tickyb.checked) {
			sessionStorage.happygames_game_money = (parseInt(sessionStorage.happygames_game_money) + building.price);
			var soldbuildingsAsString = sessionStorage.happygames_game_actualPlayer_soldbuildings;
			if (soldbuildingsAsString) {
				var soldbuildings = JSON.parse(soldbuildingsAsString);
				soldbuildings.push(building.buildingId);
				soldBuildingsName = soldBuildingsName+building.buildingName+", ";
				sessionStorage.happygames_game_actualPlayer_soldbuildings = JSON.stringify(soldbuildings);
				sessionStorage.happygames_game_isSold = true;
			}
		} 
	}

	var amt = document.getElementById("actmoney_text");
	amt.value = "Your money: "+ sessionStorage.happygames_game_money;
	
	if(parseInt(sessionStorage.happygames_game_money)>0) {
		createBuyHouseTable();
		
		var rr = document.getElementById("boughtsection");
		rr.className = "steph1";
		var bb = document.getElementById("buildingbuy_text");
		bb.value = "Sold building(s): "+soldBuildingsName;
		var bs = document.getElementById("buyhousesection");
		bs.className = "steph1 steph1-withbutton";
	} else {
		var bs = document.getElementById("finishsection");
		bs.className = "steph1 steph1-withbutton";
	}
}
function buyb() {
	var rb = document.getElementById("buysection");
	rb.className = "steph1-collapsed";
	
	var building = getPlaceBySN(parseInt(sessionStorage.happygames_game_placeSN));
	sessionStorage.happygames_game_isBuildingBought=true;
	console.log(sessionStorage.happygames_game_money);
	sessionStorage.happygames_game_money = (parseInt(sessionStorage.happygames_game_money) - building.price);
	console.log(sessionStorage.happygames_game_money);
	
	createBuyHouseTable();
	
	var amt = document.getElementById("actmoney_text");
	amt.value = "Your money: "+ sessionStorage.happygames_game_money;
	var bbt = document.getElementById("buildingbuy_text");
	console.log(JSON.stringify(building));
	bbt.value = "You bought this building: "+building.placeName;
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
function getBuildingPBuyBId(bid) {
	var array = JSON.parse(sessionStorage.happygames_game_actualPlayer_sellableBuildings);
	for(var b in array) {
		if((array[b].buildingId)==bid) {
			return (array[b]);
		}
	}
	return null;
}
function decreaseNumOfH(id) {
	var building = getBuildingPBuyBId(id);
	var textNum = document.getElementById(("boughtNumOfH" + id));
	var num = parseInt(textNum.value);
	if (num > 0) {
		sessionStorage.happygames_game_money = (parseInt(sessionStorage.happygames_game_money) + building.housePrice);

		var amt = document.getElementById("actmoney_text");
		amt.value = "Your money: "+ sessionStorage.happygames_game_money;
		textNum.value = (num - 1);
	}
}
function inreaseNumOfH(id, max) {
	var building = getBuildingPBuyBId(id);
	console.log(building);
	
	var textNum = document.getElementById(("boughtNumOfH" + id));
	var num = parseInt(textNum.value);
	console.log(building.housePrice);
	console.log(max);
	console.log(id);
	if (num < max && parseInt(sessionStorage.happygames_game_money)>building.housePrice) {
		sessionStorage.happygames_game_money = (parseInt(sessionStorage.happygames_game_money) - building.housePrice);

		var amt = document.getElementById("actmoney_text");
		amt.value = "Your money: "+ sessionStorage.happygames_game_money;
		textNum.value = (num + 1);
	}
}
function buyh() {
	var rb = document.getElementById("buyhousesection");
	rb.className = "steph1-collapsed";
	
	var rsoldbuildingsAsString = sessionStorage.happygames_game_actualPlayer_soldbuildings;
	var rsoldbuildings = [];
	if (rsoldbuildingsAsString) {
		rsoldbuildings = JSON.parse(rsoldbuildingsAsString);
	}

	var jsonarrayForHouses = JSON.parse(sessionStorage.happygames_game_actualPlayer_sellableBuildings);
	for (var bfh in jsonarrayForHouses) {
		var buildingFH = jsonarrayForHouses[bfh];
		if (rsoldbuildings.lastIndexOf(buildingFH.buildingId) == (-1)) {
			var bhnfb = JSON.parse(sessionStorage.happygames_game_boughtHouseNumberForBuildings);
			var hn = document.getElementById("boughtNumOfH" + buildingFH.buildingId);
			bhnfb.push({
				"buildingId":buildingFH.buildingId,
				"number":hn.value
			});
			sessionStorage.happygames_game_boughtHouseNumberForBuildings = JSON.stringify(bhnfb);
		}
	}
	
	var bs = document.getElementById("finishsection");
	bs.className = "steph1 steph1-withbutton";
}
function finish() {
	loadingActivate();
	var dataForServer = '[{"playerId":'+(JSON.parse(sessionStorage.happygames_game_actualPlayer)).playerId
				+',"roll":'+sessionStorage.happygames_game_roll
				+',"placeSequenceNumber":'+sessionStorage.happygames_game_placeSN
				+',"isBuildingBought":'+sessionStorage.happygames_game_isBuildingBought
				+',"isPayed":'+sessionStorage.happygames_game_isPayed
				+',"isSold":'+sessionStorage.happygames_game_isSold
				+',"soldBuildingsIds":'+sessionStorage.happygames_game_actualPlayer_soldbuildings
				+',"boughtHouseNumberForBuildings":'+sessionStorage.happygames_game_boughtHouseNumberForBuildings
				+'}]';
	console.log(dataForServer);
	$.ajax({
		type : "POST",
		data: dataForServer,
		dataType : "json",
		url : "/Monopoly/rest/gameapi/MakeStep"
	}).success(function(data) {
		var resp = JSON.parse(data);
		removeGameSessionData();
		if(resp.errorCode==0) {
			alert("You made your step.");
			window.location.href = "https://localhost:8443/Monopoly/pages/home.html";
		} else if(resp.errorCode==1) {
			alert("Sorry, you lose.");
			window.location.href = "https://localhost:8443/Monopoly/pages/home.html";
		} else if(resp.errorCode==2) {
			alert("Do not cheat! :)");
			window.location.reload();
		}
	}).error(function(e) {
		alert("Sorry, our server has problems.");
		console.log(e);
	});
}
// UI
function createSellBuildingsTable() {
	var tbody = document.getElementById("sellbuildingsbody");
	
	var jsonarray = JSON.parse(sessionStorage.happygames_game_actualPlayer_sellableBuildings);
	for (var bi in jsonarray) {
		var building = jsonarray[bi];
		var tr = document.createElement('tr');
		tbody.appendChild(tr);

		var td1 = document.createElement('td');
		td1.className = "scolumn";
		td1.textContent = building.buildingName;
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
		tickb.id = "ticky" + building.buildingId;
		td3.appendChild(tickb);
	}
}
function createBuyHouseTable() {
	var rsoldbuildingsAsString = sessionStorage.happygames_game_actualPlayer_soldbuildings;
	var rsoldbuildings = [];
	if (rsoldbuildingsAsString) {
		rsoldbuildings = JSON.parse(rsoldbuildingsAsString);
	}
	var tbody = document.getElementById("buybuildingsbody");

	var jsonarrayForHouses = JSON.parse(sessionStorage.happygames_game_actualPlayer_sellableBuildings);
	for (var bfh in jsonarrayForHouses) {
		var buildingFH = jsonarrayForHouses[bfh];
		if (rsoldbuildings.lastIndexOf(buildingFH.buildingId) == (-1)) {
			var tr = document.createElement('tr');
			tbody.appendChild(tr);

			var td1 = document.createElement('td');
			td1.className = "hcolumn";
			td1.textContent = buildingFH.buildingName;
			tbody.appendChild(td1);

			var td2 = document.createElement('td');
			td2.className = "hcolumn";
			td2.textContent = buildingFH.housePrice;
			tbody.appendChild(td2);

			var td3 = document.createElement('td');
			td3.className = "plusmin";
			tbody.appendChild(td3);

			var minus = document.createElement('i');
			minus.className = "fa fa-minus-circle";
			minus.onclick = decreaseNumOfH.bind(this, buildingFH.buildingId);
			td3.appendChild(minus);

			var num = document.createElement('input');
			num.className = "mini";
			num.type = "text";
			num.id = "boughtNumOfH" + buildingFH.buildingId;
			num.value = 0;
			num.disabled = true;
			td3.appendChild(num);

			var plus = document.createElement('i');
			plus.className = "fa fa-plus-circle";
			plus.onclick = inreaseNumOfH.bind(this, buildingFH.buildingId, buildingFH.maxHouseNumber);
			td3.appendChild(plus);
		}
	}
}

/******************************* Playerboard *******************************/
function getPlayerData(id) {
	alert(id);
}
function createMiniPlayers() {
	var basesec = document.getElementById("playersboardcontainer");
	var jsonarray = JSON.parse(sessionStorage.happygames_game_activePlayers);
	for (var pi in jsonarray) {
		var player = jsonarray[pi];
		var sec = document.createElement('section');
		sec.className = "miniplayer bordercolor"+player.playerSequence;
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
	var jsonarrayLosers = JSON.parse(sessionStorage.happygames_game_losersPlayers);
	for (var pli in jsonarrayLosers) {
		var loser = jsonarrayLosers[pli];
		var secl = document.createElement('section');
		secl.className = "miniplayer out";
		secl.id = loser.playerId;
		//secl.onclick = function () { getPlayerData(loser.playerId); };
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