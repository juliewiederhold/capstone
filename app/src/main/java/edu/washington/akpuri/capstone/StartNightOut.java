package edu.washington.akpuri.capstone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;


public class StartNightOut extends ActionBarActivity {
    private SingletonUser userInstance;
    private List<Map<String, String>> topicsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_night_out);

        userInstance = SingletonUser.getInstance();

        initList();

        ListView lv = (ListView) findViewById(R.id.default_settings_list);

        SimpleAdapter adapter = new SimpleAdapter(this, topicsList, android.R.layout.simple_list_item_1,
                new String[] {"topics", "description"}, new int[] {android.R.id.text1});
        //ListView view = (ListView) findViewById(R.id.default_settings_list);

        lv.setAdapter(adapter);

        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView setting = ((TextView) view.findViewById(android.R.id.text1));
                String topic = setting.getText().toString();

                if(topic.equals("Safety Zones")) {
                    Intent intent = new Intent(StartNightOut.this, SafetyZonePage.class);
                    startActivity(intent);
                } else if(topic.equals("Blocked Apps and Contacts")) {
                    Intent intent = new Intent(StartNightOut.this, AppNumberBlocking.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(StartNightOut.this, QuickText.class);
                    startActivity(intent);
                }

                finish();
            }
        });

      /*  view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                String item = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + item,
                        Toast.LENGTH_LONG).show();
            }
        });*/
    }

    private void initList(){
        for(int i = 0; i < userInstance.getAllDefaultSettings().size(); i++){
            topicsList.add(createTopic("topics", userInstance.getAllDefaultSettings().get(i)));
        }
    }

    private HashMap<String, String> createTopic(String key1, String value1){
        HashMap<String, String> topic = new HashMap<>();
        topic.put(key1, value1);
        return topic;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_night_out, menu);
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
