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