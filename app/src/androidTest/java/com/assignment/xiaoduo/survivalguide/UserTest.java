package com.assignment.xiaoduo.survivalguide;

import com.assignment.xiaoduo.survivalguide.entities.User;

import junit.framework.TestCase;

/**
 * Created by xiaoduo on 6/9/15.
 */
public class UserTest extends TestCase {

    User user;

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        user = new User("");
    }


    public void testSetNullToUserName() {
        try {
            user.setUserName(null);
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Message contains user name cannot be null", e.getMessage().contains("user name cannot be null"));
        }
    }

    public void testSetEmptyUserName() {
        try {
            user.setUserName("");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Message contains should be longer than 4 char", e.getMessage().contains("should be longer than 4 char"));
        }
    }

    public void testSetEmptyUserPassword() {
        try {
            user.setUserPwd("");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Message contains should be longer than 6 char", e.getMessage().contains("should be longer than 6 char"));
        }
    }

    public void testSetNullToUserPassword() {
        try {
            user.setUserPwd(null);
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Message contains user password cannot be null", e.getMessage().contains("user password cannot be null"));
        }
    }

    public void testSetEmptyUserId() {
        try {
            user.setUserID("");
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Message contains user id cannot be empty", e.getMessage().contains("user id cannot be empty"));
        }
    }

    public void testSetNullToUserId() {
        try {
            user.setUserID(null);
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Message contains user id cannot be null", e.getMessage().contains("user id cannot be null"));
        }
    }


    public void testSetUpUserFromJSONArray() {
        try {
            String ja = "[{\"userPwd\":\"1234567\",\"userName\":\"phil\",\"userID\":1}]";
            user = new User(ja);
        } catch (Exception e) {
            assertTrue("Throws IAE", e instanceof IllegalArgumentException);
            assertTrue("Message contains user id cannot be null", e.getMessage().contains("user id cannot be null"));
        }
    }

    @Override
    protected  void tearDown() throws Exception{
        super.tearDown();
    }
}
