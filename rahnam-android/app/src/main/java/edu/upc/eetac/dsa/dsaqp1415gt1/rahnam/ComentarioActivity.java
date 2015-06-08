package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by roco on 8/06/15.
 */
public class ComentarioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_comentarios);

    }

    public void SalirDeLaAplicacion(View v) {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
