package br.ufrr.promobile.ufrrmobile.ouvidoria;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.orhanobut.logger.Logger;

import java.util.LinkedList;
import java.util.List;

public class IdentificationActivity extends AppCompatActivity {
    private static final String SHARED_PREFERENCES = "user";
    private static final String SP_IS_REGISTERED = "isRegistered";
    private static final String SP_NAME = "name";
    private static final String SP_EMAIL = "email";
    private EditText etName;
    private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);

        etName = (EditText) findViewById(R.id.et_name);
        etEmail = (EditText) findViewById(R.id.et_email);

        String email = getUserEmail();

        etName.setText(getUsernameFromEmail(email));
        etEmail.setText(email);
    }

    public String getUsernameFromEmail(String email) {

        if (!email.isEmpty()) {
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0].replaceFirst(String.valueOf(parts[0].charAt(0)), String.valueOf(parts[0].charAt(0)).toUpperCase());
        }
        return null;
    }

    public String getUserEmail() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);

            if (!(email.isEmpty()))
                return email;
        }
        return null;

    }

    public void onGoButtonClick(View view){
        boolean pass = false;

        if(etName.getText().toString().isEmpty()){
            etName.setError(getResources().getText(R.string.str_msg_error_name));
            //Animation if error
            YoYo.with(Techniques.Tada)
                    .duration(700)
                    .playOn(etName);

            pass = false;
            Logger.d("Não passou no nome");

        }else{
            etName.setError(null);
            pass = true;
            Logger.d("Passou o nome!");
        }

        if (etEmail.getText().toString().isEmpty()){
            etEmail.setError(getResources().getText(R.string.str_msg_error_email));
            //Animation if error
            YoYo.with(Techniques.Tada)
                    .duration(700)
                    .playOn(etEmail);

            pass = false;
            Logger.d("Não passou no email");

        }else if (!isValidEmail(etEmail.getText().toString())){
            etEmail.setError(getResources().getText(R.string.str_msg_error_invalid_email));
            //Animation if error
            YoYo.with(Techniques.Tada)
                    .duration(700)
                    .playOn(etEmail);

            pass = false;
            Logger.d("Não passou o formato do email");

        }else{
            etEmail.setError(null);
            pass = true;
            Logger.d("Passou o email!");
        }

        if(pass){
            SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putBoolean(SP_IS_REGISTERED, pass);
            prefEditor.putString(SP_NAME, etName.getText().toString());
            prefEditor.putString(SP_EMAIL, etEmail.getText().toString());
            prefEditor.commit();

            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean isValidEmail(CharSequence email) {

            return (email == null) ? false : Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }
}
