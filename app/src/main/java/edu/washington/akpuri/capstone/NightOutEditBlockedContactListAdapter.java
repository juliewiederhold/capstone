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
 * Created by Julie on 4/23/15.
 */
public class NightOutEditBlockedContactListAdapter extends ArrayAdapter<Contact> {
    private final Context context;
    private final ArrayList<Contact> blockedContacts;
    private final SingletonNightOutSettings instance;

    public NightOutEditBlockedContactListAdapter(Context context, int resource, ArrayList<Contact> blockedContacts) {
        super(context, resource, blockedContacts);
        this.context = context;
        this.blockedContacts = blockedContacts;
        this.instance = SingletonNightOutSettings.getInstance();
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
            viewHolder.checkbox.setChecked(false);
            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            Contact person = (Contact) viewHolder.checkbox
                                    .getTag();
                            person.setSelected(buttonView.isChecked());
                            if (person.isSelected()) {
                                // Remove from Blocked Contacts
                                if(blockedContacts.contains(person))
                                    blockedContacts.remove(person);
                            } else {
                                // Make sure they are still in Blocked Contacts.
                                // Re add if user checks then unchecks
                                if(!blockedContacts.contains(person))
                                    blockedContacts.add(person);
                            }
                            instance.setNightOutBlockedContacts(blockedContacts);

                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(blockedContacts.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(blockedContacts.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.contactName.setText(blockedContacts.get(position).getName());
        holder.contactNumber.setText(blockedContacts.get(position).getPhone());
        // ImageView?

        //holder.checkbox.setChecked(blockedContacts.get(position).isSelected());
        return view;
    }
}
