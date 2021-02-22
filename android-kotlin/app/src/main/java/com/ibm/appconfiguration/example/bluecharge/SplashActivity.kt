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

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.ibm.appconfiguration.android.lib.AppConfiguration
import com.ibm.appconfiguration.android.lib.feature.FeaturesUpdateListener

class SplashActivity : AppCompatActivity() {
    var nDialog: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        nDialog = findViewById(R.id.progressBar)
        nDialog?.visibility = View.INVISIBLE
        setup()
    }

    private fun setup() {
        nDialog?.visibility = View.VISIBLE

        val expectedApiKey = "apikey"
        val expectedGuid = "guid"

        val appConfiguration = AppConfiguration.getInstance()
        appConfiguration.enableDebug(true)

        // NOTE: provide 'AppConfiguration.REGION_EU_GB' for London, and 'AppConfiguration.REGION_US_SOUTH' for Dallas
        appConfiguration.init(application, AppConfiguration.REGION_EU_GB, expectedGuid, expectedApiKey)
        appConfiguration.setCollectionId("blue-charge-android")

        appConfiguration.registerFeaturesUpdateListener(object : FeaturesUpdateListener {
            override fun onFeaturesUpdate() {
                nDialog?.visibility = View.INVISIBLE
                val mainIntent = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        })
    }
}
