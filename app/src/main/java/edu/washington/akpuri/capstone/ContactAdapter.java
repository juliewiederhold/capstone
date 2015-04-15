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
 * Created by Akash on 3/9/2015.
 * Used by Addfriends.java to import phone contacts into So-So
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    private final ArrayList<Contact> list;
    private final Context context;
    private final ArrayList<Contact> pendingList;   // temporarily store pending So-So friends
    private final SingletonContacts instance;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
                            Contact person = (Contact) viewHolder.checkbox.getTag();
                            person.setSelected(buttonView.isChecked());
                            if (person.isSelected()) {
                                // Add to pending list
                                pendingList.add(person);
                            } else {
                                // Remove from pending list
                                pendingList.remove(person);

                            }
                            // Save pending friends into singleton
                            instance.setPendingContacts(pendingList);
                            // Trying to get Current Friends to load instantly; DOESN'T WORK :(
//                            instance.setPendingFriends(pendingList);

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
        holder.checkbox.setChecked(list.get(position).isSelected());
        return view;
    }
}