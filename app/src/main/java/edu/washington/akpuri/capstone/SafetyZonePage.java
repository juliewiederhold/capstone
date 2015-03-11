package edu.washington.akpuri.capstone;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class SafetyZonePage extends ActionBarActivity {
    private static ArrayList<SafetyZone> existingSafetyZones = new ArrayList<>();
    private static ArrayList<HashMap<String, String>> safetyZoneInformation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_zone);

        final Bundle saved = savedInstanceState;
        Button addZone = (Button)findViewById(R.id.addZone);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.current_safety_zones, new CurrentSafetyZone())
                    .commit();
        }

        Button add_safety_zone_location = (Button) findViewById(R.id.add_safety_zone_location);

        add_safety_zone_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameText = (EditText) findViewById(R.id.safety_zone_name);
                EditText addressText = (EditText) findViewById(R.id.address);
                EditText cityText = (EditText) findViewById(R.id.city);
                EditText zipText = (EditText) findViewById(R.id.zip);
                EditText stateText = (EditText) findViewById(R.id.state);

                String name = nameText.getText().toString();
                String address = addressText.getText().toString();
                String city = cityText.getText().toString();
                String zip = zipText.getText().toString();
                String state = stateText.getText().toString();

                if(!name.equals("") && !address.equals("") && !city.equals("")&& !zip.equals("") && !stateText.equals("")){
                    SafetyZone newZone = new SafetyZone(name, address, city, Integer.parseInt(zip), state);
                    existingSafetyZones.add(newZone);
                    updateCurrentSafetyZone();

                    nameText.setText("");
                    addressText.setText("");
                    cityText.setText("");
                    stateText.setText("");
                    zipText.setText("");

                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "All fields must be filled out";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });


        // Add Safety Zone to list through clicking button
      /*  addZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saved == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new AddSafetyZone())
                            .commit();
                }
            }
        });*/


    }

    private void updateCurrentSafetyZone(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        CurrentSafetyZone fragment = new CurrentSafetyZone();
        ft.replace(R.id.current_safety_zones, fragment);   // replace instead of add
        ft.addToBackStack("Update Current Safety Zones");
        ft.commit();
    }

    private static void initList(){
        safetyZoneInformation = new ArrayList<>();
        for(int i = 0; i < existingSafetyZones.size(); i++){
            SafetyZone zone = existingSafetyZones.get(i);
            safetyZoneInformation.add(createTopic(zone.getName(), zone.getAddress(), zone.getCity(), zone.getState(), Integer.toString(zone.getZip())));
        }
    }

    private static HashMap<String, String> createTopic(String nameValue, String addressValue, String cityValue, String stateValue, String zipValue){
        HashMap<String, String> zoneInformation = new HashMap<>();
        zoneInformation.put("name", nameValue);
        zoneInformation.put("address", addressValue + ", " + cityValue + ", " + stateValue + " " + zipValue);

        return zoneInformation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_safety_zone, menu);
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

    public static class CurrentSafetyZone extends Fragment {
        public CurrentSafetyZone(){
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.current_safety_zone_view, container, false);

            int size = existingSafetyZones.size();
            ListView lv = (ListView) rootView.findViewById(R.id.listView_safety_zones);
            initList();

            SimpleAdapter simpleAdpt = new SimpleAdapter(rootView.getContext(), safetyZoneInformation, android.R.layout.simple_list_item_2,
                    new String[] {"name", "address"}, new int[] {android.R.id.text1, android.R.id.text2});

            lv.setAdapter(simpleAdpt);

            lv.setTextFilterEnabled(true);

            return rootView;
        }
    }
}
