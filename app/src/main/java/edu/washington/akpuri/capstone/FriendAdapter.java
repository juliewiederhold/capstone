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
    private final SingletonContacts instance;

    public FriendAdapter(Context context, int resource, ArrayList<Contact> contacts) {
        super(context, resource, contacts);
        this.context = context;
        this.list = contacts;
        this.instance = SingletonContacts.getInstance();
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.contact_friend_list_item, parent, false);

        TextView contactName = (TextView) view.findViewById(R.id.contactName);
        TextView contactNumber = (TextView) view.findViewById(R.id.contactNumber);
        ImageView contactIcon = (ImageView) view.findViewById(R.id.contactIcon);
        contactName.setText(list.get(position).getName());
        contactNumber.setText(list.get(position).getPhone());
        return view;
    }
}
