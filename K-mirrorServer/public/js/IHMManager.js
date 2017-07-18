var MENU_REGION = 1;
var WIDGET_REGION = 2;
var CARD_REGION = 3;
var currRegion=2;
var widgetOpen=false;
var menuOpen=false;
var cardOpen = false;
var currWidgetPosition=0;
var currMenuPosition=0;
var currCardPosition=0;
var maxWidget=2;
var maxMenu=2;
var maxCard=2;
var nIntervId;

$( document ).ready(function() {
    initIHM();
	
});
function initIHM(){
	flashTime();
	updateTime();
	//init IHM for Mouse Management - not use when bind with hand detection

}
function updateTime() {
    nIntervId = setInterval(flashTime, 1000*60); //<---prints the time 
}                                                //----after every minute

function flashTime() {
    var now = new Date();
    var h = format_two_digits(now.getHours());
    var m = format_two_digits(now.getMinutes());
   var time = '&nbsp;'+h + ' : ' + m;
   $('#timeClock').html(time); //<----updates the time in the $('#my_box1') [needs jQuery]
	
	var dd = format_two_digits(now.getDate());
	var mm = format_two_digits(now.getMonth()+1);
	var yyyy = now.getFullYear();
	var date = '&nbsp;'+dd+'.'+mm+'.'+yyyy
	$('#date').html(date);
}                             

function changeRegion(){
	if(currRegion==WIDGET_REGION){
		selectMenuRegion();
	}else{
		selectWidgetRegion();
	}

}

function clickRegion(){
	if(currRegion==WIDGET_REGION || currRegion == MENU_REGION){
		if(menuOpen || widgetOpen){
			Materialize.showStaggeredList('#bigCard');
			cardOpen = true;
			currRegion = CARD_REGION;
			$('div.card-action > a:eq(0)').css('box-shadow','0px 0px 2px 2px white');
		}
	}else if(currRegion==CARD_REGION){
		closeCard();
			
	}

}
function closeCard(){
		$('#bigCard > li').css('opacity',0);
		$("div.card-action > a").css('box-shadow','');
		currCardPosition=0;
		cardOpen = false;
		if(menuOpen) {
			selectMenuRegion();
		}else{
			selectWidgetRegion();
		}

}
function nextCard(){
	if(cardOpen){
		$("div.card-action > a").css('box-shadow','');
		if(currCardPosition == maxCard){
			currCardPosition=0;
		}else{
			currCardPosition++;
		}
		var str= "div.card-action > a:eq("+currCardPosition+")";
		$(str).css('box-shadow','0px 0px 2px 2px white');
	
	}

}

function previousCard(){
	if(cardOpen){
		$("div.card-action > a").css('box-shadow','');
		if(currCardPosition == 0 ){
			currCardPosition=maxCard;
		}else{
			currCardPosition--;
		}
		var str= "div.card-action > a:eq("+currCardPosition+")";
		$(str).css('box-shadow','0px 0px 2px 2px white');
	
	}

}
function format_two_digits(n) {
    return n < 10 ? '0' + n : n;
}

function closeAll(){
	$('#bigCard > li').css('opacity',0);
	closeWidget();
	closeMenu();
	
}
 function openAll(){
    openWidget();
	openMenu();
	
 }


function openMenu(){
 if(!menuOpen){
	//closeWidget();
	menuOpen=true;
	$('#menuBtn').click();
	
 }
 if(currRegion==MENU_REGION){
	   var str= "a.menuItem:eq("+currMenuPosition+")";
		$(str).css('box-shadow','0px 0px 2px 2px white')
	}else{
		$("a.menuItem").css("box-shadow","");
	}
}

function selectMenuRegion(){
	currRegion=MENU_REGION;
	closeWidget();
	openMenu();
	
}

function selectWidgetRegion(){
	currRegion=WIDGET_REGION;
	closeMenu();
	openWidget();
}
function closeMenu(){
 if(menuOpen){
	menuOpen=false;
	$('#menuBtn').click();
 }
 
}

function nextMenu(){
	if(menuOpen){
		$("a.menuItem").css('box-shadow','');
		if(currMenuPosition == maxMenu){
			currMenuPosition=0;
		}else{
			currMenuPosition++;
		}
		var str= "a.menuItem:eq("+currMenuPosition+")";
		$(str).css('box-shadow','0px 0px 2px 2px white');
	
	}


}

function previousMenu(){
	if(menuOpen){
		$("a.menuItem").css('box-shadow','');
		if(currMenuPosition == 0 ){
			currMenuPosition=maxMenu;
		}else{
			currMenuPosition--;
		}
		var str= "a.menuItem:eq("+currMenuPosition+")";
		$(str).css('box-shadow','0px 0px 2px 2px white');
	
	}


}

function openWidget(){
if(!widgetOpen){
 //closeMenu();
 widgetOpen=true;
 Materialize.showStaggeredList('#widget');
 
}
 if(currRegion==WIDGET_REGION){
	var str= "div.widgetBox:eq("+currWidgetPosition+")";
	$(str).css('box-shadow','0px 0px 5px 5px white');
 }else{
	$("div.widgetBox").css('box-shadow','');
 
 }

}

function nextWidget(){
	if(widgetOpen){
		$("div.widgetBox").css('box-shadow','');
		if(currWidgetPosition == maxWidget){
			currWidgetPosition=0;
		}else{
			currWidgetPosition++;
		}
		var str= "div.widgetBox:eq("+currWidgetPosition+")";
		console.log(str);
		$(str).css('box-shadow','0px 0px 5px 5px white');
	}
}

function previousWidget(){
	if(widgetOpen){
		$("div.widgetBox").css('box-shadow','');
		if(currWidgetPosition == 0){
			currWidgetPosition=maxWidget;
		}else{
			currWidgetPosition--;
		}
		var str= "div.widgetBox:eq("+currWidgetPosition+")";
		console.log(str);
		$(str).css('box-shadow','0px 0px 5px 5px white');
	}
}

function closeWidget(){
	if(widgetOpen){
		widgetOpen=false;
		$('li.widget').css('opacity',0);
		
	}

}
function setHandIconColor(color){
	$("#handIcon > a").removeClass("green orange red black pulse");
	$("#handIcon > a").addClass(color);
}
function getCurrentRegion(){
	return currRegion;
}