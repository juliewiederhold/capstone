package edu.washington.akpuri.capstone;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.nfc.Tag;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class Contacts extends ActionBarActivity {

    private final static String TAG = "Contacts.java";

    private static ArrayList<Contact> pendingContacts;
    private static ParseObject pendingParseContacts;
    private static ArrayList<String> pContacts;
    private static boolean allowContactRetrieval;
    private android.support.v7.app.ActionBar actionBar;
    private SingletonContacts instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts);
        allowContactRetrieval = false;
        pendingParseContacts = null;
        instance = SingletonContacts.getInstance();
        pendingContacts = new ArrayList<Contact>();
        pContacts = new ArrayList<String>();

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
                Intent safeZones = new Intent(Contacts.this, SafetyZonePage.class);
                startActivity(safeZones);
            }
        });

        Button addContacts = (Button) findViewById(R.id.addFriends);
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


        //// Get So-So Friends (current and pending)

        // Not currently loading when you first load "Contacts/Friends" page for some reason....
        // Should probably download Friends from parse every time app is loaded to get most current list
        // since users have to accept requests (i.e. changes may occur while user isn't on the app)

//        if (ParseUser.getCurrentUser().get("contacts") != null) {
//            JSONArray contacts = ParseUser.getCurrentUser().getJSONArray("contacts");
//            Log.e(TAG + " Friends", contacts.toString());
//            for (int i = 0; i < contacts.length(); i++) {
//                String id = null;
//                try {
//                    id = contacts.get(i).toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Log.e(TAG, id);
//                if (id != null) {
//                    ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
//                    query.whereEqualTo("objectId", id); // query.whereEqualTo("parent", user);
//                    query.getFirstInBackground(new GetCallback<ParseObject>() {
//                        @Override
//                        public void done(final ParseObject parseObject, ParseException e) {
//                            String name = parseObject.getString("name");
//                            String phone = parseObject.getString("phone");
//                            Log.e(TAG, name + " " + phone);
//                            Contact currentFriend = new Contact(name, phone, 0);
//                            pendingContacts.add(currentFriend);
//                        }
//                    });
//                }
//            }
//            instance.setPendingFriends(pendingContacts);
//        }



//        for (int i=0; i<instance.getPendingContacts().size(); i++) {
//            pendingContacts.add(instance.getPendingContacts().get(i));
//            Log.e(TAG + " adding", instance.getPendingContacts().get(i).toString());
//        }
        pendingContacts.addAll(instance.getPendingFriends());

        Log.i(TAG + " Pending Friends", instance.getPendingFriends().toString());
        Log.e(TAG + " adding", instance.getPendingFriends().size() + "");
        Log.i(TAG + "onCreate Pending Contacts", pendingContacts.toString());


//        if (savedInstanceState == null) {
//            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//            android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
//            FriendsFragment ff = new FriendsFragment();
//            ft.add(R.id.container, ff);
//            ft.commit();
//        }


//        if(findViewById(R.id.container) != null) {
//            if (savedInstanceState != null) {
//                return;
//            }
//            FriendsFragment ff = new FriendsFragment();
//            getSupportFragmentManager().beginTransaction().add(R.id.container, ff).commit();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
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

    //FRAGMENTS for the two tabs

    public static class FriendsFragment extends Fragment {
        //Empty Constructor
        public FriendsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            pendingContacts = instance.getPendingContacts();
//            Log.i(TAG + " FriendsFragment Pending", instance.getPendingContacts.toString());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.i("ContactsFragment", "onCreateView Fired for FriendsFragment");
            final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
            //pendngParseContacs = Array of Contact Object id's as strings?
            //Need to iterate through this array of Parse objects and look up each of the contacts through their id
            //Then we need to create a contact and add it to an arraylist to pass to the array adapter

//            if (ParseUser.getCurrentUser().get("contacts") != null) {
//
//                JSONArray contacts = ParseUser.getCurrentUser().getJSONArray("contacts");
//                Log.e(TAG + " Friends", contacts.toString());
////                pendingContacts = null;
//                for (int i = 0; i < contacts.length(); i++) {
//                    String id = null;
//                    try {
//                        id = contacts.get(i).toString();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.e(TAG, id);
//                    if (id != null) {
//                        ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
//                        query.whereEqualTo("objectId", id); // query.whereEqualTo("parent", user);
//                        query.getFirstInBackground(new GetCallback<ParseObject>() {
//                            @Override
//                            public void done(final ParseObject parseObject, ParseException e) {
//                                String name = parseObject.getString("name");
//                                String phone = parseObject.getString("phone");
//                                Log.e(TAG, name + " " + phone);
//                                Contact currentFriend = new Contact(name, phone, 0);
//                                pendingContacts.add(currentFriend);
//                                }
//                            });
//                        }
//                   }
//                Log.i(TAG + " Pending Friends", pendingContacts.toString());

            Log.i(TAG + "Frag Pending Contacts", pendingContacts.toString());
                ListView contactListView = (ListView) rootView.findViewById(R.id.friendListView);
                ListAdapter adapter = new FriendAdapter(getActivity(), R.id.contactListItem, pendingContacts);
                contactListView.setAdapter(adapter);

                TextView noFriends = (TextView) rootView.findViewById(R.id.noFriends);
                noFriends.setVisibility(View.GONE);

//            }
            //Hide noFriends if there are friends
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
