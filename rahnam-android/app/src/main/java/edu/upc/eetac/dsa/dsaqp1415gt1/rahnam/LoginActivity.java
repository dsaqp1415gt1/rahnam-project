package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.RahnamAPI;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.User;

/**
 * Created by Cristina on 31/05/2015.
 */
public class LoginActivity extends Activity {
    private final static String TAG = LoginActivity.class.getName();
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

        if ((username != null) && (userpass != null)) { //Si usuario y contraseña no son nulos, inicia la actividad
            Context context = getApplicationContext();
            CharSequence text = "Ninguno de los campos debe de ser nulo";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        setContentView(R.layout.login_layout);
    }

//funcion cuando se le da click a entrar nos autentifique.
    public void signIn(View v) {

        EditText etUsername = (EditText) findViewById(R.id.etUsername);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);

        final String username = etUsername.getText().toString(); //Obtener usuario y contraseña
        final String password = etPassword.getText().toString();

        (new FetchLoginTask()).execute(username,password);
    }

//metodo asincrono para logearnos
    private class FetchLoginTask extends
            AsyncTask<String, Void, Boolean> {
        private ProgressDialog pd;
        @Override
        protected Boolean doInBackground(String... params) {
            Boolean correctLogin = false;
            try {
                user = RahnamAPI.getInstance(LoginActivity.this).checkLogin(params[0], params[1]);
                correctLogin = user.isLoginSuccessful();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return correctLogin;
        }

        @Override
        protected void onPostExecute(Boolean loginOK) {
            evaluateLogin(loginOK);
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

    private void evaluateLogin(Boolean loginOK) {
        if (loginOK) {
            EditText etUsername = (EditText) findViewById(R.id.etUsername);
            EditText etPassword = (EditText) findViewById(R.id.etPassword);

            final String username = etUsername.getText().toString();
            final String password = etPassword.getText().toString();

            SharedPreferences prefs = getSharedPreferences("rahnam-profile",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.putString("username", username);
            editor.putString("password", password);
            boolean done = editor.commit();
            if (done)
                Log.d(TAG, "preferences set");
            else
                Log.d(TAG, "preferences not set. THIS A SEVERE PROBLEM");

            startRahnam();

        } else {
            Context context = getApplicationContext();
            CharSequence text = "El usuario o la contraseña son incorrectos";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    //metodo para entrar en la actividad.
    private void startRahnam() {
        //String url = user.getLinks().get("photos").getTarget();
        Intent intent = new Intent(this, RahnamMainActivity.class);
        //intent.putExtra("url",url);
        startActivity(intent);
        finish(); //Si no acabamos la actividad de Login, al darle al botón "back" en el móvil volvería a ella
    }

    public void activarRegistrate (View v){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
