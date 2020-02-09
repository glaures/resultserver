(function (cjs, an) {

var p; // shortcut to reference prototypes
var lib={};var ss={};var img={};
lib.ssMetadata = [];


// symbols:



(lib.bg_01_off = function() {
	this.initialize(img.bg_01_off);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,300,250);


(lib.bg_01_on = function() {
	this.initialize(img.bg_01_on);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,300,250);


(lib.bg_02 = function() {
	this.initialize(img.bg_02);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,300,250);


(lib.btn_cta = function() {
	this.initialize(img.btn_cta);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,120,25);


(lib.headline_01 = function() {
	this.initialize(img.headline_01);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,101,45);


(lib.headline_02 = function() {
	this.initialize(img.headline_02);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,144,45);


(lib.headline_04 = function() {
	this.initialize(img.headline_04);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,169,22);


(lib.hyundai_logo_blue = function() {
	this.initialize(img.hyundai_logo_blue);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,109,15);


(lib.hyundai_logo_white = function() {
	this.initialize(img.hyundai_logo_white);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,109,15);


(lib.legal = function() {
	this.initialize(img.legal);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,239,165);


(lib.stoerer = function() {
	this.initialize(img.stoerer);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,141,48);


(lib.stoerer_6d = function() {
	this.initialize(img.stoerer_6d);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,146,42);


(lib.subline = function() {
	this.initialize(img.subline);
}).prototype = p = new cjs.Bitmap();
p.nominalBounds = new cjs.Rectangle(0,0,175,30);// helper functions:

function mc_symbol_clone() {
	var clone = this._cloneProps(new this.constructor(this.mode, this.startPosition, this.loop));
	clone.gotoAndStop(this.currentFrame);
	clone.paused = this.paused;
	clone.framerate = this.framerate;
	return clone;
}

function getMCSymbolPrototype(symbol, nominalBounds, frameBounds) {
	var prototype = cjs.extend(symbol, cjs.MovieClip);
	prototype.clone = mc_symbol_clone;
	prototype.nominalBounds = nominalBounds;
	prototype.frameBounds = frameBounds;
	return prototype;
	}


(lib.subline_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.subline();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.subline_1, new cjs.Rectangle(0,0,175,30), null);


(lib.stoerer_6d_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.stoerer_6d();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.stoerer_6d_1, new cjs.Rectangle(0,0,146,42), null);


(lib.stoerer_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.stoerer();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.stoerer_1, new cjs.Rectangle(0,0,141,48), null);


(lib.overlay = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.shape = new cjs.Shape();
	this.shape.graphics.f("rgba(0,0,0,0.749)").s().p("A3bTiMAAAgnDMAu3AAAMAAAAnDg");
	this.shape.setTransform(150,125);

	this.timeline.addTween(cjs.Tween.get(this.shape).wait(1));

}).prototype = getMCSymbolPrototype(lib.overlay, new cjs.Rectangle(0,0,300,250), null);


(lib.logo_white = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Ebene 1
	this.instance = new lib.hyundai_logo_white();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.logo_white, new cjs.Rectangle(0,0,109,15), null);


(lib.logo_blue = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Ebene 1
	this.instance = new lib.hyundai_logo_blue();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.logo_blue, new cjs.Rectangle(0,0,109,15), null);


(lib.legal_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.legal();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.legal_1, new cjs.Rectangle(0,0,239,165), null);


(lib.headline_04_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.headline_04();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.headline_04_1, new cjs.Rectangle(0,0,169,22), null);


(lib.headline_02_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.headline_02();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.headline_02_1, new cjs.Rectangle(0,0,144,45), null);


(lib.headline_01_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.headline_01();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.headline_01_1, new cjs.Rectangle(0,0,101,45), null);


(lib.frame_300x250 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Ebene 1
	this.shape = new cjs.Shape();
	this.shape.graphics.f("#F6F3F2").s().p("A3bTiMAAAgnDIBuAAIAAedMAraAAAIAA+dIBvAAMAAAAnDg");
	this.shape.setTransform(-150,-125);

	this.timeline.addTween(cjs.Tween.get(this.shape).wait(1));

}).prototype = getMCSymbolPrototype(lib.frame_300x250, new cjs.Rectangle(-300,-250,300,250), null);


(lib.btn_cta_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.btn_cta();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.btn_cta_1, new cjs.Rectangle(0,0,120,25), null);


(lib.bg_02_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.bg_02();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.bg_02_1, new cjs.Rectangle(0,0,300,250), null);


(lib.bg_01_on_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.bg_01_on();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.bg_01_on_1, new cjs.Rectangle(0,0,300,250), null);


(lib.bg_01_off_1 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// Layer_1
	this.instance = new lib.bg_01_off();
	this.instance.parent = this;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = getMCSymbolPrototype(lib.bg_01_off_1, new cjs.Rectangle(0,0,300,250), null);


(lib.frame_ani = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{frameup:1});

	// timeline functions:
	this.frame_0 = function() {
		this.stop();
	}
	this.frame_16 = function() {
		this.stop();
	}

	// actions tween:
	this.timeline.addTween(cjs.Tween.get(this).call(this.frame_0).wait(16).call(this.frame_16).wait(6));

	// maske (mask)
	var mask = new cjs.Shape();
	mask._off = true;
	var mask_graphics_1 = new cjs.Graphics().p("Ag2GQIAAsfIBtAAIAAMfg");
	var mask_graphics_2 = new cjs.Graphics().p("Ag2MgIAA4/IBtAAIAAY/g");
	var mask_graphics_3 = new cjs.Graphics().p("Ag2TiMAAAgnDIBtAAMAAAAnDg");
	var mask_graphics_4 = new cjs.Graphics().p("AkmTiMAAAgnDIBuAAIAAedIHfAAIAAImg");
	var mask_graphics_5 = new cjs.Graphics().p("ApSTiMAAAgnDIBuAAIAAedIQ3AAIAAImg");
	var mask_graphics_6 = new cjs.Graphics().p("At+TiMAAAgnDIBuAAIAAedIaPAAIAAImg");
	var mask_graphics_7 = new cjs.Graphics().p("AyqTiMAAAgnDIBuAAIAAedMAjnAAAIAAImg");
	var mask_graphics_8 = new cjs.Graphics().p("A3bTiMAAAgnDIBuAAIAAedMAtJAAAIAAImg");
	var mask_graphics_9 = new cjs.Graphics().p("A3bTiMAAAgnDIBuAAIAAedMArbAAAIAAm4IBuAAIAAPeg");
	var mask_graphics_10 = new cjs.Graphics().p("A3bTiMAAAgnDIBuAAIAAedMArbAAAIAAtHIBuAAIAAVtg");
	var mask_graphics_11 = new cjs.Graphics().p("A3bTiMAAAgnDIBuAAIAAedMArbAAAIAAxzIBuAAIAAaZg");
	var mask_graphics_12 = new cjs.Graphics().p("A3bTiMAAAgnDIBuAAIAAedMArbAAAIAA2fIBuAAIAAfFg");
	var mask_graphics_13 = new cjs.Graphics().p("A3bTiMAAAgnDIBuAAIAAedMArbAAAIAA5nIBuAAMAAAAiNg");
	var mask_graphics_14 = new cjs.Graphics().p("A3bTiMAAAgnDIBuAAIAAedMArbAAAIAA7LIBuAAMAAAAjxg");
	var mask_graphics_15 = new cjs.Graphics().p("A3bTiMAAAgnDIBuAAIAAedMArbAAAIAA8vIBuAAMAAAAlVg");
	var mask_graphics_16 = new cjs.Graphics().p("A3bTiMAAAgnDIBuAAIAAedMArbAAAIAA+dIBuAAMAAAAnDg");

	this.timeline.addTween(cjs.Tween.get(mask).to({graphics:null,x:0,y:0}).wait(1).to({graphics:mask_graphics_1,x:-294.5,y:-210}).wait(1).to({graphics:mask_graphics_2,x:-294.5,y:-170}).wait(1).to({graphics:mask_graphics_3,x:-294.5,y:-125}).wait(1).to({graphics:mask_graphics_4,x:-270.5,y:-125}).wait(1).to({graphics:mask_graphics_5,x:-240.5,y:-125}).wait(1).to({graphics:mask_graphics_6,x:-210.5,y:-125}).wait(1).to({graphics:mask_graphics_7,x:-180.5,y:-125}).wait(1).to({graphics:mask_graphics_8,x:-150,y:-125}).wait(1).to({graphics:mask_graphics_9,x:-150,y:-125}).wait(1).to({graphics:mask_graphics_10,x:-150,y:-125}).wait(1).to({graphics:mask_graphics_11,x:-150,y:-125}).wait(1).to({graphics:mask_graphics_12,x:-150,y:-125}).wait(1).to({graphics:mask_graphics_13,x:-150,y:-125}).wait(1).to({graphics:mask_graphics_14,x:-150,y:-125}).wait(1).to({graphics:mask_graphics_15,x:-150,y:-125}).wait(1).to({graphics:mask_graphics_16,x:-150,y:-125}).wait(6));

	// frame
	this.instance = new lib.frame_300x250();
	this.instance.parent = this;
	this.instance.setTransform(-150,-125,1,1,0,0,0,-150,-125);
	this.instance._off = true;

	var maskedShapeInstanceList = [this.instance];

	for(var shapedInstanceItr = 0; shapedInstanceItr < maskedShapeInstanceList.length; shapedInstanceItr++) {
		maskedShapeInstanceList[shapedInstanceItr].mask = mask;
	}

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1).to({_off:false},0).wait(21));

}).prototype = p = new cjs.MovieClip();
p.nominalBounds = new cjs.Rectangle(-300,-250,300,250);


(lib.bg_01 = function(mode,startPosition,loop) {
	this.initialize(mode,startPosition,loop,{});

	// on
	this.instance = new lib.bg_01_on_1();
	this.instance.parent = this;
	this.instance.setTransform(150,125,1,1,0,0,0,150,125);
	this.instance.alpha = 0;
	this.instance._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(9).to({_off:false},0).to({alpha:1},2).wait(11).to({alpha:0},2).wait(1));

	// off
	this.instance_1 = new lib.bg_01_off_1();
	this.instance_1.parent = this;
	this.instance_1.setTransform(150,125,1,1,0,0,0,150,125);

	this.timeline.addTween(cjs.Tween.get(this.instance_1).wait(25));

}).prototype = p = new cjs.MovieClip();
p.nominalBounds = new cjs.Rectangle(0,0,300,250);


(lib._MainMC = function(mode,startPosition,loop) {
if (loop == null) { loop = false; }	this.initialize(mode,startPosition,loop,{});

	// timeline functions:
	this.frame_209 = function() {
		this.bg_01_ani.stop();
	}
	this.frame_294 = function() {
		this.frameAni.gotoAndPlay("frameup");
	}

	// actions tween:
	this.timeline.addTween(cjs.Tween.get(this).wait(209).call(this.frame_209).wait(85).call(this.frame_294).wait(96));

	// stoerer
	this.instance = new lib.stoerer_1();
	this.instance.parent = this;
	this.instance.setTransform(-70.5,216,1,1,0,0,0,70.5,24);
	this.instance._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(114).to({_off:false},0).to({x:70.5},10,cjs.Ease.quadOut).wait(75).to({alpha:0},5).wait(145).to({x:-70.5,y:206,alpha:1},0).to({x:70.5},10,cjs.Ease.quadOut).wait(31));

	// logo_blue
	this.instance_1 = new lib.logo_blue();
	this.instance_1.parent = this;
	this.instance_1.setTransform(234.5,213.5,1,1,0,0,0,54.5,7.5);
	this.instance_1.alpha = 0;
	this.instance_1._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance_1).wait(314).to({_off:false},0).to({y:233.5,alpha:1},5,cjs.Ease.get(1)).wait(71));

	// frame
	this.frameAni = new lib.frame_ani();
	this.frameAni.name = "frameAni";
	this.frameAni.parent = this;
	this.frameAni.setTransform(150,125,1,1,0,0,0,-150,-125);
	this.frameAni.alpha = 0.75;
	this.frameAni._off = true;

	this.timeline.addTween(cjs.Tween.get(this.frameAni).wait(289).to({_off:false},0).wait(101));

	// logo_white
	this.instance_2 = new lib.logo_white();
	this.instance_2.parent = this;
	this.instance_2.setTransform(234.5,233.5,1,1,0,0,0,54.5,7.5);

	this.timeline.addTween(cjs.Tween.get(this.instance_2).wait(289).to({alpha:0},5,cjs.Ease.get(1)).to({_off:true},1).wait(95));

	// btn_cta
	this.instance_3 = new lib.btn_cta_1();
	this.instance_3.parent = this;
	this.instance_3.setTransform(72,104.5,1,1,0,0,0,60,12.5);
	this.instance_3.alpha = 0;
	this.instance_3._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance_3).wait(379).to({_off:false},0).to({x:82,alpha:1},5,cjs.Ease.quadOut).wait(6));

	// subline
	this.instance_4 = new lib.subline_1();
	this.instance_4.parent = this;
	this.instance_4.setTransform(108.5,67,1,1,0,0,0,87.5,15);
	this.instance_4.alpha = 0;
	this.instance_4._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance_4).wait(324).to({_off:false},0).to({alpha:1},10).wait(56));

	// headline_04
	this.instance_5 = new lib.headline_04_1();
	this.instance_5.parent = this;
	this.instance_5.setTransform(104.5,34,1,1,0,0,0,84.5,11);
	this.instance_5.alpha = 0;
	this.instance_5._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance_5).wait(314).to({_off:false},0).to({alpha:1},10).wait(66));

	// stoerer_6d
	this.instance_6 = new lib.stoerer_6d_1();
	this.instance_6.parent = this;
	this.instance_6.setTransform(94,220,1,1,0,0,0,73,21);
	this.instance_6.alpha = 0;
	this.instance_6._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance_6).wait(14).to({_off:false},0).to({alpha:1},10).wait(75).to({alpha:0},5).to({_off:true},1).wait(285));

	// legal
	this.instance_7 = new lib.legal_1();
	this.instance_7.parent = this;
	this.instance_7.setTransform(147.5,109.5,1,1,0,0,0,119.5,82.5);
	this.instance_7.alpha = 0;
	this.instance_7._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance_7).wait(209).to({_off:false},0).to({alpha:1},9).wait(67).to({alpha:0},10).to({_off:true},1).wait(94));

	// overlay
	this.instance_8 = new lib.overlay();
	this.instance_8.parent = this;
	this.instance_8.setTransform(148.2,125,1,1,0,0,0,148.2,125);
	this.instance_8.alpha = 0;
	this.instance_8._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance_8).wait(204).to({_off:false},0).to({alpha:1},5).wait(76).to({alpha:0},10).to({_off:true},1).wait(94));

	// headline_02
	this.instance_9 = new lib.headline_02_1();
	this.instance_9.parent = this;
	this.instance_9.setTransform(140.5,44.5,1,1,0,0,0,119.5,22.5);
	this.instance_9.alpha = 0;
	this.instance_9._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance_9).wait(104).to({_off:false},0).to({alpha:1},10).wait(85).to({alpha:0},5).to({_off:true},1).wait(185));

	// headline_01
	this.instance_10 = new lib.headline_01_1();
	this.instance_10.parent = this;
	this.instance_10.setTransform(90.5,44.5,1,1,0,0,0,70.5,22.5);
	this.instance_10.alpha = 0;
	this.instance_10._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance_10).wait(3).to({_off:false},0).to({alpha:1},11).wait(85).to({alpha:0},5).to({_off:true},1).wait(285));

	// bg_02
	this.instance_11 = new lib.bg_02_1();
	this.instance_11.parent = this;
	this.instance_11.setTransform(150,125,1,1,0,0,0,150,125);
	this.instance_11.alpha = 0;
	this.instance_11._off = true;

	this.timeline.addTween(cjs.Tween.get(this.instance_11).wait(285).to({_off:false},0).to({alpha:1},10).wait(95));

	// bg
	this.bg_01_ani = new lib.bg_01();
	this.bg_01_ani.name = "bg_01_ani";
	this.bg_01_ani.parent = this;
	this.bg_01_ani.setTransform(150,125,1,1,0,0,0,150,125);

	this.timeline.addTween(cjs.Tween.get(this.bg_01_ani).wait(390));

}).prototype = p = new cjs.MovieClip();
p.nominalBounds = new cjs.Rectangle(-141,0,441,250);


