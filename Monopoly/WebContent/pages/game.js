/**************************************************************************************************************************/
/*******************************************************  Load Data *******************************************************/
/**************************************************************************************************************************/

/**
 * Get the variable from the URL.
 * 
 * @param variable The searched item.
 * @returns The value or false if we could not find the item.
 */
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

/**
 * Send a post request to get the data for the game.
 * If we got them we store and show them at the right places.
 */
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
		var gameData = JSON.parse(data);
		var nrPlayerMail = getQueryVariable("email");
		var gameId = getQueryVariable("gameid");
		// If the player has not accepted the invitation yet ...
		if(gameData.playerStatus=="notAcceptedYet") {
			var baseGD="Game name: "+gameData.name+"\nOwner of the game: "+gameData.nameOfGameOwner;
			
			// ... we show a dialog to choose that she/he want to accept or refuse it
			// and send the right answer to the server.
			if(confirm(("Do you want to accept the invitation for this game?\n"+baseGD))) {
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
				$.ajax({
					type : "POST",
					data: '{"email":"'+nrPlayerMail+'","gameId":'+gameId+'}',
					dataType : "json",
					url : "/Monopoly/rest/gamemanagementapi/RefuseInvitationFromEmail"
				}).success(function() {
					alert("You refused the invitation.\nWe will redirect you to the login page.");
				}).complete(function() {
					sessionStorage.clear();
					window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
				});
			}
		} else if(gameData.gameStatus=="init") {
			// If the game is under initialization we redirect the user to the login page.
			alert("Sorry, this game is under initialization.\nWe will redirect you to the login page.");
			sessionStorage.clear();
			window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
		} else if(gameData.gameStatus=="finished") {
			// If the game is over we also redirect the user to the login page.
			alert("Sorry, this game has been finished.\nWe will redirect you to the login page.");
			sessionStorage.clear();
			window.location.href = "https://localhost:8443/Monopoly/pages/login.html";
		} else {
			// If the game is in progress we store the game data.
			void(sessionStorage.happygames_game_name = gameData.name);
			void(sessionStorage.happygames_game_money = gameData.actualPlayer.money);
			void(sessionStorage.happygames_game_id = gameData.id);
			void(sessionStorage.happygames_game_ownerOfGame = gameData.nameOfGameOwner);
			void(sessionStorage.happygames_game_actualPlayer = JSON.stringify(gameData.actualPlayer));
			void(sessionStorage.happygames_game_actualPlayer_sellableBuildings = JSON.stringify(gameData.actualPlayer.ownedBuildings));
			void(sessionStorage.happygames_game_places = JSON.stringify(gameData.places));
			void(sessionStorage.happygames_game_activePlayers = JSON.stringify(gameData.acceptedPlayers));
			void(sessionStorage.happygames_game_losersPlayers = JSON.stringify(gameData.loserPlayers));
			
			// Set default values to the other necessary data.
			void(sessionStorage.happygames_game_roll = 0);
			void(sessionStorage.happygames_game_placeSN = 0);
			void(sessionStorage.happygames_game_isBuildingBought = false);
			void(sessionStorage.happygames_game_isPayed = false);
			void(sessionStorage.happygames_game_isSold = false);
			void(sessionStorage.happygames_game_actualPlayer_soldbuildings = "[]");
			void(sessionStorage.happygames_game_boughtHouseNumberForBuildings = "[]");

			// Set the profile page data.
			var gamenameheader = document.getElementById("gamename");
			void(gamenameheader.textContent = gameData.name);
			var ownerspan = document.getElementById("gd-owner");
			void(ownerspan.textContent = gameData.nameOfGameOwner);
			var youspan = document.getElementById("gd-you");
			void(youspan.textContent = (gameData.isActualPlayer?gameData.actualPlayer.name:nrPlayerMail));
			
			// Set the money value.
			var amt = document.getElementById("actmoney_text");
			amt.value = "Your money: "+ sessionStorage.happygames_game_money;
			
			if(gameData.isActualPlayer) {
				// If the opener is the actual player we show the roll button.
				var rb = document.getElementById("rollsection");
				rb.className = "steph1 steph1-withbutton";
			}
			if(sessionStorage.getItem("happygames_basic_username")!=null && sessionStorage.getItem("happygames_basic_username")!="") {
				// If the opener is a registered user we show the home button.
				var rgh = document.getElementById("gohomesection");
				rgh.className = "steph1 steph1-withbutton";
			}
			
			// Initialize the game board ...
			createGameBoard();
			// ... and the players' section.
			createMiniPlayers();
			loadingDeactivate();
		}
	});
}




