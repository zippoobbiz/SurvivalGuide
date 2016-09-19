package com.assignment.xiaoduo.survivalguide;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.FrameLayout;

import com.assignment.xiaoduo.survivalguide.activities.LoginLoadingActivity;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

/**
 * Created by xiaoduo on 6/6/15.
 */
public class LoginLoadingActivityTest
        extends ActivityInstrumentationTestCase2<LoginLoadingActivity> {

    private LoginLoadingActivity activity;
    private FrameLayout frame;
    public LoginLoadingActivityTest() {
        super(LoginLoadingActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        frame = (FrameLayout)activity
                .findViewById(R.id.main_frame);
    }

    public void testFrame() {
        assertNotNull(frame);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}