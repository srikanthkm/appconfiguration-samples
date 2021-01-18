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
const { AppConfigurationFeature } = require('ibm-appconfiguration-node-feature');


function logincheck(req, res, next) {
    if (req.session && req.session.userId) {
        req.isLoggedInUser = true
    } else {
        req.isLoggedInUser = false
    }
    next();
}

function featurecheck(req, res, next) {
    const client = AppConfigurationFeature.getInstance();

    // fetch the feature `left-navigation-menu` and attach the isEnbaled() value to the request header
    const leftNavMenuFeature = client.getFeature('left-navigation-menu')
    req.leftNavMenu = leftNavMenuFeature.isEnabled();

    // fetch the feature `discount-on-flight-booking` and attach the isEnabled() value & getCurrentValue(req) value to the request header
    const discountFeature = client.getFeature('discount-on-flight-booking')
    req.discountEnabled = discountFeature.isEnabled()
    req.discountValue = discountFeature.getCurrentValue(req)        // feature evaluation via the req object

    next();
}

let loginAndFeatureCheck = [logincheck, featurecheck]

/* GET flightbooking page. */
router.get('/', loginAndFeatureCheck, function (req, res, next) {
    res.render('flights', { isLoggedInUser: req.isLoggedInUser, leftNavMenu: req.leftNavMenu, discountEnabled: req.discountEnabled, discountValue: req.discountValue, userEmail: req.session.userEmail });
});


module.exports = router;
