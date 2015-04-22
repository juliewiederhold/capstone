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
                                    instance.getPendingFriends().remove(data);
                                    instance.getAllContacts().add(data);

                                    Log.e("FriendAdapter", instance.getPendingFriends().toString());
                                    remove(getItem(position));
                                    notifyDataSetChanged();


                                    // Remove from Parse
                                    final String user = ParseUser.getCurrentUser().getString("email");
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactsObject");
                                    query.whereEqualTo("user", user);
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(final ParseObject parseObject, ParseException e) {
                                            if (parseObject != null) {
                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
                                                query.whereEqualTo("user", user);
                                                query.whereEqualTo("phone", data.getPhone());
                                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                    @Override
                                                    public void done(final ParseObject parseObject, ParseException e) {
                                                        if (parseObject != null) {
                                                            Log.e(TAG, "Contact exists. Delete!");

                                                            String objectId = parseObject.getObjectId();


                                                            for(int i=0; i < instance.getCurrentContacts().size(); i++) {
                                                                String currentId = instance.getCurrentContacts().get(i);
                                                                if (!currentId.equals(objectId)) {
                                                                    newCurrentContacts.add(currentId);
                                                                }
                                                            }
                                                            instance.getCurrentContacts().clear();
                                                            instance.setCurrentContacts(newCurrentContacts);
                                                            newCurrentContacts = null;

                                                            // Delete from user's contact list

                                                            Log.e(TAG, ParseUser.getCurrentUser().get("contacts").getClass().toString());

                                                            /// CONTINUE: NEED TO DELETE FROM PARSE!!!
//                                                            ParseUser.getCurrentUser().remove("contacts");
                                                            // Need updated list of contact objectIds as an ArrayList<String>
//                                                            ParseUser.getCurrentUser().put("contacts", instance.getPendingFriends().toString());
                                                            Log.e(TAG, ParseUser.getCurrentUser().get("contacts").toString());
                                                            ParseUser.getCurrentUser().put("contacts", instance.getCurrentContacts());

                                                            // Delete contact object
                                                            parseObject.deleteInBackground();

//                                                            List<String> list = new ArrayList<String>(Arrays.asList(array));
//                                                            list.removeAll(Arrays.asList(data.getId()));
//                                                            String[] contacts = new String[list.size()];
//                                                            list.toArray(contacts);
//                                                                ParseUser.getCurrentUser().get("contacts")[position]
//                                                            ParseUser.getCurrentUser().put("contacts", array);
                                                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    Log.e(TAG, "Saved contacts[]");
                                                                }
                                                            });
                                                        } else {
                                                            Log.e(TAG, "Contact DNE yet. Can't delete");

                                                        }
                                                        // Update ContactsObject[]
                                                        parseObject.put("contacts", instance.getCurrentContacts());
                                                        parseObject.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                Log.e(TAG, "ContactsObject: " + parseObject.get("contacts").toString());
                                                                instance.getPendingContacts().clear();
                                                            }
                                                        });
                                                    }
                                                });
                                            } else {
                                                // Something went wrong
                                                Log.e("Contacts", "Failed to retrieve contactsObject: " + e);
                                            }
                                        }
                                    });
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
