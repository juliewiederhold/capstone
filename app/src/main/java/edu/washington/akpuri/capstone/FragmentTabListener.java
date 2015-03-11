package edu.washington.akpuri.capstone;

import android.app.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

/**
 * Created by Akash on 3/11/15.
 * FragmentTabListener is used as an onclicklistener by the tabs in our contacts activity
 * developer.android.com/guide/topics/ui/actionbar.html
 */
public class FragmentTabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;

    /**
     * Constructor used each time a new tab is created.
     *
     * @param activity - The host Activity, used to instantiate the fragment
     * @param tag      - The identifier tag for the fragment
     * @param clz      - The fragment's Class, used to instantiate the fragment
     */

    public FragmentTabListener(Activity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
    }

   /* The following are each of the ActionBar.TabListener callbacks */

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Check if the fragment is already initialized
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        }
    }

    //Listens for a tab that is no longer selected, meaning another tab was selected
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
