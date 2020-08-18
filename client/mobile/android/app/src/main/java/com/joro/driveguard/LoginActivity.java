package com.joro.driveguard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = "LoginActivity";

    @BindView(R.id.input_phone)
    EditText phoneText;
    @BindView(R.id.input_password)
    EditText passwordText;
    @BindView(R.id.btn_login)
    Button loginButton;
    @BindView(R.id.link_signup)
    TextView signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(v -> login());

        signupLink.setOnClickListener(v ->
        {
            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(Constants.serverUrl + "registration"));
            startActivity(viewIntent);
        });
    }

    public void login()
    {
        Log.d(TAG, "Login");

        if (!validate())
        {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Валидиране...");
        progressDialog.show();

        String phone = phoneText.getText().toString();
        String password = passwordText.getText().toString();

        try
        {
            requestAPIKey(phone, password, progressDialog);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void loginComplete(final JSONObject response, final ProgressDialog progressDialog)
    {
        Log.d(TAG, "Login complete");

        new android.os.Handler().postDelayed
        (
                () ->
                {
                    if ((response == null) || !response.has("firstName") || !response.has("phoneNumber") || !response.has("apikey"))
                    {
                        onLoginFailed();
                    }
                    else
                    {
                        try
                        {
                            final String userFirstName = response.getString("firstName");
                            final String userPhoneNumber =  response.getString("phoneNumber");
                            final String APIKey = response.getString("apikey");

                            Log.d(TAG, "First name: " + userFirstName + " Phone number: " + userPhoneNumber + " APIKey " + APIKey);

                            SharedPreferences sharedPreferences = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("APIKeySaved", true);
                            editor.putString("userFirstName", userFirstName);
                            editor.putString("userPhoneNumber", userPhoneNumber);
                            editor.putString("APIKey", APIKey);
                            editor.apply();

                            Constants.userFirstName = userFirstName;
                            Constants.userPhoneNumber = userPhoneNumber;
                            Constants.APIKey = APIKey;

                            onLoginSuccess();
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG, e.toString());
                            onLoginFailed();
                        }
                    }
                    progressDialog.dismiss();
                },
            3000
        );
    }

    @Override
    public void onBackPressed()
    {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess()
    {
        loginButton.setEnabled(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed()
    {
        Toast.makeText(getBaseContext(), "Неуспешен вход", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate()
    {
        boolean valid = true;

        String phone = phoneText.getText().toString();
        String password = passwordText.getText().toString();

        if (phone.isEmpty() || !phone.matches("08[789]\\d{7}"))
        {
            phoneText.setError("Въведете коректен телефонен номер");
            valid = false;
        } else
        {
            phoneText.setError(null);
        }

        if (password.isEmpty() || password.length() < 5)
        {
            passwordText.setError("Паролата трябва да е поне 5 символа");
            valid = false;
        } else
        {
            passwordText.setError(null);
        }

        return valid;
    }

    public void requestAPIKey(String phone, String password, final ProgressDialog progressDialog) throws JSONException
    {
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

        final String url = Constants.serverUrl + "androidLogin";

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("phoneNumber", phone);
        jsonObject.put("password", password);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
        (
          Request.Method.POST,
          url,
          jsonObject,
          response -> loginComplete(response, progressDialog),
          error ->
          {
              Log.d(TAG, error.toString());
              loginComplete(null, progressDialog);
          }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
