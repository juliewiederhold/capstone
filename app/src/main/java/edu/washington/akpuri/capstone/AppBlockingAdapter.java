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
    private final SingletonAppBlocking appInstance;
    private ArrayList<App> allApps;
    private String isNightOutInstance;

    public AppBlockingAdapter(Context context, int resource, ArrayList<App> apps, String isNightOutInstance) {
        super(context, resource, apps);
        this.appInstance = SingletonAppBlocking.getInstance();
        this.allApps = apps;
        this.isNightOutInstance = isNightOutInstance;
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
                                //appInstance.updateIsSelectedOfAppInAllApps(app, true);
                                for(int i = 0; i < appInstance.getAllApps().size(); i++){
                                    if(app.getName().equals(appInstance.getAllApps().get(i).getName())){
                                        allApps.get(i).setIsBlocked(true);
                                     /*   if(isNightOutInstance.equals("NightOut"))
                                            allApps.get(i).setIsNightOutOnly(true);
                                        else
                                            allApps.get(i).setIsNightOutOnly(false);*/
                                    }
                                }
                            } else {
                                //appInstance.updateIsSelectedOfAppInAllApps(app, false);
                                for(int i = 0; i < appInstance.getAllApps().size(); i++){
                                    if(app.getName().equals(appInstance.getAllApps().get(i).getName())){
                                        allApps.get(i).setIsBlocked(false);

                                   /*     if(isNightOutInstance.equals("NightOut"))
                                            allApps.get(i).setIsNightOutOnly(true);
                                        else
                                            allApps.get(i).setIsNightOutOnly(false);*/
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

      /*  if(isNightOutInstance.equals("NightOut")){
            holder.checkbox.setChecked(allApps.get(position).isBlocked()); // If it is a night out instance, set any checkbox since all are wanted to be shown
        } else {
            if(!allApps.get(position).getIsNightOutOnly()){         // If it is an edit default settings instance AND is NOT a Night out only setting, show as is
                holder.checkbox.setChecked(allApps.get(position).isBlocked());
            } else {                                            // If it is an edit default settings instance AND IS a night out only setting, show it as false
                holder.checkbox.setChecked(false);
            }
        }*/

        // ImageView?
        //holder.checkbox.setChecked(appList.get(position).isSelected());
        return view;
    }
}
