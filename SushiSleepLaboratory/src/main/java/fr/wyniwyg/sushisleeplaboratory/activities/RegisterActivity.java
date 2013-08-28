package fr.wyniwyg.sushisleeplaboratory.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import fr.wyniwyg.sushisleeplaboratory.utils.InternetConnection;
import fr.wyniwyg.sushisleeplaboratory.R;

public class RegisterActivity extends Activity {

    //UI Components
    private EditText lastNameView;
    private EditText firstNameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText passwordConfirmView;
    private RadioGroup genderView;
    private RadioButton gender;
    private Button birthDateView;
    private TextView mRegStatusMessageView;
    private View mRegisterFormView, mRegisterStatusView;

    //values stored at the time of the submitting
    private String email, password, passwordConfirmed, lastname, firstname;
    private int mDay = -1, mMonth = -1 /*0-11*/ , mYear = -1;
    //millis time for birth date
    private long birth = -1;

    private Context m_context;
    private String sushi_sleep_server = getString(R.string.sushisleep_url);

    private UserRegisterTask mRegTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        m_context = this;

        lastNameView = (EditText) findViewById(R.id.reg_lastname);
        firstNameView = (EditText) findViewById(R.id.reg_firstname);
        emailView = (EditText) findViewById(R.id.reg_email);
        passwordView = (EditText) findViewById(R.id.reg_password);
        passwordConfirmView = (EditText) findViewById(R.id.reg_password_confirm);
        genderView = (RadioGroup) findViewById(R.id.gender);
        birthDateView = (Button) findViewById(R.id.birthDate);
        mRegStatusMessageView = (TextView) findViewById(R.id.reg_status_message);

        mRegisterStatusView = findViewById(R.id.register_status);
        mRegisterFormView = findViewById(R.id.register_form);
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmit();
            }
        });
    }

    public void attemptSubmit(){
        if (mRegTask != null) {
            return;
        }

        // Reset errors.
        lastNameView.setError(null);
        firstNameView.setError(null);
        emailView.setError(null);
        passwordView.setError(null);
        passwordConfirmView.setError(null);

        // Store values at the time of the submit attempt.
        email = emailView.getText().toString();
        password = passwordView.getText().toString();
        passwordConfirmed = passwordConfirmView.getText().toString();
        lastname = lastNameView.getText().toString();
        firstname = firstNameView.getText().toString();
        gender = (RadioButton) findViewById(genderView.getCheckedRadioButtonId());

        boolean cancel = false;
        View focusView = null;

        //check for a valid lastname
        if(lastname.isEmpty()) {
            lastNameView.setError(getString(R.string.error_field_required));
            focusView = lastNameView;
            cancel = true;
        }

        //check for a valid firstname
        if(firstname.isEmpty()) {
            firstNameView.setError(getString(R.string.error_field_required));
            focusView = firstNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!email.contains("@")) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(passwordConfirmed)) {
            passwordConfirmView.setError(getString(R.string.error_field_required));
            focusView = passwordConfirmView;
            cancel = true;
        } else if (!TextUtils.equals(password, passwordConfirmed)) {
            passwordConfirmView.setError(getString(R.string.error_password_confirmation));
            focusView = passwordConfirmView;
            cancel = true;
        }

        if (gender == null) {
            Toast.makeText(m_context, getString(R.string.error_gender_field_required), Toast.LENGTH_LONG).show();
            focusView = genderView;
            cancel = true;
        }

        if(mYear != -1 && mMonth != -1 && mDay != -1) {
            final Calendar c = Calendar.getInstance();
            c.set(mYear, mMonth, mDay);
            birth = c.getTimeInMillis();
        }
        else {
            birthDateView.setError(getString(R.string.error_field_required));
            focusView = birthDateView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt submit and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user submit attempt.
            mRegStatusMessageView.setText(R.string.register_progress_submitting);
            showProgress(true);
            mRegTask = new UserRegisterTask();
            //mRegTask.execute((Void) null);
            Toast.makeText(this, getString(R.string.error_user_creation), Toast.LENGTH_LONG);
        }
    }

    public void openDatePickerDialog(View v){

        final Calendar c = Calendar.getInstance();
        int year = mYear == -1 ? c.get(Calendar.YEAR) : mYear;
        int month = mMonth == -1 ? c.get(Calendar.MONTH) : mMonth;
        int day = mDay == -1 ? c.get(Calendar.DAY_OF_MONTH) : mDay;

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDay = dayOfMonth;
                mMonth = monthOfYear;
                mYear = year;
                String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                birthDateView.setText(date);
            }
        }, year, month, day).show();
    }

    /**
     * Shows the progress UI and hides the register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterStatusView.setVisibility(View.VISIBLE);
            mRegisterStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mRegisterFormView.setVisibility(View.VISIBLE);
            mRegisterFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            if (! InternetConnection.isNetworkAvailable(m_context))
            {
                Toast.makeText(m_context, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean connected = false;
            String response = null;
            try {
                connected = InternetConnection.testConnectionTo(new URL(sushi_sleep_server+"/users"), m_context);
            }catch (MalformedURLException e) {
                return false;
            }catch (IOException e){
                return false;
            }
            if (!connected)
                return false;
            URL request = null;
            try {
                request = new URL(sushi_sleep_server+"/rest/users/"); //TODO
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                response = InternetConnection.getResponse(request);
            } catch (IOException e) {
                return false;
            }

            JsonReader reader = Json.createReader(new StringReader(response));
            JsonObject jo = reader.readObject();

            return jo.get("status").toString().equals("Success");
        }
    }
}
