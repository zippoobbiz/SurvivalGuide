package com.assignment.xiaoduo.survivalguide.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.assignment.xiaoduo.customized.view.uitableview.UITableView;
import com.assignment.xiaoduo.customized.view.uitableview.UITableView.ClickListener;
import com.assignment.xiaoduo.survivalguide.activities.ListViewFilterActivity;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

public class FacultyFragment extends Fragment {

    private UITableView tableView;
    public static final int UNIT_ID = 15;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_unit, container,
                false);

        tableView = (UITableView) rootView.findViewById(R.id.tableView);
        createList();
        tableView.commit();

        return rootView;
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            // 相当于Fragment的onResume
//        } else {
//            // 相当于Fragment的onPause
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    private void createList() {
        CustomClickListener listener = new CustomClickListener();
        tableView.setClickListener(listener);
        tableView.addBasicItem("Faculty of Art, Design and Architecture");
        tableView.addBasicItem("Faculty of Arts");
        tableView.addBasicItem("Faculty of Business and Economics");
        tableView.addBasicItem("Faculty of Education");
        tableView.addBasicItem("Faculty of Engineering");
        tableView.addBasicItem("Faculty of Information Technology");
        tableView.addBasicItem("Faculty of Law");
        tableView
                .addBasicItem("Faculty of Medicine, Nursing and Health Sciences");
        tableView
                .addBasicItem("Faculty of Pharmacy and Pharmaceutical Sciences");
        tableView.addBasicItem("Faculty of Science");
    }

    private class CustomClickListener implements ClickListener {

        @Override
        public void onClick(int index) {
            Intent mIntent = new Intent(getActivity(),
                    ListViewFilterActivity.class);
            mIntent.putExtra(ListViewFilterActivity.FACULTY_ID, index);
            getActivity().startActivityForResult(mIntent, UNIT_ID);
            getActivity().overridePendingTransition(
                    R.anim.pull_in_right, R.anim.push_out_left);
        }

    }

    public static FacultyFragment newInstance() {
        return new FacultyFragment();
    }

}
