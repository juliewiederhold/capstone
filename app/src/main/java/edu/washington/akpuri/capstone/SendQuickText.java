package edu.washington.akpuri.capstone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SendQuickText extends ActionBarActivity {
    private static ArrayList<HashMap<String, String>> quickTextInformation = new ArrayList<>();
    private static SingletonNightOutSettings instance;
    private static String messageToBeSent;
    private static SingletonNightOutGroup groupInstance;
    private static SingletonUser userInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_quick_text);

        instance = SingletonNightOutSettings.getInstance();
        groupInstance = SingletonNightOutGroup.getInstance();
        userInstance = SingletonUser.getInstance();

        initList();
        if(instance.getNightOutQuickTexts().size() > 0){
            TextView empty = (TextView) findViewById(R.id.emptyQuickTextMessage);
            empty.setText("");
        }

        if(messageToBeSent != null){
            EditText currentMessage = (EditText)  findViewById(R.id.message);
            currentMessage.setText(messageToBeSent);
        } else {
            messageToBeSent = "";
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.quickTextContainer, new CurrentQuickText())
                    .commit();
        }


        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMesage(v);
                messageToBeSent = "";
                Intent backToMap = new Intent(SendQuickText.this, MainMap.class);
                startActivity(backToMap);
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageToBeSent = "";
                Intent backToMap = new Intent(SendQuickText.this, MainMap.class);
                startActivity(backToMap);
            }
        });



    }

    public void sendMesage(View v){
        //String _messageNumber = messageNumber.getText().toString();

        HashMap<String, Contact> contacts = groupInstance.getGroupContact();
        contacts.put("4082096381", new Contact("Julie", "4082096381", 1));
        contacts.put("4252817575", new Contact("Jen", "4252817575", 2));
        ArrayList<String> contactNumbers = new ArrayList<>();

        Iterator<Map.Entry<String, Contact>> iterator = contacts.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Contact> entry = iterator.next();
            if(!entry.getKey().equals(userInstance.getPhone())) // do not send text message to self
                contactNumbers.add(entry.getKey());
        }

        String sent = "SMS_SENT";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(sent), 0);

        for(int i = 0; i < contactNumbers.size(); i++){
            //---when the SMS has been sent---
            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    if(getResultCode() == Activity.RESULT_OK)
                    {
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "SMS could not sent",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }, new IntentFilter(sent));

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(contactNumbers.get(i), null, messageToBeSent, sentPI, null);
        }


    }

    private static void initList(){
        quickTextInformation = new ArrayList<>();
        for(int i = 0; i < instance.getNightOutQuickTexts().size(); i++){
            String message = instance.getNightOutQuickTexts().get(i);
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

                    String itemSelected = lv.getItemAtPosition(position).toString();
                    String[] fullAddress = itemSelected.split(",");
                    String quickTextToAdd = fullAddress[0].split("=")[1];
                    if(messageToBeSent == null || messageToBeSent.equals("")){
                        messageToBeSent = quickTextToAdd;
                    } else {
                        messageToBeSent = messageToBeSent + " " + quickTextToAdd;
                    }

                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                   // EditText currentMessage = (EditText) rootView.findViewById(R.id.message);
                   // currentMessage.setText(messageToBeSent);
                   /* LayoutInflater inflater = getActivity().getLayoutInflater();

                    final View fragmentView = inflater.inflate(R.layout.fragment_send_quick_text, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());

                    String itemSelected = lv.getItemAtPosition(position).toString();
                    String[] fullAddress = itemSelected.split(",");
                    String addressSelected = fullAddress[0].split("=")[1];

                    int index = 0;

                    for(int i = 0; i < instance.getNightOutQuickTexts().size(); i++){
                        if(addressSelected.equals(instance.getNightOutQuickTexts().get(i))){
                            index = i;
                            break;
                        }
                    }

                    final int indexOfMessage = index;
                    final String currentMessage = instance.getNightOutQuickTexts().get(indexOfMessage);


                    builder.create();
                    builder.show();*/
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
