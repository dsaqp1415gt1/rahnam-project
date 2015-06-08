package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class RahnamMainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rahnam_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rahnam_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



 /*   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rahnam_main);

       /* stingsList = new ArrayList<Sting>();
        adapter = new StingAdapter(this, stingsList);
        setListAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("rahnam-profile",
                Context.MODE_PRIVATE);
        final String username = prefs.getString("username", null);
        final String userpass = prefs.getString("userpass", null);
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, userpass
                        .toCharArray());
            }
        });
        (new FetchStingsTask()).execute();
    }*/

    public void activarBusqueda(View v) {
        Intent intent = new Intent(this, BusquedaActivity.class);
        startActivity(intent);
        finish();
    }

    public void ActivarCamara(View v) {
        Intent intent = new Intent(this, TomarFotoActivity.class);
        startActivity(intent);
        finish();
    }

    public void ActivarPerfil(View v) {
        Intent intent = new Intent(this, PerfilActivity.class);
        startActivity(intent);
        finish();
    }

    public void SalirDeLaAplicacion(View v) {
        //aqui tenemos que llamar al layout login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
