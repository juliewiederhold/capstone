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
 */
public class NightOutGroupAdapter extends ArrayAdapter<Contact> {

    private static final String TAG = "NightOutGroupAdapter";
    private final ArrayList<Contact> contacts;
    private final Context context;
    private final ArrayList<Contact> sosoFriends;
    private static SingletonNightOutGroup groupInstance;

    public NightOutGroupAdapter(Context context, int resource, ArrayList<Contact> contacts, ArrayList sosoFriends){
        super(context, resource, contacts);
        this.context = context;
        this.sosoFriends = sosoFriends;
        this.contacts = contacts;
        this.groupInstance = SingletonNightOutGroup.getInstance();
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
            // NEED TO UNCHECKKKKK HOW?
            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {

                            Log.e(TAG, isChecked + "");
                            final Contact person = (Contact) viewHolder.checkbox.getTag();
                            if (isChecked) {
                                Log.e(TAG, getPosition(person) + "");
                                person.setSelected(buttonView.isChecked());
                                if (person.isSelected()) {
                                    // Add to pending list
                                    //                                pendingList.add(person);

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
                                    query.whereEqualTo("user", ParseUser.getCurrentUser().getString("email"));
                                    query.whereEqualTo("phone", person.getPhone());
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(final ParseObject parseObject, ParseException e) {
                                            if (parseObject != null) {
                                                Log.e(TAG, "Contact exists");
                                                // Shouldn't need 2 lines below if have a clean install
                                                Log.e(TAG, person.getName());
//                                                instance.getAllContacts().remove(person);
//                                                instance.removeContact(person);
                                                notifyDataSetChanged();
                                                //
                                            } else {
                                                Log.e(TAG, "Contact DNE yet");
//                                                DNE = true;

//                                                pendingList.add(person);
//                                                instance.removeContact(person);

                                                // CREATES CONTACT OBJECT RIGHT AWAY
                                                // SHOULD WAIT TILL USER HITS SEND FRIEND REQUEST
                                                // HOW: save each person into an arraylist
                                                // Then pass that arraylist and foreach through it


                                            }
                                        }
                                    });
                                } else {
                                    // Remove from pending list
                                    Log.e(TAG, "remove " + person.getName());
//                                    pendingList.remove(person);

                                }
                            } else {
                                Log.e(TAG, "remove " + person.getName());
//                                pendingList.remove(person);
                            }
                            // Save pending friends into singleton
                            //                            instance.setPendingContacts(pendingList);
//                            if (!pendingList.isEmpty()) {
//                                Log.e(TAG, instance.getAllContacts().toString());
//                                instance.setPendingContacts(pendingList);
//                                //                                createParseObjects(pendingList);
//                            }
//                            Log.e(TAG, "instance pending contacts: " + instance.getPendingContacts().toString());
                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(contacts.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(contacts.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.contactName.setText(contacts.get(position).getName());
        holder.contactNumber.setText(contacts.get(position).getPhone());
        // ImageView?
//        holder.checkbox.setChecked(list.get(position).isSelected());
        holder.checkbox.setChecked(false);

        return view;
    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = null;
//        if (convertView == null) {
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            view = inflater.inflate(R.layout.friend_list_item, parent, false);
//            final ViewHolder viewHolder = new ViewHolder();
//            viewHolder.contactName = (TextView) view.findViewById(R.id.appName);
//            viewHolder.contactNumber = (TextView) view.findViewById(R.id.contactNumber);
//            viewHolder.contactIcon = (ImageView) view.findViewById(R.id.appIcon);
//            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.appBlock);
//            view.setTag(viewHolder);
//            viewHolder.checkbox.setTag(contacts.get(position));
//        } else {
//            view = convertView;
//            ((ViewHolder) view.getTag()).checkbox.setTag(contacts.get(position));
//        }
//        ViewHolder holder = (ViewHolder) view.getTag();
//        holder.contactName.setText(contacts.get(position).getName());
//        holder.contactNumber.setText(contacts.get(position).getPhone());
//        holder.checkbox.setChecked(false);
//        return view;
//    }
}
