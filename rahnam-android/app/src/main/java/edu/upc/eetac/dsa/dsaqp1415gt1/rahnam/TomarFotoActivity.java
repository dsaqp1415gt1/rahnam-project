package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by roco on 8/06/15.
 */
public class TomarFotoActivity extends Activity {


    //OBJETOS DE LA INTERFAZ
    private ImageView imagen;
    //CONSTANTE FOTO
    private static final int FOTO = 1;
    String categorias = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tomar_la_foto);
        //instanciamos imagen
        imagen = (ImageView) findViewById(R.id.ImageView);
        //Cargamos las categorias en el EditText
        EditText x = (EditText) findViewById(R.id.etCategorias);

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

        if (getIntent().getExtras() != null) {
            categorias = (String) getIntent().getExtras().get("categorias");
            x.setText(categorias.toString());
        }
    }

    //metodo para recoger resultado
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Vemos si el código de la foto es igual a de nuestra constante FOTO
        if (requestCode == FOTO) {
            //recogemos foto en objeto Bitmap
            Bitmap miImagen = (Bitmap) data.getExtras().get("data");
            //Lo colocamos en el ImageView
            imagen.setImageBitmap(miImagen);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //metodo para invocar la camara
    public void activaLaCamara(View v) {
        Intent irAcamara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(irAcamara, FOTO);
    }

    public void startLista(View v) {
        Intent intent = new Intent(this, CategoriasListActivity.class);
        startActivity(intent);
        finish();
    }

    public void EnviarParametros(View v) {
        /*if (R.id.etDescripcion = "") {
            Toast toast = Toast.makeText(getBaseContext(), "Debes introducir una categoria, para hacerlo" +
                    "dale click encima de Cátegorias", Toast.LENGTH_SHORT);
            toast.show();
        } else {

        }*/
    }

    public void vuelveAlMain (View v){
        Intent intent = new Intent(this, RahnamMainActivity.class);
        startActivity(intent);
        finish();
    }
}
