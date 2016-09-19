package com.assignment.xiaoduo.survivalguide.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.assignment.xiaoduo.survivalguide.activities.ComposeActivity;
import com.assignment.xiaoduo.survivalguide.activities.ReadArticleActivity;
import com.assignment.xiaoduo.survivalguide.adapters.PackageAdapter;
import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.PackageItem;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.helpers.HttpHelper;
import com.assignment.xiaoduo.survivalguide.util.Util;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends ProgressFragment implements AbsListView.OnItemClickListener, FloatingActionButton.OnFragmentLoadMoreListener {


    public static final String ARG_CATEGORY_ID = "category_id";
    public static final String ARG_UNIT = "unit";
    private int category;
    private String unit;
    public static List<PackageItem> data;
    public static PackageAdapter adapter;
    SwipeRefreshLayout swipeLayout;
    public int POST_STATUS = 29;
    public static int articlePosition = 0;
    private static final int VIEW_ARTICLE = 3;
    private View mContentView;
    private static boolean mIsLoading = false;
    private ItemFragmentCallbacks mCallbacks;
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */

    // TODO: Rename and change types of parameters
    public static ItemFragment newInstance(int param1, String param2) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, param1);
        args.putString(ARG_UNIT, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            category = getArguments().getInt(ARG_CATEGORY_ID);
            unit = getArguments().getString(ARG_UNIT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (ListView) mContentView.findViewById(android.R.id.list);
        mListView.setAdapter(adapter);
        swipeLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
                ListAppTask task = new ListAppTask();
                task.execute("currentTime", "refresh");
            }
        });
        FloatingActionButton fab = (FloatingActionButton) mContentView.findViewById(R.id.fab);
        fab.attachToListView(mListView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
            }

            @Override
            public void onScrollUp() {
            }
        }, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),
                        ComposeActivity.class);
                startActivityForResult(intent, POST_STATUS);
            }
        });
        fab.setLoadMoreInterface(ItemFragment.this);
        ListAppTask task = new ListAppTask();
        task.execute("currentTime", "refresh");
        mListView.setOnItemClickListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup content view
        setContentView(mContentView);
        // Setup text for empty content
        setEmptyText(R.string.empty);
        if (mEmptyAddPostButton != null) {
            mEmptyAddPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),
                            ComposeActivity.class);
                    startActivityForResult(intent, POST_STATUS);
                }
            });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        data = new ArrayList<>();
        adapter = new PackageAdapter(getActivity(), data);
        try {
            mCallbacks = (ItemFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        articlePosition = position;
        StaticResource.item = data.get(position);
        Intent mIntent = new Intent(getActivity(),
                ReadArticleActivity.class);
        startActivityForResult(mIntent, VIEW_ARTICLE);
        getActivity().overridePendingTransition(
                R.anim.pull_in_right,
                R.anim.push_out_left);
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onFragmentLoadMore() {
        if (category != 0 && !mIsLoading) {
            mIsLoading = true;
            new ListAppTask().execute(StaticResource.lastPostTimeStamp, "loadmore");
        }
    }

    public class ListAppTask extends
            AsyncTask<String, String, List<PackageItem>> {

        JSONArray resultArray = null;
        private String mode = "";
        private SQLiteDatabase sampleDB = null;


        protected List<PackageItem> doInBackground(String... args) {
            mode = args[1];
            switch (category) {
                case -1: // new posts
                    resultArray = HttpHelper.getTop20NewPostLists(args[0]);
                    break;
                case 0: // mypost
                    resultArray = HttpHelper.myPostLists(StaticResource.user
                            .getUserID());
                    break;
                case 1: // unit evaluation
                    resultArray = HttpHelper.getTop20PostLists(
                            LocalConfiguration.CATALOG_EVALUATION + "", args[0], unit);
                    break;
                case 2: // unit qna
                    resultArray = HttpHelper.getTop20PostLists(
                            LocalConfiguration.CATALOG_QNA + "", args[0], unit);
                    break;
                default:
                    resultArray = HttpHelper.getTop20PostLists(category + "",
                            args[0]);
                    break;
            }
            JSONObject jo;
            PackageItem content;
            if (resultArray != null) {
                try {
                    ArrayList<PackageItem> shortList = new ArrayList<>();
                    sampleDB = getActivity().openOrCreateDatabase( // null pointer
                            LocalConfiguration.dbName, Activity.MODE_PRIVATE,
                            null);
                    for (int i = 0; i < resultArray.length(); i++) {
                        content = new PackageItem();
                        jo = resultArray.getJSONObject(i);
                        content.setTitle(jo.get("title").toString());
                        content.setReply("💬 "
                                + jo.get("responseCount").toString());
                        content.setTime(Util.dateFormTransfer(jo
                                .get("postDate").toString(), getActivity()));
                        content.setAuthor(jo.getJSONObject("userID").getString(
                                "userName"));
                        content.setAbstracts(jo.get("content").toString());
                        content.setPostId(jo.get("postID").toString());
                        content.setThumb("❤️ " + jo.get("postLike").toString());
                        content.setViewed("👀 "
                                + jo.get("clickCount").toString());

                        content.setAuthorID(jo.getJSONObject("userID")
                                .getString("userID"));
                        if (category == -1) {
                            content.setCatelog(jo.getJSONObject("catalogID")
                                    .getString("catalogName") + ": ");
                        } else {
                            content.setCatelog("");
                        }
                        if (jo.has("unitID")) {
                            if (jo.getJSONObject("unitID").has("unitID")) {
                                content.setSub_title(jo.getJSONObject("unitID").getString("unitID"));
                            }
                        }
                        if (i == resultArray.length() - 1) {
                            StaticResource.lastPostTimeStamp = jo.get(
                                    "postDate").toString();
                        }
                        try {
                            Cursor c = sampleDB.rawQuery(
                                    "SELECT * FROM " + LocalConfiguration.viewedTable
                                            + " where userID = "
                                            + StaticResource.user.getUserID()
                                            + " and postID = "
                                            + content.getPostId(), null);
                            // If Cursor is valid
                            if (c != null) {
                                // Move cursor to first row
                                if (c.moveToFirst()) {
                                    content.setMarked(true);
                                } else {
                                    content.setMarked(false);
                                }
                            } else {
                                content.setMarked(false);
                            }
                            if (c != null) {
                                c.close();
                            }
                        } catch (Exception e) {
                            content.setMarked(false);
                            // e.printStackTrace();
                        }

                        shortList.add(content);
                    }

                    return shortList;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        protected void onPostExecute(List<PackageItem> result) {

            swipeLayout.setRefreshing(false);
            if (mode.equals("refresh")) {
                data.clear();
                if (result != null) {
                    data.addAll(result);
                    adapter.notifyDataSetChanged();
                    setContentShown(true);
                    setContentEmpty(false);
                } else {
                    setContentEmpty(true);
                    setContentShown(true);
                }
            } else if (mode.equals("loadmore")) {
                mIsLoading = false;
                if (result != null) {
                    data.addAll(result);
                    adapter.notifyDataSetChanged();
                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == POST_STATUS) {
            if (resultCode == Activity.RESULT_OK) {
                swipeLayout.setRefreshing(true);
                ListAppTask list = new ListAppTask();
                list.execute("currentTime", "refresh");
                mCallbacks.onNewPost();
            }
        }
    }

    public interface ItemFragmentCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNewPost();

    }

}
