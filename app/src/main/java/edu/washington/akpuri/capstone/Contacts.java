package edu.washington.akpuri.capstone;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class Contacts extends ActionBarActivity {

    private final static String TAG = "Contacts.java";

    private static boolean objectExists;
    private static String objectId;
    private static ArrayList<Contact> allContacts;
    private static ArrayList<Contact> pendingContacts;
    private static ArrayList<ParseObject> pendingParseContacts;
    private static boolean allowContactRetrieval;
    private android.support.v7.app.ActionBar actionBar;
    private SingletonContacts instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        allowContactRetrieval = false;
        pendingParseContacts = new ArrayList<ParseObject>();
        instance = SingletonContacts.getInstance();
        allContacts = instance.getAllContacts();
        pendingContacts = instance.getPendingContacts();

        //Get the actionbar
        // setup action bar for tabs
        actionBar = this.getSupportActionBar();
        if (actionBar == null) {
            Log.e("Action Bar", "Action Bar is Null");
        }
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        android.support.v7.app.ActionBar.Tab first = actionBar.newTab()
                .setText("Friends")
                .setTabListener(new FragmentTabListener<FriendsFragment>(
                        this, "Friends", FriendsFragment.class));
        actionBar.addTab(first);

        android.support.v7.app.ActionBar.Tab second = actionBar.newTab()
                .setText("Groups")
                .setTabListener(new FragmentTabListener<GroupsFragment>(
                        this, "Groups", GroupsFragment.class));
        actionBar.addTab(second);

        Button next = (Button) findViewById(R.id.contactsNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "contactsNext button pressed");
                if (allowContactRetrieval) {
                    final String user = ParseUser.getCurrentUser().getString("email");
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactsObject");
                    query.whereEqualTo("user", user); // query.whereEqualTo("parent", user);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (parseObject != null) {
                                for (int i = 0; i < pendingContacts.size(); i++) {
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
                                                Log.e(TAG, "Contact exists");
                                            } else {
                                                Log.e(TAG, "Contact DNE yet");
                                                final ParseObject contact = new ParseObject("contact");
                                                contact.put("name", name);
                                                contact.put("phone", phone);    //
                                                contact.put("user", user);
                                                contact.put("id", id);
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
                                parseObject.addAllUnique("contacts", pendingParseContacts);
                                parseObject.saveInBackground();
                            } else {
                                // Something went wrong
                                Log.e("Contacts", "Failed to retrieve contactsObject: " + e);
                            }
                        }
                    });
                }
                Intent safeZones = new Intent(Contacts.this, SafetyZonePage.class);
                startActivity(safeZones);
            }
        });

        ImageButton addContacts = (ImageButton) findViewById(R.id.addFriends);
        addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allowContactRetrieval) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Allow So-So to use your Contacts? It's awfully Risky you know")
                            .setTitle("Import Contacts");
                    builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            allowContactRetrieval = true;
                            Intent addFriends = new Intent(Contacts.this, AddFriends.class);
                            startActivity(addFriends);
                        }
                    });
                    builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            //allowContactRetrieval is still false
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                if (allowContactRetrieval) {
                    Intent addFriends = new Intent(Contacts.this, AddFriends.class);
                    startActivity(addFriends);
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

    //FRAGMENTS for the two tabs

    public static class FriendsFragment extends Fragment {
        //Empty Constructor
        public FriendsFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.i("ContactsFragment", "onCreateView Fired for FriendsFrgment");
            final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
            return rootView;
        }
    }

    //
    public static class GroupsFragment extends Fragment {
        //Empty Constructor
        public GroupsFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.i("ContactsFragment", "onCreateView Fired for GroupsFragment");
            final View rootView = inflater.inflate(R.layout.fragment_group, container, false);

            return rootView;
        }
    }
}
