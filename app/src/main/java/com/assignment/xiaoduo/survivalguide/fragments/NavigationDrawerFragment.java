package com.assignment.xiaoduo.survivalguide.fragments;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.assignment.xiaoduo.customized.view.flatbutton.FButton;
import com.assignment.xiaoduo.survivalguide.activities.CroperActivity;
import com.assignment.xiaoduo.survivalguide.activities.MainActivity;
import com.assignment.xiaoduo.survivalguide.adapters.MenuAdapter;
import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.EntryItem;
import com.assignment.xiaoduo.survivalguide.entities.Item;
import com.assignment.xiaoduo.survivalguide.entities.SectionItem;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.helpers.HttpHelper;
import com.assignment.xiaoduo.survivalguide.helpers.ImageLoader;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;
    private boolean isExit = false;
    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private ImageView potrait;
    public static final int UPDATE_USER_PORTRAIT = 4;
    private MenuAdapter adapter;
    ArrayList<Item> items = new ArrayList<>();

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
        getPostDistribution();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) view.findViewById(R.id.left_drawer_list);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        items.add(new SectionItem("Mine"));
        items.add(new EntryItem(getString(R.string.my_posts), "", R.drawable.menu_icon_mypost));
        items.add(new EntryItem(getString(R.string.my_reply), "", R.drawable.menu_icon_reply));
        items.add(new EntryItem(getString(R.string.my_draft), "", R.drawable.menu_icon_draft));

        items.add(new SectionItem("Channels"));
        items.add(new EntryItem(getString(R.string.title_section1), "20", R.drawable.menu_icon_home));
        items.add(new EntryItem(getString(R.string.title_section2), "10", R.drawable.menu_icon_home));
        items.add(new EntryItem(getString(R.string.title_section3), "1", R.drawable.menu_icon_experience));
        items.add(new EntryItem(getString(R.string.title_section4), "15", R.drawable.menu_icon_trade));
        items.add(new EntryItem(getString(R.string.title_section5), "33", R.drawable.menu_icon_life));
        items.add(new EntryItem(getString(R.string.title_section6), "1", R.drawable.menu_icon_activity));
        items.add(new EntryItem(getString(R.string.title_section7), "312", R.drawable.menu_icon_home));
        items.add(new EntryItem(getString(R.string.title_section8), "11", R.drawable.menu_icon_home));
        items.add(new EntryItem(getString(R.string.title_section9), "9", R.drawable.menu_icon_feedback));
        items.add(new EntryItem(getString(R.string.title_section10), "", R.drawable.menu_icon_others));
//        items.add(new EntryItem(getString(R.string.title_section10), "10", R.drawable.menu_icon_others));
        adapter = new MenuAdapter(getActivity(), items);

