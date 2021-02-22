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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginRegisterActivity extends AppCompatActivity implements LoginDialog.LoginDialogListener, RegisterDialog.RegisterDialogListener, View.OnClickListener {

    String F_Result = "Not_Found";
    String TempPassword = "NOT_FOUND";
    Cursor cursor;
    Cursor cursorLogin;
    String SQLiteDataBaseQueryHolder;
    SQLiteDatabase sqLiteDatabaseObj;
    SQLiteDatabase sqLiteDatabaseObjLogin;
    String EmailHolder, PasswordHolder, ConfirmPasswordHolder;
    String EmailHolderLogin, PasswordHolderLogin;
    Boolean EditTextEmptyHolder;
    Boolean EditTextEmptyHolderLogin;
    SQLiteHelper sqLiteHelper;
    SQLiteHelper sqLiteHelperLogin;

    SharedPreferences prefEmail, prefLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_loginregister);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button registerButton = (Button) findViewById(R.id.registerButton);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.loginButton:
                LoginDialog loginDialog = new LoginDialog();
                loginDialog.show(getSupportFragmentManager(), "login dialog");
                break;

            case R.id.registerButton:
                RegisterDialog registerDialog = new RegisterDialog();
                registerDialog.show(getSupportFragmentManager(), "login dialog");
                break;

            default:
                break;

        }
    }

    @Override
    public void validateLogin(String email, String password) {


        prefEmail = getSharedPreferences("email", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorEmail = prefEmail.edit();

        editorEmail.putString("email", email); // Storing string
        editorEmail.apply(); // commit changes

        sqLiteHelperLogin = new SQLiteHelper(this);

        // Calling EditText is empty or no method.
        CheckEditTextStatusLogin(email, password);

        // Calling login method.
        LoginFunction();


    }

    // Checking EditText is empty or not.
    public void CheckEditTextStatusLogin(String email, String password) {

        // Getting value from All EditText and storing into String Variables.
        EmailHolderLogin = email;
        PasswordHolderLogin = password;

        // Checking EditText is empty or no using TextUtils.
        if (TextUtils.isEmpty(EmailHolderLogin) || TextUtils.isEmpty(PasswordHolderLogin)) {

            EditTextEmptyHolderLogin = false;

        } else {

            EditTextEmptyHolderLogin = true;
        }
    }

    // Login function starts from here.
    public void LoginFunction() {

        if (EditTextEmptyHolderLogin) {

            // Opening SQLite database write permission.
            sqLiteDatabaseObjLogin = sqLiteHelperLogin.getWritableDatabase();

            // Adding search email query to cursor.
            cursorLogin = sqLiteDatabaseObjLogin.query(SQLiteHelper.TABLE_NAME, null, " " + SQLiteHelper.Table_Column_1_Email + "=?", new String[]{EmailHolderLogin}, null, null, null);

            while (cursorLogin.moveToNext()) {

                if (cursorLogin.isFirst()) {

                    cursorLogin.moveToFirst();

                    // Storing Password associated with entered email.
                    TempPassword = cursorLogin.getString(cursorLogin.getColumnIndex(SQLiteHelper.Table_Column_2_Password));

                    // Closing cursor.
                    cursorLogin.close();
                }
            }

            // Calling method to check final result ..
            CheckFinalResultLogin();

        } else {

            //If any of login EditText empty then this block will be executed.
            Toast.makeText(LoginRegisterActivity.this, "Please Enter Email or Password.", Toast.LENGTH_LONG).show();

        }

    }

    // Checking entered password from SQLite database email associated password.
    public void CheckFinalResultLogin() {

        if (TempPassword.equalsIgnoreCase(PasswordHolderLogin)) {

            prefLogin = getSharedPreferences("logged_In", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorLogin = prefLogin.edit();

            editorLogin.putBoolean("logged_In", true); // Storing boolean - true/false
            editorLogin.apply(); // commit changes


            Toast.makeText(LoginRegisterActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();

            finish();


        } else {

            Toast.makeText(LoginRegisterActivity.this, "UserName or Password is Wrong, Please Try Again.", Toast.LENGTH_LONG).show();

        }
        TempPassword = "NOT_FOUND";

    }


    @Override
    public void registerUser(String email, String password, String confirmpassword) {

        sqLiteHelper = new SQLiteHelper(this);

        // Creating SQLite database if dose n't exists
        SQLiteDataBaseBuild();

        // Creating SQLite table if dose n't exists.
        SQLiteTableBuild();

        // Checking EditText is empty or Not.
        CheckEditTextStatusRegister(email, password, confirmpassword);

        // Method to check Email is already exists or not.
        CheckingEmailAlreadyExistsOrNot();


    }

    // SQLite database build method.
    public void SQLiteDataBaseBuild() {

        sqLiteDatabaseObj = openOrCreateDatabase(SQLiteHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);

    }

    // SQLite table build method.
    public void SQLiteTableBuild() {

        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " + SQLiteHelper.TABLE_NAME + "(" + SQLiteHelper.Table_Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + SQLiteHelper.Table_Column_1_Email + " VARCHAR, " + SQLiteHelper.Table_Column_2_Password + " VARCHAR);");

    }

    // Method to check EditText is empty or Not.
    public void CheckEditTextStatusRegister(String email, String password, String confirmpassword) {

        // Getting value from All EditText and storing into String Variables.
        EmailHolder = email;
        PasswordHolder = password;
        ConfirmPasswordHolder = confirmpassword;

        if (TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder) || TextUtils.isEmpty(ConfirmPasswordHolder)) {

            EditTextEmptyHolder = false;

        } else {

            EditTextEmptyHolder = true;
        }
    }

    // Checking Email is already exists or not.
    public void CheckingEmailAlreadyExistsOrNot() {

        // Opening SQLite database write permission.
        sqLiteDatabaseObj = sqLiteHelper.getWritableDatabase();

        // Adding search email query to cursor.
        cursor = sqLiteDatabaseObj.query(SQLiteHelper.TABLE_NAME, null, " " + SQLiteHelper.Table_Column_1_Email + "=?", new String[]{EmailHolder}, null, null, null);

        while (cursor.moveToNext()) {

            if (cursor.isFirst()) {

                cursor.moveToFirst();

                // If Email is already exists then Result variable value set as Email Found.
                F_Result = "Email Found";

                // Closing cursor.
                cursor.close();
            }
        }

        // Calling method to check final result and insert data into SQLite database.
        CheckFinalResult();

    }


    // Checking result
    public void CheckFinalResult() {

        // Checking whether email is already exists or not.
        if (F_Result.equalsIgnoreCase("Email Found")) {

            // If email is exists then toast msg will display.
            Toast.makeText(LoginRegisterActivity.this, "Email Already Exists", Toast.LENGTH_LONG).show();

        } else {

            // If email already dose n't exists then user registration details will entered to SQLite database.
            InsertDataIntoSQLiteDatabase();

        }

        F_Result = "Not_Found";

    }

    // Insert data into SQLite database method.
    public void InsertDataIntoSQLiteDatabase() {

        // If editText is not empty then this block will executed.
        if (EditTextEmptyHolder == true) {

            if (!PasswordHolder.equals(ConfirmPasswordHolder)) {
                Toast.makeText(LoginRegisterActivity.this, "Password and Confirm Password do not match.", Toast.LENGTH_LONG).show();
            } else {
                // SQLite query to insert data into table.
                SQLiteDataBaseQueryHolder = "INSERT INTO " + SQLiteHelper.TABLE_NAME + " (email,password) VALUES('" + EmailHolder + "', '" + PasswordHolder + "');";

                // Executing query.
                sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);

                // Closing SQLite database object.
                sqLiteDatabaseObj.close();

                prefEmail = getSharedPreferences("email", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorEmail = prefEmail.edit();

                editorEmail.putString("email", EmailHolder); // Storing string
                editorEmail.apply(); // commit changes

                prefLogin = getSharedPreferences("logged_In", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorLogin = prefLogin.edit();

                editorLogin.putBoolean("logged_In", true); // Storing boolean - true/false
                editorLogin.apply(); // commit changes

                // Printing toast message after done inserting.
                Toast.makeText(LoginRegisterActivity.this, "User Registered Successfully", Toast.LENGTH_LONG).show();


                finish();
            }

        }
        // This block will execute if any of the registration EditText is empty.
        else {

            // Printing toast message if any of EditText is empty.
            Toast.makeText(LoginRegisterActivity.this, "Please Fill All The Required Fields.", Toast.LENGTH_LONG).show();

        }

    }

}
