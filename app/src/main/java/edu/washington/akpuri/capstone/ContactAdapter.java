package edu.washington.akpuri.capstone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.washington.akpuri.capstone.Contact;
import edu.washington.akpuri.capstone.R;

/**
 * Created by Akash on 3/9/2015.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter (Context context, int resource, ArrayList<Contact> contacts) {
        super(context, resource, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.contact_list_item, parent, false);
        }

        Contact contact = getItem(position);

        ImageView contactIcon = (ImageView) v.findViewById(R.id.contactIcon);
        TextView contactName = (TextView) v.findViewById(R.id.contactName);
        TextView contactNumber = (TextView) v.findViewById(R.id.contactNumber);
        contactName.setText(contact.getName());
        contactNumber.setText(contact.getPhone());
        //LayoutInflater inflater = (LayoutInflater) context
        //        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        return v;
    }
}
