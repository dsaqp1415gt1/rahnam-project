package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.Photo;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.PhotoCollection;
import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.RahnamAPI;

/**
 * Created by roco on 8/06/15.
 */
public class PerfilActivity extends ListActivity {
    private final static String TAG = PerfilActivity.class.toString();

 //OBJETOS DE LA INTERFAZ
    private ImageView imagenPerfil;
    String user = null;
    PhotoCollection photos = new PhotoCollection();
    private ArrayList<Photo> lista;
    private ImagenesListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);
        user = (String) getIntent().getExtras().get("username");
        //instanciamos imagen de perfil
        imagenPerfil = (ImageView) findViewById(R.id.FotoPerfil);

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
        TextView NombreUsuario = (TextView) findViewById(R.id.NombreUsuario);
        NombreUsuario.setText(user);

        lista = new ArrayList<Photo>();
        adapter = new ImagenesListAdapter(this, lista);
        setListAdapter(adapter);
        (new FetchPhotosMainTask()).execute(username);
    }

    //metodo para obtener las fotos en background
    private class FetchPhotosMainTask extends
            AsyncTask<String, Void, PhotoCollection> {
        private ProgressDialog pd;

        @Override
        protected PhotoCollection doInBackground(String... params) {
            PhotoCollection photos = null;
            try {
                photos = RahnamAPI.getInstance(PerfilActivity.this).getPhotosByUser(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return photos;
        }

        @Override
        protected void onPostExecute(PhotoCollection result) {
            addPhotos(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(PerfilActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }
    //metodo para añadir foto
    private void addPhotos(PhotoCollection photos){
        lista.addAll(photos.getPhotos());
        adapter.notifyDataSetChanged();
    }

    //metodo para darle click a una foto y que me salgan sus detalles
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Photo photo = lista.get(position);
        int photoid = photo.getPhotoid();
        String valorStringPhotoid = String.valueOf(photoid);
        Log.d(TAG, photo.getLinks().get("self").getTarget());
        Intent intent = new Intent(this, FotoActivity.class);
        intent.putExtra("photoid", valorStringPhotoid);
        startActivity(intent);
        finish();
    }



    //metodo para llamar a la actividad de tomar una foto
    private void entrarActividadTomaFoto() {
        Intent intent = new Intent(this, TomarFotoActivity.class);
        startActivity(intent);
        finish();
    }

    public void SalirDeLaAplicacion(View v) {
        //aqui tenemos que llamar al layout login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void vuelveAlMain (View v){
        Intent intent = new Intent(this, RahnamMainActivity.class);
        startActivity(intent);
        finish();
    }
}
