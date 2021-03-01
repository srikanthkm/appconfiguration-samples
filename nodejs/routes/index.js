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

var express = require('express');
var router = express.Router();
var User = require('../models/users');
const { AppConfiguration } = require('ibm-appconfiguration-node-sdk');
let leftNavMenu;
let flightBookingAllowed;


function logincheck(req, res, next) {
  if (req.session && req.session.userId) {
    req.isLoggedInUser = true

  } else {
    req.isLoggedInUser = false
  }

  next();
}

function featurecheck(req, res, next) {
  let identityId = req.session.userEmail ? req.session.userEmail : 'defaultUser';
  let identityAttributes = {
    'email': req.session.userEmail
  }
  const client = AppConfiguration.getInstance();

  // fetch the feature details of featureId `left-navigation-menu` and get the isEnabled() value of the feature
  const leftNavMenuFeature = client.getFeature('left-navigation-menu')
  leftNavMenu = leftNavMenuFeature.isEnabled();


  // fetch the feature details of featureId `flight-booking` and get the getCurrentValue(identityId, identityAttributes) value of the feature
  const flightBookingAllowedFeature = client.getFeature('flight-booking')
  flightBookingAllowed = flightBookingAllowedFeature.getCurrentValue(identityId, identityAttributes)

  next();
}

let loginAndFeatureCheck = [logincheck, featurecheck]

/* GET home page. */
router.get('/', loginAndFeatureCheck, function (req, res, next) {
  res.render('index', { isLoggedInUser: req.isLoggedInUser, userEmail: req.session.userEmail, leftNavMenu: leftNavMenu, flightBookingAllowed: flightBookingAllowed });
});

/* Login & Sign Up Code*/
router.post('/', function (req, res, next) {
  if (req.body.password !== req.body.passwordConf) {
    var err = new Error('Passwords do not match.');
    err.status = 400;
    res.send("passwords dont match");
    return next(err);
  }

  // on signup
  if (req.body.email &&
    req.body.password &&
    req.body.passwordConf) {

    var userData = {
      email: req.body.email,
      password: req.body.password,
    }

    User.create(userData, function (error, user) {
      if (error) {
        return next(error);
      } else {
        req.session.userId = user._id;
        req.session.userEmail = user.email;    //attaching a custom field called "userEmail" to session property of req object

        return res.redirect('/');
      }
    });

    // on login
  } else if (req.body.logemail && req.body.logpassword) {
    User.authenticate(req.body.logemail, req.body.logpassword, function (error, user) {
      if (error || !user) {
        var err = new Error('Wrong email or password');
        err.status = 401;
        return next(err);
      } else {
        req.session.userId = user._id;
        req.session.userEmail = user.email;      //attaching a custom field called "userEmail" to session property of req object

        return res.redirect('/');
      }
    });
  } else {
    var err = new Error('All fields required.');
    err.status = 400;
    return next(err);
  }
})


/* GET for logout logout */
router.get('/logout', function (req, res, next) {
  if (req.session) {
    req.session.destroy(function (err) {
      if (err) {
        return next(err);
      } else {
        return res.redirect('/');
      }
    });
  }
});

module.exports = router;
