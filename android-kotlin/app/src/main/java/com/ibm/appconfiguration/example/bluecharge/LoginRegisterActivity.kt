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
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginRegisterActivity : AppCompatActivity(), LoginDialog.LoginDialogListener,
    RegisterDialog.RegisterDialogListener, View.OnClickListener {

    var F_Result = "Not_Found"
    var TempPassword = "NOT_FOUND"
    var cursor: Cursor? = null
    var cursorLogin: Cursor? = null
    var SQLiteDataBaseQueryHolder: String? = null
    var sqLiteDatabaseObj: SQLiteDatabase? = null
    var sqLiteDatabaseObjLogin: SQLiteDatabase? = null
    var EmailHolder: String? = null
    var PasswordHolder: kotlin.String? = null
    var ConfirmPasswordHolder: kotlin.String? = null
    var EmailHolderLogin: String? = null
    var PasswordHolderLogin: kotlin.String? = null
    var EditTextEmptyHolder: Boolean? = null
    var EditTextEmptyHolderLogin: Boolean? = null
    var sqLiteHelper: SQLiteHelper? = null
    var sqLiteHelperLogin: SQLiteHelper? = null

    var prefEmail: SharedPreferences? = null
    var prefLogin: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginregister)

        val loginButton =
            findViewById<View>(R.id.loginButton) as Button
        val registerButton =
            findViewById<View>(R.id.registerButton) as Button

        loginButton.setOnClickListener(this)
        registerButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loginButton -> {
                val loginDialog = LoginDialog()
                loginDialog.show(supportFragmentManager, "login dialog")
            }
            R.id.registerButton -> {
                val registerDialog = RegisterDialog()
                registerDialog.show(supportFragmentManager, "login dialog")
            }
            else -> {
            }
        }
    }

    override fun validateLogin(email: String?, password: String?) {

        sqLiteHelperLogin = SQLiteHelper(this)

        // Calling EditText is empty or no method.
        CheckEditTextStatusLogin(email!!, password!!)

        // Calling login method.
        LoginFunction()
    }

    // Checking EditText is empty or not.
    fun CheckEditTextStatusLogin(email: String, password: String) {

        // Getting value from All EditText and storing into String Variables.
        EmailHolderLogin = email
        PasswordHolderLogin = password

        // Checking EditText is empty or no using TextUtils.
        EditTextEmptyHolderLogin =
            !(TextUtils.isEmpty(EmailHolderLogin) || TextUtils.isEmpty(PasswordHolderLogin))
    }

    // Login function starts from here.
    fun LoginFunction() {
        if (EditTextEmptyHolderLogin!!) {

            // Opening SQLite database write permission.
            sqLiteDatabaseObjLogin = sqLiteHelperLogin!!.writableDatabase

            // Adding search email query to cursor.
            cursorLogin = sqLiteDatabaseObjLogin!!.query(
                SQLiteHelper.TABLE_NAME,
                null,
                " " + SQLiteHelper.Table_Column_1_Email + "=?",
                arrayOf(EmailHolderLogin!!),
                null,
                null,
                null
            )
            while (cursorLogin!!.moveToNext()) {
                if (cursorLogin!!.isFirst()) {
                    cursorLogin!!.moveToFirst()

                    // Storing Password associated with entered email.
                    TempPassword =
                        cursorLogin!!.getString(cursorLogin!!.getColumnIndex(SQLiteHelper.Table_Column_2_Password))

                    // Closing cursor.
                    cursorLogin!!.close()
                }
            }

            // Calling method to check final result ..
            CheckFinalResultLogin()
        } else {

            //If any of login EditText empty then this block will be executed.
            Toast.makeText(
                this@LoginRegisterActivity,
                "Please Enter Email or Password.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Checking entered password from SQLite database email associated password.
    fun CheckFinalResultLogin() {
        if (TempPassword.equals(PasswordHolderLogin, ignoreCase = true)) {
            prefEmail = getSharedPreferences("email", Context.MODE_PRIVATE)
            val editorEmail = prefEmail!!.edit()
            editorEmail.putString("email", EmailHolderLogin) // Storing string
            editorEmail.apply() // commit changes

            prefLogin = getSharedPreferences("logged_In", Context.MODE_PRIVATE)
            val editorLogin = prefLogin!!.edit()
            editorLogin.putBoolean("logged_In", true) // Storing boolean - true/false
            editorLogin.apply() // commit changes

            Toast.makeText(this@LoginRegisterActivity, "Login Successfully", Toast.LENGTH_LONG)
                .show()
            finish()
        } else {
            Toast.makeText(
                this@LoginRegisterActivity,
                "UserName or Password is Wrong, Please Try Again.",
                Toast.LENGTH_LONG
            ).show()
        }
        TempPassword = "NOT_FOUND"
    }


    override fun registerUser(email: String?, password: String?, confirmpassword: String?) {
        prefEmail = getSharedPreferences("email", Context.MODE_PRIVATE)
        val editorEmail = prefEmail!!.edit()

        editorEmail.putString("email", email) // Storing string

        editorEmail.apply() // commit changes


        sqLiteHelper = SQLiteHelper(this)

        // Creating SQLite database if dose n't exists
        SQLiteDataBaseBuild()

        // Creating SQLite table if dose n't exists.
        SQLiteTableBuild()

        // Checking EditText is empty or Not.
        CheckEditTextStatusRegister(email!!, password!!, confirmpassword!!)

        // Method to check Email is already exists or not.
        CheckingEmailAlreadyExistsOrNot()
    }

    // SQLite database build method.
    fun SQLiteDataBaseBuild() {
        sqLiteDatabaseObj = openOrCreateDatabase(
            SQLiteHelper.DATABASE_NAME,
            Context.MODE_PRIVATE,
            null
        )
    }

    // SQLite table build method.
    fun SQLiteTableBuild() {
        sqLiteDatabaseObj!!.execSQL("CREATE TABLE IF NOT EXISTS " + SQLiteHelper.TABLE_NAME + "(" + SQLiteHelper.Table_Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + SQLiteHelper.Table_Column_1_Email + " VARCHAR, " + SQLiteHelper.Table_Column_2_Password + " VARCHAR);")
    }

    // Method to check EditText is empty or Not.
    fun CheckEditTextStatusRegister(
        email: String,
        password: String,
        confirmpassword: String
    ) {

        // Getting value from All EditText and storing into String Variables.
        EmailHolder = email
        PasswordHolder = password
        ConfirmPasswordHolder = confirmpassword
        EditTextEmptyHolder =
            !(TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder) || TextUtils.isEmpty(
                ConfirmPasswordHolder
            ))
    }

    // Checking Email is already exists or not.
    fun CheckingEmailAlreadyExistsOrNot() {

        // Opening SQLite database write permission.
        sqLiteDatabaseObj = sqLiteHelper!!.writableDatabase

        // Adding search email query to cursor.
        cursor = sqLiteDatabaseObj!!.query(
            SQLiteHelper.TABLE_NAME,
            null,
            " " + SQLiteHelper.Table_Column_1_Email + "=?",
            arrayOf(EmailHolder!!),
            null,
            null,
            null
        )
        while (cursor!!.moveToNext()) {
            if (cursor!!.isFirst()) {
                cursor!!.moveToFirst()

                // If Email is already exists then Result variable value set as Email Found.
                F_Result = "Email Found"

                // Closing cursor.
                cursor!!.close()
            }
        }

        // Calling method to check final result and insert data into SQLite database.
        CheckFinalResult()
    }

    // Checking result
    fun CheckFinalResult() {

        // Checking whether email is already exists or not.
        if (F_Result.equals("Email Found", ignoreCase = true)) {

            // If email is exists then toast msg will display.
            Toast.makeText(this@LoginRegisterActivity, "Email Already Exists", Toast.LENGTH_LONG)
                .show()
        } else {

            // If email already dose n't exists then user registration details will entered to SQLite database.
            InsertDataIntoSQLiteDatabase()
        }
        F_Result = "Not_Found"
    }

    // Insert data into SQLite database method.
    fun InsertDataIntoSQLiteDatabase() {

        // If editText is not empty then this block will executed.
        if (EditTextEmptyHolder == true) {

            if (PasswordHolder != ConfirmPasswordHolder) {
                Toast.makeText(
                    this@LoginRegisterActivity,
                    "Password and Confirm Password do not match.",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                // SQLite query to insert data into table.
                SQLiteDataBaseQueryHolder =
                    "INSERT INTO " + SQLiteHelper.TABLE_NAME + " (email,password) VALUES('" + EmailHolder + "', '" + PasswordHolder + "');"

                // Executing query.
                sqLiteDatabaseObj!!.execSQL(SQLiteDataBaseQueryHolder)

                // Closing SQLite database object.
                sqLiteDatabaseObj!!.close()
                prefLogin = getSharedPreferences("logged_In", Context.MODE_PRIVATE)
                val editorLogin = prefLogin!!.edit()
                editorLogin.putBoolean("logged_In", true) // Storing boolean - true/false
                editorLogin.apply() // commit changes

                // Printing toast message after done inserting.
                Toast.makeText(
                    this@LoginRegisterActivity,
                    "User Registered Successfully",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        } else {

            // Printing toast message if any of EditText is empty.
            Toast.makeText(
                this@LoginRegisterActivity,
                "Please Fill All The Required Fields.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


}