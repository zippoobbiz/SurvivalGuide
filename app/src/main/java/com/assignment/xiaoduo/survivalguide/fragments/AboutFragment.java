package com.assignment.xiaoduo.survivalguide.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.assignment.xiaoduo.survivalguidefit4039assignment.R;


public class AboutFragment extends Fragment {
    private String linkList[] = {
            "https://github.com/makovkastar/FloatingActionButton",//floating action button
            "https://github.com/Bearded-Hen/Android-Bootstrap", //bootstrap
            "https://github.com/edmodo/cropper",                //cropper
            "https://github.com/hoang8f/android-flat-button", //flat button
            "https://github.com/sbakhtiarov/gif-movie-view",
            "https://github.com/bhavyahmehta/ListviewFilter",
            "https://github.com/thiagolocatelli/android-uitableview",
            "https://github.com/thest1/LazyList/tree/master/src/com/fedorvlasov/lazylist"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container,
                false);
        TextView tv_title = (TextView) rootView.findViewById(R.id.title);
        TextView tv_about = (TextView) rootView.findViewById(R.id.about);
        LinearLayout thanks_list = (LinearLayout) rootView.findViewById(R.id.thanks_list);
        tv_title.setText("Thanks");
        tv_about.setText("One cannot make good apps without help. " +
                "Here are the men who inspire me and made their great effort to support many developers. " +
                "I present my humble thanks to them.");
        for (final String link : linkList) {
            TextView descTv = new TextView(getActivity());
            LinearLayout.LayoutParams lpd = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            descTv.setLayoutParams(lpd);
            descTv.setPadding(convertDpToPixel(20), convertDpToPixel(10), convertDpToPixel(10), convertDpToPixel(0));
            descTv.setTextColor(Color.WHITE);
            descTv.setTypeface(null, Typeface.ITALIC);
            descTv.setText(link);
            descTv.setTextSize(12);
            descTv.setMovementMethod(LinkMovementMethod.getInstance());
            descTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
            });
            thanks_list.addView(descTv);
        }
        return rootView;
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
}
