package edu.washington.akpuri.capstone;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class Contacts extends ActionBarActivity {

    private final static String TAG = "Contacts.java";

    private static ArrayList<Contact> pendingContacts;
    private static boolean allowContactRetrieval;
    private android.support.v7.app.ActionBar actionBar;
    private static SingletonContacts instance;
    private static SingletonUser userInstance;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userInstance = SingletonUser.getInstance();
        instance = SingletonContacts.getInstance();

        setContentView(R.layout.activity_contacts);
        pendingContacts = new ArrayList<>();
        allowContactRetrieval = userInstance.getAllowContactRetrieval();


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

        android.support.v7.app.ActionBar.Tab third = actionBar.newTab()
                .setText("Requests")
                .setTabListener(new FragmentTabListener<RequestsFragment>(
                        this, "Requests", RequestsFragment.class));
        actionBar.addTab(third);

        Button next = (Button) findViewById(R.id.contactsNext);
        if(userInstance.getHasGoneThroughInitialSetUp()){
            next.setText("Done");
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "contactsNext button pressed");
                    finish();
                    Intent safeZones = new Intent(Contacts.this, EditDefaultSettings.class);
                    startActivity(safeZones);
                }
            });
        } else {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "contactsNext button pressed");
                    Intent safeZones = new Intent(Contacts.this, SafetyZonePage.class);
                    startActivity(safeZones);
                }
            });
        }


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
                            userInstance.setAllowContactRetrieval(true);
                            userInstance.getCurrentUser().put("importContacts", true);              // NICOLE: current user = null and we are unsure why
                            Intent addFriends = new Intent(Contacts.this, AddFriends.class);
                            addFriends.putExtra("caller", "Contacts");
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
                    addFriends.putExtra("caller", "Contacts");
                    startActivity(addFriends);
                }
            }
        });

        // Removing this fixed the bug where friends disappear when going through Edit Default Settings
//        if (!instance.getSosoFriends().isEmpty()) {
//            pendingContacts.addAll(instance.getSosoFriends());
//        }

