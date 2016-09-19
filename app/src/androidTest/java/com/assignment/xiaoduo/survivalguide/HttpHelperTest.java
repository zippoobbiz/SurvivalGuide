package com.assignment.xiaoduo.survivalguide;

import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.User;
import com.assignment.xiaoduo.survivalguide.helpers.HttpHelper;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaoduo on 6/9/15.
 */
public class HttpHelperTest extends TestCase {

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    public void testLogin()
    {
        JSONArray ja = HttpHelper.login("phil", "1234567");
        User u = new User(ja.toString());
        assertEquals(u.getUserID(), "1");
    }

    public void testGetTop20News()
    {
        JSONArray resultArray = HttpHelper.getTop20NewPostLists("currentTime");
        assertTrue("",resultArray.length() == 20);
    }

    public void testMyPosts()
    {
        JSONArray resultArray;
        resultArray = HttpHelper.myPostLists("1");
        for(int i=0; i< resultArray.length();i++)
        {
            try {
                JSONObject jo = resultArray.getJSONObject(i);
                assertEquals("1",jo.getJSONObject("userID").getString("userID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void testGetUnitEvaluation()
    {
        JSONArray resultArray = HttpHelper.getTop20PostLists(
                LocalConfiguration.CATALOG_EVALUATION + "", "currentTime", "FIT4039");
        assertTrue("",resultArray.length() > 0);
    }

    public void testGetQAndA()
    {
        JSONArray resultArray = HttpHelper.getTop20PostLists(
                LocalConfiguration.CATALOG_QNA + "", "currentTime", "FIT4039");
        assertTrue("",resultArray == null);
    }

    public void testGetTop20FromCategory()
    {
        JSONArray resultArray = HttpHelper.getTop20PostLists("1",
                "currentTime");
        assertTrue("",resultArray.length() > 0);
    }

    @Override
    protected  void tearDown() throws Exception{
        super.tearDown();
    }
}
