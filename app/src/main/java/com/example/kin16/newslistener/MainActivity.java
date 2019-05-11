package com.example.kin16.newslistener;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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
    String voiceS[] = {"남자", "여자"};
    String introText = "뉴스 리스너입니다. 말씀하세요.";

    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Naver");
    String mp3Name = file.getAbsolutePath() + "/naverTTS.mp3";
    String introName = file.getAbsolutePath() + "/intro.mp3";
    String chkF = file.getAbsolutePath() + "/chk";

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
    MediaPlayer audioPlay, introPlay;
    Spinner sp, vs;

    TextView tvTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        btSTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.startListening(i);
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
                }
            });
            introPlay.prepare();
            introPlay.start();

            reserveListen();
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
            reserveListen();
        }

        @Override
        public void onError(int error) {
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "Audio recording error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "Client side error";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "Network error";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "Network timeout";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "No match";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "error from server";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Didn't understand, please try again.";
                    break;
            }
            Log.d(TAG, "test error :" + message);
            reserveListen();
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

    private void startAction(ArrayList<String> rst){
        String[] rs = new String[rst.size()];
        rst.toArray(rs);
        Toast.makeText(getApplicationContext(), rs[0], Toast.LENGTH_LONG).show();
        if(rs[0].equals("읽어 줘")){
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
    }
}