/**************************************************************************************************************************/
/******************************************************* Game Board *******************************************************/
/**************************************************************************************************************************/

/**
 * Show the selected place data at the detailed information card.
 * 
 * @param id The selected place's id.
 */
function getPlaceData(id) {
	var gd= document.getElementById("gamedataspec");
	gd.className = "dataspec dataspec-unvisible";
	var pd= document.getElementById("playerdataspec");
	pd.className = "dataspec dataspec-unvisible";
	var jsonarray = JSON.parse(sessionStorage.happygames_game_places);
	for (var pi in jsonarray) {
		var place = jsonarray[pi];
		if(place.placeId==id) {
			// If we found the place we set all of the data.
			void(document.getElementById("buildingname").innerHTML=place.placeName);
			void(document.getElementById("bd-bprice").innerHTML=place.price);
			void(document.getElementById("bd-hprice").innerHTML=place.hprice);
			void(document.getElementById("bd-bsprice").innerHTML=place.baseSleepPrice);
			void(document.getElementById("bd-hsprice").innerHTML=place.houseSleepPrice);
			void(document.getElementById("bd-owner").innerHTML=place.ownerName);
			void(document.getElementById("bd-houses").innerHTML=place.houseNumber);
			void(document.getElementById("bd-aprice").innerHTML=place.totalPriceForNight);
		}
	}
	var base= document.getElementById("buildingdataspec");
	base.className = "dataspec";
}

/**
 * Initialize the game board places.
 */
function createGameBoard() {
	var jsonarray = JSON.parse(sessionStorage.happygames_game_places);
	for (var pi in jsonarray) {
		var place = jsonarray[pi];
		var basesec = document.getElementById(("place" + (place.placeSequenceNumber-1)));
		if (place.type == "BuildingPlace") {
			basesec.onclick = getPlaceData.bind(this, place.placeId);
		}

		// Set the basic place data like name and number.
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


		// Create a table for players tokens
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

		// Create cells for players' tokens.
		for (var i = 0; i < 4; i++) {
			var td1 = document.createElement('td');
			td1.id = "place" + place.placeSequenceNumber + "player" + i;
			td1.className = "transparentcolor";
			tr1.appendChild(td1);
			var td2 = document.createElement('td');
			td2.id = "place" + place.placeSequenceNumber + "player" + (i+4);
			td2.className = "transparentcolor";
			tr2.appendChild(td2);
			if (place.playersOnPlace.length > 0) {
				for (var playeri in place.playersOnPlace) {
					var player = place.playersOnPlace[playeri];
					
					// Color the right cell if the player is here.
					if (player.playerSequence == i) {
						td1.className = ("token playercolor"+i);
						td1.title = player.userName;
					} else if (player.playerSequence == (i + 4)) {
						td2.className = ("token playercolor"+(i+4));
						td2.title = player.userName;
					}
				}
			}
		}
	}
}




/**************************************************************************************************************************/
/******************************************************* Game Steps *******************************************************/
/**************************************************************************************************************************/

/**
 * Remove all the game data from the session.
 */
