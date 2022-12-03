package com.android.app_firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private EditText txtid, txtnom, txttel, txtcor;
    private Button btnbus, btnmod, btnreg, btneli;
    private ListView lvDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtid   = (EditText) findViewById(R.id.txtid);
        txtnom  = (EditText) findViewById(R.id.txtnom);
        txttel  = (EditText) findViewById(R.id.txttel);
        txtcor  = (EditText) findViewById(R.id.txtcor);
        btnbus  = (Button)   findViewById(R.id.btnbus);
        btnmod  = (Button)   findViewById(R.id.btnmod);
        btnreg  = (Button)   findViewById(R.id.btnreg);
        btneli  = (Button)   findViewById(R.id.btneli);
        lvDatos = (ListView) findViewById(R.id.lvDatos);

        botonBuscar();
        botonModificar();
        botonRegistrar();
        botonEliminar();

    }

    private void botonBuscar(){
        btnbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Digite el ID del contacto a buscar!!", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Agenda.class.getSimpleName());
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String aux = Integer.toString(id);
                            boolean res = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(aux.equalsIgnoreCase(x.child("id").getValue().toString())){
                                    res = true;
                                    ocultarTeclado();
                                    txtnom.setText(x.child("nombre").getValue().toString());
                                    txttel.setText(x.child("telefono").getValue().toString());
                                    txtcor.setText(x.child("correo").getValue().toString());
                                    break;
                                }
                            }
                            if(res == false){
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "ID ("+aux+") No encontrado!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    }

    private void botonModificar(){
        btnmod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtid.getText().toString().trim().isEmpty()
                        || txtnom.getText().toString().trim().isEmpty()
                        || txttel.getText().toString().trim().isEmpty()
                        || txtcor.getText().toString().trim().isEmpty()
                )
                {
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Complete los campos faltantes para actualizar!!", Toast.LENGTH_SHORT).show();

                }else{

                    int id = Integer.parseInt(txtid.getText().toString());
                    String nombre = txtnom.getText().toString();
                    String telefono = txttel.getText().toString();
                    String correo = txtcor.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Agenda.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            boolean res2 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("nombre").getValue().toString().equalsIgnoreCase(nombre)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "El nombre ("+nombre+") ya existe.\nimposible modificar!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                            if(res2 == false){
                                String aux = Integer.toString(id);
                                boolean res = false;
                                for(DataSnapshot x : snapshot.getChildren()){
                                    if(x.child("id").getValue().toString().equalsIgnoreCase(aux)){
                                        res = true;
                                        ocultarTeclado();
                                        x.getRef().child("nombre").setValue(nombre);
                                        x.getRef().child("telefono").setValue(telefono);
                                        x.getRef().child("correo").setValue(correo);
                                        limpiar();
                                        listarContactos();
                                        break;
                                    }
                                }

                                if(res == false){
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "ID ("+aux+") no encontrado.\nimposible modificar!!!!", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtnom.setText("");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    }

    private void botonRegistrar(){
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtid.getText().toString().trim().isEmpty()
                        || txtnom.getText().toString().trim().isEmpty()
                        || txttel.getText().toString().trim().isEmpty()
                        || txtcor.getText().toString().trim().isEmpty())
                {
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Complete los campos faltantes!!", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    String nombre = txtnom.getText().toString();
                    String telefono = txttel.getText().toString();
                    String correo = txtcor.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance(); // conexion a la base de datos
                    DatabaseReference dbref = db.getReference(Agenda.class.getSimpleName()); // referencia a la base de datos agenda

                    // evento de firebase que genera la tarea de insercion
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            /*String aux = Integer.toString(id);
                            boolean res1 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("id").getValue().toString().equalsIgnoreCase(aux)){
                                    res1 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "Error, el ID ("+aux+") ya existe!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            boolean res2 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("nombre").getValue().toString().equalsIgnoreCase(nombre)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "Error, el nombre ("+nombre+") ya existe!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            if(res1 == false && res2 == false){
                                Agenda agenda = new Agenda(id, nombre, telefono, correo);
                                dbref.push().setValue(agenda);
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "Contacto registrado correctamente!!", Toast.LENGTH_SHORT).show();
                                limpiar();
                            }*/

                            Agenda agenda = new Agenda(id, nombre, telefono, correo);
                            dbref.push().setValue(agenda);
                            ocultarTeclado();
                            Toast.makeText(MainActivity.this, "Contacto registrado correctamente!!", Toast.LENGTH_SHORT).show();
                            limpiar();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.


            }
        });
    }

    private void botonEliminar(){
        btneli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Digite el ID del contacto a eliminar!!", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Agenda.class.getSimpleName());
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String aux = Integer.toString(id);
                            final boolean[] res = {false};
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(aux.equalsIgnoreCase(x.child("id").getValue().toString())){
                                    AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                                    a.setCancelable(false);
                                    a.setTitle("Pregunta");
                                    a.setMessage("¿Está seguro(a) de querer eliminar el registro?");
                                    a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });

                                    a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            res[0] = true;
                                            ocultarTeclado();
                                            x.getRef().removeValue();
                                            listarContactos();
                                        }
                                    });
                                    a.show();
                                    break;
                                }
                            }

                            if(res[0] == false){
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "ID ("+aux+") No encontrado.\nimposible eliminar!!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    }

    private void listarContactos(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference(Agenda.class.getSimpleName());

        ArrayList<Agenda> listaagenda = new ArrayList<Agenda>();
        ArrayAdapter<Agenda> adapter = new ArrayAdapter <Agenda> (MainActivity.this, android.R.layout.simple_list_item_1, listaagenda);
        lvDatos.setAdapter(adapter);


        dbref.addChildEventListener(new ChildEventListener() {
            @Override
            //ocurre cuando se agregar un nuevo registro
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Agenda agenda = snapshot.getValue(Agenda.class);
                listaagenda.add(agenda);
                adapter.notifyDataSetChanged();
            }

            @Override
            // ocurre cuando se modifica o elimina un registro
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        lvDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Agenda agenda = listaagenda.get(i);
                AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                a.setCancelable(true);
                a.setTitle("Contacto Seleccionado");
                String msg = "ID : " + agenda.getId() +"\n\n";
                msg += "NOMBRE : " + agenda.getNombre();
                a.setMessage(msg);
                a.show();
            }
        });

    } // Cierra el método


    private void ocultarTeclado(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void limpiar() {
        txtnom.setText("");
        txttel.setText("");
        txtcor.setText("");
    }

}