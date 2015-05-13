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

    private final ArrayList<Contact> pendingRequests;
    private final Context context;
    private final SingletonContacts instance;
    private final SingletonUser userInstance;
    private LayoutInflater mInflater;
    private ArrayList<String> newPendingRequests = new ArrayList<String>();

    public RequestsAdapter(Context context, int resource, ArrayList<Contact> pendingRequests) {
        super(context, resource, pendingRequests);
        this.context = context;
        this.pendingRequests = pendingRequests;
        this.instance = SingletonContacts.getInstance();
        this.userInstance = SingletonUser.getInstance();
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
        final Contact person = pendingRequests.get(position);
        View view = null;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.requests_list_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.contactName = (TextView) view.findViewById(R.id.appName);
            viewHolder.contactNumber = (TextView) view.findViewById(R.id.contactNumber);
            viewHolder.contactIcon = (ImageView) view.findViewById(R.id.appIcon);
            viewHolder.acceptRequest = (ImageButton) view.findViewById(R.id.acceptRequest);
            viewHolder.rejectRequest = (ImageButton) view.findViewById(R.id.rejectRequest);
            viewHolder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "Accepting request from " + person.getName() + " " + person.getPhone());
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereContains("phone", person.getPhone());
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            try {
                                person.setIsPending(false);
//                                final ParseUser currentUser = ParseUser.getCurrentUser();
                                // Add to currentUser's Friends on Parse.com
                                ParseRelation<ParseUser> relation = userInstance.getCurrentUser().getRelation("Friends");
                                relation.add(parseUser);
//                                currentUser.save();
                                userInstance.getCurrentUser().saveEventually();
                                // Remove from pending requests
//                                instance.getPendingRequests().remove(person);
                                pendingRequests.remove(person);
                                // Change contact pending to false
                                Log.e(TAG, position + "");
//                                remove(getItem(position));
                                notifyDataSetChanged();
                                // TODO: Add to pending contacts
                                // TODO: Probably should be pending friends
//                                instance.addPendingContact(person);
                                instance.addSosoFriend(person);
                                // Create contact object for current user - Parse.com
                                ////// Create contact ParseObject here

                                final ParseObject contact = new ParseObject("contact");
                                contact.put("name", person.getName());
                                contact.put("phone", person.getPhone());
                                contact.put("user", userInstance.getCurrentUser().getUsername());
                                contact.put("id", person.getId());
                                contact.put("pending", false);
                                contact.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            Log.e(TAG, "Error saving contactsId: " + e);
                                        } else {
                                            // Add to instance current contacts
                                            instance.addCurrentContact(contact.getObjectId());
                                            person.setObjectId(contact.getObjectId());
                                            Log.e(TAG, person.getObjectId());
                                            // Add to currentUser's contacts[]
                                            userInstance.getCurrentUser().add("contacts", contact.getObjectId());
                                            userInstance.getCurrentUser().saveInBackground();
                                            // ContactsObject[]
                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactsObject");
                                            query.whereEqualTo("user", userInstance.getCurrentUser().getUsername());
                                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                @Override
                                                public void done(final ParseObject parseObject, ParseException e) {
                                                    if (parseObject != null) {
                                                        // User exists and has a ContactsObject
                                                        parseObject.add("contacts", contact.getObjectId());
                                                        parseObject.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                // Log.e(TAG, "ContactsObject: " + parseObject.get("contacts").toString());
                                                            }
                                                        });
                                                    } else {
                                                        // Something went wrong
                                                        Log.e("Contacts", "Failed to retrieve contactsObject: " + e);
                                                    }
                                                }
                                            });
                                        }
                                    }
//                                    instance.getPendingContacts().clear();
                                });
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
                                query.whereEqualTo("phone", userInstance.getCurrentUser().get("phone"));
                                query.whereEqualTo("user", parseUser.getUsername().toString());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        try {
                                            if (e == null) {
                                                object.put("pending", false);
                                                Log.e(TAG, object.get("user").toString() + " " + object.get("phone").toString() + " " + object.get("pending").toString());
                                                object.save();
                                            } else {
                                                e.printStackTrace();
                                            }
                                        } catch (Exception error) {
                                            error.printStackTrace();
                                        }
                                    }
                                });
                                // Add to current user's instance: sosoFriends?
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        }
                    });
                    // Get contact's contact object containing pending request
                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("contact");
                    query1.whereEqualTo("user", person.getEmail());
                    query1.whereEqualTo("phone", person.getPhone());
                    query1.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject parseObject, ParseException e) {
                            if (parseObject != null) {
                                parseObject.put("pending", false);
                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.e(TAG, parseObject.get("user").toString() + " " + parseObject.get("phone") + "Pending: " + parseObject.get("pending").toString());
                                    }
                                });
                            } else {
                                // Something went wrong
                            }
                        }
                    });
                }
            });
            viewHolder.rejectRequest.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Log.e(TAG, "Rejecting request from " + person.getName() + " " + person.getPhone());
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereContains("phone", person.getPhone());
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            try {
                                person.setIsPending(false);
                                // Remove from pending requests
//                                instance.getPendingRequests().remove(person);
                                pendingRequests.remove(person);
                                // Change contact pending to false
                                Log.e(TAG, position + "");
//                                remove(getItem(position));
                                notifyDataSetChanged();

                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        }
                    });
                    // TODO: Reject friend request
                    // Get contact's contact object containing pending request
                    Log.e(TAG, "Rejecting request from " + person.getEmail() + " " + person.getPhone());
                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("contact");
                    query1.whereEqualTo("user", person.getEmail());
                    query1.whereEqualTo("phone", userInstance.getPhone());
                    query1.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject parseObject, ParseException e) {
                            if (parseObject != null) {
//                                parseObject.deleteEventually();
                                parseObject.deleteInBackground();
                                Log.e(TAG, "Should've deleted the contact object");
                            } else {
                                // Something went wrong
                                Log.e(TAG, "Didn't delete the contact object");
                            }
                        }
                    });
                    // TODO: delete from person's ContactsObject[]
                }
            });
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.contactName.setText(person.getName());
        if (person.isPending()) {
            // TEMPORARY **** NICOLE: PROBABLY DON'T NEED THIS HERE
            // TODO: Change the way we're marking pending contacts?
            holder.contactName.setText(holder.contactName.getText());
        }
        holder.contactNumber.setText(person.getPhone());
//        Log.e("height", getCount()+ "");
        return view;
    }
}
