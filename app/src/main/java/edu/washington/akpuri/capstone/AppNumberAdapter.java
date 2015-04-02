package edu.washington.akpuri.capstone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Julie on 4/2/15.
 */
public class AppNumberAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> appList;
    private final ArrayList<String> blockedApps;
    private final SingletonContacts instance;

    public AppNumberAdapter(Context context, int resource, ArrayList<String> apps, ArrayList<String> blockedApps) {
        super(context, resource, apps);
        this.context = context;
        this.appList = apps;
        this.blockedApps = blockedApps;
        this.instance = SingletonContacts.getInstance();
    }

    static class ViewHolder {
        protected TextView appName;
        protected ImageView appIcon;
        protected CheckBox checkbox;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.friend_list_item, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.appName = (TextView) view.findViewById(R.id.appName);
            viewHolder.appIcon = (ImageView) view.findViewById(R.id.appIcon);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.appBlock);
            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            String app = (String) viewHolder.checkbox
                                    .getTag();
                            if (buttonView.isChecked()) {
                                // Add to pending list
                                blockedApps.add(app);
                            }
                          //  instance.setPendingContacts(pendingList);
                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(appList.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(appList.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        //holder.appName.setText(appList.get(position).getName());

        // ImageView?
        //holder.checkbox.setChecked(appList.get(position).isSelected());
        return view;
    }
}
