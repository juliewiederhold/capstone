package edu.washington.akpuri.capstone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class AppNumberBlocking extends ActionBarActivity {
    AppBlockingAdapter adapter = null;
    private SingletonAppBlocking appInstance;
    private SingletonContacts contactsInstance;
    private SingletonUser userInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_blocking);

        contactsInstance = SingletonContacts.getInstance();
        appInstance = SingletonAppBlocking.getInstance();
        userInstance = SingletonUser.getInstance();

        if(contactsInstance.getBlockedContacts()!= null && contactsInstance.getBlockedContacts().size() > 0){
            TextView description = (TextView) findViewById(R.id.blockedContactDescription);
            description.setText("");
        }

        displayListView();

        Button addFromContacts = (Button) findViewById(R.id.addFromContacts);
        addFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userInstance.getAllowContactRetrieval()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Allow So-So to use your Contacts? It's awfully Risky you know")
                            .setTitle("Import Contacts");
                    builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            userInstance.setAllowContactRetrieval(true);
                            Intent addFriends = new Intent(AppNumberBlocking.this, BlockContacts.class);
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
                if (userInstance.getAllowContactRetrieval()) {
                    Intent addFriends = new Intent(AppNumberBlocking.this, BlockContacts.class);
                    startActivity(addFriends);
                }
            }
        });

        Button editBlockedContacts = (Button) findViewById(R.id.editBlockedContacts);
        if(contactsInstance.getBlockedContacts().size() < 1){
            editBlockedContacts.setVisibility(View.INVISIBLE);
        } else {
            editBlockedContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(contactsInstance.getBlockedContacts() != null && contactsInstance.getBlockedContacts().size() > 0){
                        Intent editBlockedFriends = new Intent(AppNumberBlocking.this, EditBlockedContactsList.class);
                        startActivity(editBlockedFriends);
                    }
                }
            });
        }

        if(contactsInstance.getBlockedContacts() != null){
            ListView blockedNumberListView = (ListView) findViewById(R.id.blockedContacts);
            ListAdapter simpleAdpt = new BlockedContactListViewAdapter(this, R.layout.blocked_contact_list, contactsInstance.getBlockedContacts());

           // ListAdapter adapter = new ContactAdapter(this, R.id.contactListItem, allContacts, pendingContacts);
            blockedNumberListView.setAdapter(simpleAdpt);
        }

        if(userInstance.getHasGoneThroughInitialSetUp()){
            Button saveButton = (Button) findViewById(R.id.next);
            saveButton.setText("Done");
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent next = new Intent(AppNumberBlocking.this, EditDefaultSettings.class);
                    startActivity(next);
                }
            });
        } else {
            Button nextButton = (Button) findViewById(R.id.next);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent next = new Intent(AppNumberBlocking.this, QuickText.class);
                    next.putExtra("activitySent","AppNumberBlocking");
                    startActivity(next);
                }
            });
        }
    }

    private void displayListView(){
        if(appInstance.getAllApps().size() == 0){
            appInstance.addAppToAllApps(new App("Facebook"));
            appInstance.addAppToAllApps(new App("SnapChat"));
            appInstance.addAppToAllApps(new App("Twitter"));
        }

        adapter = new AppBlockingAdapter(this, R.layout.app_block_item, appInstance.getAllApps(), "EditDefault");
        ListView view = (ListView) findViewById(R.id.appContainer);
        view.setAdapter(adapter);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                String item = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + item,
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_blocking, menu);
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
