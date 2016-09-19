package com.assignment.xiaoduo.survivalguide.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.assignment.xiaoduo.survivalguide.activities.ComposeActivity;
import com.assignment.xiaoduo.survivalguide.adapters.DraftAdapter;
import com.assignment.xiaoduo.survivalguide.entities.Draft;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.helpers.SQLiteHelper;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MyDraftFragment extends ProgressFragmentNew implements AbsListView.OnItemClickListener {

    private ArrayList<Draft> drafts;
    private final int EDIT_DRAFT = 12;
    private View mContentView;
    private AbsListView mListView;
    private DraftAdapter mAdapter;
    private Set<Integer> mSelection = new HashSet<>();

    public static MyDraftFragment newInstance() {
        return new MyDraftFragment();
    }

    public MyDraftFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_my_draft, container, false);
        // Set the adapter
        mListView = (ListView) mContentView.findViewById(android.R.id.list);
        mAdapter = new DraftAdapter(getActivity(), drafts);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setOnItemLongClickListener(new AbsListView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                mListView.setItemChecked(position, !mAdapter.isPositionChecked(position));
                return false;
            }
        });
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mAdapter.clearSelection();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                nr = 0;
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.item_delete:
                        nr = 0;
                        mAdapter.clearSelection();


                        List<Object> list = new ArrayList<Object>(mSelection);
                        int[] ids = new int[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            ids[i] = (int) list.get(i);
                        }

                        SQLiteHelper.deleteDraftsSuccessfully(getActivity(), ids);
                        LoadDraftTask task = new LoadDraftTask();
                        task.execute();
                        mode.finish();
                }
                return false;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                if (checked) {
                    nr++;
                    mAdapter.setNewSelection(position, true);
                    mSelection.add(drafts.get(position).getId());
                } else {
                    nr--;
                    mAdapter.removeSelection(position);
                    mSelection.remove(drafts.get(position).getId());
                }
                mode.setTitle(nr + " selected");
            }

        });
        LoadDraftTask task = new LoadDraftTask();
        task.execute();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup content view
        setContentView(mContentView);
        // Setup text for empty content
        setEmptyText(R.string.empty);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        drafts = new ArrayList<>();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_DRAFT) {
            if (resultCode == Activity.RESULT_OK) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (StaticResource.uploadSuccess) {
                            StaticResource.uploadSuccess = false;
                            SQLiteHelper.deleteDraftSuccessfully(getActivity(), StaticResource.uploadingPostId);
                        }
                        LoadDraftTask task = new LoadDraftTask();
                        task.execute();
                    }
                }, 1000);

            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("single_draft", drafts.get(position));
        intent.setClass(getActivity(),
                ComposeActivity.class);
        startActivityForResult(intent, EDIT_DRAFT);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    class LoadDraftTask extends AsyncTask<String, Integer, String> {

        List<Draft> list = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {
            list = SQLiteHelper.getDrafts(getActivity());
            return "";
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            drafts.clear();
            drafts.addAll(list);
            if (drafts != null && drafts.size() > 0) {
                setContentShown(true);
                mAdapter.notifyDataSetChanged();
            } else {
                setContentEmpty(true);
                setContentShown(true);
            }
        }

    }

}
