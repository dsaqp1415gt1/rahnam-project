package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.RahnamAPI;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.User;

/**
 * Created by Cristina on 31/05/2015.
 */
public class LoginActivity extends Activity {


    private final static String TAG = LoginActivity.class.getName();
    String username;
    User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences prefs = getSharedPreferences("rahnam-profile",
                Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);
        String userpass = prefs.getString("userpass", null);


        setContentView(R.layout.login_layout);
    }

/*
    public void signIn(View v) {
        EditText etUsername = (EditText) findViewById(R.id.etUsername);
        EditText etUserpass = (EditText) findViewById(R.id.etUserpass);

        username = etUsername.getText().toString(); //obtiene user y pass
        String userpass = etUserpass.getText().toString();

        // Launch a background task to check if credentials are correct
        // If correct, store username and password and start Beeter activity
        // else, handle error

        // I'll suppose that u/p are correct:

        (new FetchLoginTask()).execute(username,userpass);



        SharedPreferences prefs = getSharedPreferences("rahnam-profile",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putString("username", username);
        editor.putString("userpass", userpass);
        boolean done = editor.commit();
        if (done)
            Log.d(TAG, "preferences set");
        else
            Log.d(TAG, "preferences not set. THIS A SEVERE PROBLEM");

        finish();
    }

    private void startRahnamActivity(String user) {
        Intent intent = new Intent(this, RahnamMainActivity.class);
        intent.putExtra("username", user);
        startActivity(intent);
        finish();
    }

    private class FetchLoginTask extends
            AsyncTask<String, Void, User> {
        private ProgressDialog pd;

        @Override
        protected User doInBackground(String... params) {
            try {
                user = RahnamAPI.getInstance(LoginActivity.this).getUser(params[0],params[1]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User result) {

            String user = result.getUsername();



            if ((user.equals(username))) {

                startRahnamActivity(user);
            }else{

                finish();
            }
            if (pd != null) {
                pd.dismiss();
            }

        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setTitle("Iniciando Sesion...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }


*/



}
