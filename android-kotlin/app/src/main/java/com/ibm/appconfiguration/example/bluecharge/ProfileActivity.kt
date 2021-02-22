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
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    var Email: TextView? = null
    var Message: TextView? = null
    var LogoutButton: Button? = null
    var pref: SharedPreferences? = null
    var prefEmail: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        Email = findViewById<View>(R.id.profile_email) as TextView
        Message = findViewById<View>(R.id.profile_message) as TextView
        LogoutButton = findViewById<View>(R.id.logoutButton) as Button
        val welcomemessage = getString(R.string.welcome)

        LogoutButton!!.setOnClickListener(this)

        pref = getSharedPreferences("email", Context.MODE_PRIVATE)

        // Setting up email to TextView.

        // Setting up email to TextView.
        Email!!.text = pref!!.getString("email", "defaultUser")
        Message!!.text = welcomemessage + " " + pref!!.getString("email", "defaultUser")
    }

    override fun onClick(v: View?) {

        pref = getSharedPreferences("logged_In", Context.MODE_PRIVATE)
        val editor = pref!!.edit()

        editor.putBoolean("logged_In", false) // Storing boolean - true/false

        editor.apply() // commit changes


        prefEmail = getSharedPreferences("email", Context.MODE_PRIVATE)
        val editorEmail = prefEmail!!.edit()

        editorEmail.remove("email")
        editorEmail.apply()
        Toast.makeText(this@ProfileActivity, "Log Out Successful", Toast.LENGTH_LONG).show()

        finish()

    }
}