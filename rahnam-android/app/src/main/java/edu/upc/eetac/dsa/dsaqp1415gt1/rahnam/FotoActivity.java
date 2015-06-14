package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.Comment;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.CommentCollection;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.Photo;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.RahnamAPI;

/**
 * Created by roco on 8/06/15.
 */
public class FotoActivity extends ListActivity {
    private final static String TAG = FotoActivity.class.toString();
    String photoid = null;
    String user = null;
    private ArrayList<Comment> listaComentarios;
    private ComentariosListAdapter adapter;
    String url;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foto);
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
        imageView = (ImageView) findViewById(R.id.ImageView);
        nombre.setText(username);
        user = username;

        listaComentarios = new ArrayList<Comment>();
        adapter = new ComentariosListAdapter(this, listaComentarios);
        setListAdapter(adapter);

        (new FetchPhotoResultTask()).execute(photoid);
        (new FetchComentariosTask()).execute(photoid);
    }

    //metodo asincrono para obtener la foto en background
    private class FetchPhotoResultTask extends
            AsyncTask<String, Void, Photo> {
        private ProgressDialog pd;

        @Override
        protected Photo doInBackground(String... params) {
            Photo photo = null;
            try {
                photo = RahnamAPI.getInstance(FotoActivity.this).getPhoto(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return photo;
        }

        @Override
        protected void onPostExecute(Photo result) {
            EditText titulo = (EditText) findViewById(R.id.etTituloDeLaFoto);
            titulo.setText(result.getTitle().toString());
            EditText descripcion = (EditText) findViewById(R.id.etDescripcion);
            descripcion.setText(result.getDescription().toString());
            (new FetchItems()).execute(result.getPhotoURL().toString());
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(FotoActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    //metodo asincrono para obtener los comentarios de la foto en background y ponerlos en la lista
    private class FetchComentariosTask extends
            AsyncTask<String, Void, CommentCollection> {
        private ProgressDialog pd;

        @Override
        protected CommentCollection doInBackground(String... params) {
            CommentCollection coments = null;
            try {
                coments = RahnamAPI.getInstance(FotoActivity.this).getComents(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return coments;
        }

        @Override
        protected void onPostExecute(CommentCollection result) {
            addComments(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(FotoActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    //metodo para añadir comentarios a la lista
    private void addComments(CommentCollection coments){
        listaComentarios.addAll(coments.getComments());
        adapter.notifyDataSetChanged();
    }

    public void SalirDeLaAplicacion(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void activaCrearComentarios (View v){
        String valorStringPhotoid = String.valueOf(photoid);
        Intent intent = new Intent(this, ComentarioActivity.class);
        intent.putExtra("photoid", valorStringPhotoid);
        startActivity(intent);
        finish();
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        final Comment coment = listaComentarios.get(position);
        String username = coment.getUsername();
        String usuariologeado = user;
        int comentid = coment.getCommentid();
        if (username.equals(usuariologeado)){
           //Creamos un dialogAlert, si este le da al boton de SI eliminar el mensaje, si no no.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Estas seguro de que quieres eliminar este comentario?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            (new FetchComentarioEliminarTask()).execute(String.valueOf(coment.getCommentid()));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            Context context = getApplicationContext();
            CharSequence text = "No puedes hacer nada con este comentario, no es tuyo.";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
    //metodo asincrono para eliminar el comentario
    private class FetchComentarioEliminarTask extends
            AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected String doInBackground(String... params) {
            String resultado = null;
            try {
                resultado = RahnamAPI.getInstance(FotoActivity.this).deleteComment(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(String result) {
            Context context = getApplicationContext();
            CharSequence text = result;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(FotoActivity.this);
            pd.setTitle("Elimando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    public void vuelveAlMain (View v){
        Intent intent = new Intent(this, RahnamMainActivity.class);
        startActivity(intent);
        finish();
    }

    private class FetchItems extends AsyncTask<String, Bitmap, Bitmap> {

        protected Bitmap doInBackground(String... params) {
            //Descargamos la imagen en bitmap y la almacenamos
            Bitmap imagenBitmap = downloadImage(params[0]);
            //Este return enviara la imagen al siguiente proceso, onPostExecute
            return imagenBitmap;   }

        protected void onPostExecute(Bitmap imagen) {
            //Colocamos la imagen que hemos obtenido en el ImageView
            try {
                imageView.setImageBitmap(imagen);
            } catch (Exception e) {}
        }

        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                //stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        private InputStream getHttpConnection(String urlString) throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }
}

