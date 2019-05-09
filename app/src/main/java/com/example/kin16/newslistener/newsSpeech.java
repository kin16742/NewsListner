package com.example.kin16.newslistener;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class newsSpeech {
    private static String TAG = "newsSpeech";

    public static void main(String[] args) {
        String clientId = "vinxxy8c3t";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "VsQkPEiFa0T4wWyufgOOAh7oABOkp8OrLLFPmHgn";//애플리케이션 클라이언트 시크릿값";
        try {

            String text = URLEncoder.encode(args[0], "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            // post request
            String postParams = "speaker=mijin&speed=0&text=" + text;
            con.setDoOutput(true);
            con.setDoInput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            Log.d(TAG, String.valueOf(wr));

            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            Log.d(TAG, String.valueOf(responseCode));

            if(responseCode == 200) { // 정상 호출

                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Naver");

                String dirname = file.getAbsolutePath();
                String mp3Name = file.getAbsolutePath() + "/naverTTS.mp3";
                String chkFile = file.getAbsolutePath() + "/chk";
                File dir = new File(dirname);
                Log.d(TAG, "dir 변수 할당 dir 경로 : " + dirname);
                if(!dir.exists()){
                    dir.mkdirs();
                    Log.d(TAG, "dir 없어서 생성");
                }

                File f = new File(mp3Name);
                Log.d(TAG, "mp3 파일 변수 할당");
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read =is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                Log.d(TAG, "mp3 파일 읽어옴");
                is.close();
                File chkF = new File(chkFile);
                chkF.createNewFile();
            }

            else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
