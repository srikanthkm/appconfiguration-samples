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

package com.ibm.appconfiguration.example.bluecharge

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.ibm.appconfiguration.android.lib.AppConfiguration
import com.ibm.appconfiguration.android.lib.feature.models.Feature
import org.json.JSONException
import org.json.JSONObject

class FlightActivity : AppCompatActivity() {

    var pref: SharedPreferences? = null
    var couponsView: View? = null
    var textViewDiscountValue: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight)

        val spinner: Spinner = findViewById(R.id.travel_class_spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.travel_class_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        couponsView = findViewById(R.id.offer_coupon)
        textViewDiscountValue = findViewById(R.id.text_offer_value)

        pref = getSharedPreferences("email", Context.MODE_PRIVATE)
        val email = pref!!.getString("email", "defaultUser")

        val appConfiguration = AppConfiguration.getInstance()

        val identityId = email
        val identityAttributes = JSONObject()
        try {
            identityAttributes.put("email", email)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val flightBookingDiscountFeature: Feature? = appConfiguration.getFeature("discount-on-flight-booking")
        val isFlightBookingDiscountEnabled = flightBookingDiscountFeature?.isEnabled()
        val flightBookingDiscountValue = flightBookingDiscountFeature?.getCurrentValue(identityId!!, identityAttributes)


        if (isFlightBookingDiscountEnabled!!) {
            couponsView?.visibility = View.VISIBLE
            textViewDiscountValue!!.text = "Get " + flightBookingDiscountValue + "% Offer"
        } else {
            couponsView?.visibility = View.INVISIBLE
        }

    }

}