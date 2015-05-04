package edu.washington.akpuri.capstone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.List;

public class RequestsAdapter extends ArrayAdapter<Contact> {

    private final static String TAG = "RequestsAdapter";

    private final ArrayList<Contact> list;
    private final Context context;
    private final SingletonContacts instance;
    private LayoutInflater mInflater;
    private ArrayList<String> newPendingRequests = new ArrayList<String>();

    public RequestsAdapter(Context context, int resource, ArrayList<Contact> contacts) {
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
        protected ImageButton acceptRequest;
        protected ImageButton rejectRequest;
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
//        Log.d(TAG, "position=" + position);
        final Contact data = list.get(position);
        View view = null;
        if (convertView == null) {
//            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = mInflater.inflate(R.layout.requests_list_item, parent, false);
//            Log.e("FriendAdapter", instance.getPendingFriends().toString());
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.contactName = (TextView) view.findViewById(R.id.appName);
            viewHolder.contactNumber = (TextView) view.findViewById(R.id.contactNumber);
            viewHolder.contactIcon = (ImageView) view.findViewById(R.id.appIcon);
            viewHolder.acceptRequest = (ImageButton) view.findViewById(R.id.acceptRequest);
            viewHolder.rejectRequest = (ImageButton) view.findViewById(R.id.rejectRequest);
            viewHolder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, data.getPhone());
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereContains("phone", data.getPhone());
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            try {
                                Log.e(TAG, "Pending: " + instance.getPendingRequests().toString());
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                ParseRelation<ParseUser> relation = currentUser.getRelation("Friends");
                                relation.add(parseUser);
                                currentUser.saveInBackground();
                                // Remove from pending requests
                                instance.getPendingRequests().remove(data);
                                remove(getItem(position));
                                notifyDataSetChanged();
                                Log.e(TAG, "Pending: " + instance.getPendingRequests().toString());
                                // TO-DO
                                // Add to current user's ContactsObject[] and ParseUser contacts[] - Parse.com
                                // Create contact object for current user - Parse.com
                                // Add to current user's instance: currentContacts, pendingContacts, pendingFriends
                                // LOOK AT AddFriends.java
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        }
                    });
                    // Get contact's contact object containing pending request
                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("contact");
                    query1.whereEqualTo("user", data.getEmail());
                    query1.whereEqualTo("phone", data.getPhone());
                    query1.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject parseObject, ParseException e) {
                            if (parseObject != null) {
                                parseObject.put("pending", false);
                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.e(TAG, parseObject.get("phone") + "Pending: " + parseObject.get("pending").toString());
                                    }
                                });
                            } else {
                                // Something went wrong
                            }
                        }
                    });

                }
            });
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e(TAG, data.getName() + " " + data.getId() + " clicked");
//
//                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
//                    alert.setMessage("Do you want to delete " + data.getName() + "?");
//                    alert.setNegativeButton("Cancel",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//                    alert.setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // Remove Contact from list & update list
//                                    Log.e(TAG, " BEFORE Pending Friends " + instance.getPendingFriends().toString());
//                                    instance.getPendingFriends().remove(data);
//                                    // Add deleted So-So friend back to Contacts list
//                                    instance.getAllContacts().add(data);
//                                    Log.e(TAG, " AFTER Pending Friends " + instance.getPendingFriends().toString());
//
//                                    remove(getItem(position));  // Remove from list
//                                    notifyDataSetChanged();
//
//                                    // Remove from Parse
//                                    final String user = ParseUser.getCurrentUser().getString("email");
//                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactsObject");
//                                    query.whereEqualTo("user", user);
//                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
//                                        @Override
//                                        public void done(final ParseObject parseObject1, ParseException e) {
//                                            if (parseObject1 != null) {
//                                                // get contact object
//                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
//                                                query.whereEqualTo("user", user);
//                                                query.whereEqualTo("phone", data.getPhone());
//                                                query.getFirstInBackground(new GetCallback<ParseObject>() {
//                                                    @Override
//                                                    public void done(final ParseObject parseObject, ParseException e) {
//                                                        if (parseObject != null) {
//                                                            Log.e(TAG, "Contact exists. Delete!");
//
//                                                            String objectId = parseObject.getObjectId();
//                                                            Log.e(TAG, "from parse: " + objectId);
//                                                            Log.e(TAG, "from data obj: " + data.getObjectId());
//
//                                                            for(int i=0; i < instance.getCurrentContacts().size(); i++) {
//                                                                String currentId = instance.getCurrentContacts().get(i);
//                                                                if (!currentId.equals(objectId)) {
//                                                                    newPendingRequests.add(currentId);
//                                                                }
//                                                            }
//                                                            instance.getCurrentContacts().clear();
//                                                            instance.setCurrentContacts(newPendingRequests);
//                                                            newPendingRequests = null;
//
//                                                            Log.e(TAG, "current contacts" + instance.getCurrentContacts());
//                                                            // Delete from user's contact list
//                                                            // Part 1: Delete from current user's contacts[]
//                                                            ParseUser.getCurrentUser().put("contacts", instance.getCurrentContacts());
//                                                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//                                                                @Override
//                                                                public void done(ParseException e) {
//                                                                    Log.e(TAG, "Saved contacts[]");
//                                                                }
//                                                            });
//                                                            // Part 2: Delete from current user's ContactsObject[]
//                                                            parseObject1.put("contacts", instance.getCurrentContacts());
//                                                            parseObject1.saveInBackground(new SaveCallback() {
//                                                                @Override
//                                                                public void done(ParseException e) {
//                                                                    Log.e(TAG, "Saved ContactsObject[]");
//                                                                }
//                                                            });
//
//                                                            // Delete contact object
//                                                            parseObject.deleteInBackground();
//
//                                                            notifyDataSetChanged();
//                                                        } else {
//                                                            Log.e(TAG, "Contact DNE yet. Can't delete");
//
//                                                        }
//                                                        // Where Part II used to be
//                                                    }
//                                                });
//                                                // end get contact object
//
//                                            } else {
//                                                // Something went wrong
//                                                Log.e("Contacts", "Failed to retrieve contactsObject: " + e);
//                                            }
//                                        }
//                                    });
//                                    // End remove from Parse
//                                    Toast mes = Toast.makeText(context, "Friend Requests Sent", Toast.LENGTH_LONG);
//                                    mes.show();
//                                }
//                            });
//                    alert.create().show();
//
//                }
//            });
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
        Log.e("height", getCount()+ "");
        return view;
    }
}
