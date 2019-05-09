package com.example.kin16.newslistener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.StringTokenizer;


public class recommendNews extends Fragment{
    String result = "";
    TextView tv;

    public recommendNews(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_recommend, container, false);
        return layout;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        for (int i = 1; i <= 3; i++) {
            int k = getResources().getIdentifier("tv" + i + "_0", "id", getActivity().getPackageName());
            tv = getActivity().findViewById(k);
            tv.setText(i + "번째 뉴스");
        }
    }
}
