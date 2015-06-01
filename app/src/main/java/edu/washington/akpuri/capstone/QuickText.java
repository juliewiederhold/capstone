package edu.washington.akpuri.capstone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class QuickText extends ActionBarActivity {

    private static ArrayList<HashMap<String, String>> quickTextInformation = new ArrayList<>();
    private static SingletonQuickText quickTextInstance;
    private static SingletonUser userInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_text);

        userInstance = SingletonUser.getInstance();

        quickTextInstance = SingletonQuickText.getInstance();

        if(quickTextInstance.getIsFirstViewOfQuickTexts()){
            quickTextInstance.setAllQuickTexts(new ArrayList<String>());

            quickTextInstance.addToAllQuickTexts("I'm in the bathroom.");
            quickTextInstance.addToAllQuickTexts("I'm by the bar.");
            quickTextInstance.addToAllQuickTexts("I'm upstairs.");
            quickTextInstance.addToAllQuickTexts("I'm downstairs.");
            quickTextInstance.addToAllQuickTexts("I'm in the basement.");
            quickTextInstance.addToAllQuickTexts("Heading to ");

            quickTextInstance.setIsFirstViewOfQuickTexts(false);
        }

//        if(quickTextInstance.getAllQuickTexts().size() > 0){
//            TextView empty = (TextView) findViewById(R.id.emptyQuickTextMessage);
//            empty.setText("");
//        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.quickTextContainer, new CurrentQuickText())
                    .commit();
        }

        if(userInstance.getHasGoneThroughInitialSetUp()){
            Button saveButton = (Button) findViewById(R.id.next);
            saveButton.setText("Done");
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent next = new Intent(QuickText.this, EditDefaultSettings.class);
                    startActivity(next);
                }
            });
        } else {
            Button nextButton = (Button) findViewById(R.id.next);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userInstance.setHasGoneThroughInitialSetUp(true);
                    Intent next = new Intent(QuickText.this, MainActivity.class);
                    startActivity(next);
                }
            });
        }

        Button add_new_quick_text = (Button) findViewById(R.id.addNewQuickText);

        add_new_quick_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();

                final View fragmentView = inflater.inflate(R.layout.fragment_quick_text, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(fragmentView)
                        // Add action buttons
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText message = (EditText) fragmentView.findViewById(R.id.quickText);


                                String name = message.getText().toString();

                                if (!name.equals("")) {
                                    quickTextInstance.addToAllQuickTexts(name);

                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);

                                    updateCurrentQuickText();
                                    message.setText("");
                                } else {
                                    Context context = getApplicationContext();
                                    CharSequence text = "All fields must be filled out";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });

                builder.create();
                builder.show();
            }
        });

    }

    private void updateCurrentQuickText(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        CurrentQuickText fragment = new CurrentQuickText();
        ft.replace(R.id.quickTextContainer, fragment);   // replace instead of add
        ft.addToBackStack("Update Current Quick Text");
        ft.commit();
    }

    private static void initList(){
        quickTextInformation = new ArrayList<>();
        for(int i = 0; i < quickTextInstance.getAllQuickTexts().size(); i++){
            String message = quickTextInstance.getAllQuickTexts().get(i);
            quickTextInformation.add(createTopic(message));
        }
    }

    private static HashMap<String, String> createTopic(String message){
        HashMap<String, String> zoneInformation = new HashMap<>();
        zoneInformation.put("message", message);
        zoneInformation.put("text", message);

        return zoneInformation;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quick_text, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CurrentQuickText extends Fragment {

        public CurrentQuickText() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.current_quick_text_view, container, false);
            final ListView lv = (ListView) rootView.findViewById(R.id.listViewQuickText);
            initList();

            SimpleAdapter simpleAdpt = new SimpleAdapter(rootView.getContext(), quickTextInformation, android.R.layout.simple_list_item_1,
                    new String[] {"message"}, new int[] {android.R.id.text1});

            lv.setAdapter(simpleAdpt);
            lv.setTextFilterEnabled(true);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();

                    final View fragmentView = inflater.inflate(R.layout.fragment_quick_text, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());

                    String itemSelected = lv.getItemAtPosition(position).toString();
                    String[] fullAddress = itemSelected.split(",");
                    String addressSelected = fullAddress[0].split("=")[1];

                    int index = 0;

                    for(int i = 0; i < quickTextInstance.getAllQuickTexts().size(); i++){
                        if(addressSelected.equals(quickTextInstance.getAllQuickTexts().get(i))){
                            index = i;
                            break;
                        }
                    }

                    final int indexOfMessage = index;
                    final String currentMessage = quickTextInstance.getAllQuickTexts().get(indexOfMessage);

                    final EditText text = (EditText) fragmentView.findViewById(R.id.quickText);

                    text.setText(currentMessage);

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    builder.setView(fragmentView)
                            // Add action buttons
                            .setPositiveButton("Edit Quick Text", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    EditText messageText = (EditText) fragmentView.findViewById(R.id.quickText);

                                    quickTextInstance.getAllQuickTexts().set(indexOfMessage, messageText.getText().toString());
                                    updateCurrentQuickText();
                                }
                            })
                            .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    quickTextInstance.removeFromAllQuickTexts(indexOfMessage);

                                    Intent intent = getActivity().getIntent();
                                    getActivity().finish();
                                    startActivity(intent);

                                    updateCurrentQuickText();
                                }
                            });

                    builder.create();
                    builder.show();
                }
            });
            return rootView;
        }

        private void updateCurrentQuickText(){
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            CurrentQuickText fragment = new CurrentQuickText();
            ft.replace(R.id.quickTextContainer, fragment);   // replace instead of add
            ft.addToBackStack("Update Current Quick Text");
            ft.commit();
        }
    }
}
