package com.assignment.xiaoduo.survivalguide;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.assignment.xiaoduo.survivalguide.activities.LoginLoadingActivity;
import com.assignment.xiaoduo.survivalguide.fragments.QuickRegisterFragment;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

/**
 * Created by xiaoduo on 6/14/15.
 */
public class LoginFragmentTest
        extends ActivityInstrumentationTestCase2<LoginLoadingActivity> {
    private LoginLoadingActivity mActivity;

    public LoginFragmentTest() {
        super(LoginLoadingActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }

    private Fragment startFragment(Fragment fragment) {
        FragmentTransaction transaction = mActivity.getFragmentManager().beginTransaction();
        transaction.add(R.id.main_frame, fragment, "tag");
        transaction.commit();
        getInstrumentation().waitForIdleSync();
        Fragment frag = mActivity.getFragmentManager().findFragmentByTag("tag");
        return frag;
    }

    public void testFragment() {
        QuickRegisterFragment fragment = new QuickRegisterFragment() {
            //Override methods and add assertations here.
        };
        Fragment frag = startFragment(fragment);
        assertNotNull(frag);
        assertNotNull(EditText.class.cast(frag.getView()
                .findViewById(R.id.signInId)));
        assertEquals("", EditText.class.cast(frag.getView()
                .findViewById(R.id.signInId))
                .getText().toString());

        assertNotNull(EditText.class.cast(frag.getView()
                .findViewById(R.id.signInPassword)));
        assertEquals("", EditText.class.cast(frag.getView()
                .findViewById(R.id.signInPassword))
                .getText().toString());
    }
}