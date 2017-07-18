var socketAngular = angular.module('socketAngular',[]);
socketAngular.factory('socket',function($rootScope){
	var socket = io.connect('localhost:3000');	
	
	return {
		on: function(eventName,callback){
			socket.on(eventName,function(){
				var args = arguments;
				
				$rootScope.$apply(function(){
					callback.apply(socket, args);
				});
				
			});
			
		},
		
		
		
	};
});

socketAngular.factory("handRecognition",['$rootScope','socket',function($rootScope,socket){
	var handRecognition;
	var callBackBinder = new Array();
	
	socket.on('handReco_event',function(data){
			console.log("Event "+data.event);
			var args = arguments;
			callBackBinder[data.event].apply(null,args);
		
	});
	return{
		
		bind: function(eventName,callback){
			callBackBinder[eventName]=callback;
			//console.log(callBackBinder.move);
		}
		
		
	};
	
	
}]);




