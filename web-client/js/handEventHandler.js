var HAND_ON = 1;
var HAND_OFF=2;
var HAND_UP=3;
var HAND_DOWN=4;
var HAND_NEXT=5;
var HAND_PREVIOUS=6;
var HAND_CLICK=7;


function receiveEvent(handEvent){
	switch(handEvent) {
		case HAND_ON:
			openAll();
			break;
		case HAND_OFF:
			closeAll();
			break;
		case HAND_UP:
			changeRegion();
			break;
		case HAND_DOWN:
			changeRegion();
			break;
		case HAND_NEXT:
			if(getCurrentRegion()==MENU_REGION){
				nextMenu();
			}else if(getCurrentRegion()==WIDGET_REGION){
				nextWidget();
			}else if(getCurrentRegion()==CARD_REGION){
				nextCard();
			}
			break;
		case HAND_PREVIOUS:
			if(getCurrentRegion()==MENU_REGION){
				previousMenu();
			}else if(getCurrentRegion()==WIDGET_REGION){
				previousWidget();
			} else if(getCurrentRegion()==CARD_REGION){
				previousCard();
			}
			break;
		case HAND_CLICK:
			clickRegion();
			break;
	}


}