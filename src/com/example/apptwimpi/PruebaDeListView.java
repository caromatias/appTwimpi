package com.example.apptwimpi;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PruebaDeListView extends Activity {
    private ListView miLista;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_de_list_view);
 
        miLista = (ListView)findViewById(R.id.lista);
        //pongo mi lista en modo de selección múltiple
        //con esto, al hacer click en los elementos se seleccionarán automáticamente
        miLista.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
 
        //Hago un ArrayAdapter sencillo con muchas letras A <img src="http://androcode.es/wp-includes/images/smilies/icon_razz.gif" alt=":P" class="wp-smiley"> 
        String[] nombres=new String[]{"A","A","A","A","A","A","A","A","A","A","A","A","A","A","A","A","A","A","A","A","A","A","A","A","A"};
        
        ArrayAdapter<String> data=new ArrayAdapter<String>(this,R.layout.row2,R.id.titulo, nombres);
        //ArrayAdapter<String> data=new ArrayAdapter<String>(this,R.layout.row1,R.id.titulo, nombres);
        //Le asigno ese adapter a la lista
        miLista.setAdapter(data);
    }
 
    /**
     * Este método se llama al pulsar en el botón de borrar
     * como se definió en el layout res/layout/main.xml
     */
    public void deleteSelected(View view) {
        //Obtengo los elementos seleccionados de mi lista
        SparseBooleanArray seleccionados = miLista.getCheckedItemPositions();
 
        if(seleccionados==null || seleccionados.size()==0){
            //Si no había elementos seleccionados...
            Toast.makeText(this,"No hay elementos seleccionados",Toast.LENGTH_SHORT).show();
        }else{
            //si los había, miro sus valores
 
            //Esto es para ir creando un mensaje largo que mostraré al final
            StringBuilder resultado=new StringBuilder();
            resultado.append("Se eliminarán los siguientes elementos:\n");
 
            //Recorro my "array" de elementos seleccionados
            final int size=seleccionados.size();
            for (int i=0; i<size; i++) {
                //Si valueAt(i) es true, es que estaba seleccionado
                if (seleccionados.valueAt(i)) {
                    //en keyAt(i) obtengo su posición
                    resultado.append("El elemento "+seleccionados.keyAt(i)+" estaba seleccionado\n");
                }
            }
            Toast.makeText(this,resultado.toString(),Toast.LENGTH_LONG).show();
        }
    }
}