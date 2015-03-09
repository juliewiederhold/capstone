package edu.washington.akpuri.capstone;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Loader;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.ActionBarActivity;
// import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;


public class Contacts extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //ContentResolver is used to query the contacts database to return a cursor
        ContentResolver contentResolver = getContentResolver();
        //The cursor is like an iterator, it contains the entirety of the contacts when we pass it null paramaters
        Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, new String[] {android.provider.Contacts.People._ID, android.provider.Contacts.People.NAME, android.provider.Contacts.People.NUMBER}, null, null, null);
        //Check to see if the cursor actually got contacts back
        if (cur.getCount() > 0) {
            while(cur.moveToNext()) {
                //Use cursor to query, here we grab the current contacts ID which can be used to get
                //More information for that particular contact later
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //Contact phone numbers are stored in a separate database table so must be queried separately
                Log.i("Contacts", "Contact: " + name + " has ID of " + id);
            }
        }

        Button next = (Button) findViewById(R.id.contactsNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent safeZones = new Intent(Contacts.this, SafetyZone.class);
                startActivity(safeZones);
            }
        });


        /*final ActionBar actionBar = getActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                TextView tabSelect = (TextView) findViewById(R.id.tab1);
                tabSelect.setText("Tab is selected!");
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
                TextView tabUnselect = (TextView) findViewById(R.id.tab1);
                tabUnselect.setText("Tab is unselected!");
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
                TextView tabReselect = (TextView) findViewById(R.id.tab1);
                tabReselect.setText("Tab is reselected!");
            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < 3; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Tab " + (i + 1))
                            .setTabListener(tabListener));
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
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
}
