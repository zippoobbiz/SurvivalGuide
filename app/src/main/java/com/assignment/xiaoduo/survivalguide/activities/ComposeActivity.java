package com.assignment.xiaoduo.survivalguide.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.xiaoduo.customized.view.expandablemenu.ExpandTabView;
import com.assignment.xiaoduo.customized.view.expandablemenu.ViewMiddle;
import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.Draft;
import com.assignment.xiaoduo.survivalguide.entities.ImageWithText;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.helpers.HttpHelper;
import com.assignment.xiaoduo.survivalguide.helpers.SQLiteHelper;
import com.assignment.xiaoduo.survivalguide.util.OpenFileUtil;
import com.assignment.xiaoduo.survivalguide.util.Util;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ComposeActivity extends ActionBarActivity {

    private String catalogID = "-1";
    private String catalogSubID = "null";
    private List<ImageWithText> imageTextList = new ArrayList<>();
    private Map<Integer, String> bitmapList = new HashMap<>();
    private int sequence = 0;
    private static final int PICK_IMAGE_LOW_VERSION = 1;
    private static final int PICK_IMAGE_HIGH_VERSION = 3;
    private static final int PICK_Camera_IMAGE = 2;
    Uri imageUri;
    private Bitmap bitmap;
    final ViewHolder holder = new ViewHolder();
    private int IMAGE_LIMITATION = LocalConfiguration.POST_IMAGE_LIMIT;
    private int numberOfImage = 0;
    private int picIndex = 0;
    private boolean got = false;
    private ArrayList<View> mViewArray = new ArrayList<>();
    private ViewMiddle viewMiddle;
    public static final int UNIT_ID = 15;
    private Draft draft;
    private int totalCharatersLength = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.category_menu, null);


