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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Contacts extends ActionBarActivity {

    private static boolean objectExists;
    private static String objectId;

    ArrayList<Contact> allContacts;
    ArrayList<Contact> pendingContacts;
    ArrayList<ParseObject> pendingParseContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //ContentResolver is used to query the contacts database to return a cursor
        ContentResolver contentResolver = getContentResolver();
        //The cursor is like an iterator, it contains the entirety of the contacts when we pass it null paramaters
        Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        allContacts = new ArrayList<Contact>();
        pendingContacts = new ArrayList<Contact>();
        pendingParseContacts = new ArrayList<ParseObject>();
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

            ListView contactListView = (ListView) findViewById(R.id.listView);
            ListAdapter adapter = new ContactAdapter(this, R.id.contactListItem, allContacts, pendingContacts);
            contactListView.setAdapter(adapter);

//            contactListView.setOnItemClickListener(new OnItemClickListener() {
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Contact person = (Contact) parent.getItemAtPosition(position);
//                    Toast.makeText(getApplicationContext(),
//                            "Clicked on Row: " + person.getName(),
//                            Toast.LENGTH_LONG).show();
//                }
//            });

        }

        //We now have all relevant contacts stored in our ArrayList of Contacts with their name, and phone number
        //Now we need to set up the array adapter

        Button next = (Button) findViewById(R.id.contactsNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent safeZones = new Intent(Contacts.this, SafetyZonePage.class);
//                startActivity(safeZones);
                final String user = ParseUser.getCurrentUser().getString("email");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactsObject");
                query.whereEqualTo("user", user); // query.whereEqualTo("parent", user);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject != null) {
                            for(int i = 0; i < pendingContacts.size(); i++) {
                                final String name = pendingContacts.get(i).getName();
                                final String phone = pendingContacts.get(i).getPhone();
                                final int id = pendingContacts.get(i).getId();
                                pendingContacts.get(i).setHasBeenAdded(true);
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
                                query.whereEqualTo("user", user);
                                query.whereEqualTo("phone", pendingContacts.get(i).getPhone());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(final ParseObject parseObject, ParseException e) {
                                        if (parseObject != null) {
                                            Log.e("Contacts.java", "Contact exists");
                                        } else {
                                            Log.e("Contacts.java", "Contact DNE yet");
                                            final ParseObject contact = new ParseObject("contact");
                                            contact.put("name", name);
                                            contact.put("phone", phone);    //
                                            contact.put("user", user);
                                            contact.put("id", id);
//                                            try {
//                                                contact.save();
//                                                pendingParseContacts.add(contact);
//                                            } catch (ParseException err) {
//                                                err.printStackTrace();
//                                            }
                                            contact.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        pendingParseContacts.add(contact);
                                                    } else {
                                                        Log.e("Contacts.java", "Error saving contactsId: " + e);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                            Log.e("Contacts", "Should've saved contacts.");
                            savedSuccesfully(parseObject);
//                            parseObject.put("contacts", pendingParseContacts);
                        } else {
                            // Something went wrong
                            Log.e("Contacts", "Failed to retrieve contactsObject: " + e);
                        }
                    }
                });
            }
        });
    }

    private void savedSuccesfully(ParseObject parseObject) {
        parseObject.addAllUnique("contacts", pendingParseContacts);
        parseObject.saveInBackground();
    }
    public static void objectExists(String user, String phone){
        objectExists = false;
        objectId = null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
        query.whereEqualTo("user", user);
        query.whereEqualTo("phone", phone);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null) {
                    objectExists = true;
                } else {
                    Log.e("Contacts.java", "Failed to retrieve object");
                }
            }
        });
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.action_logout:
                logout();
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
}
