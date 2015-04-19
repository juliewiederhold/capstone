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
public class AppBlockingAdapter extends ArrayAdapter<App> {
    private final Context context;
    private final SingletonAppBlocking appInstance;
    ArrayList<App> allApps;

    public AppBlockingAdapter(Context context, int resource, ArrayList<App> apps, ArrayList<App> blockedApps) {
        super(context, resource, apps);
        this.context = context;
        this.appInstance = SingletonAppBlocking.getInstance();
        this.allApps = apps;
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

            String name = viewHolder.appName.toString();



            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.appBlock);
            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            App app = (App) viewHolder.checkbox.getTag();
                            if (buttonView.isChecked()) {
                                   // Add to pending list
                                //appInstance.updateIsSelectedOfAppInAllApps(app, true);
                                for(int i = 0; i < appInstance.getAllApps().size(); i++){
                                    if(app.getName().equals(appInstance.getAllApps().get(i).getName())){
                                        allApps.get(i).setIsBlocked(true);
                                    }
                                }
                            } else {
                                //appInstance.updateIsSelectedOfAppInAllApps(app, false);
                                for(int i = 0; i < appInstance.getAllApps().size(); i++){
                                    if(app.getName().equals(appInstance.getAllApps().get(i).getName())){
                                        allApps.get(i).setIsBlocked(false);
                                    }
                                }
                            }
                            //contactsInstance.setPendingContacts(pendingList);
                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(allApps.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(allApps.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.appName.setText(allApps.get(position).getName());
        //holder.appIcon.setImageDrawable();

        if(allApps.get(position).getName().equals("Facebook")){
            holder.appIcon.setImageResource(R.drawable.social_facebook_box_blue_icon);
        } else if (allApps.get(position).getName().equals("Twitter")){
            holder.appIcon.setImageResource(R.drawable.twitter_icon);
        } else {
            holder.appIcon.setImageResource(R.drawable.snapchat_icon);
        }

        holder.checkbox.setChecked(allApps.get(position).isBlocked());

        // ImageView?
        //holder.checkbox.setChecked(appList.get(position).isSelected());
        return view;
    }
}