//        ExpandTabView expandTabView = (ExpandTabView) mCustomView
//                .findViewById(R.id.expand_tab);
//        ArrayList<String> mTextArray = new ArrayList<>();
//        ViewMiddle viewMiddle = new ViewMiddle(this);
//        mTextArray.add("Category");
//        ArrayList<View> mViewArray = new ArrayList<>();
//        mViewArray.add(viewMiddle);
//        expandTabView.setValue(mTextArray, mViewArray);
//        expandTabView.setTitle("test", 1);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().show();
        Intent intent = getIntent();
        draft = (Draft) intent.getSerializableExtra("single_draft");


        holder.et_title = (EditText) findViewById(R.id.et_title);
        holder.ll_content = (LinearLayout) findViewById(R.id.ll_content);
        holder.expandTabView = (ExpandTabView) mCustomView.findViewById(R.id.expand_tab);
        holder.tv_draft_length = (TextView) findViewById(R.id.draft_length);
        viewMiddle = new ViewMiddle(ComposeActivity.this);
        viewMiddle.setOnSelectListener(new ViewMiddle.OnSelectListener() {

            @Override
            public void getValue(int showCatalog, int showText) {

                onRefresh(viewMiddle, LocalConfiguration.mCatalogItem[showCatalog][showText]);
                catalogID = (1 + showCatalog) + "";

                if (showCatalog == 0) {
                    Intent mIntent = new Intent(ComposeActivity.this,
                            ListViewFilterActivity.class);
                    mIntent.putExtra(ListViewFilterActivity.FACULTY_ID, showText);
                    startActivityForResult(mIntent, UNIT_ID);
                    ComposeActivity.this.overridePendingTransition(
                            R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    catalogSubID = LocalConfiguration.mCatalogItem[showCatalog][showText];
                }
            }

        });
        mViewArray.add(viewMiddle);
        ArrayList<String> mTextArray = new ArrayList<>();
        mTextArray.add("Category");
        holder.expandTabView.setValue(mTextArray, mViewArray);
        holder.expandTabView.setTitle("test", 1);


        holder.iv_camera = (ImageView) findViewById(R.id.camera);
        holder.iv_camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (numberOfImage < IMAGE_LIMITATION) {
                    String fileName = "new-photo-name.jpg";
                    // create parameters for Intent with
                    // filename
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION,
                            "Image captured by camera");
                    // imageUri is the current activity
                    // attribute, define and save it
                    // for later usage (also in
                    // onSaveInstanceState)
                    imageUri = ComposeActivity.this.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
                    // create new Intent
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(intent, PICK_Camera_IMAGE);
                } else {
                    toast(getString(R.string.no_more_than) + " " + IMAGE_LIMITATION + "" + getString(R.string.pictures));
                }
            }
        });
        holder.iv_gallery = (ImageView) findViewById(R.id.gallery);
        holder.iv_gallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (numberOfImage < IMAGE_LIMITATION) {
                    try {
                        if (Build.VERSION.SDK_INT < 19) {
                            Intent gIntent = new Intent();
                            gIntent.setType("image/*");
                            gIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(
                                            gIntent, "Select Picture"),
                                    PICK_IMAGE_LOW_VERSION);
                        } else {
                            Intent intent = new Intent(
                                    Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/jpeg");
                            startActivityForResult(intent,
                                    PICK_IMAGE_HIGH_VERSION);
                        }
                    } catch (Exception e) {
                        toast(e.getMessage());
                    }
                } else {
                    toast(getString(R.string.no_more_than) + " " + IMAGE_LIMITATION + "" + getString(R.string.pictures));
                }
            }
        });

        holder.iv_save = (ImageView) findViewById(R.id.save);
        holder.iv_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (catalogID.equals("-1") || catalogSubID.equals("null")) {
                    holder.expandTabView.open();
                    toast("Please choose category.");
                } else {
                    Time t = new Time();
                    t.setToNow();
                    int year = t.year;
                    int month = t.month + 1;
                    int date = t.monthDay;
                    int hour = t.hour; // 0-23
                    int minute = t.minute;
                    draft.setAutoSavedTime(date + "/" + month + "/" + year + " " + hour + ":" + minute);
                    draft.setCatalog(catalogID);
                    draft.setCatalogSub(catalogSubID);

                    wrapContent();
                    String content = "";
                    for (ImageWithText iwt : imageTextList) {
                        if (iwt.isHasImage()) {
                            content += LocalConfiguration.IMAGE_SYMBOL_START + iwt.getId()
                                    + LocalConfiguration.IMAGE_SYMBOL_END;
                            content += LocalConfiguration.IMAGE_TEXT_SYMBOL_START + iwt.getText()
                                    + LocalConfiguration.IMAGE_TEXT_SYMBOL_END + "\n";
                        } else {
                            content += iwt.getText() + "\n";
                        }
                    }
                    draft.setContent(content);
                    ArrayList<String> tempList = new ArrayList<>();
                    for (int i = 0; i < bitmapList.size(); i++) {
                        tempList.add(bitmapList.get(i));
                    }
                    draft.setImagePaths(tempList);
                    draft.setNumOfImage(bitmapList.size());
                    draft.setTitle(holder.et_title.getText().toString());

//                    File xmlFile = new File(getActivity().getFilesDir(), "Draft3.xml");
//                    DraftXMLHelper.saveAsDraft(draft, xmlFile);
                    if (draft.getId() < 0) {
                        SQLiteHelper.saveDraft(draft, ComposeActivity.this);
                        ComposeActivity.this.finish();
                    } else {
                        SQLiteHelper.updateDraftSuccessfully(ComposeActivity.this, draft);
                        Intent returnIntent = new Intent();
                        ComposeActivity.this.setResult(Activity.RESULT_OK, returnIntent);
                        ComposeActivity.this.finish();
                    }
                }
            }
        });
        holder.iv_send = (TextView) mCustomView.findViewById(R.id.send);
        holder.iv_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (catalogID.equals("-1") || catalogSubID.equals("null")) {
                    holder.expandTabView.open();
                    toast("Please choose category.");
                } else if (holder.et_title.getText().toString().equals("")) {
                    toast("Input your topic.");
                } else {
                    Time t = new Time();
                    t.setToNow();
                    int year = t.year;
                    int month = t.month + 1;
                    int date = t.monthDay;
                    int hour = t.hour; // 0-23
                    int minute = t.minute;
                    draft.setAutoSavedTime(date + "/" + month + "/" + year + " " + hour + ":" + minute);
                    draft.setCatalog(catalogID);
                    draft.setCatalogSub(catalogSubID);

                    wrapContent();
                    String content = "";
                    for (ImageWithText iwt : imageTextList) {
                        if (iwt.isHasImage()) {
                            content += LocalConfiguration.IMAGE_SYMBOL_START + iwt.getId()
                                    + LocalConfiguration.IMAGE_SYMBOL_END;
                            content += LocalConfiguration.IMAGE_TEXT_SYMBOL_START + iwt.getText()
                                    + LocalConfiguration.IMAGE_TEXT_SYMBOL_END + "\n";
                        } else {
                            content += iwt.getText() + "\n";
                        }
                    }
                    draft.setContent(content);
                    ArrayList<String> tempList = new ArrayList<>();
                    for (int i = 0; i < bitmapList.size(); i++) {
                        tempList.add(bitmapList.get(i));
                    }
                    draft.setImagePaths(tempList);
                    draft.setNumOfImage(bitmapList.size());
                    draft.setTitle(holder.et_title.getText().toString());

//                    File xmlFile = new File(getActivity().getFilesDir(), "Draft3.xml");
//                    DraftXMLHelper.saveAsDraft(draft, xmlFile);
                    if (draft.getId() < 0) {
                        SQLiteHelper.saveDraft(draft, ComposeActivity.this);
                        StaticResource.uploadingPostId = SQLiteHelper.getTheLatestDraftId(ComposeActivity.this);
                        postTask post = new postTask(ComposeActivity.this);
                        post.execute(holder.et_title.getText().toString(), content);
                    } else {
                        StaticResource.uploadingPostId = draft.getId();
                        SQLiteHelper.updateDraftSuccessfully(ComposeActivity.this, draft);
                        postTask post = new postTask(ComposeActivity.this);
                        post.execute(holder.et_title.getText().toString(), content);
                    }
                    Intent data = new Intent();
                    ComposeActivity.this.setResult(Activity.RESULT_OK, data);
                    ComposeActivity.this.finish();
                }
            }
        });

        if (draft != null) {
            if (draft.getTitle() != null && !draft.getTitle().equals("") && !draft.getTitle().equals("null")) {
                holder.et_title.setText(draft.getTitle());
            }
            if (draft.getContent() != null && !draft.getContent().equals("")) {
                updateContent(draft.getContent());
            } else {
                addText("");
            }
            catalogID = draft.getCatalog();
            catalogSubID = draft.getCatalogSub();
            onRefresh(viewMiddle, catalogSubID);
            viewMiddle.updateShowText(LocalConfiguration.mCatalog[Integer.parseInt(catalogID) - 1], catalogSubID);

        } else {
            draft = new Draft();
            contentInitial();
        }
    }

    private void onRefresh(View view, String catalogSubId) {

        holder.expandTabView.onPressBack();
        int position = getPosition(view);
        if (position >= 0
                && !holder.expandTabView.getTitle(position).equals(catalogSubId)) {
            holder.expandTabView.setTitle(catalogSubId, position);
        }
    }

    private int getPosition(View tView) {
        for (int i = 0; i < mViewArray.size(); i++) {
            if (mViewArray.get(i) == tView) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;
        String filePath = null;
        if (requestCode == PICK_Camera_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                selectedImageUri = imageUri;
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Picture was not taken");
            } else {
                toast("Picture was not taken");
            }
            if (selectedImageUri != null) {
                try {
                    // OI FILE Manager
                    String fileManagerString = selectedImageUri.getPath();

                    // MEDIA GALLERY
                    String selectedImagePath = OpenFileUtil.getPath(
                            this, selectedImageUri);

                    if (selectedImagePath != null) {
                        filePath = selectedImagePath;
                    } else if (fileManagerString != null) {
                        filePath = fileManagerString;
                    }

                    if (filePath != null) {
                        addImage(filePath, "");
                    } else {
                        bitmap = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == PICK_IMAGE_LOW_VERSION) {
            if (resultCode == Activity.RESULT_OK) {
                selectedImageUri = data.getData();

                if (selectedImageUri != null) {
                    try {
                        // OI FILE Manager
                        String fileManagerString = selectedImageUri.getPath();

                        // MEDIA GALLERY
                        String selectedImagePath = OpenFileUtil.getPath(
                                this, selectedImageUri);

                        if (selectedImagePath != null) {
                            filePath = selectedImagePath;
                        } else if (fileManagerString != null) {
                            filePath = fileManagerString;
                        }

                        if (filePath != null) {
                            addImage(filePath, "");
                        } else {
                            bitmap = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } else if (requestCode == PICK_IMAGE_HIGH_VERSION) {
            if (resultCode == Activity.RESULT_OK) {
                selectedImageUri = data.getData();
                this.getContentResolver().takePersistableUriPermission(
                        selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                filePath = OpenFileUtil.getPath(this, selectedImageUri);
                addImage(filePath, "");
            }
        } else if (requestCode == UNIT_ID) {
            if (resultCode == Activity.RESULT_OK) {
                catalogSubID = data.getStringExtra(ListViewFilterActivity.FACULTY_ID);
                onRefresh(viewMiddle, catalogSubID);
                toast("Unit was " + catalogSubID);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Unit was not token");
            } else {
                toast("Unit was not token");
            }
        }

    }

    public Bitmap decodeFile(String filePath) {
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
        bitmap = BitmapFactory.decodeFile(filePath, o2);
        return bitmap;
    }

    public void wrapText() {
        if (holder.ll_content.getChildAt(holder.ll_content.getChildCount() - 1) != null
                && holder.ll_content.getChildAt(
                holder.ll_content.getChildCount() - 1)
                .onCheckIsTextEditor()) {
            EditText tempText = (EditText) holder.ll_content
                    .getChildAt(holder.ll_content.getChildCount() - 1);
            if (tempText.getText().toString().trim().equals("")) {
                holder.ll_content.removeViewAt(holder.ll_content
                        .getChildCount() - 1);
            }
        }
    }


    public void updateContent(String content) {
        Pattern p = Pattern.compile(LocalConfiguration.IMAGE_SYMBOL_START + "\\d+"
                + LocalConfiguration.IMAGE_SYMBOL_END);
        String[] strarray = p.split(content);
        if (strarray.length > 0) {
            for (int i = 0; i < strarray.length; i++) {
                String tempString = strarray[i];
                Pattern p1 = Pattern.compile(LocalConfiguration.IMAGE_TEXT_SYMBOL_START);
                Pattern p2 = Pattern.compile(LocalConfiguration.IMAGE_TEXT_SYMBOL_END);
                Matcher m1 = p1.matcher(tempString);
                Matcher m2 = p2.matcher(tempString);
                if (m1.find() && m2.find()) {
                    if ((i - 1) >= 0) {
                        String url = draft.getImagePaths().get(i - 1);
                        addImage(url, tempString.substring(m1.end(), m2.start()).trim());
                    }
                    addText(tempString.substring(m2.end()).trim());
                } else {
                    addText(tempString.trim());
                }
            }
        }
    }


    static class ViewHolder {
        EditText et_title;
        ExpandTabView expandTabView;
        LinearLayout ll_content;
        ImageView iv_camera;
        ImageView iv_gallery;
        ImageView iv_save;
        TextView iv_send;
        TextView tv_draft_length;
    }


    public class postTask extends AsyncTask<String, String, String> {

        Context context;

        public postTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... args) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("title", args[0]);
                jo.put("content", args[1]);
                jo.put("catalogID", catalogID);
                jo.put("postPic", bitmapList.size());
                jo.put("userID", StaticResource.user.getUserID());
                jo.put("subCatalogID", catalogSubID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return HttpHelper.insertPost(jo.toString());
        }

        @Override
        protected void onPostExecute(String result) {
            if (isNumeric(result)) {
                StaticResource.uploadSuccess = true;
                postPicTask postPic = new postPicTask();
                postPic.execute(result);
            }
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public class postPicTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            String tempPath = "";
            while (!got && (picIndex <= numberOfImage)) {
                if (bitmapList.containsKey(picIndex)) {
                    tempPath = bitmapList.get(picIndex);
                    got = true;
                } else {
                    picIndex++;
                }
            }
            got = false;

            if (!tempPath.equals("")) {
                return HttpHelper.uploadImage((picIndex) + "", tempPath,
                        args[0]);
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                bitmapList.remove(picIndex);
                if (bitmapList.size() > 0) {
                    postPicTask postPic = new postPicTask();
                    postPic.execute(result);
                }
            }
        }
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public void addImage(String imagePath, String text) {
        Bitmap image;
        int degree;
        degree = Util.readPictureDegree(imagePath);
        image = Util.rotateBitmap(decodeFile(imagePath), degree);

        bitmapList.put(numberOfImage, imagePath);
        numberOfImage++;
        wrapText();
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, convertDpToPixel(10), 0, convertDpToPixel(10));
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ImageView iv = new ImageView(this);
        iv.setLayoutParams(new LinearLayout.LayoutParams(convertDpToPixel(120),
                convertDpToPixel(120)));
        iv.setImageBitmap(image);
        ll.addView(iv);
        EditText et = new EditText(this);
        et.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        et.setMinimumHeight(convertDpToPixel(120));
        ll.addView(et);
        et.setBackgroundColor(Color.GRAY);
        et.setHint(R.string.add_description);
        et.setGravity(Gravity.TOP | Gravity.START);
        et.setTextColor(Color.WHITE);
        et.setHintTextColor(Color.WHITE);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                totalCharatersLength += -before + count;
                holder.tv_draft_length.setText("Wrote " + totalCharatersLength + " charaters.");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.ll_content.addView(ll);
        if (text.equals("")) {
            addText("");
        } else {
            et.setText(text);
        }
    }

    public void addText(String text) {
        EditText et = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, convertDpToPixel(10), 0, convertDpToPixel(10));
        et.setLayoutParams(lp);
        et.setPadding(convertDpToPixel(8), convertDpToPixel(8),
                convertDpToPixel(8), convertDpToPixel(8));
        et.setMinimumHeight(convertDpToPixel(120));
        et.setBackgroundColor(Color.GRAY);
        et.setHint(R.string.input);
        et.setGravity(Gravity.TOP | Gravity.START);
        et.setTextColor(Color.WHITE);
        et.setHintTextColor(Color.WHITE);
        et.setText(text);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                totalCharatersLength += -before + count;
                holder.tv_draft_length.setText("Wrote " + totalCharatersLength + " charaters.");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.ll_content.addView(et);
    }

    public void contentInitial() {
        holder.ll_content.removeAllViews();
        imageTextList.clear();
        sequence = 0;
        addText("");
    }


    public void wrapContent() {
        imageTextList.clear();
        for (int i = 0; i < holder.ll_content.getChildCount(); i++) {
            if (holder.ll_content.getChildAt(i).onCheckIsTextEditor()) {
                EditText tempText = (EditText) holder.ll_content.getChildAt(i);
                if (!tempText.getText().toString().trim().equals("")) {
                    ImageWithText iwt = new ImageWithText();
                    iwt.setHasImage(false);
                    iwt.setText(tempText.getText().toString());
                    imageTextList.add(iwt);
                }
            } else {
                LinearLayout tempLayout = (LinearLayout) holder.ll_content
                        .getChildAt(i);
                EditText tempText = (EditText) tempLayout.getChildAt(tempLayout
                        .getChildCount() - 1);
                ImageWithText iwt = new ImageWithText();
                iwt.setHasImage(true);
                iwt.setId(sequence++);
                iwt.setText(tempText.getText().toString());
                imageTextList.add(iwt);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (catalogID.equals("-1") || catalogSubID.equals("null")) {
            finish();
        } else {
            Time t = new Time();
            t.setToNow();
            int year = t.year;
            int month = t.month + 1;
            int date = t.monthDay;
            int hour = t.hour; // 0-23
            int minute = t.minute;
            draft.setAutoSavedTime(date + "/" + month + "/" + year + " " + hour + ":" + minute);
            draft.setCatalog(catalogID);
            draft.setCatalogSub(catalogSubID);

            wrapContent();
            String content = "";
            for (ImageWithText iwt : imageTextList) {
                if (iwt.isHasImage()) {
                    content += LocalConfiguration.IMAGE_SYMBOL_START + iwt.getId()
                            + LocalConfiguration.IMAGE_SYMBOL_END;
                    content += LocalConfiguration.IMAGE_TEXT_SYMBOL_START + iwt.getText()
                            + LocalConfiguration.IMAGE_TEXT_SYMBOL_END + "\n";
                } else {
                    content += iwt.getText() + "\n";
                }
            }
            draft.setContent(content);
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 0; i < bitmapList.size(); i++) {
                tempList.add(bitmapList.get(i));
            }
            draft.setImagePaths(tempList);
            draft.setNumOfImage(bitmapList.size());
            draft.setTitle(holder.et_title.getText().toString());

//                    File xmlFile = new File(getActivity().getFilesDir(), "Draft3.xml");
//                    DraftXMLHelper.saveAsDraft(draft, xmlFile);
            if (draft.getId() < 0) {
                SQLiteHelper.saveDraft(draft, ComposeActivity.this);
                ComposeActivity.this.finish();
            } else {
                SQLiteHelper.updateDraftSuccessfully(ComposeActivity.this, draft);
                Intent returnIntent = new Intent();
                ComposeActivity.this.setResult(Activity.RESULT_OK, returnIntent);
                ComposeActivity.this.finish();
            }
            toast("Draft saved");
        }

    }

    public void toast(String text) {
        Toast toast = Toast.makeText(ComposeActivity.this, text,
                Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackground(null);
        toast.show();
    }

}
