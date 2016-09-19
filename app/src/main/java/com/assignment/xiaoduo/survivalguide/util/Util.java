package com.assignment.xiaoduo.survivalguide.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;

import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Util {

    public static String dateFormTransfer(String dateStr, Context context) {
        Time t = new Time();
        t.setToNow();
        int year = t.year;
        int month = t.month + 1;
        int date = t.monthDay;
        int hour = t.hour; // 0-23
        int minute = t.minute;
        int second = t.second;

        try {
            int serverYear = Integer.parseInt(dateStr.substring(0, 4));
            int serverMonth = Integer.parseInt(dateStr.substring(5, 7));
            int serverDay = Integer.parseInt(dateStr.substring(8, 10));
            int serverHour = Integer.parseInt(dateStr.substring(11, 13));
            int serverMinute = Integer.parseInt(dateStr.substring(14, 16));
            int serverSecond = Integer.parseInt(dateStr.substring(17, 19));

            if (year - serverYear > 0) {
                return (year - serverYear)
                        + context.getString(R.string.past_years);
            }
            if (month - serverMonth > 0) {
                return (month - serverMonth)
                        + context.getString(R.string.past_months);
            }
            if (date - serverDay > 0) {
                return (date - serverDay)
                        + context.getString(R.string.past_days);
            }
            if (hour - serverHour > 0) {
                return (hour - serverHour)
                        + context.getString(R.string.past_hours);
            }
            if (minute - serverMinute > 0) {
                return (minute - serverMinute)
                        + context.getString(R.string.past_minutes);
            }
            if (second - serverSecond > 0) {
                return context.getString(R.string.just_past);
            }
        } catch (Exception e) {
            return dateStr;
        }

        return dateStr;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap b, int degrees) {
        if (degrees == 0) {
            return b;
        }
        if (b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth(), (float) b.getHeight());
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                        b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
                return b;
            }
        }
        return b;
    }

    public static Bitmap decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeFile(filePath, o2);
    }


    public static Bitmap decodeFile(String filePath, int size) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < size && height_tmp < size)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeFile(filePath, o2);
    }

    public static Bitmap decodeFile(Bitmap src, Uri filePath, int size, Context context) {
        // Decode image size
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            src = BitmapFactory.decodeStream(context
                            .getContentResolver().openInputStream(filePath), null,
                    options);
            options.inSampleSize = calculateInSampleSize(options, size, size);
            options.inJustDecodeBounds = false;
            src = BitmapFactory.decodeStream(context
                            .getContentResolver().openInputStream(filePath), null,
                    options);
            if (src != null) {

                return src;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (src != null) {
            return src;
        }
        return null;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int convertDpToPixel(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public static String[] loadItems(int rawResourceId, Context context) {
        if (context != null) {
            try {
                Log.e("", "getResources()from mode" + context);
                ArrayList<String> itemList = new ArrayList<>();
                InputStream inputStream = context.getResources()
                        .openRawResource(rawResourceId);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    itemList.add(line);
                }
                reader.close();
                String[] itemArray = new String[itemList.size()];
                for (int i = 0; i < itemList.size(); i++) {
                    itemArray[i] = itemList.get(i);
                }
                return itemArray;
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }
}