// stage content:
(lib.SEE_SondermodelleYES_Affiperf_RON_MSSB_300x250_Kona_MPU = function(mode,startPosition,loop) {
if (loop == null) { loop = false; }	this.initialize(mode,startPosition,loop,{});

	// MainMC
	this.instance = new lib._MainMC();
	this.instance.parent = this;
	this.instance.setTransform(150,125,1,1,0,0,0,150,125);

	this.timeline.addTween(cjs.Tween.get(this.instance).wait(1));

}).prototype = p = new cjs.MovieClip();
p.nominalBounds = new cjs.Rectangle(150,125,150,125);
// library properties:
lib.properties = {
	id: 'AC989250F00E2D4DA41A774EB224BB16',
	width: 300,
	height: 250,
	fps: 24,
	color: "#FFFFFF",
	opacity: 1.00,
	manifest: [
		{src:"./bg_01_off.jpg", id:"bg_01_off"},
		{src:"./bg_01_on.jpg", id:"bg_01_on"},
		{src:"./bg_02.jpg", id:"bg_02"},
		{src:"./btn_cta.png", id:"btn_cta"},
		{src:"./headline_01.png", id:"headline_01"},
		{src:"./headline_02.png", id:"headline_02"},
		{src:"./headline_04.png", id:"headline_04"},
		{src:"./hyundai_logo_blue.png", id:"hyundai_logo_blue"},
		{src:"./hyundai_logo_white.png", id:"hyundai_logo_white"},
		{src:"./legal.png", id:"legal"},
		{src:"./stoerer.png", id:"stoerer"},
		{src:"./stoerer_6d.png", id:"stoerer_6d"},
		{src:"./subline.png", id:"subline"}
	],
	preloads: []
};



