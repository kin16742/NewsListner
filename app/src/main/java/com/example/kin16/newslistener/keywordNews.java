package com.example.kin16.newslistener;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class keywordNews extends AppCompatActivity{
    String address = "";
    String chk = "1";
    String result = "";
    BackgroundTask task;
    TextView tvk, tvj, tvTitle;
    String[] mTextString;
    private NaverTTSTask mNaverTTSTask;
    Spinner vs;
    String keyword, voice;
    MediaPlayer MP;
    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Naver");
    String chkF = file.getAbsolutePath() + "/chk";
    String mp3Name = file.getAbsolutePath() + "/naverTTS.mp3";

    SpeechRecognizer mRecognizer;
    Intent i;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_keyword);

        Intent intent = new Intent(this.getIntent());
        keyword = intent.getStringExtra("key");
        voice = intent.getStringExtra("voice");

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        String test;
        tvk = findViewById(R.id.tv1_key);
        tvTitle = findViewById(R.id.tv_key_title);
        tvTitle.setText(keyword + " 검색 결과");
        vs = findViewById(R.id.voiceSelect);
        test = tvk.getText().toString();
        if(test.equals(chk)) {
            task = new BackgroundTask();
            task.execute();

            while (result == "") ;

            StringTokenizer st = new StringTokenizer(result, "뒑");
            StringTokenizer st2;
            StringTokenizer st3;

            for (int i = 1; i <= 10; i++) {
                String strTemp = st.nextToken();
                st2 = new StringTokenizer(strTemp, "궭");
                String strT1 = st2.nextToken();
                int k = getResources().getIdentifier("tv" + i + "_key", "id", getPackageName());
                int j = getResources().getIdentifier("tv" + i + "_1_key", "id", getPackageName());
                tvj = findViewById(j);
                tvj.setText(strT1);

                strT1 = st2.nextToken();
                st3 = new StringTokenizer(strT1, "뉅");
                String strT2 = st3.nextToken();
                tvk = findViewById(k);
                tvk.setText(strT2);

                final String strLink = st3.nextToken();
                tvk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(strLink));
                        startActivity(intent);
                    }
                });
            }

        }


        File chkFile = new File(chkF);
        mTextString = new String[]{keyword + " 검색 결과입니다. 읽어드릴까요?"};

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
            while(!chkFile.exists());
            chkFile.delete();

            MP.setDataSource(mp3Name);

            MP.prepare();
            MP.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void reserveListen(){
        mRecognizer.startListening(i);
    }
    private class NaverTTSTask extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... strings){
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
    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
            address = "http://211.112.85.72:8000/polls/keyword/" + keyword;
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

    private void startAction(ArrayList<String> rst){
        String[] rs = new String[rst.size()];
        rst.toArray(rs);
        File chkFile = new File(chkF);
        Toast.makeText(getApplicationContext(), rs[0], Toast.LENGTH_LONG).show();

        if(rs[0].equals("읽어 줘") || rs[0].equals("응")) {
            int k = getResources().getIdentifier("tv1_key", "id", getPackageName());
            TextView myText = findViewById(k);
            String mText = "1번째 뉴스입니다.\n" + myText.getText().toString() + ".\n";

            for (int i = 2; i <= 10; i++) {
                k = getResources().getIdentifier("tv" + i + "_key", "id", getPackageName());
                myText = findViewById(k);
                mText = mText + i + "번째 뉴스입니다.\n" + myText.getText().toString() + ".\n";
            }
            mTextString = new String[]{mText};

            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);

            try {
                MP = new MediaPlayer();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                    }
                });
                while(!chkFile.exists());
                chkFile.delete();
                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        else if (rs[0].equals("아니") || rs[0].equals("괜찮아")){
            mTextString = new String[]{"그럼 원래 화면으로 돌아가겠습니다."};

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
                while(!chkFile.exists());
                chkFile.delete();

                MP.setDataSource(mp3Name);

                MP.prepare();
                MP.start();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }

    @Override
    protected void onStop(){
        if(MP != null && MP.isPlaying()) {
            MP.stop();
            MP.release();
        }
        super.onStop();
    }
}
