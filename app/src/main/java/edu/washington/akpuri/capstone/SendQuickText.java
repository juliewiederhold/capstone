package edu.washington.akpuri.capstone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class SendQuickText extends ActionBarActivity {
    private static ArrayList<HashMap<String, String>> quickTextInformation = new ArrayList<>();
    private static SingletonNightOutSettings instance;

    static class ViewHolder {
        protected TextView appName;
        protected CheckBox checkbox;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_quick_text);

        instance = SingletonNightOutSettings.getInstance();

        initList();
        if(instance.getNightOutQuickTexts().size() > 0){
            TextView empty = (TextView) findViewById(R.id.emptyQuickTextMessage);
            empty.setText("");
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
                Intent backToMap = new Intent(SendQuickText.this, MainMap.class);
                startActivity(backToMap);
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMap = new Intent(SendQuickText.this, MainMap.class);
                startActivity(backToMap);
            }
        });



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
                    LayoutInflater inflater = getActivity().getLayoutInflater();

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
