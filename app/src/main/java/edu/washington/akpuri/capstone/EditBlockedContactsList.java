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
import android.widget.TextView;

import java.util.ArrayList;


public class EditBlockedContactsList extends ActionBarActivity {

    private final static String TAG = "EditBlockedContactsList.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        TextView header = (TextView) findViewById(R.id.contactListHeader);
        header.setText("Edit Blocked Contacts");

        SingletonContacts instance = SingletonContacts.getInstance();

        if(instance.getBlockedContacts() == null)
            instance.setBlockedContacts(new ArrayList<Contact>());
        ListView contactListView = (ListView) findViewById(R.id.addFriendsList);
        ListAdapter adapter = new EditBlockedContactListAdapter(this, R.id.contactListItem, instance.getBlockedContacts());
        contactListView.setAdapter(adapter);

        Button sendRequest = (Button) findViewById(R.id.sendFriendRequest);
        sendRequest.setText("Remove from Blocked Contacts");
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToBlocking = new Intent(EditBlockedContactsList.this, AppNumberBlocking.class);
                startActivity(backToBlocking);
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
