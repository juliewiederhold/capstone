package edu.washington.akpuri.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.util.HashMap;

/**
 * Create night out group
 */
public class NightOutGroup extends ActionBarActivity {

    private final static String TAG = "NightOutGroup";
    private static SingletonContacts contactsInstance;
    private static SingletonUser userInstance;
    private static SingletonNightOutGroup groupInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_night_out_group);
        setContentView(R.layout.activity_add_friends);


        contactsInstance = SingletonContacts.getInstance();
        userInstance = SingletonUser.getInstance();
        groupInstance = SingletonNightOutGroup.getInstance();

        Log.e(TAG, contactsInstance.getSosoFriends().toString());

        // TODO
        // NOT WORKING
//        ListView friendsListView = (ListView) findViewById(R.id.addFriendsToGroupList);
////        ListAdapter adapter = new NightOutGroupAdapter(this, R.id.friendListItem, contactsInstance.getAllContacts(), contactsInstance.getSosoFriends());
//        ListAdapter adapter = new ContactAdapter(this, R.id.friendListItem, contactsInstance.getAllContacts(), contactsInstance.getSosoFriends());
//        friendsListView.setAdapter(adapter);


        ListView contactListView = (ListView) findViewById(R.id.addFriendsList);
        ListAdapter adapter = new NightOutGroupAdapter(this, R.id.friendListItem, contactsInstance.getAllContacts(), contactsInstance.getSosoFriends());
        contactListView.setAdapter(adapter);

//        Button sendRequest = (Button) findViewById(R.id.sendGroupRequest);
        Button sendRequest = (Button) findViewById(R.id.sendFriendRequest);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast mes = Toast.makeText(getApplicationContext(), "Group Request Sent", Toast.LENGTH_LONG);
                mes.show();
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ListView contactListView = (ListView) findViewById(R.id.addFriendsList);
        ListAdapter adapter = new NightOutGroupAdapter(this, R.id.friendListItem, contactsInstance.getAllContacts(), contactsInstance.getSosoFriends());
        contactListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_add_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
