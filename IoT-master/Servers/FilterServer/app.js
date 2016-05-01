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

//app.use('/', routes);
//app.use('/users', users);

app.use(function(req, res, next){
  req.db = db;
  next();
});


app.get('/', function(req, res){
  console.log("Filter Server running !!!");
  res.send(200);
});

// Command API to get data pertaining a particular sensor ID

app.get('/command/*', function(req, res){

    var id = req.query.id;
    if(isNaN(id))
    {
        res.send("Error! Incorrect ID!");
        return;
    }
    var flag = 0;


    var key1 = "active";
    var key2 = "id"
    console.log(id);
    var db = req.db;
    var sensordata = db.get('sensordata');
    var reg = db.get('registry');
    var query = {};
    //query[key1] = 1;
    query[key2] = parseInt(id);
    console.log('id: '+parseInt(id));
    reg.find(query,{}, function(ee, docs)
    {
          console.log(docs);
          if(docs.length==0)
          {
              res.send("Error! Incorrect ID!");
              return;
          }
          var obj = docs[0];
          if(obj["active"]!=1)
          {
              res.send("Error! Sensor not Active!");
              return;
          }

          var newq = {};
          newq[key2] = parseInt(id);
          sensordata.find(newq,{}, function(err, docs1)
          {
              console.log(docs1);
              if(err)
              {
                  res.send("Cannot Find Sensor!");
                  return;
              }
              obj = docs1[0];
              res.send(''+obj["data"]);
          });

    });    

});

// API access by Gateway to update new sensor data

app.get('/new/*', function(req, res){

    var db = req.db;
    var sensordata = db.get('sensordata');
    var registry = db.get('registry');
    var id = parseInt(req.query.id);
    var data = parseFloat(req.query.data);
    
    if(isNaN(id))
    {
        res.send("Error!");
        return;
    }
    if(isNaN(data))
    {
        console.log("error:" + data);
        registry.update({"id": id}, {$set: {"active": 0}});
        res.send("Error!");
        return;
    }
    console.log(''+id);
    console.log(''+data);
    var time = new Date();
    sensordata.update({"id": id}, {$set: {"data": data, "timestamp": time}});
    registry.update({"id": id}, {$set: {"active": 1}});
    res.send(200);
});

// New Medical data

app.get('/newmedicaldata/*', function(req, res){

    var db = req.db;
    var sensordata = db.get('sensordata');
    var registry = db.get('registry');
    var id = parseInt(req.query.id);
    var data = req.query.data;
    var aid = req.query.aid;
    var type = req.query.type;

    var ids = aid.split("-");
    var p1 = ids[0];
    var p2 = parseInt(ids[1]);
    
    console.log(p1+p2);
    /*if(isNaN(id))
    {
        res.send("Error!");
        return;
    }*/
    
    console.log(''+id);
    console.log(''+data);
    var time = new Date();
    sensordata.update({"type": type, "lat":p1, "lon":p2}, {$set: {"data": data, "timestamp": time}});
    registry.update({"id": id}, {$set: {"active": 1}});
    res.send(200);
});

app.get('/path/*', function(req, res){

    var db = req.db;
    var pathdata = db.get('pathdata');
    var registry = db.get('registry');
    var id = parseInt(req.query.id);
    var data = req.query.data;
    var type = req.query.type;
    var aid = req.query.aid;
    
    if(isNaN(id))
    {
        res.send("Error!");
        return;
    }

    var vals = data.split(";");

    pathdata.update({"path": 'ab', 'aid':aid}, {$set: {"traffic": vals[0]}});
    pathdata.update({"path": 'ac', 'aid':aid}, {$set: {"traffic": vals[1]}});
    pathdata.update({"path": 'ad', 'aid':aid}, {$set: {"traffic": vals[2]}});
    pathdata.update({"path": 'bg', 'aid':aid}, {$set: {"traffic": vals[3]}});
    pathdata.update({"path": 'bf', 'aid':aid}, {$set: {"traffic": vals[4]}});
    pathdata.update({"path": 'cf', 'aid':aid}, {$set: {"traffic": vals[5]}});
    pathdata.update({"path": 'ce', 'aid':aid}, {$set: {"traffic": vals[6]}});
    pathdata.update({"path": 'de', 'aid':aid}, {$set: {"traffic": vals[7]}});
    pathdata.update({"path": 'gh', 'aid':aid}, {$set: {"traffic": vals[8]}});
    pathdata.update({"path": 'fh', 'aid':aid}, {$set: {"traffic": vals[9]}});
    pathdata.update({"path": 'fi', 'aid':aid}, {$set: {"traffic": vals[10]}});
    pathdata.update({"path": 'ei', 'aid':aid}, {$set: {"traffic": vals[11]}});
    pathdata.update({"path": 'hj', 'aid':aid}, {$set: {"traffic": vals[12]}});
    pathdata.update({"path": 'ij', 'aid':aid}, {$set: {"traffic": vals[13]}});

    
    console.log(''+id);
    console.log(''+data);
    //var time = new Date();
    ///sensordata.update({"id": id}, {$set: {"data": data, "timestamp": time}});
    //registry.update({"id": id}, {$set: {"active": 1}});
    res.send(200);
});


