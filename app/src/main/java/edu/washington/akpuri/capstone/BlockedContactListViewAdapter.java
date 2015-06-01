package edu.washington.akpuri.capstone;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Julie on 4/20/15.
 */
public class BlockedContactListViewAdapter extends ArrayAdapter<Contact> {
    private final ArrayList<Contact> blockedContacts;
    private static SingletonUser userInstance = SingletonUser.getInstance();

    public BlockedContactListViewAdapter(Context context, int resource,  ArrayList<Contact> blockedContacts) {
        super(context, resource, blockedContacts);
        this.blockedContacts = blockedContacts;
    }

    static class ViewHolder {
        protected TextView contactName;
        protected TextView contactNumber;
        protected ImageView contactIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.blocked_contact_list, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.contactName = (TextView) view.findViewById(R.id.appName);
            viewHolder.contactNumber = (TextView) view.findViewById(R.id.contactNumber);
            viewHolder.contactIcon = (ImageView) view.findViewById(R.id.appIcon);

            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.contactName.setText(blockedContacts.get(position).getName());
        holder.contactNumber.setText(blockedContacts.get(position).getPhone());

        Drawable picture = userInstance.getProfilePicture();
        if(picture != null)
            holder.contactIcon.setImageDrawable(picture);

        return view;
    }
}
