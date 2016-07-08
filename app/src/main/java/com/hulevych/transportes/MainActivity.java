package com.hulevych.transportes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerOrigem;
    private Spinner spinnerDestino;
    private  List<String> transportes;
    private  List<Estacao> estacoes;
    private Servico ser;
    List<String> lista_est;
    List<String> lista_est_destino;

    private int min;
    private int hour;
    private int week;
    private int isAm;

    private List<String> horas;
    private int nOrigem;
    private int nDestino;
    private int nTransporte=0;

    private Context context;

    private float x1,x2;
    static final int MIN_DISTANCE = 250;

    private String origem;
    private String destino;

    private  List<Integer> destinos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        }

        AssetManager am = getAssets();

        try {
            InputStream inputStream = openFileInput("saved.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                nTransporte = Integer.parseInt(stringBuilder.toString());

            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        context =this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerOrigem = (Spinner) findViewById(R.id.origem);
        spinnerDestino = (Spinner) findViewById(R.id.destino);

        ser = new Servico(am);
        transportes = ser.getTransportes();
        if(nTransporte>transportes.size()-1)
            nTransporte=0;

        estacoes = ser.getParagens(transportes.get(nTransporte));

        lista_est = new ArrayList<String>();
        lista_est_destino = new ArrayList<String>();
        for (Estacao e: estacoes)
            lista_est.add(e.getName());

        horas = ser.getHoras(transportes.get(nTransporte),nOrigem,nDestino, week, hour+":"+min);
        destinos = ser.getDestinos(transportes.get(nTransporte));
        for(Integer i: destinos) {
            if(i!=0)
                lista_est_destino.add(estacoes.get(i).getName());
        }

        nOrigem=0;
        nDestino=destinos.get(1);

        origem=lista_est.get(0);
        destino=lista_est.get(destinos.get(1));

        calculateHours();

        spinnerOrigem.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(!origem.equals(spinnerOrigem.getSelectedItem().toString())) {
                    origem=spinnerOrigem.getSelectedItem().toString();
                    lista_est_destino = new ArrayList<String>();
                    for (Estacao e : estacoes) {
                        if (e.getName().equals(spinnerOrigem.getSelectedItem().toString())) {
                           if (e.getNumero() == 0) {
                               for(Integer i: destinos) {
                                   if(i!=0)
                                       lista_est_destino.add(estacoes.get(i).getName());
                               }
                                nDestino=destinos.get(1);
                                destino=lista_est.get(destinos.get(1));
                            } else if (e.getNumero() == (estacoes.size() - 1)) {
                               for(Integer i: destinos) {
                                   if(i!=estacoes.size()-1)
                                       lista_est_destino.add(estacoes.get(i).getName());
                               }
                               nDestino=destinos.get(0);
                               destino=lista_est.get(destinos.get(0));

                            } else {
                               for(Integer i: destinos) {
                                   if(i!=e.getNumero())
                                       lista_est_destino.add(estacoes.get(i).getName());
                               }
                               nDestino=destinos.get(0);
                               destino=lista_est.get(destinos.get(0));
                            }

                            nOrigem = e.getNumero();
                            calculateHours();
                            break;
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinnerDestino.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(!destino.equals(spinnerDestino.getSelectedItem().toString())) {
                    for (Estacao e : estacoes) {
                        if (e.getName().equals(spinnerDestino.getSelectedItem().toString())) {
                            nDestino = e.getNumero();
                            calculateHours();
                            destino=spinnerDestino.getSelectedItem().toString();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    @Override
    public void onPause() {
        super.onPause();


        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("saved.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(""+nTransporte);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        calculateHours();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if ((x2>x1 || x2<x1) && Math.abs(deltaX) > MIN_DISTANCE) {

                    if (x2>x1)
                        nTransporte = (nTransporte + 1) % (transportes.size());
                    else{
                        nTransporte = nTransporte-1;
                        if(nTransporte==-1)
                            nTransporte=transportes.size()-1;
                    }

                    estacoes = ser.getParagens(transportes.get(nTransporte));

                    horas = ser.getHoras(transportes.get(nTransporte), nOrigem, nDestino, week, hour + ":" + min);
                    destinos = ser.getDestinos(transportes.get(nTransporte));

                    lista_est = new ArrayList<String>();
                    lista_est_destino = new ArrayList<String>();
                    for (Estacao e : estacoes)
                        lista_est.add(e.getName());

                    for(Integer i: destinos) {
                        if(i!=0)
                            lista_est_destino.add(estacoes.get(i).getName());
                    }

                    nOrigem=0;
                    nDestino=destinos.get(1);

                    origem=lista_est.get(0);
                    destino=lista_est.get(destinos.get(1));

                    calculateHours();

                }

                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("nOrigem", nOrigem);
        savedInstanceState.putInt("nDestino", nDestino);
        savedInstanceState.putInt("nTransporte", nTransporte);
        savedInstanceState.putString("origem", origem);
        savedInstanceState.putString("destino", destino);

        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        nOrigem = savedInstanceState.getInt("nOrigem");
        nDestino = savedInstanceState.getInt("nDestino");
        nTransporte = savedInstanceState.getInt("nTransporte");
        origem = savedInstanceState.getString("origem");
        destino = savedInstanceState.getString("destino");

        estacoes = ser.getParagens(transportes.get(nTransporte));

        horas = ser.getHoras(transportes.get(nTransporte), nOrigem, nDestino, week, hour + ":" + min);
        destinos = ser.getDestinos(transportes.get(nTransporte));

        lista_est = new ArrayList<String>();
        lista_est_destino = new ArrayList<String>();
        for (Estacao e : estacoes)
            lista_est.add(e.getName());

        for(Integer i: destinos) {
            if(i!=0)
                lista_est_destino.add(estacoes.get(i).getName());
        }

        calculateHours();

    }


    private void calculateHours(){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_style, lista_est);
        spinnerOrigem.setAdapter(adapter);
        spinnerOrigem.setSelection(nOrigem);


        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_style, lista_est_destino);
        spinnerDestino.setAdapter(adapter2);
        for(int i=0; i<lista_est_destino.size();i++){
            if(lista_est_destino.get(i).equals(estacoes.get(nDestino).getName()))
                spinnerDestino.setSelection(i);
        }

        Calendar cal = Calendar.getInstance();
        min = cal.get(Calendar.MINUTE);
        hour = cal.get(Calendar.HOUR);
        week = cal.get(Calendar.DAY_OF_WEEK);
        isAm = cal.get(Calendar.AM_PM);

        if(isAm==1)
            hour+=12;

        week=week-1;
        if(week==0)
            week=7;

        horas = ser.getHoras(transportes.get(nTransporte),nOrigem,nDestino, week, hour+":"+min);
        //dia 1 é o domingo
        List<String> horasTemp = new ArrayList<String>();
        for(String s: horas){
            String[] hora = s.split(":");
            if(Integer.parseInt(hora[0])>=hour && !horasTemp.contains(s))
                horasTemp.add(s);
        }

        for(String s: horas){
            String[] hora = s.split(":");
            if(Integer.parseInt(hora[0])<hour && !horasTemp.contains(s))
                horasTemp.add(s);
            if(horasTemp.size()==6)
                break;
        }

        horas=horasTemp;

        week=week+1;
        if(week==8)
            week=1;

        List<String> horas2;
        if (horas.size()<6){
            horas2 = ser.getHoras(transportes.get(nTransporte), nOrigem, nDestino, week, "0:0");
            int i=0;
            while(horas.size()<6) {
                horas.add(horas2.get(i));
                i++;
            }
        }


        //ordenando as horas


        TextView tv = (TextView)findViewById(R.id.nome);
        tv.setText(transportes.get(nTransporte));

        TextView tv1 = (TextView)findViewById(R.id.hora1);
        TextView tv2 = (TextView)findViewById(R.id.hora2);
        TextView tv3 = (TextView)findViewById(R.id.hora3);
        TextView tv4 = (TextView)findViewById(R.id.hora4);
        TextView tv5 = (TextView)findViewById(R.id.hora5);
        TextView tv6 = (TextView)findViewById(R.id.hora6);

        tv1.setText(horas.get(0));
        tv2.setText(horas.get(1));
        tv3.setText(horas.get(2));
        tv4.setText(horas.get(3));
        tv5.setText(horas.get(4));
        tv6.setText(horas.get(5));
    }

}
