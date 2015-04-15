package edu.washington.akpuri.capstone;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.security.cert.CertificateParsingException;
import java.util.ArrayList;


public class AddFriends extends ActionBarActivity {

    private final static String TAG = "AddFriends";

    private static ArrayList<Contact> pendingContacts;
    private static ArrayList<ParseObject> pendingParseContacts;
    private static ArrayList<String> pContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        final SingletonContacts instance = SingletonContacts.getInstance();
        ArrayList<Contact> allContacts = new ArrayList<Contact>();
        pendingContacts = new ArrayList<Contact>();
        pContacts = new ArrayList<String>();
        pendingParseContacts = new ArrayList<>();

        //ContentResolver is used to query the contacts database to return a cursor
        ContentResolver contentResolver = getContentResolver();
        //The cursor is like an iterator, it contains the entirety of the contacts when we pass it null paramaters
        Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //Check to see if the cursor actually got contacts back
        Log.i("Querying Contacts", "Cur Count is :" + cur.getCount());
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                //Use cursor to query, here we grab the current contacts ID which can be used to get
                //More information for that particular contact later
                int identity = Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)));
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
                            new String[]{identity + ""}, null);
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
                Contact person = new Contact(name, phone, identity);
                if (phone.length() >= 7) {
                    allContacts.add(person);
                    Log.i("Contacts", "Contact: " + name + " has ID of " + identity + " and phone number of " + phone);
                }
            }
            cur.close();
            instance.setContacts(allContacts);
            ListView contactListView = (ListView) findViewById(R.id.addFriendsList);
            ListAdapter adapter = new ContactAdapter(this, R.id.contactListItem, allContacts, pendingContacts);
            contactListView.setAdapter(adapter);
        }

        Button sendRequest = (Button) findViewById(R.id.sendFriendRequest);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = ParseUser.getCurrentUser().getString("email");
                // Get ContactsObject for current user
                // ContactsObject has contacts[] array containing objectId of user's current and pending friends
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactsObject");
                query.whereEqualTo("user", user);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(final ParseObject parseObject, ParseException e) {
                        if (parseObject != null) {
                            // User exists and has a ContactsObject
                            for (int i = 0; i < instance.getPendingContacts().size(); i++) {
                                final Contact aContact= instance.getPendingContacts().get(i);
                                final String name = aContact.getName();
                                final String phone = aContact.getPhone();
                                final int id = aContact.getId();
                                aContact.setHasBeenAdded(true);
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
                                query.whereEqualTo("user", user);
                                query.whereEqualTo("phone", aContact.getPhone());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(final ParseObject parseObject, ParseException e) {
                                        if (parseObject != null) {
                                            Log.e(TAG, "Contact exists");
                                        } else {
                                            Log.e(TAG, "Contact DNE yet");
                                            // Create and save new contact
                                            final ParseObject contact = new ParseObject("contact");
                                            contact.put("name", name);
                                            contact.put("phone", phone);
                                            contact.put("user", user);
                                            contact.put("id", id);
                                            contact.put("pending", true);   // pending So-So friend; should be false once accepted
                                            contact.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e != null) {
                                                        Log.e(TAG, "Error saving contactsId: " + e);
                                                    } else {
                                                        pContacts.add(contact.getObjectId());
                                                        instance.setCurrentcontacts(contact.getObjectId());
                                                        pendingParseContacts.add(contact);
//                                                        ParseUser.getCurrentUser().put("contacts", pContacts);  // need to check if overwrites
                                                        // might have to retrieve current copy, then overwrite
//                                                        ParseUser.getCurrentUser().addAllUnique("contacts", pContacts);
                                                        ParseUser.getCurrentUser().saveInBackground();

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                // Nicole: Not adding to Current Friend's list right away.....
                                instance.addPendingFriend(aContact);
                                Log.e(TAG, " pending friends: " + instance.getPendingFriends().toString());

                            }
                            ParseUser.getCurrentUser().put("contacts", instance.getCurrentContacts());
                            Log.e(TAG, "Current contacts[]: " + ParseUser.getCurrentUser().get("contacts").toString());
//                            instance.setPendingFriends(pendingParseContacts);
                            Log.e(TAG, "Pending parse contacts: " + pendingParseContacts.toString());
                            parseObject.put("contacts", instance.getCurrentContacts());
                            // addAllUnique doesn't work for some reason
//                            parseObject.addAllUnique("contacts", instance.getCurrentContacts());
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    // TO-DO: Remove contacts who have been added from the Contacts List
                                    // Set pendingContacts and pendingParseContacts to empty
                                    Log.e(TAG, "ContactsObject: " + parseObject.get("contacts").toString());
                                    instance.getPendingContacts().clear();
                                    pendingParseContacts.clear();
                                }
                            });
                        } else {
                            // Something went wrong
                            Log.e("Contacts", "Failed to retrieve contactsObject: " + e);
                        }
                    }
                });
                Toast mes = Toast.makeText(getApplicationContext(), "Friend Requests Sent", Toast.LENGTH_LONG);
                mes.show();
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_friends, menu);
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
