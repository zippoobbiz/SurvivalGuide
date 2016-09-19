package com.assignment.xiaoduo.survivalguide.helpers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.Draft;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoduo on 5/21/15.
 */
public class SQLiteHelper {

    private static SQLiteDatabase sampleDB = null;

    public void dropDraftTable(Context context) {
        try {
            sampleDB = context.openOrCreateDatabase(LocalConfiguration.dbName, Activity.MODE_PRIVATE, null);

            sampleDB.execSQL("DROP TABLE IF EXISTS "
                    + LocalConfiguration.draftTable);
            sampleDB.close();
        } catch (SQLiteException se) {
            se.printStackTrace();
        } finally {
            if (sampleDB != null) {
                sampleDB.close();
            }
        }
    }


    public static void saveDraft(Draft draft, Context context) {
        if (draft.getTitle() == null) {
            draft.setTitle("");
        }
        if (draft.getImagePaths() == null) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add("");
            temp.add("");
            temp.add("");
            draft.setImagePaths(temp);
        } else if (draft.getImagePaths().size() < 3) {
            ArrayList<String> temp = draft.getImagePaths();
            temp.add("");
            temp.add("");
            temp.add("");
            draft.setImagePaths(temp);
        }

        try {
            sampleDB = context.openOrCreateDatabase(LocalConfiguration.dbName, Activity.MODE_PRIVATE, null);

            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + LocalConfiguration.draftTable
                    + " ("
                    + "id INTEGER PRIMARY KEY, title varchar(20),catalog varchar(20),catalogSub varchar(20),"
                    + "content varchar(2000),numOfImage INTEGER,imagePath1 varchar(50), imagePath2 varchar(50), imagePath3 "
                    + "varchar(50), autoSavedTime varchar(30))");
            String sqlString = "REPLACE INTO " + LocalConfiguration.draftTable
                    + " Values ( null"
                    + "," + "'" + draft.getTitle()
                    + "'," + "'" + draft.getCatalog()
                    + "'," + "'" + draft.getCatalogSub()
                    + "'," + "'" + draft.getContent()
                    + "'," + draft.getNumOfImage()
                    + "," + "'" + draft.getImagePaths().get(0)
                    + "'," + "'" + draft.getImagePaths().get(1)
                    + "'," + "'" + draft.getImagePaths().get(2)
                    + "'," + "'" + draft.getAutoSavedTime() + "');";
            sampleDB.execSQL(sqlString);
        } catch (SQLiteException se) {
            se.printStackTrace();
        } finally {
            if (sampleDB != null) {
                sampleDB.close();
            }
        }
    }


//    private int id;
//    private String title;
//    private String catalog;
//    private String catalogSub;
//    private String content;
//    private int numOfImage;
//    private ArrayList<String> imagePaths;
//    private String autoSavedTime;
//

    public static List<Draft> getDrafts(Context context) {
        List<Draft> drafts = new ArrayList<>();
        try {

            sampleDB = context.openOrCreateDatabase(LocalConfiguration.dbName, Activity.MODE_PRIVATE, null);

            Cursor c = sampleDB.rawQuery("SELECT * FROM "
                    + LocalConfiguration.draftTable, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        int id = c.getInt(c
                                .getColumnIndex("id"));
                        String title = c.getString(c
                                .getColumnIndex("title"));
                        String catalog = c.getString(c
                                .getColumnIndex("catalog"));
                        String catalogSub = c.getString(c
                                .getColumnIndex("catalogSub"));
                        String content = c.getString(c
                                .getColumnIndex("content"));
                        int numOfImage = c.getInt(c
                                .getColumnIndex("numOfImage"));
                        String imagePath1 = c.getString(c
                                .getColumnIndex("imagePath1"));
                        String imagePath2 = c.getString(c
                                .getColumnIndex("imagePath2"));
                        String imagePath3 = c.getString(c
                                .getColumnIndex("imagePath3"));
                        String autoSavedTime = c.getString(c
                                .getColumnIndex("autoSavedTime"));
                        ArrayList<String> imagePaths = new ArrayList<>();
                        imagePaths.add(imagePath1);
                        imagePaths.add(imagePath2);
                        imagePaths.add(imagePath3);
                        Draft d = new Draft(id, title, catalog, catalogSub, content, numOfImage, imagePaths, autoSavedTime);
                        drafts.add(d);
                    } while (c.moveToNext()); // Move to next row
                }
                c.close();
            }

        } catch (SQLiteException se) {
            se.printStackTrace();
        } finally {
            if (sampleDB != null) {
                sampleDB.close();
            }
        }
        return drafts;
    }

    public static boolean updateDraftSuccessfully(Context context, Draft d) {
        if (d.getTitle() == null) {
            d.setTitle("");
        }
        if (d.getImagePaths() == null) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add("");
            temp.add("");
            temp.add("");
            d.setImagePaths(temp);
        } else if (d.getImagePaths().size() < 3) {
            ArrayList<String> temp = d.getImagePaths();
            temp.add("");
            temp.add("");
            temp.add("");
            d.setImagePaths(temp);
        }
        try {
            sampleDB = context.openOrCreateDatabase(LocalConfiguration.dbName, Activity.MODE_PRIVATE, null);


            sampleDB.execSQL("UPDATE " + LocalConfiguration.draftTable + " SET " +
                    "title ='" + d.getTitle() + "', " +
                    "catalog ='" + d.getCatalog() + "', " +
                    "catalogSub ='" + d.getCatalogSub() + "', " +
                    "content ='" + d.getContent() + "', " +
                    "numOfImage = " + d.getNumOfImage() + "," +
                    "imagePath1 ='" + d.getImagePaths().get(0) + "', " +
                    "imagePath2 ='" + d.getImagePaths().get(1) + "', " +
                    "imagePath3 ='" + d.getImagePaths().get(2) + "', " +
                    "autoSavedTime ='" + d.getAutoSavedTime() + "' " +
                    "WHERE id = " + d.getId());

            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (sampleDB != null) {
                sampleDB.close();
            }
        }
    }

    public static boolean deleteDraftSuccessfully(Context context, int id) {
        try {
            if (context == null)
                sampleDB = context.openOrCreateDatabase(LocalConfiguration.dbName, Activity.MODE_PRIVATE, null);
            sampleDB.execSQL("DELETE FROM " + LocalConfiguration.draftTable + " WHERE id = " + id);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (sampleDB != null) {
                sampleDB.close();
            }
        }
    }

    public static boolean deleteDraftsSuccessfully(Context context, int[] ids) {
        try {
            sampleDB = context.openOrCreateDatabase(LocalConfiguration.dbName, Activity.MODE_PRIVATE, null);
            String sqlStr = "DELETE FROM " + LocalConfiguration.draftTable + " WHERE id = ";
            for (int id : ids) {
                sqlStr += id + " or id = ";
            }
            sqlStr = sqlStr.substring(0, sqlStr.length() - 8);
            sampleDB.execSQL(sqlStr);

            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (sampleDB != null) {
                sampleDB.close();
            }
        }
    }

    public static int getTheLatestDraftId(Context context) {
        try {
            sampleDB = context.openOrCreateDatabase(LocalConfiguration.dbName, Activity.MODE_PRIVATE, null);
            try (Cursor c = sampleDB.rawQuery("SELECT MAX(id) FROM " + LocalConfiguration.draftTable, null)) {
                c.moveToFirst();
                return c.getInt(0);
            }
        } catch (Exception e) {
            return -1;
        } finally {
            if (sampleDB != null) {
                sampleDB.close();
            }
        }
    }

}
