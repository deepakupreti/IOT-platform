var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var routes = require('./routes/index');
var users = require('./routes/users');

var app = express();

var fs = require('fs');
var mongo = require('mongodb');
var monk = require('monk');
var db = monk('localhost:27017/RegRepoSec');

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'hjs');

// uncomment after placing your favicon in /public
//app.use(favicon(__dirname + '/public/favicon.ico'));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(require('less-middleware')(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, 'public')));

app.use(function(req, res, next){
  req.db = db;
  next();
});


/* Routing Functions
*/
app.get('/', function(req, res){

    console.log("Server Runnning !!!");
    res.send(200);
    //res.write("hello Aditya");
    //res.end();
});

app.get('/activesensors/?', function(req, res){

    var db = req.db;
    var reg = db.get('registry');
    reg.find({"active":1}, {} , function(err, docs) {
          res.send(docs);
      });

});

app.get('/data/?', function(req, res){

    console.log('hello');
    res.end();
});


// Add new sensor

app.get('/newsensor/*', function(req, res){

    var id = req.query.id;
    var type = req.query.type;
    var lat = req.query.lat;
    var lon = req.query.lon;
    var gateway = req.query.gateway;

    var db = req.db;
    var repo = db.get('repository');
    var registry = db.get('registry');
    var sd = db.get('sensordata');
    var time = new Date();

    repo.insert({'id':id, 'type':type, 'lat':lat,'lon':lon, 'gateway': gateway});
    registry.insert({'id':id, 'type':type, 'gateway': gateway, 'active': 1});
    sd.insert({'id':id, 'type':type, 'lat':lat,'lon':lon, 'data':0, 'timestamp':time});
    res.send(200);
});

// Return gateways sensors

app.get('/getsensors/*', function(req, res){


    var db = req.db;
    var sd = db.get('repository');

    var res1 = '[';
    
    var name = req.query.gateway;
    console.log(name);
    var query = {};
    var gate = "gateway";
    query[gate] = name;

    sd.find(query,{},function(err, docs){

        if(err)
        {
            res.send("Error");
            return;
        }

        for(var i=0;i<docs.length;i++)
        {
            var o = docs[i];
            console.log(o);
            var id1 = o["id"];
            res1 = res1 + "{ id: " + id1 + "},";
        }
        if(res1.charAt(res1.length-1)==',')
            res1 = res1.substring(0, res1.length -1);
        res1 = res1+"]";
        //console.log(res1);
        res.send(res1);
    });

});

// Reads a JSON file and fills the Repository database and registry database
function readJSONFile()
{
    var obj;
    var mydb = db;
    var repo = db.get('repository');
    var activeReg = db.get('registry');
    activeReg.remove({});
    repo.remove({},{}, function(err, number){

          console.log(''+number);
          fs.readFile('sensors.json', 'utf8', function (err, data) {
          if (err) throw err;
          obj = JSON.parse(data);

          // obj is an  array
          for(var i=0;i<obj.length;i++)
          {
              var o = obj[i];
              var id1 = o["id"];
              var t = o["type"];
              var g = o["gateway"];
              var act = 1;

              activeReg.insert({'id':id1, 'type':t, 'gateway': g, 'active':act});
          }
          repo.insert(obj);
          //console.log('id founf' + obj[0]["type"]);
          //console.log(obj);
        });
    });
    
    /*
    repo.find({},{}, function(err, col){
      console.log("find");
        console.log(col);
    });
  */
}

function readTypeHandlers()
{
    var obj;
    var mydb = db;
    var repo = db.get('typehandles');

    fs.readFile('typehandler.json', 'utf8', function (err, data) {
          if (err) throw err;
          obj = JSON.parse(data);

          // obj is an  array
          for(var i=0;i<obj.length;i++)
          {
              var o = obj[i];
              var t = o["type"];
              var unit = o["unit"];

              repo.insert({'type':t, 'unit':unit});
          }
          
        });
}

//readTypeHandlers()

function printDBState()
{
    var mydb = db;
    var reg = db.get('registry');
    reg.find({},{}, function(err, col)
    {
        console.log(col);
    });
}

// Uncomment below line to call method to enter sensor information in repository db
//readJSONFile()
//printDBState()


//app.use('/', routes);
//app.use('/users', users);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});

app.listen(5000);

module.exports = app;
