package edu.washington.akpuri.capstone;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
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

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AddFriends extends ActionBarActivity {

    private final static String TAG = "AddFriends";

    private static ArrayList<Contact> pendingContacts;
    private static ArrayList<ParseObject> pendingParseContacts;
    private static ArrayList<String> contactObjectIds;
    private static ArrayList<Contact> allContacts;
    private static SingletonContacts instance = SingletonContacts.getInstance();
    private static SingletonUser userInstance = SingletonUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        allContacts = new ArrayList<Contact>();
        pendingContacts = instance.getPendingContacts();
        contactObjectIds = new ArrayList<String>();
        pendingParseContacts = new ArrayList<>();

        // Get intent that called the activity
        Intent intent = getIntent();
        String caller = intent.getStringExtra("caller");

        if (!instance.hasImported()) {

            Log.e(TAG, "hasImported: " + instance.hasImported() + "");

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
                                phone = phone.replaceAll("[^\\d.]", "");
                            }
                        }
                        phoneCur.close();
                    }
                    //If a contact does not have a phone number stored or their phone number is shorter than
                    //a length of 7 (example: AT&T service numbers for things like checking data are only 4 characters long)
                    //
                    Contact person = new Contact(name, phone.replaceAll("[^\\d]",""), identity);
                    person.setHasBeenAdded(false);
                    if (phone.length() >= 7) {
                        //                    allContacts.add(person);
                        instance.addContact(person);
                        Log.i("Contacts", "Contact: " + name + " has ID of " + identity + " and phone number of " + phone);
                    }
                }
                cur.close();

                instance.setHasImported(true);

                //            instance.setContacts(allContacts);
                ListView contactListView = (ListView) findViewById(R.id.addFriendsList);
                ListAdapter adapter = new ContactAdapter(this, R.id.friendListItem, instance.getAllContacts(), pendingContacts);
                contactListView.setAdapter(adapter);
            }
        }

        Log.e(TAG, "Contacts:" + instance.getAllContacts().toString());

        Button sendRequest = (Button) findViewById(R.id.sendFriendRequest);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createParseObjects(instance.getPendingContacts());
                Toast mes = Toast.makeText(getApplicationContext(), "Friend Requests Sent", Toast.LENGTH_LONG);
                mes.show();
                finish();
            }
        });
    }

    private void createParseObjects(ArrayList<Contact> pending){

        final String user = ParseUser.getCurrentUser().getString("email");

        for(int i=0; i < pending.size(); i++) {
            final Contact person = pending.get(i);

            // Look up user on Parse.com
            ParseQuery<ParseUser> queryA = ParseUser.getQuery();
            queryA.whereContains("phone", person.getPhone());
            queryA.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (parseUser != null) {
                        person.setEmail(parseUser.getEmail());
                    }
                }
            });

            ////// Create contact ParseObject here
            final ParseObject contact = new ParseObject("contact");
            contact.put("name", person.getName());
            contact.put("phone", person.getPhone());
            contact.put("user", user); // could probably save this in singleton
            contact.put("id", person.getId());
            contact.put("email", person.getEmail());
            Log.e(TAG, "contact id: " + person.getId());
            contact.put("pending", true);   // pending So-So friend; should be false once accepted
            contact.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error saving contactsId: " + e);
                    } else {
                        instance.addCurrentContact(contact.getObjectId());
                        person.setObjectId(contact.getObjectId());
                        Log.e(TAG, person.getObjectId());
                        ParseUser.getCurrentUser().add("contacts", contact.getObjectId());
                        ParseUser.getCurrentUser().saveInBackground();

                        // CONTINUE
//                        ParseQuery<ParseUser> query1 = ParseUser.getQuery();
//                        query1.whereContains("phone", contact.get("phone").toString());
//                        query1.getFirstInBackground(new GetCallback<ParseUser>() {
//                            @Override
//                            public void done(ParseUser parseUser, ParseException e) {
//                                try {
//                                    contact.put("email", parseUser.getEmail());
//                                } catch (Exception e1) {
//                                    e1.printStackTrace();
//                                }
//                            }
//                        });


                        /// WORKING
                        // ideas, maybe I can save each time a person is created??? but is that a good idea??

                        // Get ContactsObject for current user
                        // ContactsObject has contacts[] array containing objectId of user's current and pending friends

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactsObject");
                        query.whereEqualTo("user", user);
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(final ParseObject parseObject, ParseException e) {
                                try {
                                    if (parseObject != null) {
                                        // User exists and has a ContactsObject

                                        Log.e(TAG, "Current contacts[]: " + ParseUser.getCurrentUser().get("contacts").toString());
                                        parseObject.add("contacts", contact.getObjectId());
                                        parseObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Log.e(TAG, "ContactsObject: " + parseObject.get("contacts").toString());

                                            }
                                        });

                                        // Get contact's username/email address === doesn't currently work
                                        // Need to get email for contact
                                        // Or use phone numbers instead

                                        // Send push notifications
//                                        ParseQuery pushQuery = userInstance.getCurrentInstallation().getQuery();
//                                        pushQuery.whereEqualTo("user", person.getEmail());
//                                        ParsePush push = new ParsePush();
//                                        push.setQuery(pushQuery);
//                                        push.setMessage(userInstance.getName() + " (" + userInstance.getPhone() + ") sent you a friend request.");
//                                        //JSONObject data = new JSONObject(
//                                        //        "{\"alert\": \""+userInstance.getName()+"\", " +
//                                        //                "\"uri\": \"app://host/contacts\"}");
//
//                                        // json doesn't work but should be working -_-
////                                        JSONObject data = new JSONObject("{\"alert\":userInstance.getName(), \"uri\": \"app://host/contacts\" }");
////                                        push.setData(data);
//                                        push.sendInBackground();
//                                        Log.e(TAG, "sent to: " + person.getEmail());

//                                        ParseCloud.callFunctionInBackground("test", null, new FunctionCallback<Map<String, Object>>() {
//                                            @Override
//                                            public void done(Map<String, Object> stringObjectMap, ParseException e) {
//                                                if (e == null) {
//                                                    Toast.makeText(getApplicationContext(), stringObjectMap.get("answer").toString(), Toast.LENGTH_LONG).show();
//                                                }
//                                            }
//                                        });
                                        // Todo: change message and try to open request fragment if possible (via JS cloud code?)
                                        String message = userInstance.getName() + " (" + userInstance.getPhone() + ") sent you a friend request.";
                                        HashMap<String, Object> params = new HashMap<String, Object>();
                                        params.put("recipientId", person.getObjectId());
                                        params.put("recipientEmail", person.getEmail());
                                        params.put("message", message);
                                        params.put("uri", "app://host/contacts");
                                        ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
                                            public void done(String success, ParseException e) {
                                                if (e == null) {
                                                    // Push sent successfully
                                                    Log.e(TAG, success);
                                                }
                                                else {
                                                    Log.e(TAG, e.toString());
                                                }
                                            }
                                        });

                                    } else {
                                        // Something went wrong
                                        Log.e("Contacts", "Failed to retrieve contactsObject: " + e);
                                    }
                                } catch (Exception err) {
                                    err.printStackTrace();
                                }
                            }
                        });

                        // Create connection between current user and friends
//                        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Friends");
//                        query2.whereEqualTo("pendingUser", user);
//                        query2.getFirstInBackground(new GetCallback<ParseObject>() {
//                            @Override
//                            public void done(final ParseObject parseObject, ParseException e) {
//                                if (parseObject != null) {
//                                    parseObject.add("requester", ParseUser.getCurrentUser().getObjectId());
//                                    parseObject.add("accepter", contact.getObjectId());
//                                    parseObject.saveInBackground(new SaveCallback() {
//                                        @Override
//                                        public void done(ParseException e) {
//                                            // Done
//                                        }
//                                    });
//                                } else {
//                                    // Something went wrong
//                                }
//                            }
//                        });
                    }
                    instance.getPendingContacts().clear();
                }
            });
        }


    }

    @Override
    public void onResume(){
        super.onResume();
        ListView contactListView = (ListView) findViewById(R.id.addFriendsList);
        ListAdapter adapter = new ContactAdapter(this, R.id.friendListItem, instance.getAllContacts(), pendingContacts);
        contactListView.setAdapter(adapter);
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
