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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class RegisterDialog extends AppCompatDialogFragment {

    TextView editTextEmail;
    TextView editTextPassword;
    TextView getEditTexConfirmPassword;
    RegisterDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_register, null);


        builder.setView(view)
                .setTitle("REGISTER")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = editTextEmail.getText().toString();
                        String password = editTextPassword.getText().toString();
                        String confirmpassword = getEditTexConfirmPassword.getText().toString();
                        listener.registerUser(email, password, confirmpassword);
                    }
                });
        editTextEmail = view.findViewById(R.id.EditTextEmailSignup);
        editTextPassword = view.findViewById(R.id.EditTextPasswordSignup);
        getEditTexConfirmPassword = view.findViewById(R.id.EditTextConfirmpasswordSignup);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (RegisterDialog.RegisterDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement RegisterDialogListener");
        }
    }

    public interface RegisterDialogListener {
        void registerUser(String email, String password, String confirmpassword);
    }
}