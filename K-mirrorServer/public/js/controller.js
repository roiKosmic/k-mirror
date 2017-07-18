angular.module("socketAngular").controller("socketController",['$scope','handRecognition',function($scope,handRecognition){
	$scope.status = "connection in progress";
	
	var HAND_ON = 1;
	var HAND_OFF=2;
	var HAND_UP=3;
	var HAND_DOWN=4;
	var HAND_NEXT=5;
	var HAND_PREVIOUS=6;
	var HAND_CLICK=7;
	var HAND_DETECTED = 8;
	
	$scope.dispatchEvent = receiveEvent;
	
	handRecognition.bind('HAND_ORIGIN_SET',function(data){
		$scope.status = "HAND_ORIGIN_SET";
		//console.log(data.data.reason);
		//if(data.data.reason==="Hand first detection"){
			$scope.dispatchEvent(HAND_ON);
		//}
		
	});
	
	handRecognition.bind('HAND_DETECTED',function(data){
		
		//console.log(data.data.reason);
		if(data.data.reason==="Hand first detection"){
			$scope.dispatchEvent(HAND_DETECTED);
		}
		
	});
	
	handRecognition.bind('HAND_NOT_DETECTED',function(data){
		$scope.status = "HAND_NOT_DETECTED";
		$scope.dispatchEvent(HAND_OFF);
		
	});
	
	handRecognition.bind('HAND_CLICK',function(data){
		$scope.status = "HAND_CLICK";
		$scope.dispatchEvent(HAND_CLICK);
		
	});
	
	handRecognition.bind('MOVE',function(data){
		if(data.data.xMove==="RIGHT"){
			$scope.status = "Move right";
			$scope.dispatchEvent(HAND_NEXT);
		}
		if(data.data.xMove==="LEFT"){
			$scope.status = "Move left";
			$scope.dispatchEvent(HAND_PREVIOUS);
		}
		
		if(data.data.yMove==="UP"){
			$scope.status = "Move UP";
			$scope.dispatchEvent(HAND_UP);
		}
		
		if(data.data.yMove==="DOWN"){
			$scope.status = "Move DOWN";
			$scope.dispatchEvent(HAND_DOWN);
		}
	});
	
}]);
