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

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment

class RegisterDialog : AppCompatDialogFragment() {
    var editTextEmail: TextView? = null
    var editTextPassword: TextView? = null
    var getEditTexConfirmPassword: TextView? = null
    var listener: RegisterDialogListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder =
            AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_register, null)
        builder.setView(view)
            .setTitle("REGISTER")
            .setNegativeButton(
                "Cancel"
            ) { dialogInterface, i -> }
            .setPositiveButton(
                "Register"
            ) { dialogInterface, i ->
                val email = editTextEmail!!.text.toString()
                val password = editTextPassword!!.text.toString()
                val confirmpassword =
                    getEditTexConfirmPassword!!.text.toString()
                listener!!.registerUser(email, password, confirmpassword)
            }
        editTextEmail = view.findViewById(R.id.EditTextEmailSignup)
        editTextPassword = view.findViewById(R.id.EditTextPasswordSignup)
        getEditTexConfirmPassword = view.findViewById(R.id.EditTextConfirmpasswordSignup)
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as RegisterDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                        "must implement RegisterDialogListener"
            )
        }
    }

    interface RegisterDialogListener {
        fun registerUser(
            email: String?,
            password: String?,
            confirmpassword: String?
        )
    }
}