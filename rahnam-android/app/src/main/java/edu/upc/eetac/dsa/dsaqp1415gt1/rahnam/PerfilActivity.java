package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by roco on 8/06/15.
 */
public class PerfilActivity extends Activity {

 //OBJETOS DE LA INTERFAZ
    private ImageView imagenPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);
        //instanciamos imagen de perfil
        imagenPerfil = (ImageView) findViewById(R.id.FotoPerfil);
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

    public void EntrarEditarPerfil(View v) {
        //aqui tenemos que llamar al layout de editar el perfil
    }

    public void BotonVolverInicio(View v) {
        //aqui tenemos que llamar al layout de inicio
    }

}
