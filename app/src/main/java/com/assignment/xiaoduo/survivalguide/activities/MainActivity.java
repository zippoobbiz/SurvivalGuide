package com.assignment.xiaoduo.survivalguide.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.fragments.AboutFragment;
import com.assignment.xiaoduo.survivalguide.fragments.FacultyFragment;
import com.assignment.xiaoduo.survivalguide.fragments.ItemFragment;
import com.assignment.xiaoduo.survivalguide.fragments.MyDraftFragment;
import com.assignment.xiaoduo.survivalguide.fragments.NavigationDrawerFragment;
import com.assignment.xiaoduo.survivalguide.fragments.MyReplyFragment;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ItemFragment.ItemFragmentCallbacks {


    private NavigationDrawerFragment mNavigationDrawerFragment;
    private int currentMenuSelected = 4;
    private CharSequence mTitle;
    public static final int PICK_IMAGE = 1;
    public static final int PICK_Camera_IMAGE = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        mTitle = getString(R.string.title_section1);
        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
    }

    //when selected a item from navigation drawer
    @Override
    public void onNavigationDrawerItemSelected(int number) {
        // update the main content by replacing fragments
        if (number != currentMenuSelected || number == 7) {
            if (number == 7) {
                mTitle = getString(R.string.title_section2);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FacultyFragment.newInstance())
                        .commit();
            } else if (number == 3) {
                mTitle = getString(R.string.my_reply);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MyReplyFragment.newInstance())
                        .commit();
            } else if (number == 4) {
                mTitle = getString(R.string.my_draft);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MyDraftFragment.newInstance())
                        .commit();
            } else if (number == 15) {
                mTitle = getString(R.string.title_section10);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AboutFragment.newInstance())
                        .commit();
            } else {
                int catalogID = -1;
                String unitTitle = "none";
                FragmentManager fragmentManager = getFragmentManager();
                switch (number) {
                    case 2:
                        mTitle = getString(R.string.my_posts);
                        catalogID = LocalConfiguration.CATALOG_MY_POST;
                        break;
                    case 6:
                        mTitle = getString(R.string.title_section1);
                        catalogID = LocalConfiguration.CATALOG_NEW_POSTS;
                        break;
                    case 8:
                        mTitle = getString(R.string.title_section3);
                        catalogID = LocalConfiguration.CATALOG_EXPERIENCE;
                        break;
                    case 9:
                        mTitle = getString(R.string.title_section4);
                        catalogID = LocalConfiguration.CATALOG_TRADE;
                        break;
                    case 10:
                        mTitle = getString(R.string.title_section5);
                        catalogID = LocalConfiguration.CATALOG_LIFE;
                        break;
                    case 11:
                        mTitle = getString(R.string.title_section6);
                        catalogID = LocalConfiguration.CATALOG_ACTIVITY;
                        break;
                    case 12:
                        mTitle = getString(R.string.title_section7);
                        catalogID = LocalConfiguration.CATALOG_HOUSE_RENT;
                        break;
                    case 13:
                        mTitle = getString(R.string.title_section8);
                        catalogID = LocalConfiguration.CATALOG_CAREER;
                        break;
                    case 14:
                        mTitle = getString(R.string.title_section9);
                        catalogID = LocalConfiguration.CATALOG_FEEDBACK;
                        break;
//                    case 15:
//                        mTitle = getString(R.string.title_section10);
//                        catalogID = LocalConfiguration.CATALOG_OTHER;
//                        break;
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.container, ItemFragment.newInstance(catalogID, unitTitle))
                        .commit();
            }
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(mTitle);
            currentMenuSelected = number;
        }
    }

    //when user click logout, callback fram Navigation fragment
    @Override
    public void OnLogoutButtonClicked() {
        StaticResource.clear();
        StaticResource.numberOfUnReadThreshold = 0;
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,
                LoginLoadingActivity.class);
        intent.putExtra(LoginLoadingActivity.LOG_STATUS, true);
        startActivity(intent);
        finish();
    }

    @Override
    public void OnPortraitClick() {
        new AlertDialog.Builder(MainActivity.this).setItems(
                new String[]{getString(R.string.take_picture), getString(R.string.select_from_gallery)},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (which) {
                            case 0:
                                String fileName = "new-photo-name.jpg";
                                // create parameters for Intent with
                                // filename
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE,
                                        fileName);
                                values.put(
                                        MediaStore.Images.Media.DESCRIPTION,
                                        "Image captured by camera");
                                // imageUri is the current activity
                                // attribute, define and save it
                                // for later usage (also in
                                // onSaveInstanceState)
                                imageUri = MainActivity.this
                                        .getContentResolver()
                                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                values);
                                // create new Intent
                                Intent intent = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        imageUri);
                                intent.putExtra(
                                        MediaStore.EXTRA_VIDEO_QUALITY, 1);
                                startActivityForResult(intent,
                                        PICK_Camera_IMAGE);
                                break;
                            case 1:
                                try {
                                    Intent gintent = new Intent();
                                    gintent.setType("image/*");
                                    gintent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent
                                                    .createChooser(gintent,
                                                            "Select Picture"),
                                            PICK_IMAGE);
                                } catch (Exception e) {
                                    toast(e.getMessage());
                                }
                                break;
                            default:
                                break;
                        }

                    }
                }).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
//            getActionBar().setTitle(mTitle);
            //do not create menu for now
//            if (false && currentMenuSelected != 4) {
//                getMenuInflater().inflate(R.menu.main, menu);
//                restoreActionBar();
//                return true;
//            }
        }
        return super.onCreateOptionsMenu(menu);
    }


    //set option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_Camera_IMAGE) {
            if (resultCode == RESULT_OK) {
                NavigationDrawerFragment fragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
                fragment.updatePortrait(imageUri);
            } else if (resultCode == RESULT_CANCELED) {
                toast("Picture was not taken");
            }
        } else if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImageUri = data.getData();
                NavigationDrawerFragment fragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
                fragment.updatePortraitFromGallery(selectedImageUri);
            }
        } else if (requestCode == FacultyFragment.UNIT_ID) {
            if (resultCode == RESULT_OK) {
                String faculty = data.getStringExtra(ListViewFilterActivity.FACULTY_ID);
                mTitle = faculty;
                getSupportActionBar().setTitle(faculty);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ItemFragment.newInstance(LocalConfiguration.CATALOG_EVALUATION, faculty))
                        .commit();
            }
        }

    }

    //after post new article, refresh the drawer menu
    @Override
    public void onNewPost() {
        NavigationDrawerFragment fragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        fragment.getPostDistribution();
    }

    //when user press back button
    @Override
    public void onBackPressed() {
        NavigationDrawerFragment fragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        fragment.toggle();

    }

    public void toast(String text) {
        Toast toast = Toast.makeText(MainActivity.this, text,
                Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackground(null);
        toast.show();
    }
}