// bootstrap callback support:

(lib.Stage = function(canvas) {
	createjs.Stage.call(this, canvas);
}).prototype = p = new createjs.Stage();

p.setAutoPlay = function(autoPlay) {
	this.tickEnabled = autoPlay;
}
p.play = function() { this.tickEnabled = true; this.getChildAt(0).gotoAndPlay(this.getTimelinePosition()) }
p.stop = function(ms) { if(ms) this.seek(ms); this.tickEnabled = false; }
p.seek = function(ms) { this.tickEnabled = true; this.getChildAt(0).gotoAndStop(lib.properties.fps * ms / 1000); }
p.getDuration = function() { return this.getChildAt(0).totalFrames / lib.properties.fps * 1000; }

p.getTimelinePosition = function() { return this.getChildAt(0).currentFrame / lib.properties.fps * 1000; }

an.bootcompsLoaded = an.bootcompsLoaded || [];
if(!an.bootstrapListeners) {
	an.bootstrapListeners=[];
}

an.bootstrapCallback=function(fnCallback) {
	an.bootstrapListeners.push(fnCallback);
	if(an.bootcompsLoaded.length > 0) {
		for(var i=0; i<an.bootcompsLoaded.length; ++i) {
			fnCallback(an.bootcompsLoaded[i]);
		}
	}
};

an.compositions = an.compositions || {};
an.compositions['AC989250F00E2D4DA41A774EB224BB16'] = {
	getStage: function() { return exportRoot.getStage(); },
	getLibrary: function() { return lib; },
	getSpriteSheet: function() { return ss; },
	getImages: function() { return img; }
};

an.compositionLoaded = function(id) {
	an.bootcompsLoaded.push(id);
	for(var j=0; j<an.bootstrapListeners.length; j++) {
		an.bootstrapListeners[j](id);
	}
}

an.getComposition = function(id) {
	return an.compositions[id];
}



})(createjs = createjs||{}, AdobeAn = AdobeAn||{});
var createjs, AdobeAn;