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

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;

import com.ibm.appconfiguration.android.lib.AppConfiguration;
import com.ibm.appconfiguration.android.lib.feature.FeaturesUpdateListener;


public class SplashActivity extends AppCompatActivity {
    private ProgressBar nDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        nDialog = findViewById(R.id.progressBar);
        nDialog.setVisibility(View.INVISIBLE);
        setup();
    }

    private void setup() {
        nDialog.setVisibility(View.VISIBLE);

        String expectedApiKey = "apikey";
        String expectedGuid = "guid";

        AppConfiguration appConfiguration = AppConfiguration.getInstance();
        appConfiguration.enableDebug(true);

        // NOTE: provide 'AppConfiguration.REGION_EU_GB' for London, and 'AppConfiguration.REGION_US_SOUTH' for Dallas
        appConfiguration.init(getApplication(), AppConfiguration.REGION_EU_GB, expectedGuid, expectedApiKey);
        appConfiguration.setCollectionId("blue-charge-android");

        appConfiguration.registerFeaturesUpdateListener(new FeaturesUpdateListener() {
            @Override
            public void onFeaturesUpdate() {
                nDialog.setVisibility(View.INVISIBLE);
                Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
