package com.example.android.myapp.registration;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapp.MainActivity;
import com.example.android.myapp.R;
import com.example.android.myapp.helper.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BasicRegistrationActivity_1 extends AppCompatActivity {

    private final int SIGNUP_REQUEST_CODE = 10;
    private final int SIGNIN_REQUEST_CODE = 100;

    private TextView tvMobileNo, tvGender, tvSignIn;
    private EditText etName, etFatherName;

    private RadioGroup radioGroup;
    private RadioButton selectedRadioButton;

    private FirebaseAuth firebaseAuth;

    private final List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.PhoneBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_registration_1);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.login);

        firebaseAuth = FirebaseAuth.getInstance();

        tvMobileNo = findViewById(R.id.tv_mobile_no);
        etName = findViewById(R.id.et_register_name);
        etFatherName = findViewById(R.id.et_register_father_name);
        tvGender = findViewById(R.id.tv_register_gender);
        tvSignIn = findViewById(R.id.tvSignIn);

        radioGroup = findViewById(R.id.registrationRadioGroup);

        radioGroup.setVisibility(View.GONE);

        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/myApp") ;
        dir.mkdirs();

        // On click listeners

        tvMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(etName.getText())) {
                    Toast.makeText(BasicRegistrationActivity_1.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etFatherName.getText())) {
                    Toast.makeText(BasicRegistrationActivity_1.this, "Enter Father's Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(BasicRegistrationActivity_1.this, "Select a gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        SIGNUP_REQUEST_CODE);
            }
        });

        tvGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.setVisibility(View.VISIBLE);
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        SIGNIN_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == SIGNUP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                    Toast.makeText(this, "Phone No. Verified", Toast.LENGTH_SHORT).show();

                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    selectedRadioButton = findViewById(selectedId);

                    final String phone_no = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

                    UserHelper userHelper = new UserHelper();
                    userHelper.setName(etName.getText().toString());
                    userHelper.setfName(etFatherName.getText().toString());
                    userHelper.setPhone(phone_no);
                    userHelper.setGender(selectedRadioButton.getText().toString());

                    finish();

                    Intent intent = new Intent(this, BasicRegistrationActivity_2.class);
                    intent.putExtra("user_data", userHelper);
                    startActivity(intent);
                }
            }
            // Sign up failed
            else {
                if (response == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //response.getError();
                }
            }

        }

        else if(requestCode == SIGNIN_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }

        }

    }


}
