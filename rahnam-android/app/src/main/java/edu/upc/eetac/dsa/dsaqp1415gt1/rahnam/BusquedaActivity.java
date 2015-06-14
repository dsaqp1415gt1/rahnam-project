package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
public class BusquedaActivity extends ListActivity {
    private final static String TAG = BusquedaActivity.class.toString();
    String user = null;
    PhotoCollection photos = new PhotoCollection();
    private ArrayList<Photo> lista;
    private ImagenesListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busqueda);

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
    //metodo que se activa el boton para la busqueda
    public void clickMe (View v) {
        RadioButton radioButtonCategorias = (RadioButton) findViewById(R.id.radioButtonCategorias);
        RadioButton radioButtonUsuario = (RadioButton) findViewById(R.id.radioButtonUsuario);
        RadioButton radioButtonTitulo = (RadioButton) findViewById(R.id.radioButtonTitulo);
        EditText search = (EditText) findViewById(R.id.etBusqueda);
        final String busqueda = search.getText().toString();
        if (busqueda.equals("")){
            Context context = getApplicationContext();
            CharSequence text = "Debe poner algo para buscar, hagame el favor de hacerlo";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else {
            if (radioButtonCategorias.isChecked()==false && radioButtonTitulo.isChecked()==false && radioButtonUsuario.isChecked()==false)
            {
                Context context = getApplicationContext();
                CharSequence text = "Debes de elegir una forma de buscar";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else {
                if (radioButtonCategorias.isChecked()) {
                    lista = new ArrayList<Photo>();
                    adapter = new ImagenesListAdapter(this, lista);
                    setListAdapter(adapter);
                    (new FetchPhotosCategoriaTask()).execute(busqueda.toString());

                }
                if (radioButtonUsuario.isChecked()) {
                    lista = new ArrayList<Photo>();
                    adapter = new ImagenesListAdapter(this, lista);
                    setListAdapter(adapter);
                    (new FetchPhotosUsuarioTask()).execute(busqueda.toString());
                }
                if (radioButtonTitulo.isChecked()) {
                    lista = new ArrayList<Photo>();
                    adapter = new ImagenesListAdapter(this, lista);
                    setListAdapter(adapter);
                    (new FetchPhotosTituloTask()).execute(busqueda.toString());
                }
            }
        }
    }
    //metodo para obtener las fotos en background por usuario
    private class FetchPhotosCategoriaTask extends
            AsyncTask<String, Void, PhotoCollection> {
        private ProgressDialog pd;

        @Override
        protected PhotoCollection doInBackground(String... params) {
            PhotoCollection photos = null;
            try {
                photos = RahnamAPI.getInstance(BusquedaActivity.this).getPhotosByCategoria(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return photos;
        }

        @Override
        protected void onPostExecute(PhotoCollection result) {
            if (result.getPhotos().size() == 0) {
                Context context = getApplicationContext();
                CharSequence text = "No hay ninguna categoria con ese nombre";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else
            addPhotos(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(BusquedaActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }
    //metodo para obtener las fotos en background por usuario
    private class FetchPhotosUsuarioTask extends
            AsyncTask<String, Void, PhotoCollection> {
        private ProgressDialog pd;

        @Override
        protected PhotoCollection doInBackground(String... params) {
            PhotoCollection photos = null;
            try {
                photos = RahnamAPI.getInstance(BusquedaActivity.this).getPhotosByUser(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return photos;
        }

        @Override
        protected void onPostExecute(PhotoCollection result) {
            if (result.getPhotos().size() == 0) {
                Context context = getApplicationContext();
                CharSequence text = "No hay ningun usuario con ese username";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else
                addPhotos(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(BusquedaActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }
    //metodo para obtener las fotos en background por titulo
    private class FetchPhotosTituloTask extends
            AsyncTask<String, Void, PhotoCollection> {
        private ProgressDialog pd;

        @Override
        protected PhotoCollection doInBackground(String... params) {
            PhotoCollection photos = null;
            try {
                photos = RahnamAPI.getInstance(BusquedaActivity.this).getPhotosByTitulo(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return photos;
        }

        @Override
        protected void onPostExecute(PhotoCollection result) {
            if (result.getPhotos().size() == 0) {
                Context context = getApplicationContext();
                CharSequence text = "No hay ninguna foto con ese titulo";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else
                addPhotos(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(BusquedaActivity.this);
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
}