//        Log.i(TAG, " Soso Friends " + instance.getSosoFriends().toString());
//        Log.e(TAG, " adding " + instance.getSosoFriends().size() + "");
//        Log.i(TAG, " onCreate Pending Contacts " + pendingContacts.toString());

        // Get all friend requests
        if (!instance.hasSavedRequests()) {
            Log.e(TAG, "Adding friend requests");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
            query.whereEqualTo("phone", userInstance.getPhone());
            query.whereEqualTo("pending", true);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        counter = 0;
                        for (ParseObject matches : objects) {
                            // use dealsObject.get('columnName') to access the properties of the Deals object.
                            String match = matches.get("user").toString();
                            // Get each match's info from parse
                            ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                            query1.whereContains("username", match);
                            query1.getFirstInBackground(new GetCallback<ParseUser>() {
                                @Override
                                public void done(ParseUser parseUser, ParseException e) {
                                    try {
                                        Contact person = new Contact(parseUser.get("firstname").toString() + " " + parseUser.get("lastname").toString(),
                                                parseUser.get("phone").toString(),
                                                counter);
                                        person.setEmail(parseUser.getUsername());
                                        instance.addPendingRequests(person);
                                        counter++;
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else {
                        // Error
                    }

                }
            });
            instance.setHasSavedRequests(true);
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String jsonData = extras.getString("com.parse.Data");
            Log.e(TAG, jsonData);
            if (jsonData != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    String uri = jsonObject.getString("uri");
                    if (uri.equals("app://host/contacts")) {
                        // Get friend requests again or add
                        String message = jsonObject.getString("message");
                        String phone = getNumberFromMessage(message);
                        ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                        query1.whereContains("phone", phone);
                        query1.getFirstInBackground(new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                try {
                                    Contact person = new Contact(parseUser.get("firstname").toString() + " " + parseUser.get("lastname").toString(),
                                            parseUser.get("phone").toString(),
                                            counter);
                                    person.setEmail(parseUser.getUsername());
                                    instance.addPendingRequests(person);
                                    counter++;
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });

                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            // https://parse.com/questions/how-do-i-get-the-json-from-a-push-notification-in-android-when-the-activity-is-opened-by-clicking-on-the-notification
        }
    }

    public static String getNumberFromMessage(String str){
        return str.substring(str.indexOf('(')+1,str.indexOf(')'));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "on resume");
//        pendingContacts.clear();
//        if (!instance.getPendingContacts().isEmpty()) {
//            instance.addSosoFriends(instance.getPendingContacts());
////            instance.getPendingContacts().clear();
//        }
//        if (!instance.getSosoFriends().isEmpty()) {
//            pendingContacts.addAll(instance.getSosoFriends());
//        }

//        Log.e(TAG, "pending friends: " + instance.getSosoFriends().toString());
//        Log.e(TAG, "pending contacts: " + instance.getPendingContacts().toString());
//        Log.e(TAG, "pending requests: " + instance.getPendingRequests().toString());
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String jsonData = extras.getString("com.parse.Data");
            Log.e(TAG, jsonData);
            if (jsonData != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    String uri = jsonObject.getString("uri");
                    if (uri.equals("app://host/contacts")) {
                        // Get friend requests again or add
                        String message = jsonObject.getString("alert");
                        String phone = getNumberFromMessage(message);
                        boolean exists = false;
                        for (Contact contact : instance.getPendingRequests()) {
                            if (contact.getPhone().equals(phone)) {
                                exists = true;
                            }
                        }
                        if (!exists) {
                            Log.e(TAG, "Does not exist yet.");
                            ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                            query1.whereContains("phone", phone);
                            query1.getFirstInBackground(new GetCallback<ParseUser>() {
                                @Override
                                public void done(ParseUser parseUser, ParseException e) {
                                    try {
                                        Contact person = new Contact(parseUser.get("firstname").toString() + " " + parseUser.get("lastname").toString(),
                                                parseUser.get("phone").toString(),
                                                0);
                                        person.setEmail(parseUser.getUsername());
                                        instance.addPendingRequests(person);
//                                        counter++;
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
                        }

                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            // https://parse.com/questions/how-do-i-get-the-json-from-a-push-notification-in-android-when-the-activity-is-opened-by-clicking-on-the-notification
        }
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
            /*case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;*/
            case R.id.action_logout:
                logout();
                break;
            /*case R.id.action_safetyzones:
                Intent intent2 = new Intent(this, SafetyZonePage.class);
                this.startActivity(intent2);
                break;
            case R.id.action_contacts:
                Intent intent3 = new Intent(this, Contacts.class);
                this.startActivity(intent3);
                break;*/
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void logout() {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        userInstance.setCurrentUser(currentUser);
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

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

            Button contactsNext = (Button) container.findViewById(R.id.contactsNext);
            contactsNext.setVisibility(View.VISIBLE);

            Button addFriends = (Button) container.findViewById(R.id.addFriends);
            addFriends.setVisibility(View.VISIBLE);

            Button createGroup = (Button) container.findViewById(R.id.createGroup);
            createGroup.setVisibility(View.GONE);

            ListView contactListView = (ListView) rootView.findViewById(R.id.friendListView);
            TextView noFriends = (TextView) rootView.findViewById(R.id.noFriends);
            if (!pendingContacts.isEmpty()) {
                noFriends.setVisibility(View.GONE);
            } else {
                noFriends.setVisibility(View.VISIBLE);
            }

            // Populate with current friends
            // NICOLE: should replace pendingContacts with instance.getSosoFriends()
            final ListAdapter adapter = new FriendAdapter(getActivity(), R.id.contactListItem, pendingContacts);
            contactListView.setAdapter(adapter);

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            // Hits this but not the contacts.java onresume
            Log.e(TAG, "Frag pending friends: " + instance.getSosoFriends().toString());
            Log.e(TAG, "Frag pending contacts: " + instance.getPendingContacts().toString());

//            if (!instance.getPendingContacts().isEmpty()) {
//                pendingContacts.addAll(instance.getPendingContacts());
//                instance.getPendingContacts().clear();
//            }


            // ADDED THIS, but adds over and over
//            if (!instance.getSosoFriends().isEmpty()) {
//                pendingContacts.addAll(instance.getSosoFriends());
//            }

            pendingContacts.clear();

            if (!instance.getPendingContacts().isEmpty()) {
                instance.addSosoFriends(instance.getPendingContacts());
//            instance.getPendingContacts().clear();
            }
            if (!instance.getSosoFriends().isEmpty()) {
                pendingContacts.addAll(instance.getSosoFriends());
            }

            ListView contactListView = (ListView) getView().findViewById(R.id.friendListView);

            TextView noFriends = (TextView) getView().findViewById(R.id.noFriends);
            if (!pendingContacts.isEmpty()) {
                noFriends.setVisibility(View.GONE);
            } else {
                noFriends.setVisibility(View.VISIBLE);
            }

            // Need to nullify existing adapter?
            final ListAdapter adapter = new FriendAdapter(getActivity(), R.id.contactListItem, pendingContacts);
            contactListView.setAdapter(adapter);
        }

//        http://cyrilmottier.com/2011/06/20/listview-tips-tricks-1-handle-emptiness/
    }

    public static class GroupsFragment extends Fragment {
        //Empty Constructor
        public GroupsFragment(){}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_group, container, false);

            Button contactsNext = (Button) container.findViewById(R.id.contactsNext);
            contactsNext.setVisibility(View.GONE);

            Button addFriends = (Button) container.findViewById(R.id.addFriends);
            addFriends.setVisibility(View.GONE);

            Button createGroup = (Button) container.findViewById(R.id.createGroup);
            createGroup.setVisibility(View.VISIBLE);

//            Log.e(TAG, instance.getPendingRequests().toString())
//            ListView contactListView = (ListView) getView().findViewById(R.id.addFriendsList);
//            ListAdapter adapter = new ContactAdapter(getActivity(), R.id.friendListItem, instance.getAllContacts(), instance.getPendingRequests());
//            contactListView.setAdapter(adapter);

            return rootView;
        }
    }

    public static class RequestsFragment extends Fragment {
        public RequestsFragment() {
            // Empty constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_requests, container, false);

            Button contactsNext = (Button) container.findViewById(R.id.contactsNext);
            contactsNext.setVisibility(View.GONE);

            Button addFriends = (Button) container.findViewById(R.id.addFriends);
            addFriends.setVisibility(View.GONE);

            Button createGroup = (Button) container.findViewById(R.id.createGroup);
            createGroup.setVisibility(View.GONE);

//            /// START CODE BLOCK
//            // This code should be for friends who have already accepted the friend request
//            // Nicole -----> move to correct place
//            ParseRelation<ParseUser> relation = ParseUser.getCurrentUser().getRelation("Friends");
////            ParseQuery query = relation.getQuery();
////            relation.getQuery().whereEqualTo("username", ParseUser.getCurrentUser());
//            relation.getQuery().findInBackground(new FindCallback<ParseUser>() {
//                @Override
//                public void done(List<ParseUser> results, ParseException e) {
//                    if (e != null) {
//                        // There was an error
//                    } else {
//                        // results have all the Posts the current user liked.
//                        Log.e(TAG, results.toString());
//                    }
//                }
//            });
//            // END CODE BLOCK

            ListView contactListView = (ListView) rootView.findViewById(R.id.pendingFriendListView);
            TextView noPendingFriends = (TextView) rootView.findViewById(R.id.noPendingFriends);
            if (!instance.getPendingRequests().isEmpty()) {
                noPendingFriends.setVisibility(View.GONE);
            } else {
                noPendingFriends.setVisibility(View.VISIBLE);
            }


            final ListAdapter adapter = new RequestsAdapter(getActivity(), R.id.requestsListItem, instance.getPendingRequests());
            contactListView.setAdapter(adapter);


            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();

            Log.e(TAG, "Frag pending requests: " + instance.getPendingRequests().toString());

            ListView contactListView = (ListView) getView().findViewById(R.id.pendingFriendListView);
            TextView noPendingFriends = (TextView) getView().findViewById(R.id.noPendingFriends);
            if (!instance.getPendingRequests().isEmpty()) {
                noPendingFriends.setVisibility(View.GONE);
            } else {
                noPendingFriends.setVisibility(View.VISIBLE);
            }

            final ListAdapter adapter = new RequestsAdapter(getActivity(), R.id.requestsListItem, instance.getPendingRequests());
            contactListView.setAdapter(adapter);
        }



    }
}
