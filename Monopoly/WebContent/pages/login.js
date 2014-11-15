﻿function signIn(nameID, passwdID) {
    var nameinput = document.getElementById(nameID);
    var passwdinput = document.getElementById(passwdID);
    var body = document.getElementById("body");
    body.innerHTML = "New line";
    window.confirm(nameinput.value + ": " + passwdinput.value);
}

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