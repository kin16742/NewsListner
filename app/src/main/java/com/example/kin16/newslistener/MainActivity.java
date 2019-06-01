package com.example.kin16.newslistener;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
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

    String array[] = {"정치","경제","사회","생활/문화","세계","IT/과학","연예","스포츠"};
    String voiceS[] = {"남자", "여자"};
    String introText = "뉴스 리스너입니다. 말씀하세요.";
    String address = " ", result = "";
    String phoneID, curURL, curCom;

    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Naver");
    String mp3Name = file.getAbsolutePath() + "/naverTTS.mp3";
    String introName = file.getAbsolutePath() + "/intro.mp3";
    String chkF = file.getAbsolutePath() + "/chk";

    int recognize = 1, searchmode = 0, mode = 0;

    Intent i;
    SpeechRecognizer mRecognizer;

    HorizontalScrollView sv;
    customViewPager vp;
    LinearLayout ll1, ll2;
    private NaverTTSTask mNaverTTSTask;
    private introMaker introMakerTask;
    String[] mTextString;
    Button btTTS, btSTT;
    TextView myText;
    MediaPlayer audioPlay, introPlay, MP;

    Spinner sp, vs;
    Switch sw;

    TextView tvTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        PermissionListener pll = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        };
        new TedPermission(this)
                    .setPermissionListener(pll)
                    .setPermissions(Manifest.permission.READ_PHONE_STATE)
                    .check();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager mgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            phoneID = mgr.getDeviceId();
        }

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
        PermissionListener pl2 = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        };
        new TedPermission(this)
                .setPermissionListener(pl2)
                .setPermissions(Manifest.permission.RECORD_AUDIO)
                .check();
        PermissionListener pl3 = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        };
        new TedPermission(this)
                .setPermissionListener(pl3)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
        /*PermissionListener pl4 = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        };
        new TedPermission(this)
                .setPermissionListener(pl4)
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();*/


        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        tvTemp = findViewById(R.id.tv1_0);
        vp = findViewById(R.id.vp);
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        btTTS = findViewById(R.id.btTTS);
        btSTT = findViewById(R.id.btSTT);
        sv = findViewById(R.id.sv);
        sp = findViewById(R.id.favorite);
        vs = findViewById(R.id.voiceSelect);

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

        ArrayAdapter<String> spinner_adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_text, voiceS);
        spinner_adapter2.setDropDownViewResource(R.layout.spinner_text);
        vs.setAdapter(spinner_adapter2);
        vs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btSTT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mode = 1;
                reserveListen();
            }
        });

        btTTS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (btTTS.getText().toString().equals("읽어줘")) {
                            ReadArt dd = new ReadArt();
                            dd.execute();
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


        Button tab_1 = findViewById(R.id.tab1);
        Button tab_2 = findViewById(R.id.tab2);
        Button tab_3 = findViewById(R.id.tab3);

        Button cate_1 = findViewById(R.id.category1);
        Button cate_2 = findViewById(R.id.category2);
        Button cate_3 = findViewById(R.id.category3);
        Button cate_4 = findViewById(R.id.category4);
        Button cate_5 = findViewById(R.id.category5);
        Button cate_6 = findViewById(R.id.category6);
        Button cate_7 = findViewById(R.id.category7);
        Button cate_8 = findViewById(R.id.category8);

        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setOffscreenPageLimit(10);
        vp.setCurrentItem(0);

        tab_1.setOnClickListener(movePageListener);
        tab_1.setTag(0);
        tab_2.setOnClickListener(movePageListener);
        tab_2.setTag(1);
        tab_3.setOnClickListener(movePageListener);
        tab_3.setTag(2);

        cate_1.setOnClickListener(movePageListener);
        cate_1.setTag(3);
        cate_2.setOnClickListener(movePageListener);
        cate_2.setTag(4);
        cate_3.setOnClickListener(movePageListener);
        cate_3.setTag(5);
        cate_4.setOnClickListener(movePageListener);
        cate_4.setTag(6);
        cate_5.setOnClickListener(movePageListener);
        cate_5.setTag(7);
        cate_6.setOnClickListener(movePageListener);
        cate_6.setTag(8);
        cate_7.setOnClickListener(movePageListener);
        cate_7.setTag(9);
        cate_8.setOnClickListener(movePageListener);
        cate_8.setTag(10);

        tab_1.setSelected(true);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                recognize = 10;
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
                while (i < 3) {
                    if (position == i) {
                        ll2.findViewWithTag(i).setSelected(true);
                    } else {
                        ll2.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
                while( i < 11){
                    if (position == i) {
                        int svLength = sv.getChildAt(0).getRight() - sv.getWidth() + sv.getPaddingLeft();
                        Log.d(TAG, String.valueOf(svLength));

                        if(i==10)
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

    @Override
    protected void onResume(){
        super.onResume();

        File iF = new File(introName);

        if(iF.exists()){
            intro();
        }
        else {
            mTextString = new String[]{introText};

            introMakerTask = new introMaker();
            introMakerTask.execute(mTextString);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();

        File iF = new File(introName);

        if(iF.exists()){
            iF.delete();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        File iF = new File(introName);

        if(iF.exists()){
            iF.delete();
        }
    }
    private void intro(){

        introPlay = new MediaPlayer();
        try {
            Log.d(TAG, "인트로 읽는 중");
            introPlay.setDataSource(introName);
            introPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    File iF = new File(introName);
                    iF.delete();
                    reserveListen();
                }
            });
            introPlay.prepare();
            introPlay.start();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void reserveListen(){
        mRecognizer.startListening(i);
    }

    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();

            int i = 0;
            while (i < 3) {
                if (tag == i) {
                    ll2.findViewWithTag(i).setSelected(true);
                } else {
                    ll2.findViewWithTag(i).setSelected(false);
                }
                i++;
            }
            while( i < 11){
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
                    //return new recommendNews();
                case 2:
                    return new hotNews();
                case 3:
                    return new categoryNews_1();
                case 4:
                    return new categoryNews_2();
                case 5:
                    return new categoryNews_3();
                case 6:
                    return new categoryNews_4();
                case 7:
                    return new categoryNews_5();
                case 8:
                    return new categoryNews_6();
                case 9:
                    return new categoryNews_7();
                case 10:
                    return new categoryNews_8();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 11;
        }
    }
    private class ReadArt extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... strings){
            recognize = 1;
            int curr = vp.getCurrentItem();
            TextView ttmp, tttmp;
            File chkFile = new File(chkF);
            int k, h, j;
            String mText;
            for(int i=1;i<=10;i++){
                k = getResources().getIdentifier("tv" + i +"_" + curr, "id", getPackageName());
                h = getResources().getIdentifier("tv" + i +"_2_" + curr, "id", getPackageName());
                j = getResources().getIdentifier("tv" + i +"_1_" + curr, "id", getPackageName());
                myText = findViewById(k);
                mText = i + "번째 뉴스입니다.\n" + myText.getText().toString() + ".\n 더 자세히 읽어드릴까요?";
                Log.d(TAG, mText);

                while(recognize == 0);

                Log.d(TAG,"recognize : " + recognize);

                try{
                    Thread.sleep(200);
                }
                catch (Exception e) {
                    System.out.println(e);
                }

                if(recognize == 2){
                    recognize = 0;
                    break;
                }
                if(recognize == 10) {
                    recognize = 0;
                    break;
                }
                recognize = 0;
                mode = 2;
                mTextString = new String[]{mText};
                Log.d(TAG, "sss");

                /*mNaverTTSTask = new NaverTTSTask();
                mNaverTTSTask.execute(mTextString);*/

                String voice = vs.getSelectedItem().toString();
                if(voice.equals("남자"))
                    newsSpeech.main(mTextString, "jinho");
                else
                    newsSpeech.main(mTextString, "mijin");
                Log.d(TAG, "dasdd");

                ttmp = findViewById(h);
                tttmp = findViewById(j);
                curURL = ttmp.getText().toString();
                curCom = tttmp.getText().toString();

                try {
                    audioPlay = new MediaPlayer();
                    audioPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            reserveListen();
                        }
                    });
                    while(!chkFile.exists());
                    chkFile.delete();

                    audioPlay.setDataSource(mp3Name);

                    audioPlay.prepare();
                    audioPlay.start();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            mode = 0;
            recognize = 1;
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }
    private class NaverTTSTask extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... strings){
            Log.d(TAG, "zzzz");
            String voice = vs.getSelectedItem().toString();
            if(voice.equals("남자"))
                newsSpeech.main(mTextString, "jinho");
            else
                newsSpeech.main(mTextString, "mijin");
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }
    private class introMaker extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... strings){
            String voice = vs.getSelectedItem().toString();
            if(voice.equals("남자"))
                makeIntro.main(mTextString, "jinho");
            else
                makeIntro.main(mTextString, "mijin");
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            if(recognize != 2)
                recognize = 1;
        }

        @Override
        public void onError(int error) {
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> rsts = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            startAction(rsts);
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    private void startAction(ArrayList<String> rst) {
        String[] rs = new String[rst.size()];
        rst.toArray(rs);
        File chkFile = new File(chkF);
        Toast.makeText(getApplicationContext(), rs[0], Toast.LENGTH_LONG).show();
        Log.d(TAG, recognize + " ");

        if (rs[0].equals("그만")) {
            recognize = 2;
        } else if (rs[0].equals("읽어 줘")) {
            Log.d(TAG, "모드 : "+ mode);
            if(mode == 0 || mode == 1){
                ReadArt dd = new ReadArt();
                dd.execute();
                btTTS.setText("일시정지");
            }
            else if (mode == 2) {
                recognize = 2;
                BackgroundTask task = new BackgroundTask();
                task.execute();

                while (result == "");
                Log.d(TAG, "결과값 : "+ result);

                mTextString = new String[]{result};

                mNaverTTSTask = new NaverTTSTask();
                mNaverTTSTask.execute(mTextString);

                result = "";

                try {
                    audioPlay = new MediaPlayer();
                    audioPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            audioPlay.release();
                            recognize = 2;
                        }
                    });
                    while (!chkFile.exists()) ;
                    chkFile.delete();

                    audioPlay.setDataSource(mp3Name);

                    audioPlay.prepare();
                    audioPlay.start();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } else if (searchmode == 1) {
            Intent pintent = new Intent(MainActivity.this, keywordNews.class);
            pintent.putExtra("key", rs[0]);
            pintent.putExtra("voice", vs.getSelectedItem().toString());
            startActivity(pintent);
            searchmode = 0;
        } else if (rs[0].equals("안녕")) {
            mTextString = new String[]{"안녕하세요 저는 뉴스리스너입니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        MP.release();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("추천") || rs[0].equals("추천 뉴스")) {
            mTextString = new String[]{"추천 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(0);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("지역별") || rs[0].equals("지역별 뉴스")) {
            mTextString = new String[]{"지역별 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(1);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("인기") || rs[0].equals("인기 뉴스")) {
            mTextString = new String[]{"인기 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(2);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("정치") || rs[0].equals("정치 뉴스")) {
            mTextString = new String[]{"정치 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(4);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("경제") || rs[0].equals("경제 뉴스")) {
            mTextString = new String[]{"경제 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(5);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("사회") || rs[0].equals("사회 뉴스")) {
            mTextString = new String[]{"사회 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(6);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("생활") || rs[0].equals("문화")) {
            mTextString = new String[]{"생활/문화 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(7);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("세계") || rs[0].equals("세계 뉴스")) {
            mTextString = new String[]{"세계 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(8);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("IT") || rs[0].equals("과학")) {
            mTextString = new String[]{"IT/과학 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(9);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("연예") || rs[0].equals("연예 뉴스")) {
            mTextString = new String[]{"연예 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(10);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("스포츠") || rs[0].equals("스포츠 뉴스")) {
            mTextString = new String[]{"스포츠 뉴스로 이동합니다."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
                vp.setCurrentItem(11);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("고마워") || rs[0].equals("수고했어")) {
            mTextString = new String[]{"그럼 안녕히계세요."};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        finish();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (rs[0].equals("검색")) {
            mTextString = new String[]{"무엇을 검색하시겠습니까?"};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);
            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        searchmode = 1;
                        reserveListen();
                    }
                });
                while (!chkFile.exists()) ;
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }


    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
            int curr = vp.getCurrentItem();
            curURL = curURL.replace("/","$");
            curURL = curURL.replace("?","!");
            if(curr > 2) {
                String tt = array[curr - 3];
                if(curr == 6) tt = "생활";
                if(curr == 8) tt = "IT";
                address = "http://211.112.85.72:8000/polls/insert/" + phoneID + "/" + sp.getSelectedItem() + "/"+ tt +"/" + curCom + "/" + curURL;
            }
            else if (curr == 0){
                address = "http://211.112.85.72:8000/polls/insert/" + phoneID + "/" + sp.getSelectedItem() + "/정치/" + curCom + "/" + curURL;
            }
            else{

            }
            Log.d(TAG,address);
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            // TODO Auto-generated method stub
            result = request(address);
            return null;
        }

        protected void onPostExecute(Integer a) {
        }
    }

    private String request(String urlStr) {
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                int resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
                    String line = null;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        output.append(line + "\n");
                    }

                    reader.close();
                    conn.disconnect();
                }
            }
        } catch(Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }

        return output.toString();
    }
}
