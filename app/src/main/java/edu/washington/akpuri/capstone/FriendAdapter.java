package edu.washington.akpuri.capstone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iguest on 3/12/15.
 */
public class FriendAdapter extends ArrayAdapter<Contact> {

    private final ArrayList<Contact> list;
    private final Context context;
    private final ArrayList<Contact> pendingList;
    private final SingletonContacts instance;

    public FriendAdapter(Context context, int resource, ArrayList<Contact> contacts, ArrayList<Contact> pendingList) {
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
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.friend_list_item, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.contactName = (TextView) view.findViewById(R.id.contactName);
            viewHolder.contactNumber = (TextView) view.findViewById(R.id.contactNumber);
            viewHolder.contactIcon = (ImageView) view.findViewById(R.id.contactIcon);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.contactName.setText(list.get(position).getName());
        holder.contactNumber.setText(list.get(position).getPhone());
        return view;
    }
}
