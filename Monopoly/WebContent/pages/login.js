﻿/**************************************************************************************************************************/
/************************************************  Event handler functions ************************************************/
/**************************************************************************************************************************/

function signIn() {
    var nameinput = document.getElementById("lname");
    var passwdinput = document.getElementById("lpasswd");
	if(nameinput.checkValidity() && passwdinput.checkValidity()) {
		loadingActivate();
	    var jsonObject = [];
		
		jsonObject.push({
			"userName" : nameinput.value,
			"password" : passwdinput.value
		});
		$.ajax({
		    type: "POST",
		    data: JSON.stringify(jsonObject),
		    url: "/Monopoly/rest/userapi/Login",
		    success: function(data,textStatus,jqXHR) {
				sessionStorage.happygames_basic_username = nameinput.value;
				window.location.href = "https://localhost:8443/Monopoly/pages/home.html";
			},
			error: function(jqXHR,textStatus,errorThrown ) {
				console.log(jqXHR);
				sessionStorage.errorcode = jqXHR;
				console.log(textStatus);
				console.log(errorThrown);
		    	loadingDeactivate();
			}
		});
	}
}
function signUp() {
    var nameinput = document.getElementById("name");
    var passwdinput = document.getElementById("passwd");
    var mailinput = document.getElementById("mail");
    if(nameinput.validity.valid && passwdinput.validity.valid && mailinput.validity.valid) {
	    loadingActivate();
		var jsonObject = [];
		
	    jsonObject.push({
			"email" : mailinput.value,
			"password" : passwdinput.value,
			"name" : nameinput.value
		});
	
		
		$.ajax({
		    type: "POST",
		    data: JSON.stringify(jsonObject),
		    url: "/Monopoly/rest/userapi/Registration",
		    success: function() {
		    	var nameinput = document.getElementById("name");
		        var passwdinput = document.getElementById("passwd");
		        var mailinput = document.getElementById("mail");
		        nameinput.vlue = "";
		        passwdinput.vlue = "";
		        mailinput.vlue = "";
		        loginsel();
			},
		    complete: function() {
				loadingDeactivate();
			}
		});
    }
}
function remind() {
	 var mailinput = document.getElementById("rmail");
	 if(mailinput.validity.valid) {
		    loadingActivate();
		var jsonObject = [];

		jsonObject.push({
			"email" : mailinput.value
		});

		$.ajax({
			type : "POST",
			data : JSON.stringify(jsonObject),
			url : "/Monopoly/rest/userapi/Remind",
			succes: function() {
				alert("We sent you the reminder.");
			},
			error: function() {
				alert("Sorry, we can not send you reminder.");
			},
			complete: function() {
				loadingDeactivate();
			}
		});
	 }
}

/**************************************************************************************************************************/
/***********************************************  UI modification functions ***********************************************/
/**************************************************************************************************************************/

function loadingActivate() {
	var loading=document.getElementById("loading");
	loading.className = "loading";
}
function loadingDeactivate() {
	var loading=document.getElementById("loading");
	loading.className = "loading invisible";
}

/**
 * Reset the other sections.
 * 
 * @param e
 */
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

/**
 * Reset the other icons.
 * 
 * @param icon
 */
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

/**
 * Set the box height to the content.
 * 
 * @param height
 */
function boxheight(height) {
    var box = document.getElementById("loginbox");
    box.style.transitionDuration = '0.6s';
    box.style.height = height;
}

/**
 * Set the section visible.
 * 
 * @param e
 */
function secvisible(e) {
    if (e.style.display != 'block') {
        e.style.display = 'block';
        e.className = "visiblesec";
    }
}

// Set the selected section to active.
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