function removeGameSessionData() {
	var removables = [];
	console.log("Keys:");
	for(var i=0; i<sessionStorage.length; i++) {
		var key = sessionStorage.key(i);
		console.log(" - "+key);
		if(key.indexOf("happygames_game_")!=-1) {
			removables.push(key);
			console.log("   can remove");
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

// Simulate dice.
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

/**
 * The roll button's onClick event handler.
 * We simulate a roll, set the player new place (also show on the gameboard it),
 * give some money to him/her if necessary and calculate that he/she has to pay,
 * can buy building or just houses.
 */
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
	// If the player go throught the start place we give some money.
	if(oldPlaceSequenceNumber>newPlaceSequenceNumber) {
		sessionStorage.happygames_game_money = (parseInt(sessionStorage.happygames_game_money)+1000);

		var amt = document.getElementById("actmoney_text");
		amt.value = "Your money: "+ sessionStorage.happygames_game_money;
	}
	// Store the new place.
	sessionStorage.happygames_game_placeSN = newPlaceSequenceNumber;
	// Show the new place on the gameboard.
	var oldplace = document.getElementById("place" + oldPlaceSequenceNumber + "player" + player.playerSequence);
	oldplace.className = "transparentcolor";
	var newplace = document.getElementById("place" + newPlaceSequenceNumber + "player" + player.playerSequence);
	newplace.className = ("token playercolor"+ player.playerSequence);
	
	var newPlace = getPlaceBySN(newPlaceSequenceNumber);
	// Show the new place in the step section also.
	var pt = document.getElementById("place_text");
	pt.value = pt.value.concat(" " + newPlace.placeSequenceNumber);
	var rt = document.getElementById("roll_text");
	rt.value = rt.value.concat(" " + rollresult);
	var rr = document.getElementById("rollresults");
	rr.className = "steph1";
	
	// Decide what the user should do next.
	if (newPlace.type == "BuildingPlace") {
		if(newPlace.owner==0 && parseInt(sessionStorage.happygames_game_money)>newPlace.price) {
			// - can buy the finish place
			var bs = document.getElementById("buysection");
			bs.className = "steph1 steph1-withbutton";
		} else if (newPlace.owner!=player.playerId && newPlace.owner!=0){
			// - should pay
			var ps = document.getElementById("paysection");
			ps.className = "steph1 steph1-withbutton";
		} else {
			// - or just houses
			createBuyHouseTable();
			var bhs = document.getElementById("buyhousesection");
			bhs.className = "steph1 steph1-withbutton";
		}
	} else {
		// If he/she does not step on a building place he/she only can buy houses to him/her buildings.
		createBuyHouseTable();
		var bhs2 = document.getElementById("buyhousesection");
		bhs2.className = "steph1 steph1-withbutton";
	}
}

/**
 * The pay button's onClick event handler.
 * The player should pay the night tax and if he/she has not enough money we offer
 * that sell some buildings. If he/she has enough money we show the house buying table.
 */
function pay() {
	var rb = document.getElementById("paysection");
	rb.className = "steph1-collapsed";
	var building = getPlaceBySN(parseInt(sessionStorage.happygames_game_placeSN));
	var payment = building.totalPriceForNight;
	sessionStorage.happygames_game_money = (parseInt(sessionStorage.happygames_game_money)-payment);

	var amt = document.getElementById("actmoney_text");
	amt.value = "Your money: "+ sessionStorage.happygames_game_money;
	
	sessionStorage.happygames_game_isPayed = true;
	if (parseInt(sessionStorage.happygames_game_money)<0) {
		var ps = document.getElementById("sellbuildingsection");
		ps.className = "steph1 steph1-withbutton";
		createSellBuildingsTable();
		var soldbuildings = [];
		sessionStorage.setItem("happygames_game_actualPlayer_soldbuildings", JSON.stringify(soldbuildings));
	} else {
		createBuyHouseTable();
		var bs = document.getElementById("buyhousesection");
		bs.className = "steph1 steph1-withbutton";
	}
}

/**
 * The sell buildings button's onClick event handler.
 * If the player select some building to sell we take away them and give the right money to the player.
 * If he/she has not enough money yet we only show the finish button.
 */
function sellb() {
	var rb = document.getElementById("sellbuildingsection");
	rb.className = "steph1-collapsed";
	var jsonarraySellable = JSON.parse(sessionStorage.happygames_game_actualPlayer_sellableBuildings);
	var soldBuildingsName = "";
	// Set the sold buildings from the table.
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

	// Show the new money value.
	var amt = document.getElementById("actmoney_text");
	amt.value = "Your money: "+ sessionStorage.happygames_game_money;
	
	// Decide what the player can do next.
	if(parseInt(sessionStorage.happygames_game_money)>0) {
		// If have any money than show house buying table ...
		createBuyHouseTable();
		
		var rr = document.getElementById("boughtsection");
		rr.className = "steph1";
		var bbt = document.getElementById("buildingbuy_text");
		bbt.value = "Sold building(s): "+soldBuildingsName;
		var bs = document.getElementById("buyhousesection");
		bs.className = "steph1 steph1-withbutton";
	} else {
		// ... else show finish button.
		var bs = document.getElementById("finishsection");
		bs.className = "steph1 steph1-withbutton";
	}
}

/**
 * The buy building button's onClick event handler.
 * Store that the player want to buy the building on which he/she steps.
 * And take the building price away from him/her money.
 */
function buyb() {
	var rb = document.getElementById("buysection");
	rb.className = "steph1-collapsed";
	
	var building = getPlaceBySN(parseInt(sessionStorage.happygames_game_placeSN));
	sessionStorage.happygames_game_isBuildingBought=true;
	sessionStorage.happygames_game_money = (parseInt(sessionStorage.happygames_game_money) - building.price);
	
	createBuyHouseTable();
	
	var amt = document.getElementById("actmoney_text");
	amt.value = "Your money: "+ sessionStorage.happygames_game_money;
	var bbt = document.getElementById("buildingbuy_text");
	void(bbt.value = "You bought this building: "+building.placeName);
	var rr = document.getElementById("boughtsection");
	rr.className = "steph1";
	var bs = document.getElementById("buyhousesection");
	bs.className = "steph1 steph1-withbutton";
}

/**
 * The not buy building button's onClick event handler.
 * Show the buy houses table.
 */
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

/**
 * Decrease the selected building's house number if we can: 
 * house number cannot be lower than zero.
 * 
 * @param id The selected building place's id.
 */
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

/**
 * Increase the selected building's house number if the player
 * has enough money and can be more house on the building place.
 * 
 * @param id The selected building place's id.
 */
function inreaseNumOfH(id, max) {
	var building = getBuildingPBuyBId(id);
	
	var textNum = document.getElementById(("boughtNumOfH" + id));
	var num = parseInt(textNum.value);
	if (num < max && parseInt(sessionStorage.happygames_game_money)>=building.housePrice) {
		sessionStorage.happygames_game_money = (parseInt(sessionStorage.happygames_game_money) - building.housePrice);

		var amt = document.getElementById("actmoney_text");
		amt.value = "Your money: "+ sessionStorage.happygames_game_money;
		textNum.value = (num + 1);
	}
}

/**
 * The buy houses button's onClick event handler.
 * Store all the buyed houses' number which was set by the player.
 */
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

/**
 * The finish button's onClick event handler. Post the step to the server.
 * Send all of step data to the server and show
 */
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
	$.ajax({
		type : "POST",
		data: dataForServer,
		dataType : "json",
		url : "/Monopoly/rest/gameapi/MakeStep"
	}).success(function(data) {
		var resp = JSON.parse(data);
		removeGameSessionData();
		if(resp.errorCode==0) {
			alert("You made your step.\nWe will redirect you to the login page.");
			sessionStorage.clear();
			window.location.href = "https://localhost:8443/Monopoly/pages/home.html";
		} else if(resp.errorCode==1) {
			alert("Sorry, you lose.\nWe will redirect you to the login page.");
			sessionStorage.clear();
			window.location.href = "https://localhost:8443/Monopoly/pages/home.html";
		} else if(resp.errorCode==2) {
			alert("Do not cheat! :)\nWe will reload the page and you can try again the step.");
			window.location.reload();
		}
	}).error(function(e) {
		alert("Sorry, our server has problems.");
	});
}

// Table creators
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




/**************************************************************************************************************************/
/******************************************************  Playerboard ******************************************************/
/**************************************************************************************************************************/

/**
 * Show basic game data.
 */
function basicGameData() {
	var base= document.getElementById("buildingdataspec");
	base.className = "dataspec dataspec-unvisible";
	var base= document.getElementById("playerdataspec");
	base.className = "dataspec dataspec-unvisible";
	var base= document.getElementById("gamedataspec");
	base.className = "dataspec";
}

/**
 * Show the selected player's data which get from the server.
 * Not implemented yet.
 * 
 * @param id The selected player's id.
 */
function getPlayerData(id) { }

/**
 * Create mini player cards at the players' section.
 */
function createMiniPlayers() {
	var basesec = document.getElementById("playersboardcontainer");
	var jsonarray = JSON.parse(sessionStorage.happygames_game_activePlayers);
	// Cards with data for the players who have not lost.
	for (var pi in jsonarray) {
		var player = jsonarray[pi];
		var sec = document.createElement('section');
		// set the border color for the player color.
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
	
	// Cards with name and loser stamp (in css) for the losers.
	var jsonarrayLosers = JSON.parse(sessionStorage.happygames_game_losersPlayers);
	for (var pli in jsonarrayLosers) {
		var loser = jsonarrayLosers[pli];
		var secl = document.createElement('section');
		secl.className = "miniplayer out";
		secl.id = loser.playerId;
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




/**************************************************************************************************************************/
/********************************************************  Loading ********************************************************/
/**************************************************************************************************************************/

function loadingActivate() {
	var loading=document.getElementById("loading");
	loading.className = "loading";
}
function loadingDeactivate() {
	var loading=document.getElementById("loading");
	loading.className = "loading invisible";
}