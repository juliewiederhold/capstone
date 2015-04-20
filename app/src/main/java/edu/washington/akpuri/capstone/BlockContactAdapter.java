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
 * Created by Julie on 4/5/15.
 */
public class BlockContactAdapter extends ArrayAdapter<Contact> {
    private final ArrayList<Contact> contactList;
    private final Context context;
    private final ArrayList<Contact> blockedContacts;
    private final SingletonContacts instance;

    public BlockContactAdapter(Context context, int resource, ArrayList<Contact> allContacts, ArrayList<Contact> blockedContacts) {
        super(context, resource, allContacts);
        this.context = context;
        this.contactList = allContacts;
        this.blockedContacts = blockedContacts;
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
                            Contact person = (Contact) viewHolder.checkbox
                                    .getTag();
                            person.setSelected(buttonView.isChecked());
                            if (person.isSelected()) {
                                // Add to pending contactList
                                if(!blockedContacts.contains(person))
                                    blockedContacts.add(person);
                            } else {
                                // Remove from pending contactList
                                Toast.makeText(context,
                                        "Removed: " + person.getName(),
                                        Toast.LENGTH_SHORT).show();
                                blockedContacts.remove(person);

                            }
                            instance.setBlockedContacts(blockedContacts);

                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(contactList.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(contactList.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.contactName.setText(contactList.get(position).getName());
        holder.contactNumber.setText(contactList.get(position).getPhone());
        // ImageView?
        holder.checkbox.setChecked(contactList.get(position).isSelected());
        return view;
    }
}
