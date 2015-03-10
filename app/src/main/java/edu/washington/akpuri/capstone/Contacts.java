package edu.washington.akpuri.capstone;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class Contacts extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //ContentResolver is used to query the contacts database to return a cursor
        ContentResolver contentResolver = getContentResolver();
        //The cursor is like an iterator, it contains the entirety of the contacts when we pass it null paramaters
        Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        ArrayList<Contact> allContacts = new ArrayList<Contact>();
        //Check to see if the cursor actually got contacts back
        if (cur.getCount() > 0) {
            while(cur.moveToNext()) {
                //Use cursor to query, here we grab the current contacts ID which can be used to get
                //More information for that particular contact later
                int id = Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phone = "";
                //Check to see if contact has a phone number
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //If they do have a phone number, we need to create a new cursor for phone because
                    //Contact phone numbers are stored in a separate database table so must be queried separately
                    Cursor phoneCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id + ""}, null);
                    while (phoneCur.moveToNext()) {
                        int type = phoneCur.getInt(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        //We only want to grab the mobile phone number of a contact
                        if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                            phone = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                    }
                    phoneCur.close();
                }
                //If a contact does not have a phone number stored or their phone number is shorter than
                //a length of 7 (example: AT&T service numbers for things like checking data are only 4 characters long)
                //
                Contact person = new Contact(name, phone, id);
                if (phone.length() >= 7) {
                    allContacts.add(person);
                    Log.i("Contacts", "Contact: " + name + " has ID of " + id + " and phone number of " + phone);
                }
            }
            cur.close();

            ListView contactListView = (ListView) findViewById(R.id.contactListView);
            ListAdapter adapter = new ContactAdapter(this, R.id.contactListItem, allContacts);
            contactListView.setAdapter(adapter);
        }

        //We now have all relevant contacts stored in our ArrayList of Contacts with their name, and phone number
        //Now we need to set up the array adapter

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
