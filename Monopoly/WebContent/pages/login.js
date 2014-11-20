/********************* Event handler functions *********************/

function signIn() {
    var nameinput = document.getElementById("lname");
    var passwdinput = document.getElementById("lpasswd");
    var jsonObject = [];
	
	jsonObject.push({
		"email" : nameinput.value,
		"password" : passwdinput.value
	});

	
	$.ajax({
	    type: "POST",
	    data: JSON.stringify(jsonObject),
	    url: "/Monopoly/rest/userapi/login",
	    success: function(data,textStatus,jqXHR) {
	    	console.log("***************success***************");
			console.log(data);
			console.log(textStatus);
			console.log(jqXHR);
		},
		error: function(jqXHR,textStatus,errorThrown ) {
	    	console.log("***************error***************");
			console.log(jqXHR);
			console.log(textStatus);
			console.log(errorThrown);
		},
		complete : function(jqXHR,textStatus) {
	    	console.log("***************complete***************");
			console.log(jqXHR);
			console.log(textStatus);
		}
	});
}
function signUp() {
    var nameinput = document.getElementById("name");
    var passwdinput = document.getElementById("passwd");
    var mailinput = document.getElementById("mail");
   
    var jsonObject = [];
	
	jsonObject.push({
		"email" : mailinput.value,
		"password" : passwdinput.value,
		"name" : nameinput.value
	});

	
	$.ajax({
	    type: "POST",
	    data: JSON.stringify(jsonObject),
	    url: "/Monopoly/rest/userapi/Registration"
	});
    alert(jsonObject);
}
function remind() {
	 var mailinput = document.getElementById("rmail");
  
	var jsonObject = [];
	
	jsonObject.push({
		"email" : mailinput.value
	});

	
	$.ajax({
	    type: "POST",
	    data: JSON.stringify(jsonObject),
	    url: "/Monopoly/rest/userapi/Reminder"
	});
	alert(jsonObject);
}

/********************* UI modification functions *********************/

function resetsecs(e) {
    var l = document.getElementById("loginsec");
    if (l != e) {
        l.className = "";
        l.style.display = 'none';
    }
    var r = document.getElementById("registrationsec");
    if (r != e) {
        r.className = "";
        r.style.display = 'none';
    }
    var rem = document.getElementById("remindersec");
    if (rem != e) {
        rem.className = "";
        rem.style.display = 'none';
    }
}
function reseticons(icon) {
    var iconb1 = document.getElementById("loginiconb");
    if (iconb1 != icon) {
        iconb1.className = "header";
    }
    var iconb2 = document.getElementById("registrationiconb");
    if (iconb2 != icon) {
        iconb2.className = "header";
    }
    var iconb3 = document.getElementById("remindericonb");
    if (iconb3 != icon) {
        iconb3.className = "header";
    }
}
function boxheight(height) {
    var box = document.getElementById("loginbox");
    box.style.transitionDuration = '0.6s';
    box.style.height = height;
}
function secvisible(e) {
    if (e.style.display != 'block') {
        e.style.display = 'block';
        e.className = "visiblesec";
    }
}
function selectSection(sec, icon) {
    resetsecs(sec);
    reseticons(icon);
    secvisible(sec);
    icon.className = "selectedHeader";
}
function loginsel() {
    boxheight('250px');
    var e = document.getElementById("loginsec");
    var icon = document.getElementById("loginiconb");
    selectSection(e, icon);
}
function registersel() {
    boxheight('300px');
    var e = document.getElementById("registrationsec");
    var icon = document.getElementById("registrationiconb");
    selectSection(e, icon);
}
function remindersel() {
    boxheight('170px');
    var e = document.getElementById("remindersec");
    var icon = document.getElementById("remindericonb");
    selectSection(e, icon);
}