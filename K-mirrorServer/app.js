
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./routes')
  , user = require('./routes/user')
  , client = require('./routes/client')
  , http = require('http')
  , path = require('path');

var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}
var server = http.createServer(app).listen(app.get('port'), function(){
	  console.log('Express server listening on port ' + app.get('port'));
	});
var io = require('socket.io').listen(server);


var connected;

app.get('/', routes.index);
app.get('/users', user.list);
app.get('/client',client.page);
app.post('/api/handreco/',function(req,res){
	
	console.log(req.body);
	res.json({"result":"success"});
	io.sockets.emit('handReco_event',req.body);
});






io.on('connection',function(socket){
	socket.emit('connected',"success");
	connected = true;
}
);