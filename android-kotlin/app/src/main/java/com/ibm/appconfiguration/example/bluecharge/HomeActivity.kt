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
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ibm.appconfiguration.android.lib.AppConfiguration
import com.ibm.appconfiguration.android.lib.feature.models.Feature
import org.json.JSONException
import org.json.JSONObject

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private var mobile: LinearLayout? = null
    private var broadband: LinearLayout? = null
    private var dth: LinearLayout? = null
    private var bus: LinearLayout? = null
    private var flight: LinearLayout? = null
    private var toolbar_profile: ImageView? = null
    private var floating_profile: FloatingActionButton? = null

    var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mobile = findViewById<View>(R.id.mobile) as LinearLayout
        broadband = findViewById<View>(R.id.broadband) as LinearLayout
        dth = findViewById<View>(R.id.dth) as LinearLayout
        bus = findViewById<View>(R.id.bus) as LinearLayout
        flight = findViewById<View>(R.id.flight) as LinearLayout
        toolbar_profile =
            findViewById<View>(R.id.toolbar_profile) as ImageView
        floating_profile =
            findViewById<View>(R.id.floating_profile) as FloatingActionButton

        mobile!!.setOnClickListener(this)
        broadband!!.setOnClickListener(this)
        dth!!.setOnClickListener(this)
        bus!!.setOnClickListener(this)
        flight!!.setOnClickListener(this)
        toolbar_profile!!.setOnClickListener(this)
        floating_profile!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        pref = getSharedPreferences("email", Context.MODE_PRIVATE)
        val email = pref!!.getString("email", "defaultUser")

        val appConfiguration = AppConfiguration.getInstance()

        val floatingProfileFeature: Feature? = appConfiguration.getFeature("floating-profile")
        val isFloatingProfile = floatingProfileFeature?.isEnabled()

        if (isFloatingProfile!!) {
            toolbar_profile!!.visibility = View.INVISIBLE
            floating_profile!!.visibility = View.VISIBLE
        } else {
            toolbar_profile!!.visibility = View.VISIBLE
            floating_profile!!.visibility = View.INVISIBLE
        }


        val identityId = email
        val identityAttributes = JSONObject()
        try {
            identityAttributes.put("email", email)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val flightBookingFeature: Feature? = appConfiguration.getFeature("flight-booking")
        val isFlightBookingFeatureAllowed = flightBookingFeature?.getCurrentValue(identityId!!, identityAttributes)

        if (isFlightBookingFeatureAllowed!! as Boolean) {
            flight?.visibility = View.VISIBLE
        } else {
            flight?.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        val i: Intent

        when (v?.id) {
            R.id.mobile -> {
                i = Intent(this, MobileActivity::class.java)
                startActivity(i)
            }
            R.id.broadband -> {
                i = Intent(this, BroadbandActivity::class.java)
                startActivity(i)
            }
            R.id.dth -> {
                i = Intent(this, DTHActivity::class.java)
                startActivity(i)
            }
            R.id.bus -> {
                i = Intent(this, BusActivity::class.java)
                startActivity(i)
            }
            R.id.flight -> {
                i = Intent(this, FlightActivity::class.java)
                startActivity(i)
            }
            R.id.toolbar_profile, R.id.floating_profile -> {
                pref = getSharedPreferences("logged_In", Context.MODE_PRIVATE)

                i = if (pref!!.getBoolean("logged_In", false)) {
                    Intent(this, ProfileActivity::class.java)
                } else {
                    Intent(this, LoginRegisterActivity::class.java)
                }
                startActivity(i)
            }
            else -> {
            }
        }
    }
}