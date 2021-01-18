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
var flightBookingRouter = require('./routes/flights')

var app = express();

/* FEATURE SDK init - start */
const { AppConfigurationCore, Logger } = require('ibm-appconfiguration-node-core');
const { AppConfigurationFeature } = require('ibm-appconfiguration-node-feature');


// NOTE: Add your custom values
const coreClient = AppConfigurationCore.getInstance({
  region: 'us-south',         //use `us-south` for Dallas. `eu-gb` for London
  guid: 'xxx-abc-xyz',
  apikey: 'xxx-abc-xyz',
})


const client = AppConfigurationFeature.getInstance({
  collectionId: 'blue-charge',
  liveFeatureUpdateEnabled: true
})

// Enable logger
var appconfigLogger = Logger.getInstance()
appconfigLogger.setDebug(true)

/* FEATURE SDK init - end */


// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));


// NOTE: USE EITHER OF BELOW url, options

/* when running the app locally */
const url = 'mongodb://127.0.0.1:27017/bluecharge'
const options = {
  useNewUrlParser: true,
  useUnifiedTopology: true
};

/* when running the app on IKS (IBM Kubernetes Service) */
const url = 'mongodb://mongodb-standalone-0.database:27017/bluecharge?authSource=admin'
const options = {
  user: process.env.MONGO_USER,
  pass: process.env.MONGO_ROOT_PASSWORD,
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


/* Below middleware will add "email" property to the req header, which is used by getCurrentValue() for feature evaluation */
app.use(function (req, res, next) {
  if (req.session.userEmail) {
    req.headers['email'] = req.session.userEmail
  }
  next()
})

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
