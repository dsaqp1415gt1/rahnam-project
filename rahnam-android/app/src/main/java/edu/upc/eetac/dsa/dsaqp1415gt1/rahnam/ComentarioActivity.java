package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.Comment;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.RahnamAPI;

/**
 * Created by roco on 8/06/15.
 */
public class ComentarioActivity extends Activity {
    private final static String TAG = ComentarioActivity.class.toString();
    String photoid = null;
    String user = null;
    Comment coment = new Comment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_comentarios);
        photoid = (String) getIntent().getExtras().get("photoid");

        //metodo para tener el password y el username siempre.
        SharedPreferences prefs = getSharedPreferences("rahnam-profile",
                Context.MODE_PRIVATE);
        final String username = prefs.getString("username", null);
        final String password = prefs.getString("password", null);
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password //Esto estaba mal en los gists
                        .toCharArray());
            }
        });

        //aqui cambio el valor y asi siempre me sale el nombre del usuario.
        TextView nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(username);
        user = username;
    }


    public void EnviarComentario (View v) {
        EditText comentario = (EditText) findViewById(R.id.etContentComentario);
        String x = (comentario.getText().toString());
        String valorStringPhotoid = String.valueOf(photoid);
        if (x.matches("")) {
            Context context = getApplicationContext();
            CharSequence text = "No puede ser un comentario nulo";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else {
            (new FetchEscribirComentarioTask()).execute(user,x,valorStringPhotoid);
        }
    }

    //metodo para postear el comentario
    private class FetchEscribirComentarioTask extends
            AsyncTask<String, Void, Comment> {
        private ProgressDialog pd;

        @Override
        protected Comment doInBackground(String... params) {
            Comment coment = null;
            try {
                coment = RahnamAPI.getInstance(ComentarioActivity.this).postComment(params[0],params[1],params[2]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return coment;
        }

        @Override
        protected void onPostExecute(Comment result) {
            startActivityFoto();
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(ComentarioActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    private void startActivityFoto() {
        Context context = getApplicationContext();
        CharSequence text = "Bien enviado";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        String valorStringPhotoid = String.valueOf(photoid);
        Intent intent = new Intent(this, FotoActivity.class);
        intent.putExtra("photoid", valorStringPhotoid);
        startActivity(intent);
        finish();
    }

    public void vuelveAlMain (View v){
        Intent intent = new Intent(this, RahnamMainActivity.class);
        startActivity(intent);
        finish();
    }

    public void SalirDeLaAplicacion(View v) {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
