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

import java.util.ArrayList;

/**
 * Created by Julie on 4/2/15.
 */
public class AppNumberAdapter extends ArrayAdapter<App> {
    private final Context context;
    private final SingletonContacts contactsInstance;
    private final SingletonAppBlocking appInstance;
    private final ArrayList<Contact> pendingList;

    public AppNumberAdapter(Context context, int resource, ArrayList<App> apps, ArrayList<App> blockedApps) {
        super(context, resource, apps);
        this.context = context;
        this.pendingList = new ArrayList<>();
        this.contactsInstance = SingletonContacts.getInstance();
        this.appInstance = SingletonAppBlocking.getInstance();
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
            view = inflater.inflate(R.layout.app_block_item, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.appName = (TextView) view.findViewById(R.id.appName);
            viewHolder.appIcon = (ImageView) view.findViewById(R.id.appIcon);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.appBlock);
            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            App app = (App) viewHolder.checkbox.getTag();
                            if (buttonView.isChecked()) {
                                   // Add to pending list
                                appInstance.updateIsSelectedOfAppInAllApps(app, true);
                            } else {
                                appInstance.updateIsSelectedOfAppInAllApps(app, false);
                            }
                            //contactsInstance.setPendingContacts(pendingList);
                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(appInstance.getAllApps().get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(appInstance.getAllApps().get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.appName.setText(appInstance.getAllApps().get(position).getName());

        holder.checkbox.setChecked(appInstance.getAllApps().get(position).isSelected());

        // ImageView?
        //holder.checkbox.setChecked(appList.get(position).isSelected());
        return view;
    }
}
