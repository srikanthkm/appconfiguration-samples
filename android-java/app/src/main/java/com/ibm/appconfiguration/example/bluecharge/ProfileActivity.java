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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView Email, Message;
    Button LogoutButton;
    SharedPreferences pref, prefEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Email = (TextView) findViewById(R.id.profile_email);
        Message = (TextView) findViewById(R.id.profile_message);
        LogoutButton = (Button) findViewById(R.id.logoutButton);
        String welcomemessage = getString(R.string.welcome);

        LogoutButton.setOnClickListener(this);

        pref = getSharedPreferences("email", Context.MODE_PRIVATE);

        // Setting up email to TextView.
        Email.setText(pref.getString("email", "defaultUser"));
        Message.setText(welcomemessage + " " + pref.getString("email", "defaultUser"));
    }


    @Override
    public void onClick(View view) {

        pref = getSharedPreferences("logged_In", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("logged_In", false); // Storing boolean - true/false
        editor.apply(); // commit changes

        prefEmail = getSharedPreferences("email", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorEmail = prefEmail.edit();

        editorEmail.remove("email");
        editorEmail.apply();

        Toast.makeText(ProfileActivity.this, "Log Out Successful", Toast.LENGTH_LONG).show();

        finish();


    }
}

