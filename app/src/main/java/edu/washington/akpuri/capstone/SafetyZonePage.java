package edu.washington.akpuri.capstone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;


public class SafetyZonePage extends ActionBarActivity {
    private static ArrayList<SafetyZone> existingSafetyZones = new ArrayList<>();
    ArrayList<ParseObject> pendingSafetyZones;                              // parse
    private static ArrayList<HashMap<String, String>> safetyZoneInformation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_zone);

        pendingSafetyZones = new ArrayList<ParseObject>();                  // parse

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

                    ///// begin parse
                    /// Nicole: is it possible to assign IDs to safetyzones? going by name for uniqueness check atm

                    final String user = ParseUser.getCurrentUser().getString("email");
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("SafetyZonesObject");
                    query.whereEqualTo("user", user); // query.whereEqualTo("parent", user);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (parseObject != null) {
                                for(int i = 0; i < existingSafetyZones.size(); i++) {
                                    final String name = existingSafetyZones.get(i).getName();
                                    final String address = existingSafetyZones.get(i).getAddress();
                                    final String city = existingSafetyZones.get(i).getCity();
                                    final int zip = existingSafetyZones.get(i).getZip();
                                    final String state = existingSafetyZones.get(i).getState();
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("safetyzone");
                                    query.whereEqualTo("user", user);
                                    query.whereEqualTo("name", name);
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(final ParseObject parseObject, ParseException e) {
                                            if (parseObject != null) {
                                                Log.e("Contacts.java", "Contact exists");
                                            } else {
                                                Log.e("Contacts.java", "Contact DNE yet");
                                                final ParseObject safetyzone = new ParseObject("safetyzone");
                                                safetyzone.put("user", user);
                                                safetyzone.put("name", name);
                                                safetyzone.put("address", address);    //
                                                safetyzone.put("city", city);
                                                safetyzone.put("zip", zip);
                                                safetyzone.put("state", state);
                                                safetyzone.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            pendingSafetyZones.add(safetyzone);
                                                        } else {
                                                            Log.e("Contacts.java", "Error saving contactsId: " + e);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                Log.e("SafetyZonePage.java", "Doesn't really save to SafetyZone object yet.");
                                parseObject.addAllUnique("contacts", pendingSafetyZones);
                                parseObject.saveInBackground();
                            } else {
                                // Something went wrong
                                Log.e("SafetyZonePage", "Failed to retrieve contactsObject: " + e);
                            }
                        }
                    });
                    ///// end parse

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
                    String stateSeleced = currentZone.getState();
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
                    stateText.setText(stateSeleced);

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
            ft.replace(R.id.current_safety_zones, fragment);   // replace instead of add
            ft.addToBackStack("Update Current Safety Zones");
            ft.commit();
        }
    }
}
