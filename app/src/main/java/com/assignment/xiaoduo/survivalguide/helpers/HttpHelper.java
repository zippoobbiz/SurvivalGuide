package com.assignment.xiaoduo.survivalguide.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.util.Util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by xiaoduo on 6/4/15.
 */
public class HttpHelper {

    public enum JSON_TYPE {
        /**
         * JSONObject
         */
        JSON_TYPE_OBJECT,
        /**
         * JSONArray
         */
        JSON_TYPE_ARRAY,
        /**
         * Not json
         */
        JSON_TYPE_ERROR
    }


    public static String Create(String jsonStr, String entityUrl, String primaryKey) {
        String URL = LocalConfiguration.getUrl() + entityUrl + primaryKey;
        DefaultHttpClient httpClient;
        String result = "";
        HttpParams paramsw = createHttpParams_LongTimeOut();
        httpClient = new DefaultHttpClient(paramsw);
        HttpPost post = new HttpPost(URL);
        try {
            StringEntity se = new StringEntity(jsonStr, "UTF-8");
            se.setContentType("application/json");
            post.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(post);
            int httpCode = httpResponse.getStatusLine().getStatusCode();
            if ((httpCode == 204 || httpCode == HttpURLConnection.HTTP_OK)) {
                Header[] headers = httpResponse.getAllHeaders();
                HttpEntity entity = httpResponse.getEntity();
                Header header = httpResponse.getFirstHeader("content-type");
                InputStream inputStream = entity.getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(
                        inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String s;
                while (((s = reader.readLine()) != null)) {
                    result += s;
                }
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "noConection";
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    public static String GetSingleResoult(String primaryKey, String entityUrl) {
        String URL = LocalConfiguration.getUrl() + entityUrl + primaryKey;
        URL = URL.replaceAll(" ", "%20");
        DefaultHttpClient httpClient;
        StringBuilder result = new StringBuilder();
        HttpParams paramsw = createHttpParams();
        httpClient = new DefaultHttpClient(paramsw);
        HttpGet get = new HttpGet(URL);
        get.setHeader("Accept", "application/json");
        get.setHeader("Content-type", "application/json");
        try {
            HttpResponse httpResponse = httpClient.execute(get);
            int httpCode = httpResponse.getStatusLine().getStatusCode();
            if (httpCode == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = httpResponse.getEntity();
                InputStream inputStream = entity.getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(
                        inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String s;
                while (((s = reader.readLine()) != null)) {
                    result.append(s);
                }
                httpClient.getConnectionManager().shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return result.toString().trim();
    }

    /*
     * GET function
     */
    public static JSONArray Get(String primaryKey, String entityUrl) {
        String URL = LocalConfiguration.getUrl() + entityUrl + primaryKey;
        URL = URL.replaceAll(" ", "%20");
        DefaultHttpClient httpClient;
        Log.e("","URL:"+URL);
        StringBuilder result = new StringBuilder();
        HttpParams paramsw = createHttpParams();
        httpClient = new DefaultHttpClient(paramsw);
        HttpGet get = new HttpGet(URL);
        get.setHeader("Accept", "application/json");
        get.setHeader("Content-type", "application/json");
        try {
            HttpResponse httpResponse = httpClient.execute(get);
            int httpCode = httpResponse.getStatusLine().getStatusCode();
            Log.e("", "httpCode:" + httpCode);
            if (httpCode == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = httpResponse.getEntity();
                InputStream inputStream = entity.getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(
                        inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String s;
                while (((s = reader.readLine()) != null)) {
                    result.append(s);
                }
                reader.close();
                String jsonA = "";
                switch (getJSONType(result.toString())) {
                    case JSON_TYPE_OBJECT:
                        jsonA += "[" + result.toString() + "]";
                        break;
                    case JSON_TYPE_ARRAY:
                        jsonA = result.toString();
                        break;
                    case JSON_TYPE_ERROR:
                        break;
                    default:
                        break;
                }
                httpClient.getConnectionManager().shutdown();
                if (jsonA.length() > 10) {
                    LocalConfiguration.GET_STATUS = 1;
                    return new JSONArray(jsonA);
                } else {
                    LocalConfiguration.GET_STATUS = 2;
                    return null;
                }
            } else {
                LocalConfiguration.GET_STATUS = 4;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LocalConfiguration.GET_STATUS = 3;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return null;
    }

    public static HttpParams createHttpParams() {
        int TIMEOUT = 60;
        final HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 50);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192 * 5);
        return params;
    }

    public static HttpParams createHttpParams_LongTimeOut() {
        int TIMEOUT = 60;
        final HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 500);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192 * 5);
        return params;
    }

    public static JSON_TYPE getJSONType(String str) {
        if (TextUtils.isEmpty(str)) {
            return JSON_TYPE.JSON_TYPE_ERROR;
        }

        final char[] strChar = str.substring(0, 1).toCharArray();
        final char firstChar = strChar[0];

        if (firstChar == '{') {
            return JSON_TYPE.JSON_TYPE_OBJECT;
        } else if (firstChar == '[') {
            return JSON_TYPE.JSON_TYPE_ARRAY;
        } else {
            return JSON_TYPE.JSON_TYPE_ERROR;
        }
    }

    // return type:
    // {"userFaculty":"IT","userGender":"M","userHome":"Beijing","userID":1,"userName":"phil","userPwd":"123","userPic":"xxx","userCount":"xxx"}
    public static JSONArray login(String userName, String userPwd) {
        String url = UrlComposer.login(userName, userPwd);
        return Get(url, UrlComposer.user_url);
    }

    public static JSONArray getMyPostResponse(String userID) {
        String url = UrlComposer.getMyPostResponse(userID);
        return Get(url, UrlComposer.postresponse_url);
    }

    //set as marked
    public static String setAsMarked(String userID) {
        String url = UrlComposer.setAsMarked(userID);
        return GetSingleResoult(url, UrlComposer.postresponse_url);
    }

    public static JSONArray getPostResponse(String postID) {
        String url = UrlComposer.getPostResponse(postID);
        return Get(url, UrlComposer.postresponse_url);
    }

    public static JSONArray getPostContent(String postID) {
        String url = UrlComposer.getPostContent(postID);
        return Get(url, UrlComposer.post_url);
    }


    public static String responsePost(String jsonStr) {
        String url = UrlComposer.responsePost();
        return Create(jsonStr, UrlComposer.postresponse_url, url);
    }

    public static String insertPost(String jsonStr) {
        String url = UrlComposer.insertPost();
        return Create(jsonStr, UrlComposer.post_url, url);
    }

    public static void like(String postId) {
        String url = UrlComposer.likeAddOne(postId);
        Get(url, UrlComposer.post_url);
    }

    public static void unlike(String postId) {
        String url = UrlComposer.unLikeAddOne(postId);
        Get(url, UrlComposer.post_url);
    }

    public static void viewaddone(String postId) {
        String url = UrlComposer.viewAddOne(postId);
        Get(url, UrlComposer.post_url);
    }

    // 快速注册：
    public static String quickRegister(String jsonStr) {
        String url = UrlComposer.quickRegister();
        return Create(jsonStr, UrlComposer.user_url, url);
    }

    //myPost
    public static JSONArray getSinglePostById(String postId) {
        return Get(postId, UrlComposer.post_url);
    }

    //myPost
    public static JSONArray myPostLists(String userID) {
        String url = UrlComposer.getMyPostLists(userID);
        return Get(url, UrlComposer.post_url);
    }

    //get top 20 by catalog id
    public static JSONArray getTop20PostLists(String catalogId, String time) {
        String url = UrlComposer.getTop20PostLists(catalogId, time);
        return Get(url, UrlComposer.post_url);
    }

    //get top 20 evaluation
    public static JSONArray getTop20PostLists(String catalogId, String time, String unitID) {
        String url = UrlComposer.getTop20PostLists(catalogId, time, unitID);
        return Get(url, UrlComposer.post_url);
    }

    //get top 20
    public static JSONArray getTop20NewPostLists(String time) {
        String url = UrlComposer.getTop20NewPostLists(time);
        return Get(url, UrlComposer.post_url);
    }

    // get post distribution
    public static String getPostDistribution() {
        String url = UrlComposer.getPostDistributionDirectory();
        return GetSingleResoult(url, UrlComposer.post_url);
    }

    public static String uploadImage(String fileName, String theFilePath, String postId) {
        String url = UrlComposer.uploadImage();
        try {
            return postFile(url, UrlComposer.post_url, fileName, theFilePath, postId, StaticResource.user.getUserID());
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }

    }


    public static String postFile(String url, String entityUrl, String fileName, String filePath, String postId, String userId) throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(LocalConfiguration.getUrl() + entityUrl + url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


        int degree = Util.readPictureDegree(filePath);

        Bitmap bitmap;
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = LocalConfiguration.bitMapUploadCompress;

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
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        bitmap = Util.rotateBitmap(bitmap, degree);

        String filePathTemp = filePath + ".tempTestFile";
        File fileTemp = new File(filePathTemp);
        try {
            fileTemp.createNewFile();
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(fileTemp);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fOut)) {
                fOut.flush();
                fOut.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        final File file = new File(filePathTemp);
        FileBody fb = new FileBody(file);

        builder.addPart("image", fb);
        builder.addTextBody("fileName", fileName);
        builder.addTextBody("postId", postId);
        builder.addTextBody("userId", userId);

        final HttpEntity yourEntity = builder.build();
        ProgressiveEntity myEntity = new ProgressiveEntity(yourEntity);

        post.setEntity(myEntity);
        HttpResponse response = client.execute(post);

        if (fileTemp.exists()) {
            fileTemp.delete();
        }
        return getContent(response);

    }

    public static String postPortrait(Bitmap bitmap, Context context) {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(LocalConfiguration.getUrl() + "com.entities.user/postPic");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        String filePathTemp = context.getFilesDir() + "/" + StaticResource.user.getUserName() + ".tempTestFile";
        File fileTemp = new File(filePathTemp);
        try {
            fileTemp.createNewFile();
            FileOutputStream fOut;
            fOut = new FileOutputStream(fileTemp);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fOut)) {
                fOut.flush();
                fOut.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final File file = new File(filePathTemp);
        FileBody fb = new FileBody(file);

        builder.addPart("userPic", fb);
        builder.addTextBody("userID", StaticResource.user.getUserID());

        final HttpEntity yourEntity = builder.build();
        ProgressiveEntity myEntity = new ProgressiveEntity(yourEntity);

        post.setEntity(myEntity);
        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileTemp.exists()) {
            fileTemp.delete();
        }
        try {
            return getContent(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    static class ProgressiveEntity implements HttpEntity {
        HttpEntity yourEntity;

        public ProgressiveEntity(HttpEntity entity) {
            yourEntity = entity;
        }

        @Override
        public void consumeContent() throws IOException {
            yourEntity.consumeContent();
        }

        @Override
        public InputStream getContent() throws IOException,
                IllegalStateException {
            return yourEntity.getContent();
        }

        @Override
        public Header getContentEncoding() {
            return yourEntity.getContentEncoding();
        }

        @Override
        public long getContentLength() {
            return yourEntity.getContentLength();
        }

        @Override
        public Header getContentType() {
            return yourEntity.getContentType();
        }

        @Override
        public boolean isChunked() {
            return yourEntity.isChunked();
        }

        @Override
        public boolean isRepeatable() {
            return yourEntity.isRepeatable();
        }

        @Override
        public boolean isStreaming() {
            return yourEntity.isStreaming();
        } // CONSIDER put a _real_ delegator into here!

        @Override
        public void writeTo(OutputStream outstream) throws IOException {

            class ProxyOutputStream extends FilterOutputStream {
                /**
                 * @author Xiaoduo xu
                 */

                public ProxyOutputStream(OutputStream proxy) {
                    super(proxy);

                }

                public void write(int idx) throws IOException {
                    out.write(idx);
                }

                public void write(byte[] bts) throws IOException {
                    out.write(bts);
                }

                public void write(byte[] bts, int st, int end) throws IOException {
                    out.write(bts, st, end);
                }

                public void flush() throws IOException {
                    out.flush();
                }

                public void close() throws IOException {
                    out.close();
                }
            } // CONSIDER import this class (and risk more Jar File Hell)

            class ProgressiveOutputStream extends ProxyOutputStream {
                public ProgressiveOutputStream(OutputStream proxy) {
                    super(proxy);
                }

                public void write(byte[] bts, int st, int end) throws IOException {
                    out.write(bts, st, end);
                }
            }

            yourEntity.writeTo(new ProgressiveOutputStream(outstream));
        }

    }

    public static String getContent(HttpResponse response) throws IOException {
        try {
            //may be a null pointer , so around with try catch
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String body;
            String content = "";

            while ((body = rd.readLine()) != null) {
                content += body + "\n";
            }
            return content.trim();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
