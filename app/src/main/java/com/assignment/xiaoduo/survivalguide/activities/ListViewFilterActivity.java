package com.assignment.xiaoduo.survivalguide.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.assignment.xiaoduo.customized.view.listviewfilter.IndexBarView;
import com.assignment.xiaoduo.customized.view.listviewfilter.PinnedHeaderAdapter;
import com.assignment.xiaoduo.customized.view.listviewfilter.PinnedHeaderListView;
import com.assignment.xiaoduo.survivalguide.util.Util;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class ListViewFilterActivity extends ActionBarActivity {

    public static String FACULTY_ID = "facultyId";
    public static String FACULTY_DESC = "facultyDesc";
    public final static int FACULTY_OF_ART = 0;
    public final static int FACULTY_OF_ARTS = 1;
    public final static int FACULTY_OF_BUSINESS = 2;
    public final static int FACULTY_OF_EDUCATION = 3;
    public final static int FACULTY_OF_ENGINEERING = 4;
    public final static int FACULTY_OF_IT = 5;
    public final static int FACULTY_OF_LAW = 6;
    public final static int FACULTY_OF_MEDICINE = 7;
    public final static int FACULTY_OF_PHARMACY = 8;
    public final static int FACULTY_OF_SCIENCE = 9;
    // an array of countries to display in the list
    String[] ITEMS_ARY = new String[]{"East Timor", "Ecuador", "Egypt",
            "El Salvador", "Equatorial Guinea",};

    // unsorted list items
    ArrayList<String> mItems;

    // array list to store section positions
    ArrayList<Integer> mListSectionPos;

    // array list to store listView data
    ArrayList<String> mListItems;

    // custom list view with pinned header
    PinnedHeaderListView mListView;

    // custom adapter
    PinnedHeaderAdapter mAdaptor;

    // search box
    EditText mSearchView;

    // loading view
    ProgressBar mLoadingView;

    // empty view
    TextView mEmptyView;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        int facultyId = getIntent().getIntExtra(FACULTY_ID, 0);
        switch (facultyId) {
            case FACULTY_OF_ART:
                ITEMS_ARY = Util.loadItems(R.raw.art, ListViewFilterActivity.this);
                break;
            case FACULTY_OF_ARTS:
                ITEMS_ARY = Util.loadItems(R.raw.arts, ListViewFilterActivity.this);
                break;
            case FACULTY_OF_BUSINESS:
                ITEMS_ARY = Util.loadItems(R.raw.bus, ListViewFilterActivity.this);
                break;
            case FACULTY_OF_EDUCATION:
                ITEMS_ARY = Util.loadItems(R.raw.edu, ListViewFilterActivity.this);
                break;
            case FACULTY_OF_ENGINEERING:
                ITEMS_ARY = Util.loadItems(R.raw.eng, ListViewFilterActivity.this);
                break;
            case FACULTY_OF_IT:
                ITEMS_ARY = Util.loadItems(R.raw.it, ListViewFilterActivity.this);
                break;
            case FACULTY_OF_LAW:
                ITEMS_ARY = Util.loadItems(R.raw.law, ListViewFilterActivity.this);
                break;
            case FACULTY_OF_MEDICINE:
                ITEMS_ARY = Util.loadItems(R.raw.med, ListViewFilterActivity.this);
                break;
            case FACULTY_OF_PHARMACY:
                ITEMS_ARY = Util.loadItems(R.raw.pha, ListViewFilterActivity.this);
                break;
            case FACULTY_OF_SCIENCE:
                ITEMS_ARY = Util.loadItems(R.raw.sci, ListViewFilterActivity.this);
                break;
        }

        // UI elements
        setupViews();

        // Array to ArrayList
        mItems = new ArrayList<>(Arrays.asList(ITEMS_ARY));
        mListSectionPos = new ArrayList<>();
        mListItems = new ArrayList<>();

        // for handling configuration change
        if (savedInstanceState != null) {
            mListItems = savedInstanceState.getStringArrayList("mListItems");
            mListSectionPos = savedInstanceState
                    .getIntegerArrayList("mListSectionPos");

            if (mListItems != null && mListItems.size() > 0
                    && mListSectionPos != null && mListSectionPos.size() > 0)
                setListAdaptor();

            String constraint = savedInstanceState.getString("constraint");
            if (constraint != null && constraint.length() > 0) {
                mSearchView.setText(constraint);
                setIndexBarViewVisibility(constraint);
            }

        } else {
            new Poplulate().execute(mItems);
        }

    }

    private void setupViews() {
        setContentView(R.layout.listview_filter_main_act);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        mSearchView = (EditText) findViewById(R.id.search_view);
        mLoadingView = (ProgressBar) findViewById(R.id.loading_view);
        mListView = (PinnedHeaderListView) findViewById(R.id.list_view);
        mEmptyView = (TextView) findViewById(R.id.empty_view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // DO SOMETHING WHEN BUTTON PRESSED!
                ListViewFilterActivity.this.finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        mSearchView.addTextChangedListener(filterTextWatcher);
        super.onPostCreate(savedInstanceState);
    }

    private void setListAdaptor() {
        // create instance of PinnedHeaderAdapter and set adapter to list view
        mAdaptor = new PinnedHeaderAdapter(this, mListItems, mListSectionPos);
        mListView.setAdapter(mAdaptor);

        // set header view
        View mPinnedHeaderView = LayoutInflater.from(this).inflate(
                R.layout.listview_filter_section_row_view, mListView, false);
        mListView.setPinnedHeaderView(mPinnedHeaderView);

        // set index bar view
        IndexBarView mIndexBarView = (IndexBarView) LayoutInflater.from(this)
                .inflate(R.layout.listview_filter_index_bar_view, mListView,
                        false);
        mIndexBarView.setData(mListView, mListItems, mListSectionPos);
        mListView.setIndexBarView(mIndexBarView);

        // set preview text view
        View mPreviewTextView = LayoutInflater.from(this).inflate(
                R.layout.listview_filter_preview_view, mListView, false);
        mListView.setPreviewView(mPreviewTextView);

        // for configure pinned header view on scroll change
        mListView.setOnScrollListener(mAdaptor);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                String temp = mListItems.get(arg2);
                String tempDesc = temp.substring(temp.indexOf(' '), temp.length() - 1);
                temp = temp.substring(0, temp.indexOf(' '));
                Intent data = new Intent();
                data.putExtra(FACULTY_ID, temp);
                data.putExtra(FACULTY_DESC, tempDesc);
                setResult(RESULT_OK, data);
                finish();
            }

        });
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (mAdaptor != null && str != null)
                mAdaptor.getFilter().filter(str);
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

    };

    public class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread,
            // and
            // not the UI thread.

            FilterResults result = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {

                ArrayList<String> filt = new ArrayList<>();
                ArrayList<String> Items;
                synchronized (this) {
                    Items = mItems;

                    for (int i = 0; i < Items.size(); i++) {
                        String item = Items.get(i);
                        if (item.toLowerCase(Locale.getDefault()).contains(
                                constraint.toString().toLowerCase(
                                        Locale.getDefault()))) {
                            filt.add(item);
                        }
                    }

                    result.count = filt.size();
                    result.values = filt;
                }
            } else {

                synchronized (this) {
                    result.count = mItems.size();
                    result.values = mItems;
                }

            }
            return result;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            ArrayList<String> filtered = (ArrayList<String>) results.values;
            setIndexBarViewVisibility(constraint.toString());
            // sort array and extract sections in background Thread
            new Poplulate().execute(filtered);

        }

    }

    private void setIndexBarViewVisibility(String constraint) {
        // hide index bar for search results
        if (constraint != null && constraint.length() > 0)
            mListView.hideIndexBarView();
        else
            mListView.showIndexBarView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    // sort array and extract sections in background Thread here we use
    // AsyncTask
    private class Poplulate extends AsyncTask<ArrayList<String>, Void, Void> {

        private void showLoading(View content_view, View mLoadingView,
                                 View mEmptyView) {
            content_view.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }

        private void showContent(View content_view, View mLoadingView,
                                 View mEmptyView) {
            content_view.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
        }

        private void showEmptyText(View content_view, View mLoadingView,
                                   View mEmptyView) {
            content_view.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            // show loading indicator
            showLoading(mListView, mLoadingView, mEmptyView);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            mListItems.clear();
            mListSectionPos.clear();
            ArrayList<String> mItems = params[0];
            if (mItems.size() > 0) {
                // NOT forget to sort array
                Collections.sort(mItems);

                int i = 0;
                String prev_section = "";
                while (i < mItems.size()) {
                    String current_item = mItems.get(i);
                    String current_section = current_item.substring(0, 3)
                            .toUpperCase(Locale.getDefault());
                    if (!prev_section.equals(current_section)) {
                        mListItems.add(current_section);
                        mListItems.add(current_item);
                        mListSectionPos
                                .add(mListItems.indexOf(current_section));// array
                        // list
                        // of
                        // section
                        // positions
                        prev_section = current_section;
                    } else {
                        mListItems.add(current_item);
                    }
                    i++;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!isCancelled()) {
                if (mListItems.size() <= 0) {
                    showEmptyText(mListView, mLoadingView, mEmptyView);
                } else {
                    setListAdaptor();
                    showContent(mListView, mLoadingView, mEmptyView);
                }
            }
            super.onPostExecute(result);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mListItems != null && mListItems.size() > 0)
            outState.putStringArrayList("mListItems", mListItems);

        if (mListSectionPos != null && mListSectionPos.size() > 0)
            outState.putIntegerArrayList("mListSectionPos", mListSectionPos);

        String search_text = mSearchView.getText().toString();
        if (search_text != null && search_text.length() > 0)
            outState.putString("constraint", search_text);

        super.onSaveInstanceState(outState);
    }
}
