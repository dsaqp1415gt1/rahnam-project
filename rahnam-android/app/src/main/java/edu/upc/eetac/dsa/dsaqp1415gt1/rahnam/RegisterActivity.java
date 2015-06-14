package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.RahnamAPI;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.User;

/**
 * Created by Cristina on 31/05/2015.
 */
public class RegisterActivity extends Activity {

    private final static String TAG = RegisterActivity.class.getName();
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_layout);
    }

    //metodo para registrarse
    public void RegistrateYA(View v) {
        EditText etUsername = (EditText) findViewById(R.id.reg_nombre);
        EditText etName = (EditText) findViewById(R.id.reg_fullname);
        EditText etEmail = (EditText) findViewById(R.id.reg_email);
        EditText etPassword = (EditText) findViewById(R.id.reg_contraseña);
        EditText etPassword2 = (EditText) findViewById(R.id.reg_contraseñabis);
        RadioButton etHombre = (RadioButton) findViewById(R.id.radioButton);
        RadioButton etMujer = (RadioButton) findViewById(R.id.radioButton2);
        String username = etUsername.getText().toString();
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String password2 = etPassword2.getText().toString();
        String sexo = null;
        if (etHombre.isChecked() && etMujer.isChecked()){

            Context context = getApplicationContext();
            CharSequence text = "Eres de un solo género, por favor descamarca una";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }else {
            if (etHombre.isChecked()) {
                sexo = etHombre.getText().toString();
            }
            if (etMujer.isChecked()) {
                sexo = etMujer.getText().toString();
            }
        }
        if ((username.equals("")) || (name.equals("")) || (email.equals("")) || (password.equals("")) || (password2.equals("")) || (sexo.equals(""))) {
            Context context = getApplicationContext();
            CharSequence text = "Todos los campos son obligatorios";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (!password.equals(password2)) {
            Context context = getApplicationContext();
            CharSequence text = "Las contraseñas no coinciden";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (username.length() > 20) {
            Context context = getApplicationContext();
            CharSequence text = "El username es demasiado largo";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            (new FetchRegisterTask()).execute(username, name, email, password, sexo);
        }
    }

    //metodo para obtener las fotos en background
    private class FetchRegisterTask extends
            AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected String doInBackground(String... params) {
            String respuesta = null;
            try {
                respuesta = RahnamAPI.getInstance(RegisterActivity.this).createUser(params[0], params[1], params[2], params[3], params[4]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(String result) {
            Context context = getApplicationContext();
            CharSequence text = result;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(RegisterActivity.this);
            pd.setTitle("Registrandote...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    //metodo para salir de este layout.
    public void irAtras (View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
