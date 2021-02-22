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

package com.ibm.appconfiguration.example.bluecharge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ibm.appconfiguration.android.lib.AppConfiguration;
import com.ibm.appconfiguration.android.lib.feature.models.Feature;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mobile, broadband, dth, bus, flight;
    private ImageView toolbar_profile;
    private FloatingActionButton floating_profile;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mobile = (LinearLayout) findViewById(R.id.mobile);
        broadband = (LinearLayout) findViewById(R.id.broadband);
        dth = (LinearLayout) findViewById(R.id.dth);
        bus = (LinearLayout) findViewById(R.id.bus);
        flight = (LinearLayout) findViewById(R.id.flight);
        toolbar_profile = (ImageView) findViewById(R.id.toolbar_profile);
        floating_profile = (FloatingActionButton) findViewById(R.id.floating_profile);

        mobile.setOnClickListener(this);
        broadband.setOnClickListener(this);
        dth.setOnClickListener(this);
        bus.setOnClickListener(this);
        flight.setOnClickListener(this);
        toolbar_profile.setOnClickListener(this);
        floating_profile.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        pref = getSharedPreferences("email", Context.MODE_PRIVATE);
        String email = pref.getString("email", "defaultUser");

        AppConfiguration appConfiguration = AppConfiguration.getInstance();

        Feature floatingProfileFeature = (Feature) appConfiguration.getFeature("floating-profile");
        Boolean isFloatingProfile = (Boolean) floatingProfileFeature.isEnabled();

        if (isFloatingProfile) {
            toolbar_profile.setVisibility(View.INVISIBLE);
            floating_profile.setVisibility(View.VISIBLE);
        } else {
            toolbar_profile.setVisibility(View.VISIBLE);
            floating_profile.setVisibility(View.INVISIBLE);
        }


        String identityId = email;
        JSONObject identityAttributes = new JSONObject();
        try {
            identityAttributes.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Feature flightBookingFeature = appConfiguration.getFeature("flight-booking");
        Boolean isFlightBookingFeatureAllowed = (Boolean) flightBookingFeature.getCurrentValue(identityId, identityAttributes);

        if (isFlightBookingFeatureAllowed) {
            flight.setVisibility(View.VISIBLE);
        } else {
            flight.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()) {
            case R.id.mobile:
                i = new Intent(this, MobileActivity.class);
                startActivity(i);
                break;
            case R.id.broadband:
                i = new Intent(this, BroadbandActivity.class);
                startActivity(i);
                break;
            case R.id.dth:
                i = new Intent(this, DTHActivity.class);
                startActivity(i);
                break;
            case R.id.bus:
                i = new Intent(this, BusActivity.class);
                startActivity(i);
                break;
            case R.id.flight:
                i = new Intent(this, FlightActivity.class);
                startActivity(i);
                break;
            case R.id.toolbar_profile:
            case R.id.floating_profile:
                pref = getSharedPreferences("logged_In", Context.MODE_PRIVATE);

                if (pref.getBoolean("logged_In", false)) {
                    i = new Intent(this, ProfileActivity.class);
                } else {
                    i = new Intent(this, LoginRegisterActivity.class);
                }
                startActivity(i);
                break;

            default:
                break;

        }
    }


}