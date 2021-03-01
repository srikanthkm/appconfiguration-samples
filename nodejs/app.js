/*
 * (C) Copyright IBM Corp. 2021.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');

var mongoose = require('mongoose');
var session = require('express-session');
var MongoStore = require('connect-mongo')(session);

var indexRouter = require('./routes/index');
var flightBookingRouter = require('./routes/flights');

var app = express();

// App Configuration SDK require & init
const { AppConfiguration } = require('ibm-appconfiguration-node-sdk');

let region = process.env.REGION;
let guid = process.env.GUID;
let apikey = process.env.APIKEY;

const client = AppConfiguration.getInstance();

client.setDebug(true);             //enable debug
client.init(region, guid, apikey);
client.setCollectionId(process.env.COLLECTION_ID);

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));


// NOTE: USE EITHER OF BELOW url, options

/* when running the app locally */
// const url = 'mongodb://127.0.0.1:27017/bluecharge'
// const options = {
//   useNewUrlParser: true,
//   useUnifiedTopology: true
// };

/* when running the app on Kuberenetes with Minikube or on IKS (IBM Kubernetes Service) */
const url = 'mongodb://mongodb-standalone-0.database:27017/bluecharge?authSource=admin'     //`admin` is the database name associated with the user’s credentials
const options = {
  user: process.env.MONGO_USER,
  pass: process.env.MONGO_PWD,
  keepAlive: true,
  keepAliveInitialDelay: 300000,
  useNewUrlParser: true,
  useUnifiedTopology: true
};


//connect to MongoDB
mongoose.connect(url, options)
var db = mongoose.connection;

//handle mongo error
db.once('open', _ => {
  console.log('Database connected:', url)
})

db.on('error', err => {
  console.error('connection error:', err)
})

//use sessions for tracking logins
app.use(session({
  secret: 'work hard',
  resave: true,
  saveUninitialized: false,
  store: new MongoStore({
    mongooseConnection: db
  })
}));


app.use('/', indexRouter);
app.use('/flightbooking', flightBookingRouter)


// catch 404 and forward to error handler
app.use(function (req, res, next) {
  next(createError(404));
});

// error handler
app.use(function (err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');

});

module.exports = app;
