package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by roco on 8/06/15.
 */
public class CategoriasListActivity extends Activity {

    ListView myList;

    String[] listContent = {
            "videojuegos",
            "Anime",
            "Alimentacion",
            "Bebidas",
            "Arte",
            "Arquitectura",
            "Escalada",
            "Baseball",
            "Futbol",
            "Coches y motos",
            "Moda",
            "Viajes",
            "Animales",
            "Naturaleza",
            "Fotografia",
            "Peinados y maquillaje"
    };

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias_list);
        myList = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,
                listContent);
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myList.setAdapter(adapter);

    }


    public void aceptarCategorias(View v) {
        String selected = "";
        int cntChoice = myList.getCount();
        SparseBooleanArray sparseBooleanArray = myList.getCheckedItemPositions();
        for (int i = 0; i < cntChoice; i++) {

            if (sparseBooleanArray.get(i)) {
                selected += myList.getItemAtPosition(i).toString() + ", ";
            }
        }

        Toast.makeText(CategoriasListActivity.this,
                selected,
                Toast.LENGTH_LONG).show();

        String x = String.valueOf(selected);
        Intent intent = new Intent(this, TomarFotoActivity.class);
        intent.putExtra("categorias", x);
        startActivity(intent);
    }
}