//        mDrawerListView.setAdapter(new ArrayAdapter<String>(
//                getActionBar().getThemedContext(),
//                android.R.layout.simple_list_item_activated_1,
//                android.R.id.text1,
//                new String[]{
//                        getString(R.string.my_posts),
//                        getString(R.string.my_reply),
//                        getString(R.string.my_draft),
//                        getString(R.string.title_section1),
//                        getString(R.string.title_section2),
//                        getString(R.string.title_section3),
//                        getString(R.string.title_section4),
//                        getString(R.string.title_section5),
//                        getString(R.string.title_section6),
//                        getString(R.string.title_section7),
//                        getString(R.string.title_section8),
//                        getString(R.string.title_section9),
//                        getString(R.string.title_section10),
//                }));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.header_user_info, container);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,convertDpToPixel(178));
        header.setLayoutParams(lp);
        TextView username = (TextView) header.findViewById(R.id.userName);
        username.setText(StaticResource.user.getUserName());
        potrait = (ImageView) header.findViewById(R.id.portrait);
        ImageLoader mLoader = new ImageLoader(getActivity());
        String url = LocalConfiguration.getPicUrl() + LocalConfiguration.image_url + "user"
                + StaticResource.user.getUserID() + "/portrait";
        mLoader.DisplayImage(url, potrait);
        potrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.OnPortraitClick();
            }
        });
        LinearLayout footer = (LinearLayout) inflater.inflate(R.layout.footer_user_logout, container);
        Button logout = (FButton) footer.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.OnLogoutButtonClicked();
            }
        });

        mDrawerListView.addHeaderView(header);
        mDrawerListView.addFooterView(footer);
        mDrawerListView.setAdapter(adapter);
        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                toolbar,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);

        void OnLogoutButtonClicked();

        void OnPortraitClick();
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null,
                null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    public void updatePortrait(Uri imageUri) {
        String filePath = null;
        try {
            // OI FILE Manager
            String fileManagerString = imageUri.getPath();

            // MEDIA GALLERY
            String selectedImagePath = getPath(imageUri);

            if (selectedImagePath != null) {
                filePath = selectedImagePath;
            } else if (fileManagerString != null) {
                filePath = fileManagerString;
            } else {
                toast("Unknown path");
            }

            if (filePath != null) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), CroperActivity.class);
                intent.putExtra(CroperActivity.IMAGE_PATH, filePath);
                intent.putExtra(CroperActivity.GET_IMAGE_MODE, MainActivity.PICK_Camera_IMAGE);
                startActivityForResult(intent, UPDATE_USER_PORTRAIT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePortraitFromGallery(Uri imageUri) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), CroperActivity.class);
        intent.setData(imageUri);
        intent.putExtra(CroperActivity.GET_IMAGE_MODE, MainActivity.PICK_IMAGE);
        startActivityForResult(intent, UPDATE_USER_PORTRAIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_USER_PORTRAIT) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap portraitImage = data.getParcelableExtra("croped_image");
                uploadPortraitTask task = new uploadPortraitTask();
                task.execute(portraitImage);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Picture was not taken");
            }
        }
    }

    public class uploadPortraitTask extends AsyncTask<Bitmap, String, String> {

        Bitmap b;

        @Override
        protected String doInBackground(Bitmap... args) {
            b = args[0];
            return HttpHelper.postPortrait(args[0], getActivity());
        }

        @Override
        protected void onPostExecute(String result) {
            ImageLoader mLoader = new ImageLoader(getActivity());
            mLoader.put(LocalConfiguration.getPicUrl() + LocalConfiguration.image_url + "user"
                    + StaticResource.user.getUserID() + "/portrait", b);
            potrait.setImageBitmap(b);
        }
    }

    public void getPostDistribution() {
        new getPostDistributionTask().execute();
    }

    public class getPostDistributionTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            return HttpHelper.getPostDistribution();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                String[] distribution = result.split(",");
                int sum = 0;
                for (String c : distribution) {
                    sum += Integer.parseInt(c);
                }

                items.clear();
                items.add(new SectionItem("Mine"));
                items.add(new EntryItem(getString(R.string.my_posts), "", R.drawable.menu_icon_mypost));
                items.add(new EntryItem(getString(R.string.my_reply), "", R.drawable.menu_icon_reply));
                items.add(new EntryItem(getString(R.string.my_draft), "", R.drawable.menu_icon_draft));
                items.add(new SectionItem("Channels"));
                items.add(new EntryItem(getString(R.string.title_section1), sum + "", R.drawable.menu_icon_home));
                items.add(new EntryItem(getString(R.string.title_section2), distribution[0] + "/" + distribution[1], R.drawable.menu_icon_home));
                items.add(new EntryItem(getString(R.string.title_section3), distribution[2], R.drawable.menu_icon_experience));
                items.add(new EntryItem(getString(R.string.title_section4), distribution[3], R.drawable.menu_icon_trade));
                items.add(new EntryItem(getString(R.string.title_section5), distribution[4], R.drawable.menu_icon_life));
                items.add(new EntryItem(getString(R.string.title_section6), distribution[5], R.drawable.menu_icon_activity));
                items.add(new EntryItem(getString(R.string.title_section7), distribution[6], R.drawable.menu_icon_home));
                items.add(new EntryItem(getString(R.string.title_section8), distribution[7], R.drawable.menu_icon_home));
                items.add(new EntryItem(getString(R.string.title_section9), distribution[8], R.drawable.menu_icon_feedback));
                items.add(new EntryItem(getString(R.string.title_section10), "", R.drawable.menu_icon_others));
                adapter.notifyDataSetChanged();
            }
        }
    }
    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public void toggle()
    {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView)) {
            ToQuitTheApp();
        }else if(mDrawerLayout != null)
        {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }
    private void ToQuitTheApp() {
        if (isExit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            isExit = false;
            startActivity(intent);
            System.exit(0);
        } else {
            isExit = true;
            toast(getString(R.string.press_again_to_close));
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }
    }
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    public void toast(String text) {
        Toast toast = Toast.makeText(getActivity(), text,
                Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackground(null);
        toast.show();
    }
}
