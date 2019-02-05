package projetstage.aldric.fr.mapsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class informations_stations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations_stations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if(intent!=null)
        {
            Long nombre = intent.getLongExtra("Number",0);
            String Nom_contrat = intent.getStringExtra("Contract Name");
            String adresse = intent.getStringExtra("Adresse");
            boolean banking= intent.getBooleanExtra("banking",false);
            Long bike_stands = intent.getLongExtra("Bike Stands",0);
            Long available_bike_stands = intent.getLongExtra("Available Bike Stands",0);
            Long available_bikes= intent.getLongExtra("Available Bikes",0);
            String status = intent.getStringExtra("Status");
            Long last_update= intent.getLongExtra("Last Update",0);
            setTitle("Information station "+adresse);


            TextView textView1 = findViewById(R.id.nombre);
            TextView textView2 = findViewById(R.id.contrat);
            TextView textView3 = findViewById(R.id.adresse);
            TextView textView4 = findViewById(R.id.banking);
            TextView textView5 = findViewById(R.id.Bikes_Stands);
            TextView textView6 = findViewById(R.id.Available_BS);
            TextView textView7 = findViewById(R.id.Available_Bikes);
            TextView textView8 = findViewById(R.id.Statut);
            TextView textView9 = findViewById(R.id.maj);

            textView1.setText("Number : "+nombre);
            textView2.setText("Contract Name : "+Nom_contrat);
            textView3.setText("Address : "+adresse);
            textView4.setText("Banking : "+banking);
            textView5.setText("Bike Stands: "+bike_stands);
            textView6.setText("Available Bike Stands : "+available_bike_stands);
            textView7.setText("Available Bikes : "+available_bikes);
            textView8.setText("Status: "+status);
            textView9.setText("Last Update: "+last_update);
        }

    }

}
