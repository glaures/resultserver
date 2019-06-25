setTimeout(function(){
    'use strict';
    try{
        var stringifyFunc = null;
		if(window.JSON){
			stringifyFunc = window.JSON.stringify;
		} else {
			if(window.parent && window.parent.JSON){
				stringifyFunc = window.parent.JSON.stringify;
			}
		}
		if(!stringifyFunc){
			return;
		}
        var msg = {
            action: 'notifyBrandShieldAdEntityInformation',
            bsAdEntityInformation: {
                brandShieldId:'cfb3a35c12bd4394b59958c12fc23c19',
                comparisonItems:[{name : 'cmp', value : 15106341},{name : 'plmt', value : 15602675}]
            }
        };
        var msgString = stringifyFunc(msg);
        var bst2tWin = null;

        var findAndSendMessage = function() {
            if (!bst2tWin) {
                var frame = document.getElementById('bst2t_1561388349849686');
                if (frame) {
                    bst2tWin = frame.contentWindow;
                }
            }

            if (bst2tWin) {
                bst2tWin.postMessage(msgString, '*');
            }
        };

        findAndSendMessage();
        setTimeout(findAndSendMessage, 50);
        setTimeout(findAndSendMessage, 500);
    } catch(err){}
}, 10);var dvObj = $dvbsr;function np764531(g,i){function d(){function d(){function f(b,a){b=(i?"dvp_":"")+b;e[b]=a}var e={},a=function(b){for(var a=[],c=0;c<b.length;c+=2)a.push(String.fromCharCode(parseInt(b.charAt(c)+b.charAt(c+1),32)));return a.join("")},h=window[a("3e313m3937313k3f3i")];h&&(a=h[a("3g3c313k363f3i3d")],f("pltfrm",a));(function(){var a=e;e={};dvObj.registerEventCall(g,a,2E3,true)})()}try{d()}catch(f){}}try{dvObj.pubSub.subscribe(dvObj==window.$dv?"ImpressionServed":"BeforeDecisionRender",g,"np764531",d)}catch(f){}}
;np764531("cfb3a35c12bd4394b59958c12fc23c19",false);


try{__tagObject_callback_244586691865({ImpressionID:"cfb3a35c12bd4394b59958c12fc23c19", ServerPublicDns:"tps20515.doubleverify.com"});}catch(e){}
try{$dvbsr.pubSub.publish('BeforeDecisionRender', "cfb3a35c12bd4394b59958c12fc23c19");}catch(e){}
try{__verify_callback_244586691865({
ResultID:2,
Passback:"",
AdWidth:728,
AdHeight:90});}catch(e){}
try{$dvbsr.pubSub.publish('AfterDecisionRender', "cfb3a35c12bd4394b59958c12fc23c19");}catch(e){}
