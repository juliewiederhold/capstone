package edu.washington.akpuri.capstone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by iguest on 3/12/15.
 */
public class FriendAdapter extends ArrayAdapter<Contact> {

    private final static String TAG = "FriendAdapter";

    private final ArrayList<Contact> list;
    private final Context context;
    private final SingletonContacts instance;
    private LayoutInflater mInflater;
    private ArrayList<String> newCurrentContacts = new ArrayList<String>();

    public FriendAdapter(Context context, int resource, ArrayList<Contact> contacts) {
        super(context, resource, contacts);
        this.context = context;
        this.list = contacts;
        this.instance = SingletonContacts.getInstance();
        // Cache reference to avoid looking it up on every getView call
        mInflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        protected TextView contactName;
        protected TextView contactNumber;
        protected ImageView contactIcon;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
//        Log.d(TAG, "position=" + position);
        final Contact data = list.get(position);
        View view = null;
        if (convertView == null) {
//            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = mInflater.inflate(R.layout.contact_friend_list_item, parent, false);
//            Log.e("FriendAdapter", instance.getPendingFriends().toString());
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.contactName = (TextView) view.findViewById(R.id.appName);
            viewHolder.contactNumber = (TextView) view.findViewById(R.id.contactNumber);
            viewHolder.contactIcon = (ImageView) view.findViewById(R.id.appIcon);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, data.getName() + " " + data.getId() + " clicked");

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Do you want to delete " + data.getName() + "?");
                    alert.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alert.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Remove Contact from list & update list
                                    Log.e(TAG, " BEFORE Pending Friends " + instance.getPendingFriends().toString());
                                    instance.getPendingFriends().remove(data);
                                    instance.getAllContacts().add(data);
                                    Log.e(TAG, " AFTER Pending Friends " + instance.getPendingFriends().toString());

                                    remove(getItem(position));  // Remove from list
                                    notifyDataSetChanged();

                                    // Remove from Parse
                                    final String user = ParseUser.getCurrentUser().getString("email");
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactsObject");
                                    query.whereEqualTo("user", user);
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(final ParseObject parseObject, ParseException e) {
                                            if (parseObject != null) {
                                                // get contact object
                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
                                                query.whereEqualTo("user", user);
                                                query.whereEqualTo("phone", data.getPhone());
                                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                    @Override
                                                    public void done(final ParseObject parseObject, ParseException e) {
                                                        if (parseObject != null) {
                                                            Log.e(TAG, "Contact exists. Delete!");

                                                            String objectId = parseObject.getObjectId();
                                                            Log.e(TAG, "from parse: " + objectId);
                                                            Log.e(TAG, "from data obj: " + data.getObjectId());

                                                            for(int i=0; i < instance.getCurrentContacts().size(); i++) {
                                                                String currentId = instance.getCurrentContacts().get(i);
                                                                if (!currentId.equals(objectId)) {
                                                                    newCurrentContacts.add(currentId);
                                                                }
                                                            }
                                                            instance.getCurrentContacts().clear();
                                                            instance.setCurrentContacts(newCurrentContacts);
                                                            newCurrentContacts = null;

                                                            // Delete contact object
                                                            parseObject.deleteInBackground();

                                                            // Where Part I used to be
                                                        } else {
                                                            Log.e(TAG, "Contact DNE yet. Can't delete");

                                                        }
                                                        // Where Part II used to be
                                                    }
                                                });
                                                // end get contact object

                                                // Delete from user's contact list
                                                // Part 1: Delete from current user's contacts[]
                                                ParseUser.getCurrentUser().put("contacts", instance.getCurrentContacts());
                                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        Log.e(TAG, "Saved contacts[]");
                                                    }
                                                });
                                                // Part 2: Delete from current user's ContactsObject[]
                                                parseObject.put("contacts", instance.getCurrentContacts());
                                                parseObject.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        Log.e(TAG, "Saved ContactsObject[]");
                                                    }
                                                });
                                            } else {
                                                // Something went wrong
                                                Log.e("Contacts", "Failed to retrieve contactsObject: " + e);
                                            }
                                        }
                                    });
                                    // End remove from Parse
                                    Toast mes = Toast.makeText(context, "Friend Requests Sent", Toast.LENGTH_LONG);
                                    mes.show();
                                }
                            });
                    alert.create().show();

                }
            });
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.contactName.setText(data.getName());
        if (data.isPending()) {
            // TEMPORARY ****
            holder.contactName.setText(holder.contactName.getText() + " Pending");
        }
        holder.contactNumber.setText(data.getPhone());
        return view;
    }
}
