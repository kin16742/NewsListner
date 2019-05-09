package com.example.kin16.newslistener;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "메인";

    String array[] = {"(탭해서 선택)", "정치","경제","사회","생활/문화","세계","IT/과학","연예","스포츠"};

    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Naver");
    String mp3Name = file.getAbsolutePath() + "/naverTTS.mp3";
    String chkF = file.getAbsolutePath() + "/chk";

    HorizontalScrollView sv;
    customViewPager vp;
    LinearLayout ll1, ll2;
    private NaverTTSTask mNaverTTSTask;
    String[] mTextString;
    Button btTTS;
    TextView myText;
    MediaPlayer audioPlay;
    Spinner sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vp = findViewById(R.id.vp);
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        btTTS = findViewById(R.id.btTTS);
        sv = findViewById(R.id.sv);
        sp = findViewById(R.id.favorite);


        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_text, array);
        spinner_adapter.setDropDownViewResource(R.layout.spinner_text);
        sp.setAdapter(spinner_adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        btTTS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (btTTS.getText().toString().equals("읽어줘")) {
                            int curr = vp.getCurrentItem();
                            File chkFile = new File(chkF);

                            int k = getResources().getIdentifier("tv1_" + curr, "id", getPackageName());
                            myText = findViewById(k);
                            String mText = "1번째 뉴스입니다.\n" + myText.getText().toString() + ".\n";

                            for (int i = 2; i <= 10; i++) {
                                k = getResources().getIdentifier("tv" + i + "_" + curr, "id", getPackageName());
                                myText = findViewById(k);
                                mText = mText + i + "번째 뉴스입니다.\n" + myText.getText().toString() + ".\n";
                            }
                            Log.d(TAG, mText);
                            mTextString = new String[]{mText};

                            mNaverTTSTask = new NaverTTSTask();
                            mNaverTTSTask.execute(mTextString);


                            try {
                                audioPlay = new MediaPlayer();
                                audioPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        audioPlay.seekTo(0);
                                        btTTS.setText("재생");
                                    }
                                });
                                while(!chkFile.exists());

                                audioPlay.setDataSource(mp3Name);

                                audioPlay.prepare();
                                audioPlay.start();
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                            btTTS.setText("일시정지");
                        }
                        else if (btTTS.getText().toString().equals("일시정지") && audioPlay.isPlaying()) {
                            btTTS.setText("재생");
                            audioPlay.pause();
                        }
                        else if(btTTS.getText().toString().equals("재생") && !audioPlay.isPlaying()){
                            btTTS.setText("일시정지");
                            audioPlay.start();
                        }
                    }
                });

        PermissionListener pl = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        };
        new TedPermission(this)
                .setPermissionListener(pl)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

        Button tab_1 = findViewById(R.id.tab1);
        Button tab_2 = findViewById(R.id.tab2);
        Button tab_3 = findViewById(R.id.tab3);
        Button tab_4 = findViewById(R.id.tab4);

        Button cate_1 = findViewById(R.id.category1);
        Button cate_2 = findViewById(R.id.category2);
        Button cate_3 = findViewById(R.id.category3);
        Button cate_4 = findViewById(R.id.category4);
        Button cate_5 = findViewById(R.id.category5);
        Button cate_6 = findViewById(R.id.category6);
        Button cate_7 = findViewById(R.id.category7);
        Button cate_8 = findViewById(R.id.category8);

        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setOffscreenPageLimit(11);
        vp.setCurrentItem(0);

        tab_1.setOnClickListener(movePageListener);
        tab_1.setTag(0);
        tab_2.setOnClickListener(movePageListener);
        tab_2.setTag(1);
        tab_3.setOnClickListener(movePageListener);
        tab_3.setTag(2);
        tab_4.setOnClickListener(movePageListener);
        tab_4.setTag(3);

        cate_1.setOnClickListener(movePageListener);
        cate_1.setTag(4);
        cate_2.setOnClickListener(movePageListener);
        cate_2.setTag(5);
        cate_3.setOnClickListener(movePageListener);
        cate_3.setTag(6);
        cate_4.setOnClickListener(movePageListener);
        cate_4.setTag(7);
        cate_5.setOnClickListener(movePageListener);
        cate_5.setTag(8);
        cate_6.setOnClickListener(movePageListener);
        cate_6.setTag(9);
        cate_7.setOnClickListener(movePageListener);
        cate_7.setTag(10);
        cate_8.setOnClickListener(movePageListener);
        cate_8.setTag(11);

        tab_1.setSelected(true);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                File chkFile = new File(chkF);
                if(audioPlay != null && audioPlay.isPlaying()){
                    audioPlay.stop();
                }
                if(audioPlay != null && !audioPlay.isPlaying()){
                    btTTS.setText("읽어줘");
                    audioPlay.stop();
                }
                if(chkFile.exists()) chkFile.delete();
            }

            @Override
            public void onPageSelected(int position) {
                int i = 0;
                while (i < 4) {
                    if (position == i) {
                        ll2.findViewWithTag(i).setSelected(true);
                    } else {
                        ll2.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
                while( i < 12){
                    if (position == i) {
                        int svLength = sv.getChildAt(0).getRight() - sv.getWidth() + sv.getPaddingLeft();
                        Log.d(TAG, String.valueOf(svLength));

                        if(i==11)
                            sv.smoothScrollTo(svLength,0);
                        else
                            sv.smoothScrollTo(svLength / 8 * (i - 4),0);

                        ll1.findViewWithTag(i).setSelected(true);
                    } else {
                        ll1.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();

            int i = 0;
            while (i < 4) {
                if (tag == i) {
                    ll2.findViewWithTag(i).setSelected(true);
                } else {
                    ll2.findViewWithTag(i).setSelected(false);
                }
                i++;
            }
            while( i < 12){
                if (tag == i) {
                    ll1.findViewWithTag(i).setSelected(true);
                } else {
                    ll1.findViewWithTag(i).setSelected(false);
                }
                i++;
            }

            vp.setCurrentItem(tag);
        }
    };

    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new recommendNews();
                case 1:
                    return new locationNews();
                case 2:
                    return new hotNews();
                case 3:
                    return new todayNews();
                case 4:
                    return new categoryNews_1();
                case 5:
                    return new categoryNews_2();
                case 6:
                    return new categoryNews_3();
                case 7:
                    return new categoryNews_4();
                case 8:
                    return new categoryNews_5();
                case 9:
                    return new categoryNews_6();
                case 10:
                    return new categoryNews_7();
                case 11:
                    return new categoryNews_8();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 12;
        }
    }

    private class NaverTTSTask extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... strings){
            newsSpeech.main(mTextString);
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }
}
