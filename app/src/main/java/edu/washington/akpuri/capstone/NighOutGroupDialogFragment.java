package edu.washington.akpuri.capstone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by iguest on 5/27/15.
 */
// TODO update strings
public class NighOutGroupDialogFragment extends DialogFragment {

    private final static String TAG = "NighOutGroupDialogFragment";
    private android.widget.AdapterView.OnItemClickListener onItemClickListener;

        /* The activity that creates an instance of this dialog fragment must
        * implement this interface in order to receive event callbacks.
        * Each method passes the DialogFragment in case the host needs to query it. */


    ArrayList<String> members;
    static NighOutGroupDialogFragment newInstance(ArrayList<String> members) {
        NighOutGroupDialogFragment dialog = new NighOutGroupDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("members", members);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        members = getArguments().getStringArrayList("members");
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.dialog_nightout, container, false);
//
//    }

    public interface NighOutGroupDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NighOutGroupDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NighOutGroupDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NighOutGroupDialogListener");
        }
    }


    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ListView list = new ListView(getActivity());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, members);
        list.setAdapter(adapter);
        list.setOnItemClickListener(onItemClickListener);
        builder.setView(list);
        builder.setMessage("Night Out Group Invitation")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "Accepted");
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(NighOutGroupDialogFragment.this);
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User rejected request
                        Log.e(TAG, "Rejected");
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(NighOutGroupDialogFragment.this);
                    }
                });
        // Create AlertDialog object and return it
        return builder.create();
    }

//    ListView myList;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.dialog_nightout, null, false);
//        myList = (ListView) view.findViewById(R.id.nightOutList);
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listitems);
//        myList.setAdapter(adapter);
//        myList.setOnClickListener(this);
//    }
}
