package edu.washington.akpuri.capstone;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

/**
 * Created by Akash on 3/9/2015.
 * Used by Addfriends.java to import phone contacts into So-So
 *
 * BUGS:
 *  - If on contacts list, if you check, uncheck and then quickly hit send request button, it’ll still add that person you unchecked!
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    private final ArrayList<Contact> list;
    private final Context context;
    private final ArrayList<Contact> pendingList;   // temporarily store pending So-So friends
    private final SingletonContacts instance;
    private final static String TAG = "ContactAdapter";
    private Boolean DNE = false;

    public ContactAdapter(Context context, int resource, ArrayList<Contact> contacts, ArrayList<Contact> pendingList) {
        super(context, resource, contacts);
        this.context = context;
        this.list = contacts;
        this.pendingList = pendingList;
        this.instance = SingletonContacts.getInstance();
    }

    static class ViewHolder {
        protected TextView contactName;
        protected TextView contactNumber;
        protected ImageView contactIcon;
        protected CheckBox checkbox;
    }

    //http://stackoverflow.com/questions/14509552/uncheck-all-checbox-in-listview-in-android
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.friend_list_item, parent, false);

//            viewHolder.contactName.setBackgroundColor(666);
//            viewHolder.contactName.setTextColor(333);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.contactName = (TextView) view.findViewById(R.id.appName);
            viewHolder.contactNumber = (TextView) view.findViewById(R.id.contactNumber);
            viewHolder.contactIcon = (ImageView) view.findViewById(R.id.appIcon);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.appBlock);
            viewHolder.checkbox.setChecked(false);
            // NEED TO UNCHECKKKKK HOW?
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.e(TAG, isChecked + "");

                            // Get the position of the checkbox
                            final Contact person = (Contact) viewHolder.checkbox.getTag();
                            // Set the value of the checkbox to maintain its state
                            person.setSelected(buttonView.isChecked());
                            if (buttonView.isChecked()) {
                                Log.e(TAG, getPosition(person) + "");
//                                person.setSelected(buttonView.isChecked());
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

                                                instance.removeContact(person);
                                                notifyDataSetChanged();
                                                //
                                            } else {
                                                Log.e(TAG, "Contact DNE yet");
                                                DNE = true;

                                                pendingList.add(person);
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
//                                    Log.e(TAG, "remove " + person.getName());
//                                    pendingList.remove(person);

                                }
                            } else {
                                Log.e(TAG, "remove " + person.getName());
                                pendingList.remove(person);
                            }
                            // Save pending friends into singleton
                            //                            instance.setPendingContacts(pendingList);
                            if (!pendingList.isEmpty()) {
                                Log.e(TAG, instance.getAllContacts().toString());
                                instance.setPendingContacts(pendingList);
                                //                                createParseObjects(pendingList);
                            }
                            Log.e(TAG, "instance pending contacts: " + instance.getPendingContacts().toString());
                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.contactName.setText(list.get(position).getName());
        holder.contactNumber.setText(list.get(position).getPhone());

        // ImageView?
//        holder.checkbox.setChecked(list.get(position).isSelected());
//        holder.checkbox.setChecked(false);

        return view;
    }

}