package edu.washington.akpuri.capstone;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by iguest on 5/20/15.
 *
 * REPLACE contacts with sosofriends????
 */
public class NightOutGroupAdapter extends ArrayAdapter<Contact> {

    private static final String TAG = "NightOutGroupAdapter";
    private final Context context;
    private final ArrayList<Contact> sosoFriends;
    private static SingletonNightOutGroup groupInstance;
    private static SingletonContacts contactsInstance;
    private static SingletonUser userInstance;


    public NightOutGroupAdapter(Context context, int resource, ArrayList sosoFriends){
        super(context, resource, sosoFriends);
        this.context = context;
        this.sosoFriends = sosoFriends;
        this.groupInstance = SingletonNightOutGroup.getInstance();
        this.contactsInstance = SingletonContacts.getInstance();
        this.userInstance = SingletonUser.getInstance();
    }

    private static class ViewHolder {
        protected TextView contactName;
        protected TextView contactNumber;
        protected ImageView contactIcon;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.friend_list_item, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.contactName = (TextView) view.findViewById(R.id.appName);
            viewHolder.contactNumber = (TextView) view.findViewById(R.id.contactNumber);
            viewHolder.contactIcon = (ImageView) view.findViewById(R.id.appIcon);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.appBlock);
            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            final Contact person = (Contact) viewHolder.checkbox.getTag();
                            // If checked, add to NightOutGroup
                            if (isChecked) {
                                Log.e(TAG, getPosition(person) + "");
                                person.setSelected(buttonView.isChecked());
                                if (person.isSelected()) {
                                    // Add to group
                                    groupInstance.addMemberContact(person);

                                    // Look up user on Parse.com
                                    ParseQuery<ParseUser> queryA = ParseUser.getQuery();
                                    queryA.whereContains("phone", person.getPhone());
                                    queryA.getFirstInBackground(new GetCallback<ParseUser>() {
                                        @Override
                                        public void done(ParseUser parseUser, ParseException e) {
                                            if (parseUser != null) {
                                                person.setEmail(parseUser.getEmail());
                                                // Add to group
                                                groupInstance.addMemberParse(person, parseUser);
                                            }
                                        }
                                    });

                                } else {
                                    // Remove from group
//                                    Log.e(TAG, "remove " + person.getName());
//                                    groupInstance.removeMember(person);
                                }
                            } else {
                                Log.e(TAG, "remove " + person.getName());
                                groupInstance.removeMember(person);
                            }
                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(sosoFriends.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(sosoFriends.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.contactName.setText(sosoFriends.get(position).getName());
        holder.contactNumber.setText(sosoFriends.get(position).getPhone());
        // ImageView?
//        holder.checkbox.setChecked(list.get(position).isSelected());
        holder.checkbox.setChecked(false);

        return view;
    }
}
