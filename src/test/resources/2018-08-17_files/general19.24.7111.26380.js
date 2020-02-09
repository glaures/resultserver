if(typeof(ov) == 'undefined') {
var ov = {};
};
HTTP_GET_VARS = new Array();
var strGET = document.location.search.substr(1,document.location.search.length);
if (strGET != '') {
gArr = strGET.split('&');
for (i = 0; i < gArr.length; ++i) {
v = ''; vArr = gArr[i].split('=');
if (vArr.length > 1) { v = vArr[1]; }
HTTP_GET_VARS[unescape(vArr[0])] = unescape(v);
}
}
function getHttpParameter(v) {
if (!HTTP_GET_VARS[v]) { return 'undefined'; }
return HTTP_GET_VARS[v];
}
ov.voting = {
ajaxContainerID : '',ajax_root : 'https://votingrelaunch.kicker.de',ajax_reload : '',slider_boxwidth: 0,ovVotingMandantID: 0,ovVotingAltTitel: '',showPage : function (data) {
jQuery("#" + ov.voting.ajaxContainerID).empty().append(data);
(jQuery)('#VotingContentFocus').focus();
/* location.hash="#VotingContentFocus"; */
},submitVotingPrototype : function (event,myThis,votingGUID) {
/* stop form from submitting normally */
event.preventDefault();
if (votingGUID=='603e624d-727b-4266-a6cf-2f7626cf114a') {
ov.voting.ajaxContainerID = 'ajaxContainerSave50JBL' + votingGUID.replace(/-/g,"");
} else {
ov.voting.ajaxContainerID = 'voting_' + votingGUID;
}
if (document.URL.indexOf('source') && typeof $('source') != 'undefined') {
//source im formular setzen.
$('source').value = getHttpParameter('source');
}
if ($('VotingContentFocus')) {
$('VotingContentFocus').focus();
}
var actionUrl = myThis.action;
if (actionUrl.indexOf('http://') > -1) {
actionUrl = actionUrl.replace('http://','');
actionUrl = actionUrl.substr(actionUrl.indexOf('/'));
actionUrl = ov.voting.ajax_root + actionUrl;
}
var myAjax = new Ajax.Updater(ov.voting.ajaxContainerID,actionUrl,{ parameters: $(myThis.id).serialize(true),onCreate: function (response) {
// cross-origin resource sharing (CORS) fix
try {
var t = response.transport;
t.setRequestHeader = t.setRequestHeader.wrap(function (original,k,v) {
if (/^(accept|accept-language|content-language)$/i.test(k))
return original(k,v);
if (/^content-type$/i.test(k) &&
/^(application\/x-www-form-urlencoded|multipart\/form-data|text\/plain)(;.+)?$/i.test(v))
return original(k,v);
return;
});
} catch (e) { }
},onSuccess: function (response) {
var str = response.responseText;
var i = str.indexOf("<!-- ovVotingSaveDone -->");
if (i > -1) {
str = str.substr(i + 30,36);
ov.voting.WriteVotedCookie(str);
}
},evalScripts: true
});
return false;
},submitVotingJQuery : function (event,myThis,votingGUID) {
/* stop form from submitting normally */
event.preventDefault();
ov.voting.ajaxContainerID = 'voting_' + votingGUID;
(jQuery)('#VotingContentFocus').focus();
/* location.hash="#VotingContentFocus"; */
var myForm = jQuery(myThis);
var url = myForm.attr('action');
var params = {};
for (var i = 0; i < myThis.elements.length; i++) {
e = myThis.elements[i];
if (e.type == 'radio') {
if (e.checked) {
params[e.name] = e.value;
}
}
else if (e.type == 'checkbox') {
if (e.checked) {
if (params[e.name]) {
params[e.name] += ',' + e.value;
}
else {
params[e.name] = e.value;
}
}
}
else {
params[e.name] = e.value;
}
}
var reloadIcon = ov.voting.ajax_reload;
if (reloadIcon != null && reloadIcon.length > 0) {
jQuery("#" + ov.voting.ajaxContainerID).html(reloadIcon);
}
jQuery.post(url,params,ov.voting.showPage)
.done(function (data) {
var str = data;
var i = str.indexOf("<!-- ovVotingSaveDone -->");
if (i > -1) {
str = str.substr(i + 30,36);
ov.voting.WriteVotedCookie(str);
}
});
return false;
},NextQuestion : function () {
ov.voting.setNextStep(1);
},PrevQuestion : function () {
ov.voting.setNextStep(-1);
},LastQuestion : function () {
ov.voting.setNextStep(2);
},ShowErgebnis: function (votingGUID,activeposition,add2ContainerID) {
var votingContainerID = "ajaxContainer" + votingGUID;
if (add2ContainerID != null && add2ContainerID.length > 0) {
votingContainerID = votingContainerID + "_" + add2ContainerID;
}
var locationHostname = window.location.hostname;
var hash = window.location.hash;
if (hash != null && hash.length > 0) {
hash = "&hash=" + hash.replace("#","");
}
else {
hash = "";
}
var loadUrl = ov.voting.ajax_root + "/ajax.ashx?ajaxtype=votingContent&votingguid=" + votingGUID + "&activeposition=" + activeposition + "&locationHostname=" + locationHostname + hash;
votingContainerID = votingContainerID.replace(/-/g,"");
if (ov.voting.checkAjax()) {
ov.voting.setAjaxReloadIcon(votingContainerID);
var myAjax = new Ajax.Updater({ success: votingContainerID },loadUrl,{ method: 'get',withCredentials: true,onCreate: function (response) {
// cross-origin resource sharing (CORS) fix
try {
var t = response.transport;
t.setRequestHeader = t.setRequestHeader.wrap(function (original,k,v) {
if (/^(accept|accept-language|content-language)$/i.test(k))
return original(k,v);
if (/^content-type$/i.test(k) &&
/^(application\/x-www-form-urlencoded|multipart\/form-data|text\/plain)(;.+)?$/i.test(v))
return original(k,v);
return;
});
} catch (e) { }
},onFailure: function () {
var CONTAINER = $(votingContainerID);
CONTAINER.innerHTML = '';
var ErrorMsg = document.createElement('span');
ErrorMsg.id = votingContainerID+"_subcont_ErrorMsg";
ErrorMsg.className = "AjaxError";
ErrorMsg.innerHTML = "Fehler beim Laden";
ErrorMsg.style.fontSize = "0.88em";
if(document.getElementById(votingContainerID+"_subcont_ErrorMsg")) {
CONTAINER.removeChild(document.getElementById(votingContainerID+"_subcont_ErrorMsg"));
}
CONTAINER.appendChild(ErrorMsg,CONTAINER.firstChild);
ErrorMsg.style.padding = "0 0 0 "+Math.round((Math.round(CONTAINER.offsetWidth)/2)-(Math.round(ErrorMsg.offsetWidth)/2))+"px";
Effect.Fade(ErrorMsg.id,{ duration: 3.0 });
},evalScripts: true });
}
else if (ov.voting.checkJQuery(votingContainerID)) {
//jQuery("#" + votingContainerID).html(ov.voting.ajax_reload).load(loadUrl);
jQuery("#" + votingContainerID).html(ov.voting.ajax_reload);
jQuery.ajax({
url: loadUrl,crossDomain: true,dataType: "html",success: function (data) {
jQuery("#" + votingContainerID).html(data);
}
});
}
},setNextStep : function (value) {
var votingGUID = '';
if (ov.voting.checkAjax()) {
try {
votingGUID = $('votingContentGUID').value;
} catch (e) { }
$('NextStep').value = value;
}
else if (ov.voting.checkJQuery('NextStep')) {
try {
votingGUID = jQuery('#votingContentGUID')[0].value;
} catch (e) { }
var hiddenNextStep = jQuery('#NextStep');
hiddenNextStep.attr('value',value);
}
ov.voting.refreshIVW('21001;ki_voting','voting_content',votingGUID);
},refreshIVW: function (ivwCode,ctrlName,votingGUID) {
var manID = 0;
var altTitel = '';
var votingContainerID = "ajaxContainer" + votingGUID;
votingContainerID = votingContainerID.replace(/-/g,"");
if (ov.voting.checkAjax()) {
try {
manID = $('votingManID_' + votingGUID).value;
altTitel = $('votingAltTitel_' + votingGUID).value;
} catch (e) { }
}
else if (ov.voting.checkJQuery(votingContainerID)) {
try {
manID = jQuery('#votingManID_' + votingGUID)[0].value;
altTitel = jQuery('#votingAltTitel_' + votingGUID)[0].value;
} catch (e) { }
}
if (manID == 1) {
//kicker
if (typeof ovAjax != "undefined") {
if (altTitel.length > 0) {
ovAjax.lastControlname = ctrlName + '_' + altTitel;
} else {
ovAjax.lastControlname = ctrlName;
}
ovAjax.SetIVW(ivwCode);
}
else if (typeof ov != "undefined" && typeof ov.tracking != "undefined") {
ov.tracking.setIVW('');
}
}
else if (manID == 3) {
//alpin
//setivw("");
//Omniture
try {
var s = s_gi(ovs_account);
var ajaxControlname = "voting";
s.linkTrackVars = "prop5,prop3";
s.prop5 = "ajax_" + ajaxControlname;
s.tl(this,"o","ajax_" + ajaxControlname);
} catch (e) { }
//IVW
if (typeof iom != "undefined") {
iom.h(iam_data,1);
}
}
},VideoVote: function (votingroundGUID,answer) {
var loadUrl = ov.voting.ajax_root + '/ajax.ashx?ajaxtype=videovoting&votingroundguid=' + votingroundGUID + '&answer=' + answer + '&r=' + (Math.random() * 100000);
var votingContainerID = 'videoVotingRoundContainer_' + votingroundGUID;
$(votingContainerID).innerHTML=ov.voting.ajax_reload;
var myAjax = new Ajax.Updater(votingContainerID,loadUrl,{ method: 'get',onCreate: function (response) {
// cross-origin resource sharing (CORS) fix
try {
var t = response.transport;
t.setRequestHeader = t.setRequestHeader.wrap(function (original,k,v) {
if (/^(accept|accept-language|content-language)$/i.test(k))
return original(k,v);
if (/^content-type$/i.test(k) &&
/^(application\/x-www-form-urlencoded|multipart\/form-data|text\/plain)(;.+)?$/i.test(v))
return original(k,v);
return;
});
} catch (e) { }
},evalScripts: true
});
},// ---------------------------------------------------------------------------
//Widget:Frage der Woche
getVotingCookie : function (name) {
var dc = document.cookie;
var prefix = name + "=";
var begin = dc.indexOf("; " + prefix);
if (begin == -1) {
begin = dc.indexOf(prefix);
if (begin != 0) return null;
}
else {
begin += 2;
}
var end = document.cookie.indexOf(";",begin);
if (end == -1) end = dc.length;
return unescape(dc.substring(begin + prefix.length,end));
},checkAjax : function () {
var b = false;
try {
if (Ajax != null) {
b = true
}
}
catch (e) { }
return b;
},checkJQuery : function (votingContainerID) {
var b = false;
try {
if (jQuery("#" + votingContainerID) != null) {
b = true;
}
}
catch (e) { }
return b;
},loadVoting : function (votingGUID,votingRoundGuid,add2ContainerID,hash) {
if (ov.voting.ajax_root.indexOf('msn') > -1) {
// Do nothing!
}
else {
var votingContainerID = "ajaxContainer" + votingGUID;
if (add2ContainerID != null && add2ContainerID.length > 0) {
votingContainerID = votingContainerID + "_" + add2ContainerID;
}
var locationHostname = window.location.hostname;
var loadUrl = ov.voting.ajax_root + "/ajax.ashx?ajaxtype=votingContent&votingguid=" + votingGUID;
if (hash != null && hash.length > 0) {
loadUrl += "&hash=" + hash.replace("#","");
}
if (votingGUID == '141b05e2-ce9d-4092-8dd5-d9e76497051a') {
//Video-Voting
loadUrl += '&r=' + (Math.random() * 100000);
}
else {
loadUrl += "&locationHostname=" + locationHostname;
}
if (typeof (ovDreamTeamId) != "undefined") {
loadUrl += "&teamid=" + ovDreamTeamId;
}
if (window.location.search.toLowerCase().indexOf("teamid") > -1) {
loadUrl += "&teamid=" + ov.voting.getUrlVars()["teamid"];
}
votingContainerID = votingContainerID.replace(/-/g,"");
if (ov.voting.checkAjax()) {
ov.voting.setAjaxReloadIcon(votingContainerID);
var myAjax = new Ajax.Updater({ success: votingContainerID },loadUrl,{
method: 'get',withCredentials: true,onCreate: function (response) {
// cross-origin resource sharing (CORS) fix
try {
var t = response.transport;
t.setRequestHeader = t.setRequestHeader.wrap(function (original,k,v) {
if (/^(accept|accept-language|content-language)$/i.test(k))
return original(k,v);
if (/^content-type$/i.test(k) &&
/^(application\/x-www-form-urlencoded|multipart\/form-data|text\/plain)(;.+)?$/i.test(v))
return original(k,v);
return;
});
} catch (e) {
var err = e.message;
alert(err);
}
},onFailure: function () {
var CONTAINER = $(votingContainerID);
CONTAINER.innerHTML = '';
var ErrorMsg = document.createElement('span');
ErrorMsg.id = votingContainerID+"_subcont_ErrorMsg";
ErrorMsg.className = "AjaxError";
ErrorMsg.innerHTML = "Fehler beim Laden";
ErrorMsg.style.fontSize = "0.88em";
if(document.getElementById(votingContainerID+"_subcont_ErrorMsg")) {
CONTAINER.removeChild(document.getElementById(votingContainerID+"_subcont_ErrorMsg"));
}
CONTAINER.appendChild(ErrorMsg,CONTAINER.firstChild);
ErrorMsg.style.padding = "0 0 0 "+Math.round((Math.round(CONTAINER.offsetWidth)/2)-(Math.round(ErrorMsg.offsetWidth)/2))+"px";
Effect.Fade(ErrorMsg.id,{ duration: 3.0 });
},evalScripts: true
});
}
else if (ov.voting.checkJQuery(votingContainerID)) {
//Alpin,Nordbayern
var cookieValue = ov.voting.getCookie(votingRoundGuid + '_voted');
loadUrl = ov.voting.ajax_root + "/ajax.ashx?ajaxtype=votingContent&votingguid=" + votingGUID;
if (hash != null && hash.length > 0) {
loadUrl += "&hash=" + hash.replace("#","");
}
loadUrl += "&checked=" + cookieValue + "&locationHostname=" + locationHostname;
//jQuery("#" + votingContainerID).html(ov.voting.ajax_reload).load(loadUrl);
jQuery("#" + votingContainerID).html(ov.voting.ajax_reload);
jQuery.ajax({
url: loadUrl,crossDomain: true,xhrFields: {
withCredentials: true
},dataType: "html",success: function (data) {
jQuery("#" + votingContainerID).html(data);
}
});
}
}
},getCookie: function (name) {
var dc = document.cookie;
var prefix = name + "=";
var begin = dc.indexOf("; " + prefix);
if (begin == -1) {
begin = dc.indexOf(prefix);
if (begin != 0) return null;
}
else begin += 2;
var end = document.cookie.indexOf(";",begin);
if (end == -1) end = dc.length;
return unescape(dc.substring(begin + prefix.length,end));
},save50JBLVideoVoting : function (votingGUID,answerID) {
if (ov.voting.ajax_root.indexOf('msn') > -1) {
// Do nothing!
}
else {
var votingContainerID = "ajaxContainerSave50JBL" + votingGUID;
var loadUrl = ov.voting.ajax_root + "/ajax.ashx?ajaxtype=save50jblvideovoting&votingguid=" + votingGUID + "&answer=" + answerID;
loadUrl += '&r=' + (Math.random() * 100000);
votingContainerID = votingContainerID.replace(/-/g,"");
if (ov.voting.checkAjax()) {
ov.voting.setAjaxReloadIcon(votingContainerID);
var myAjax = new Ajax.Updater({ success: votingContainerID },loadUrl,{ method: 'get',onCreate: function (response) {
// cross-origin resource sharing (CORS) fix
try {
var t = response.transport;
t.setRequestHeader = t.setRequestHeader.wrap(function (original,k,v) {
if (/^(accept|accept-language|content-language)$/i.test(k))
return original(k,v);
if (/^content-type$/i.test(k) &&
/^(application\/x-www-form-urlencoded|multipart\/form-data|text\/plain)(;.+)?$/i.test(v))
return original(k,v);
return;
});
} catch (e) { }
},onSuccess: function () {
Shadowbox.init({ skipSetup: true });
Shadowbox.setup();
},onFailure:function(){
var CONTAINER = $(votingContainerID);
CONTAINER.innerHTML = '';
var ErrorMsg = document.createElement('span');
ErrorMsg.id = votingContainerID+"_subcont_ErrorMsg";
ErrorMsg.className = "AjaxError";
ErrorMsg.innerHTML = "Fehler beim Laden";
ErrorMsg.style.fontSize = "0.88em";
if(document.getElementById(votingContainerID+"_subcont_ErrorMsg")) {
CONTAINER.removeChild(document.getElementById(votingContainerID+"_subcont_ErrorMsg"));
}
CONTAINER.appendChild(ErrorMsg,CONTAINER.firstChild);
ErrorMsg.style.padding = "0 0 0 "+Math.round((Math.round(CONTAINER.offsetWidth)/2)-(Math.round(ErrorMsg.offsetWidth)/2))+"px";
Effect.Fade(ErrorMsg.id,{ duration: 3.0 });
},evalScripts: true });
}
else if (ov.voting.checkJQuery(votingContainerID)) {
//jQuery("#" + votingContainerID).html(ov.voting.ajax_reload).load(loadUrl);
jQuery("#" + votingContainerID).html(ov.voting.ajax_reload);
jQuery.ajax({
url: loadUrl,crossDomain: true,dataType: "html",success: function (data) {
jQuery("#" + votingContainerID).html(data);
}
});
}
}
},refresh50JBLUebersicht : function (votingGUID) {
parent.location.reload();
parent.Shadowbox.close();
},loadVotingErgebnisRedation : function (votingGUID,votingRoundGUID,activeposition) {
if (ov.voting.ajax_root.indexOf('msn') > -1) {
// Do nothing!
}
else {
var votingContainerID = "ajaxContainer" + votingGUID;
var locationHostname = window.location.hostname;
var loadUrl = ov.voting.ajax_root + "/ajax.ashx?ajaxtype=votingContent&forceErgebnis=true&votingguid=" + votingGUID + "&votingroundguid=" + votingRoundGUID + "&locationHostname=" + locationHostname;
var hash = window.location.hash;
if (hash != null && hash.length > 0) {
loadUrl += "&hash=" + hash.replace("#","");
}
if (activeposition)
loadUrl += "&activeposition=" + activeposition;
votingContainerID = votingContainerID.replace(/-/g,"");
if (ov.voting.checkAjax()) {
ov.voting.setAjaxReloadIcon(votingContainerID);
var myAjax = new Ajax.Updater({ success: votingContainerID },loadUrl,{ method: 'get',onCreate: function (response) {
// cross-origin resource sharing (CORS) fix
try {
var t = response.transport;
t.setRequestHeader = t.setRequestHeader.wrap(function (original,k,v) {
if (/^(accept|accept-language|content-language)$/i.test(k))
return original(k,v);
if (/^content-type$/i.test(k) &&
/^(application\/x-www-form-urlencoded|multipart\/form-data|text\/plain)(;.+)?$/i.test(v))
return original(k,v);
return;
});
} catch (e) { }
},onFailure: function () {
var CONTAINER = $(votingContainerID);
CONTAINER.innerHTML = '';
var ErrorMsg = document.createElement('span');
ErrorMsg.id = votingContainerID+"_subcont_ErrorMsg";
ErrorMsg.className = "AjaxError";
ErrorMsg.innerHTML = "Fehler beim Laden";
ErrorMsg.style.fontSize = "0.88em";
if(document.getElementById(votingContainerID+"_subcont_ErrorMsg")) {
CONTAINER.removeChild(document.getElementById(votingContainerID+"_subcont_ErrorMsg"));
}
CONTAINER.appendChild(ErrorMsg,CONTAINER.firstChild);
ErrorMsg.style.padding = "0 0 0 "+Math.round((Math.round(CONTAINER.offsetWidth)/2)-(Math.round(ErrorMsg.offsetWidth)/2))+"px";
Effect.Fade(ErrorMsg.id,{ duration: 3.0 });
},evalScripts: true });
}
else if (ov.voting.checkJQuery(votingContainerID)) {
//jQuery("#" + votingContainerID).html(ov.voting.ajax_reload).load(loadUrl);
jQuery("#" + votingContainerID).html(ov.voting.ajax_reload);
jQuery.ajax({
url: loadUrl,crossDomain: true,dataType: "html",success: function (data) {
jQuery("#" + votingContainerID).html(data);
}
});
}
}
},doAjax : function (votingGUID,loadUrl,ignorIVW) {
var votingContainerID = 'votingContainer_' + votingGUID;
if (ov.voting.checkAjax()) {
var elements = document.getElementsByClassName(votingContainerID);
if (elements != null && elements.length > 0) {
for (var i = 0; i < elements.length; i++) {
var newID = elements[i].id;
ov.voting.setAjaxReloadIcon(newID);
var myAjax = new Ajax.Updater(newID,loadUrl,{
method: 'get',onCreate: function (response) {
// cross-origin resource sharing (CORS) fix
try {
var t = response.transport;
t.setRequestHeader = t.setRequestHeader.wrap(function (original,k,v) {
if (/^(accept|accept-language|content-language)$/i.test(k))
return original(k,v);
if (/^content-type$/i.test(k) &&
/^(application\/x-www-form-urlencoded|multipart\/form-data|text\/plain)(;.+)?$/i.test(v))
return original(k,v);
return;
});
} catch (e) { }
},onSuccess: function (response) {
var str = response.responseText;
var i = str.indexOf("<!-- ovVotingSaveDone -->");
if (i > -1) {
str = str.substr(i + 30,36);
ov.voting.WriteVotedCookie(str);
}
},evalScripts: true
});
}
}
}
else if (ov.voting.checkJQuery(votingContainerID)) {
var elements = jQuery("." + votingContainerID);
elements.each(function () {
//$(this).css("border","9px solid red");
var elemID = "#" + this.id;
jQuery(elemID).html(ov.voting.ajax_reload);
jQuery.ajax({
url: loadUrl,crossDomain: true,dataType: "html",success: function (data) {
jQuery(elemID).html(data);
var str = data;
var i = str.indexOf("<!-- ovVotingSaveDone -->");
if (i > -1) {
str = str.substr(i + 30,36);
ov.voting.WriteVotedCookie(str);
}
}
});
});
}
if (ignorIVW == null || ignorIVW == undefined || ignorIVW == false) {
ov.voting.FdwRefresh(votingGUID);
}
},setAjaxReloadIcon : function (votingContainerID) {
if ($(votingContainerID)) {
$(votingContainerID).innerHTML+=ov.voting.ajax_reload;
}
},FdwCheck : function (votingGUID,votingRoundGUID,hash) {
var votingCookie = ov.voting.getVotingCookie(votingRoundGUID + "_voted");
var locationHostname = window.location.hostname;
if (hash != null && hash.length > 0) {
hash = "&hash=" + hash;
}
else {
hash = "";
}
if (votingCookie != null) {
var loadUrl = ov.voting.ajax_root + '/ajax.ashx?ajaxtype=widgetvoting&voted=true&votingguid=' + votingGUID + "&locationHostname=" + locationHostname + hash;
ov.voting.doAjax(votingGUID,loadUrl,true);
}
else {
var loadUrl = ov.voting.ajax_root + '/ajax.ashx?ajaxtype=widgetvoting&nocookiecheck=true&votingguid=' + votingGUID + "&locationHostname=" + locationHostname + hash;
ov.voting.doAjax(votingGUID,loadUrl,true);
}
},FdwBack: function (votingGUID,hash) {
var locationHostname = window.location.hostname;
if (hash != null && hash.length > 0) {
hash = "&hash=" + hash.replace("#","");
}
else {
hash = "";
}
var loadUrl = ov.voting.ajax_root + '/ajax.ashx?ajaxtype=widgetvoting&nocookiecheck=true&votingguid=' + votingGUID + "&locationHostname=" + locationHostname + hash;
ov.voting.doAjax(votingGUID,loadUrl);
},FdwResult: function (votingGUID,hash) {
var locationHostname = window.location.hostname;
if (hash != null && hash.length > 0) {
hash = "&hash=" + hash.replace("#","");
}
else {
hash = "";
}
var loadUrl = ov.voting.ajax_root + '/ajax.ashx?ajaxtype=widgetvoting&Zwischenergebnis=true&votingguid=' + votingGUID + "&locationHostname=" + locationHostname + hash;
ov.voting.doAjax(votingGUID,loadUrl);
},FdwVote : function (form,votingGUID,hash) {
var name;
var value;
for (i = 0; i < form.length; i++) {
name = form[i].name;
if (name == 'antwort') {
if (form[i].type == 'checkbox') {
if (form[i].checked) {
if (value != null && value.length > 0) {
value += ",";
} else {
value = "";
}
value += form[i].value;
}
} else if (form[i].type == 'radio') {
if (form[i].checked) {
value = form[i].value;
}
} else if (form[i].type == 'select-one') {
value = form[i].value;
}
}
}
var locationHostname = window.location.hostname;
if (hash != null && hash.length > 0) {
hash = "&hash=" + hash.replace("#","");
}
else {
hash = "";
}
var loadUrl = ov.voting.ajax_root + '/ajax.ashx?ajaxtype=widgetvoting&votingguid=' + votingGUID + '&userAntwort=' + value + "&locationHostname=" + locationHostname + hash;
ov.voting.doAjax(votingGUID,loadUrl);
},FdwRefresh: function (votingGUID) {
ov.voting.refreshIVW('21100;ki_fdw_box','voting_fdw',votingGUID);
},FdwNoRefresh : function ()
{ },MoveRightVideoVoting : function (moveThis) {
var myBox = $('slidcont');
var mySlider = $('myslider');
var newleft = parseInt(myBox.style.left) - moveThis;
if (newleft < -ov.voting.slider_boxwidth) {
newleft = -ov.voting.slider_boxwidth;
}
var sliderPos = parseInt(parseInt(newleft) / parseInt(ov.voting.slider_boxwidth) * -776);
if (parseInt(sliderPos) > 776) {
sliderPos = 776;
}
myBox.setStyle({ left: newleft + 'px' });
mySlider.style.left = parseInt(sliderPos) + 'px';
},MoveLeftVideoVoting : function(moveThis) {
var myBox = $('slidcont');
var mySlider = $('myslider');
var newleft = parseInt(myBox.style.left) + moveThis;
if (newleft > 0) {
newleft = 0;
}
myBox.setStyle({ left: newleft + 'px' });
mySlider.style.left = parseInt(parseInt(newleft) / parseInt(ov.voting.slider_boxwidth) * -776) + 'px';
},WriteVotedCookie: function (votingRoundGUID) {
var hostname = window.location.hostname;
var parts = hostname.split('.');
var sndleveldomain = parts.slice(-2).join('.');
var jetzt = new Date();
var heute = jetzt.getTime();
var dann = heute + (7 * 24 * 60 * 60 * 1000);
jetzt.setTime(dann);
document.cookie = votingRoundGUID + "_voted=true;Path=/;expires=" + jetzt.toGMTString() + ";domain=" + sndleveldomain;
},getUrlVars: function() {
var vars = {};
var parts = window.location.search.toLowerCase().replace(/[?&]+([^=&]+)=([^&]*)/gi,function(m,key,value) {
vars[key] = value;
});
return vars;
}
};
