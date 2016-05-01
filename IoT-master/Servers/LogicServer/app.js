var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var routes = require('./routes/index');
var users = require('./routes/users');

var http = require('http');
var mongo = require('mongodb');
var monk = require('monk');
var db = monk('localhost:27017/RegRepoSec');

var app = express();

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

//app.use('/', routes);
//app.use('/users', users);

app.use(function(req, res, next){
  req.db = db;
  next();
});

var uh = '';
var ur = '';
var ug = '';
var up = '';
var uw = '';
var uo = '';

// ROutes

app.get('/', function(req, res){

    console.log("Running");
    console.log(''+uh+ug+ur+up);
    res.send(200);

});

// Forward Android Apps request to Filter Server and get response which is again sent back to android app
app.get('/command/*', function(req, res){

    var id = req.query.id;
    console.log(req.url);
    return http.get({
        host: 'localhost',
        port: 19620,
        path: req.url
    }, function(response) {
        // Continuously update stream with data
        var body = '';
        response.on('data', function(d) {
            body += d;
        });
        response.on('end', function() {

            // Data reception is done, do whatever with it!
            console.log('body: '+body)
            /*var parsed = JSON.parse(body);
            console.log(parsed);
            res.send(''+parsed);*/
            res.send(body);
          //console.log(parsed);
        });
    });

});


// set a new path data

app.get('/settraffic/*', function(req, res){

    var db = req.db;
    var pathdata = db.get('pathdata');

    var path = req.query.path;
    var traffic = req.query.traffic

    pathdata.update({"path": path}, {$set: {"traffic": traffic}});
    //sensordata.update({"id": id}, {$set: {"data": data, "timestamp": time}});
    res.send(200);

});

// Get Medical data and respond accordingly

app.get('/getmedicaldata/*', function(req, res){

    var db = req.db;
    var sensordata = db.get('sensordata');
    var part = req.query.part;
    var type = req.query.type;
    var aid;
    var handles = db.get('typehandles');

    if(type=='medical' || type=='traffic')
    {
        aid = req.query.aid;
    }

    console.log("type: "+type);

    var query = {};
    var key = 'type';
    query[key] = 'medical';
    sensordata.find(query, {}, function(err, docs){

        if(err)
        {
            console.log('error!');
            res.send('error!');
        }
        var obj = docs[0];
        for(var i=0;i<docs.length;i++)
        {
            var o = docs[i];
            var amb = o["lat"]+"-"+o["lon"];
            console.log("amb: "+amb);
            if(aid==amb)
            {
                obj = docs[i];
                break;
            }
        }
        
        var val = obj['data'];
        var vals = val.split(';');

        

        if(part=='heart')
        {
            // If heart rate drops below 20, give electric shock

            var value = vals[0];
            if(parseInt(value) < 20)
            {
                res.send('a;Charge Defibrillator;'+value+" "+uh);
            }
            else
            {
                res.send(''+value+" "+uh);
            }
        }
        else if(part=='respire')
        {
            // If breathing rate goes beyond 50, give oxygen
            var value = vals[1];
            if(parseInt(value) > 50)
            {
                res.send('a;Put Oxygen Mask;'+value+" "+ur);
            }
            else
            {
                res.send(''+value+" "+ur);
            }
        }
        else if(part=='glucose')
        {
            // If glucose level drops below 30, provide another bottle
            var value = vals[2];
            if(parseInt(value) <= 30)
            {
                res.send('a;Provide Glucose Bottle;'+value+" "+ug);
            }
            else
            {
                res.send(''+value+" "+ug);
            }
        }
        else if(part=='pressure')
        {
            // If blood pressure drop below 35, provide anasthesia
            var value = vals[3];
            if(parseInt(value) > 50)
            {
                res.send('a;Provide Anathesia;'+value+" "+up);
            }
            else
            {
                res.send(''+value+" "+up);
            }
        }

    });

});

function loadUnits()
{
    var mydb = db;
    
    var handles = mydb.get('typehandles');

    handles.find({}, {}, function(err, docs){

        if(err)
            console.log(err);
        else
        {
            for(var i=0;i<docs.length;i++)
            {
                var type = docs[i]['type'];
                var unit = docs[i]['unit'];

                if(type=='heart')
                {
                    uh = unit;
                }
                else if(type=='respire')
                {
                    ur = unit;
                }
                else if(type=='glucose')
                    ug = unit;
                else if(type=='pressure')
                    up = unit;
                else if(type=='water')
                    uw = unit;
                else if(type=='oil')
                    uo = unit;
            }
        }
    });
}

loadUnits()

app.get('/minpath/*', function(req, res){


    var db = req.db;
    var intersection = req.query.interpoint;
    var query = {};
    var aid = req.query.aid;
    var key = 'src';
    var k1 = 'aid';
    query[k1] = aid;
    query[key] = intersection;

    var minimum = 10000;
    var mindest = '';

    var pathdata = db.get('pathdata');
    console.log(intersection);

    pathdata.find(query,{},function(err, docs){


        if(err)
        {
            console.log('error');
            return;
        }

        for(var  i=0;i<docs.length;i++)
        {
            var obj = docs[i];
            var dest = obj['dest'];
            var traffic = obj['traffic'];
            var p = intersection+dest;

            if(minimum > parseInt(traffic))
            {
                minimum = parseInt(traffic);
                mindest = dest;
            }             
        }

        res.send(mindest+":"+minimum);
    });

});


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

app.listen(9658);
//http.request(options, callback).end();

function getActiveSensors(callback) {

    return http.get({
        host: 'localhost',
        port: 5000,
        path: '/activesensors'
    }, function(response) {
        // Continuously update stream with data
        var body = '';
        response.on('data', function(d) {
            body += d;
        });
        response.on('end', function() {

            // Data reception is done, do whatever with it!
            var parsed = JSON.parse(body);
           /* callback({
                email: parsed.email,
                password: parsed.pass
            });
          */
          //console.log(parsed);
        });
    });

}

var val = getActiveSensors()
//console.log(val);
module.exports = app;
