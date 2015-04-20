package edu.washington.akpuri.capstone;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;


public class SafetyZonePage extends ActionBarActivity {
    private static ArrayList<SafetyZone> existingSafetyZones = new ArrayList<>();
    private static ArrayList<HashMap<String, String>> safetyZoneInformation = new ArrayList<>();
    private static SingletonUser userInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_zone);

        userInstance = SingletonUser.getInstance();

        if(existingSafetyZones.size() > 0){
            TextView description = (TextView) findViewById(R.id.safetyZoneDescription);
            description.setText("");
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.current_safety_zones, new CurrentSafetyZone())
                    .commit();
        }

        if(userInstance.getHasGoneThroughInitialSetUp()){
            Button saveButton = (Button) findViewById(R.id.next);
            saveButton.setText("Done");
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent next = new Intent(SafetyZonePage.this, EditDefaultSettings.class);
                    startActivity(next);
                }
            });
        } else {
            Button nextButton = (Button) findViewById(R.id.next);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent next = new Intent(SafetyZonePage.this, AppNumberBlocking.class);
                    next.putExtra("activitySent","SafetyZonePage");
                    startActivity(next);
                }
            });
        }



        Button add_safety_zone_location = (Button) findViewById(R.id.add_safety_zone_location);

        add_safety_zone_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();

                final View fragmentView = inflater.inflate(R.layout.fragment_safety_zone, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(fragmentView)
                        // Add action buttons
                        .setPositiveButton("Add", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText nameText = (EditText) fragmentView.findViewById(R.id.safety_zone_name);
                                EditText addressText = (EditText) fragmentView.findViewById(R.id.address);
                                EditText cityText = (EditText) fragmentView.findViewById(R.id.city);
                                EditText zipText = (EditText) fragmentView.findViewById(R.id.zip);
                                EditText stateText = (EditText) fragmentView.findViewById(R.id.state);

                                String name = nameText.getText().toString();
                                String address = addressText.getText().toString();
                                String city = cityText.getText().toString();
                                String zip = zipText.getText().toString();
                                String state = stateText.getText().toString();

                                if(!name.equals("") && !address.equals("") && !city.equals("")&& !zip.equals("") && !stateText.equals("")){
                                    SafetyZone newZone = new SafetyZone(name, address, city, Integer.parseInt(zip), state);
                                    existingSafetyZones.add(newZone);

                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);

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
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                builder.create();
                builder.show();
            }
        });

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
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.action_logout:
                logout();
                break;
            case R.id.action_safetyzones:
                Intent intent2 = new Intent(this, SafetyZonePage.class);
                this.startActivity(intent2);
                break;
            case R.id.action_contacts:
                Intent intent3 = new Intent(this, Contacts.class);
                this.startActivity(intent3);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void logout() {
        // Call the Parse log out method
        ParseUser.logOut();
        // Start and intent for the dispatch activity
        // Below will start invalidate user's session and redirect to WelcomeActivity
        Intent intent = new Intent(this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static class CurrentSafetyZone extends Fragment {
        public CurrentSafetyZone(){
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.current_safety_zone_view, container, false);
            final ListView lv = (ListView) rootView.findViewById(R.id.listView_safety_zones);
            initList();

            SimpleAdapter simpleAdpt = new SimpleAdapter(rootView.getContext(), safetyZoneInformation, android.R.layout.simple_list_item_2,
                    new String[] {"name", "address"}, new int[] {android.R.id.text1, android.R.id.text2});

            lv.setAdapter(simpleAdpt);
            lv.setTextFilterEnabled(true);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();

                    final View fragmentView = inflater.inflate(R.layout.fragment_safety_zone, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());

                    String itemSelected = lv.getItemAtPosition(position).toString();
                    String[] fullAddress = itemSelected.split(",");
                    String addressSelected = fullAddress[0].split("=")[1];

                    int index = 0;

                    for(int i = 0; i < existingSafetyZones.size(); i++){
                        if(addressSelected.equals(existingSafetyZones.get(i).getAddress())){
                            index = i;
                            break;
                        }
                    }

                    final int indexOfZone = index;
                    final SafetyZone currentZone = existingSafetyZones.get(indexOfZone);

                    String citySelected = currentZone.getCity();
                    String stateSelected = currentZone.getState();
                    String zipSelected = Integer.toString(currentZone.getZip());
                    String nameSelected = currentZone.getName();

                    EditText nameText = (EditText) fragmentView.findViewById(R.id.safety_zone_name);
                    EditText addressText = (EditText) fragmentView.findViewById(R.id.address);
                    EditText cityText = (EditText) fragmentView.findViewById(R.id.city);
                    EditText zipText = (EditText) fragmentView.findViewById(R.id.zip);
                    EditText stateText = (EditText) fragmentView.findViewById(R.id.state);

                    nameText.setText(nameSelected);
                    addressText.setText(addressSelected);
                    cityText.setText(citySelected);
                    zipText.setText(zipSelected);
                    stateText.setText(stateSelected);

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    builder.setView(fragmentView)
                            // Add action buttons
                            .setPositiveButton(R.string.update_safety_zone, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    EditText nameText = (EditText) fragmentView.findViewById(R.id.safety_zone_name);
                                    EditText addressText = (EditText) fragmentView.findViewById(R.id.address);
                                    EditText cityText = (EditText) fragmentView.findViewById(R.id.city);
                                    EditText zipText = (EditText) fragmentView.findViewById(R.id.zip);
                                    EditText stateText = (EditText) fragmentView.findViewById(R.id.state);

                                    currentZone.setName(nameText.getText().toString());
                                    currentZone.setAddress(addressText.getText().toString());
                                    currentZone.setCity(cityText.getText().toString());
                                    currentZone.setZip(Integer.parseInt(zipText.getText().toString()));
                                    currentZone.setState(stateText.getText().toString());

                                    existingSafetyZones.set(indexOfZone, currentZone);
                                    updateCurrentSafetyZone();
                                }
                            })
                            .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    existingSafetyZones.remove(indexOfZone);

                                    Intent intent = getActivity().getIntent();
                                    getActivity().finish();
                                    startActivity(intent);

                                    updateCurrentSafetyZone();
                                }
                            });

                    builder.create();
                    builder.show();
                }
            });
            return rootView;
        }

        private void updateCurrentSafetyZone(){
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            CurrentSafetyZone fragment = new CurrentSafetyZone();
            ft.replace(R.id.current_safety_zones, fragment);
            ft.addToBackStack("Update Current Safety Zones");
            ft.commit();
        }
    }
}