// Get sensors in range

app.get('/inrange/*', function(req, res){

    var db = req.db;
    var sd = db.get('sensordata');

    if(isNaN(req.query.lat) || isNaN(req.query.lon) || isNaN(req.query.range))
    {
        res.send("Invalid Parameters");
        return;
    }

    var res1 = '{ inrange:[';
    var lat = parseInt(req.query.lat);
    var lon = parseInt(req.query.lon);
    var ran = parseInt(req.query.range);
    console.log("here");
    sd.find({},{},function(err, docs){

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
            var t = o["type"];
            var lat1 = parseInt(o["lat"]);
            var lon1 = parseInt(o["lon"]);

            var val = Math.pow((lat1-lat), 2) + Math.pow((lon1-lon), 2);

            if(Math.sqrt(val) <= ran)
            {
                res1 = res1 + "{ id: " + id1 + "," + " type: " + t + "},";
            }
        }
        if(res1.charAt(res1.length-1)==',')
            res1 = res1.substring(0, res1.length -1);
        res1 = res1+"]}";
        console.log(res1);
        res.send(res1);
    });

});

// Callback API

app.get('/callback/*', function(req, res){

  try{
   var id1 = parseInt(req.query.id1);
   var val1 = parseInt(req.query.val1);
   var op1 = req.query.op1;
   var join = req.query.join;

   var db = req.db;
   var col = db.get('sensordata');

   if(id1=='5')
        flag = 1;

   var id2, val2, op2;0
   if(join!="none")
   {
        id2 = parseInt(req.query.id2);
        val2 = parseInt(req.query.val2);
        op2 = req.query.op2;
   } 
   var query = {};
   var key = "id";
   query[key] = id1;
   var i =1;
   if(join=="none")
   {
          var refreshIntervalId = setInterval(function(){
          console.log("Run: "+ i);
          col.find(query, {}, function(err, docs){

              if(err)
              {
                  console.log("Error!");
                  res.send("Error!");
                  clearInterval(refreshIntervalId);
                  return;
              }
              if(docs.length == 0)
              {
                  console.log("Error! Sensor Not Found!");
                  res.send("Error!");
                  clearInterval(refreshIntervalId);
                  return;
              }

              var obj = docs[0];
              var value = obj["data"];

              if(op1=="eq")
              {
                  if(value==val1)
                  {
                      res.send("ALERT !!!");
                      clearInterval(refreshIntervalId);
                      return;
                  }
              }
              else if(op1=="le")
              {
                  if(value<=val1)
                  {
                      res.send("ALERT !!!");
                      clearInterval(refreshIntervalId);
                      return;
                  }
              }
              else if(op1=="ge")
              {
                  if(value>=val1)
                  {
                      res.send("ALERT !!!");
                      clearInterval(refreshIntervalId);
                      return;
                  }
              }
              else if(op1=="lt")
              {
                  if(value<val1)
                  {
                      res.send("ALERT !!!");
                      clearInterval(refreshIntervalId);
                      return;
                  }
              }
              else if(op1=="gt")
              {
                  if(value>val1)
                  {
                      res.send("ALERT !!!");
                      clearInterval(refreshIntervalId);
                      return;
                  }
              }

          });
          i+=1;
      }, 5000);
   }
   else
   {
          var f1 = 0;
          var f2 = 0;
      var refreshIntervalId = setInterval(function(){

          var q1 = {};
          var q2 = {};
          var k1 = "id";
          q1[k1] = id1;
          q2[k1] = id2;

          

          console.log("Run: "+ i);
          i+=1;
          col.find(q1, {}, function(err, docs){
                if(err){
                  console.log("Error! Sensor Not Found!");
                  res.send("Error!");
                  clearInterval(refreshIntervalId);
                  return;
                }
              if(docs.length == 0)
              {
                  console.log("Error! Sensor Not Found!");
                  res.send("Error!");
                  clearInterval(refreshIntervalId);
                  return;
              }

              var obj = docs[0];
              var value = parseInt(obj["data"]);

              if(op1=="eq")
              {
                  if(value==val1)
                  {
                      //res.send("Equal!");
                      f1 = 1;
                      //clearInterval(refreshIntervalId);
                      //return;
                  }
                  else
                    f1 = 0;
              }
              else if(op1=="le")
              {
                  if(value<=val1)
                  {
                      //res.send("Less or Equal!");
                      f1 = 1;
                      //clearInterval(refreshIntervalId);
                      //return;
                  }
                  else
                    f1 = 0;
              }
              else if(op1=="ge")
              {
                  if(value>=val1)
                  {
                      //res.send("Greater or Equal!");
                      //clearInterval(refreshIntervalId);
                      f1 = 1;
                      //return;
                  }
                  else
                    f1 = 0;
              }
              else if(op1=="lt")
              {
                  if(value<val1)
                  {
                      console.log("value: "+value+" val1: "+val1);
                      //res.send("Less Than!");
                      //clearInterval(refreshIntervalId);
                      f1 = 1;
                      console.log("f1 in: "+f1);
                      //return;
                  }
                  else
                    f1 = 0;
              }
              else if(op1=="gt")
              {
                  if(value>val1)
                  {
                      //res.send("Greater Than!");
                      //clearInterval(refreshIntervalId);
                      //return;
                      f1 = 1;
                  }
                  else
                    f1 = 0;
              }

          });
          console.log("f1 "+ f1);
          
          // Condition 2 Check

          col.find(q2, {}, function(err, docs){

              if(err){
                  console.log("Error! Sensor Not Found!");
                  res.send("Error!");
                  clearInterval(refreshIntervalId);
                  return;
                }

              if(docs.length == 0)
              {
                  console.log("Error! Sensor Not Found!");
                  res.send("Error!");
                  clearInterval(refreshIntervalId);
                  return;
              }

              var obj = docs[0];
              var value = obj["data"];

              if(op2=="eq")
              {
                  if(value==val2)
                  {
                      //res.send("Equal!");
                      f2 = 1;
                      //clearInterval(refreshIntervalId);
                      //return;
                  }
                  else
                    f2 = 0;
              }
              else if(op2=="le")
              {
                  if(value<=val2)
                  {
                      //res.send("Less or Equal!");
                      f2 = 1;
                      //clearInterval(refreshIntervalId);
                      //return;
                  }
                  else
                    f2 = 0;
              }
              else if(op2=="ge")
              {
                  if(value>=val2)
                  {
                      //res.send("Greater or Equal!");
                      //clearInterval(refreshIntervalId);
                      f2 = 1;
                      //return;
                  }
                  else
                    f2 = 0;
              }
              else if(op2=="lt")
              {
                  if(value<val2)
                  {
                      //res.send("Less Than!");
                      //clearInterval(refreshIntervalId);
                      f2 = 1;
                      //return;
                  }
                  else
                    f2 = 0;
              }
              else if(op2=="gt")
              {
                  if(value>val2)
                  {
                      //res.send("Greater Than!");
                      //clearInterval(refreshIntervalId);
                      //return;
                      f2 = 1;
                  }
                  else
                    f2 = 0;
              }

          });

          if(join=="and")
          {
              console.log("and " + f1 + " "+ f2);
              if(f1==1 && f2==1)
              {
                  res.send("ALERT !!!");
                  clearInterval(refreshIntervalId);
                  return;
              }
          }
          else if(join=="or")
          {
              if(f1==1 || f2==1)
              {
                  res.send("ALERT !!!");
                  clearInterval(refreshIntervalId);
                  return;
              }
          }

      }, 5000);
   }
 }catch(err)
 {
    console.log(err);
 }

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


app.listen(19620);

function createSensorCollection()
{
    var mydb = db;
    var col = db.get('sensordata');
    var col1 = db.get('repository');
    col.remove({}, {}, function(err, number){
        console.log('Total Entries removed: '+number);
        col1.find({},{},function(err, obj){

            console.log(obj.length);
            for(var i=0;i<obj.length;i++)
            {
                var o = obj[i];
                var id1 = o["id"];
                var t = o["type"];
                var lat = o["lat"];
                var lon = o["lon"];
                var time = new Date();
                col.insert({'id':id1, 'type':t, "lat": lat, "lon": lon, "data":0, "timestamp":time});
            }

        });
    });
}

//createSensorCollection()

function createPathData()
{
    var mydb = db;
    var col = db.get('pathdata');
    col.insert({'src': 'a', 'dest': 'b','path':'ab','lat':30, 'lon':50,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'a', 'dest': 'c','path':'ac','lat':50, 'lon':32,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'a', 'dest': 'd','path':'ad','lat':20, 'lon':54,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'b', 'dest': 'g','path':'bg','lat':35, 'lon':34,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'b', 'dest': 'f','path':'bf','lat':33, 'lon':12,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'c', 'dest': 'f','path':'cf','lat':54, 'lon':46,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'c', 'dest': 'e','path':'ce','lat':24, 'lon':34,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'd', 'dest': 'e','path':'de','lat':65, 'lon':23,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'g', 'dest': 'h','path':'gh','lat':24, 'lon':75,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'f', 'dest': 'h','path':'fh','lat':65, 'lon':87,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'f', 'dest': 'i','path':'fi','lat':56, 'lon':32,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'e', 'dest': 'i','path':'ei','lat':24, 'lon':88,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'h', 'dest': 'j','path':'hj','lat':45, 'lon':44,'traffic':0,'aid':'a-1'});
    col.insert({'src': 'i', 'dest': 'j','path':'ij','lat':32, 'lon':98,'traffic':0,'aid':'a-1'});

    col.insert({'src': 'a', 'dest': 'b','path':'ab','lat':30, 'lon':50,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'a', 'dest': 'c','path':'ac','lat':50, 'lon':32,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'a', 'dest': 'd','path':'ad','lat':20, 'lon':54,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'b', 'dest': 'g','path':'bg','lat':35, 'lon':34,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'b', 'dest': 'f','path':'bf','lat':33, 'lon':12,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'c', 'dest': 'f','path':'cf','lat':54, 'lon':46,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'c', 'dest': 'e','path':'ce','lat':24, 'lon':34,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'd', 'dest': 'e','path':'de','lat':65, 'lon':23,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'g', 'dest': 'h','path':'gh','lat':24, 'lon':75,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'f', 'dest': 'h','path':'fh','lat':65, 'lon':87,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'f', 'dest': 'i','path':'fi','lat':56, 'lon':32,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'e', 'dest': 'i','path':'ei','lat':24, 'lon':88,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'h', 'dest': 'j','path':'hj','lat':45, 'lon':44,'traffic':0,'aid':'a-2'});
    col.insert({'src': 'i', 'dest': 'j','path':'ij','lat':32, 'lon':98,'traffic':0,'aid':'a-2'});

    col.insert({'src': 'a', 'dest': 'b','path':'ab','lat':30, 'lon':50,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'a', 'dest': 'c','path':'ac','lat':50, 'lon':32,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'a', 'dest': 'd','path':'ad','lat':20, 'lon':54,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'b', 'dest': 'g','path':'bg','lat':35, 'lon':34,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'b', 'dest': 'f','path':'bf','lat':33, 'lon':12,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'c', 'dest': 'f','path':'cf','lat':54, 'lon':46,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'c', 'dest': 'e','path':'ce','lat':24, 'lon':34,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'd', 'dest': 'e','path':'de','lat':65, 'lon':23,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'g', 'dest': 'h','path':'gh','lat':24, 'lon':75,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'f', 'dest': 'h','path':'fh','lat':65, 'lon':87,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'f', 'dest': 'i','path':'fi','lat':56, 'lon':32,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'e', 'dest': 'i','path':'ei','lat':24, 'lon':88,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'h', 'dest': 'j','path':'hj','lat':45, 'lon':44,'traffic':0,'aid':'a-3'});
    col.insert({'src': 'i', 'dest': 'j','path':'ij','lat':32, 'lon':98,'traffic':0,'aid':'a-3'});

    col.insert({'src': 'a', 'dest': 'b','path':'ab','lat':30, 'lon':50,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'a', 'dest': 'c','path':'ac','lat':50, 'lon':32,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'a', 'dest': 'd','path':'ad','lat':20, 'lon':54,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'b', 'dest': 'g','path':'bg','lat':35, 'lon':34,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'b', 'dest': 'f','path':'bf','lat':33, 'lon':12,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'c', 'dest': 'f','path':'cf','lat':54, 'lon':46,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'c', 'dest': 'e','path':'ce','lat':24, 'lon':34,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'd', 'dest': 'e','path':'de','lat':65, 'lon':23,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'g', 'dest': 'h','path':'gh','lat':24, 'lon':75,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'f', 'dest': 'h','path':'fh','lat':65, 'lon':87,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'f', 'dest': 'i','path':'fi','lat':56, 'lon':32,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'e', 'dest': 'i','path':'ei','lat':24, 'lon':88,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'h', 'dest': 'j','path':'hj','lat':45, 'lon':44,'traffic':0,'aid':'a-4'});
    col.insert({'src': 'i', 'dest': 'j','path':'ij','lat':32, 'lon':98,'traffic':0,'aid':'a-4'});

}

//createPathData()

module.exports = app;
