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
const { AppConfiguration } = require('ibm-appconfiguration-node-sdk');
let leftNavMenu;
let discountEnabled;
let discountValue;


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

    // fetch the feature details of featureId `left-navigation-menu` and get the isEnabled() value
    const leftNavMenuFeature = client.getFeature('left-navigation-menu')
    leftNavMenu = leftNavMenuFeature.isEnabled();

    // fetch the feature details of featureId `discount-on-flight-booking` and get the isEnabled() value & getCurrentValue(identityId, identityAttributes) value of the feature
    const discountFeature = client.getFeature('discount-on-flight-booking')
    discountEnabled = discountFeature.isEnabled()
    discountValue = discountFeature.getCurrentValue(identityId, identityAttributes)

    next();
}

let loginAndFeatureCheck = [logincheck, featurecheck]

/* GET flightbooking page. */
router.get('/', loginAndFeatureCheck, function (req, res, next) {
    res.render('flights', { isLoggedInUser: req.isLoggedInUser, userEmail: req.session.userEmail, leftNavMenu: leftNavMenu, discountEnabled: discountEnabled, discountValue: discountValue });
});


module.exports = router;
