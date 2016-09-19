package com.assignment.xiaoduo.survivalguide.configurations;

import android.content.Context;
import android.util.DisplayMetrics;

import com.assignment.xiaoduo.survivalguide.entities.AppTheme;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;


public class LocalConfiguration {

    //display switch
    public static boolean switchOn = true;


    public static int GET_STATUS = 0;
    //at home
//    public static String[] IPs = {"192","168","0","3"};
    //at uni
//	public static String[] IPs = {"118","139","46","229"};

    public static String[] IPs = {"119", "9", "45", "67"};
    public final static int userNameMinLength = 4;
    public final static int userNameMaxLength = 10;
    public final static int userPwdMinLength = 5;
    public final static int userPwdMaxLength = 15;
    private final static String PORT = "8080";
    private final static String resourceName = "ForumWebService/webresources/";
    private final static String ServerName = "ForumWebService/";


    //Entity URL
    public final static String image_url = "images/";

    //SQLite database
    //Database name
    public static final String dbName = "recycle";
    //Table name
    public static final String userTable = "UserTable";
    public static final String draftTable = "DraftTable";
    public static final String likeTable = "LikeTable";
    public static final String viewedTable = "ViewedTable";

    //image compress parameters
    public static int bitMapCompress = 800;
    public static int bitMapCameraCompress = 800;
    public static int bitMapUploadCompress = 2000;


    public static String getPicUrl() {
        return "http://" + IPs[0] + "." + IPs[1] + "." + IPs[2] + "." + IPs[3] + ":" + PORT + "/" + ServerName;
    }

    public static String getUrl() {
        return "http://" + IPs[0] + "." + IPs[1] + "." + IPs[2] + "." + IPs[3] + ":" + PORT + "/" + resourceName;
    }


    public final static int CATALOG_EXPERIENCE = 3;
    public final static int CATALOG_TRADE = 4;
    public final static int CATALOG_LIFE = 5;
    public final static int CATALOG_ACTIVITY = 6;
    public final static int CATALOG_HOUSE_RENT = 7;
    public final static int CATALOG_CAREER = 8;
    public final static int CATALOG_FEEDBACK = 9;
    //	public final static int CATALOG_OTHER = 10;
    public final static int CATALOG_EVALUATION = 1;
    public final static int CATALOG_QNA = 2;
    public final static int CATALOG_MY_POST = 0;
    public final static int CATALOG_NEW_POSTS = -1;


    private final static int FAKE_ID = -1;

    private final static AppTheme GREEN_DAY_THEME = new AppTheme("#64d8d6", R.drawable.loading10, FAKE_ID
            , 0xff257cae, 0xff257cae, 0xffe3eff7, 0xff000000, 0xff000000, 0xffaed1e8, 0xff257cae, 0xffd0e3f2, 0xffffffff);

    public static AppTheme currentTheme = GREEN_DAY_THEME;

    public final static int POST_IMAGE_LIMIT = 4;

    public final static String IMAGE_SYMBOL_START = "<#I#M#A#G#E#>";
    public final static String IMAGE_SYMBOL_END = "</#I#M#A#G#E#>";
    public final static String IMAGE_TEXT_SYMBOL_START = "<#T#E#X#T#>";
    public final static String IMAGE_TEXT_SYMBOL_END = "</#T#E#X#T#>";

    public static void setResolution(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
//        Log.e("Configuration.java", "w_screen:" + w_screen);
//        Log.e("Configuration.java", "h_screen:" + h_screen);
        int maxMemory = ((int) Runtime.getRuntime().maxMemory()) / 1024 / 1024;
        //应用程序已获得内存
        long totalMemory = ((int) Runtime.getRuntime().totalMemory()) / 1024 / 1024;
        //应用程序已获得内存中未使用内存
        long freeMemory = ((int) Runtime.getRuntime().freeMemory()) / 1024 / 1024;
//        long cached = ((int) Runtime.getRuntime().)/1024/1024;
//        Log.e("Configuration.java", "maxMemory:" + maxMemory);
//        Log.e("Configuration.java", "totalMemory:" + totalMemory);
//        Log.e("Configuration.java", "freeMemory:" + freeMemory);
        if (freeMemory > 0) {
            bitMapCompress *= freeMemory;
            bitMapCameraCompress *= freeMemory;
            if (bitMapCompress > 1000) {
                bitMapCompress = 1000;
            }
            if (bitMapCameraCompress > 1500) {
                bitMapCameraCompress = 1500;
            }
        }

//        Log.e("Configuration.java", "bitMapCompress:" + bitMapCompress);
    }

    public final static String[] mCatalog = {"Unit Evaluation", "Unit Q & A",
            "Experience", "Secondary trading", "Life", "Activities",
            "House Renting", "Job Hunting", "Feedback"
//			, "Others"
    };
    public final static String[][] mCatalogItem = {
            // Unit Evaluation -- facauties
            {"Art Design & Architecture", "Arts", "Business & Economics",
                    "Education", "Engineering", "Information Technology",
                    "Law", "Medicine Nursing & Health Sciences",
                    "Pharmacy & Pharmaceutical Sciences", "Science"},
            // Unit Q & A -- facauties
            {"Art Design & Architecture", "Arts", "Business & Economics",
                    "Education", "Engineering", "Information Technology",
                    "Law", "Medicine Nursing & Health Sciences",
                    "Pharmacy & Pharmaceutical Sciences", "Science"},
            // Experience
            {"Traveling", "Cooking", "Shopping", "Others"},
            // Secondary trading
            {"Book", "Car", "Bike", "Furniture", "Electronic ", "Pets",
                    "Others"},
            // Life
            {"Reading", "Fitness", "Music", "Movies", "Others"},
            // Activities
            {"Club 1", "Club 2", "Club 3", "Club 4", "Club 5", "Others"},
            // House Renting
            {"Caufield", "Clayton", "Others"},
            // Job Hunting
            {"Arts", "Business", "Education", "Engineering", "I.T.",
                    "Medical", "Pharmacy", "Law", "Science", "Others"},
            // Feedback
            {"Suggest", "Idea", "Bug report", "Others"}
//			,
//            // Others
//            {}
    };
}
