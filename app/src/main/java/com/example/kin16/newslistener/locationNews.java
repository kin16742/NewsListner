package com.example.kin16.newslistener;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

public class locationNews extends Fragment {
    String address = "";
    String chk = "1";
    String result = "";
    BackgroundTask task;
    TextView tvk, tvj;
    double longitude;
    double latitude;
    public locationNews() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_location, container, false);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = loc.getLongitude();
        latitude = loc.getLatitude();

        String test;
        tvk = getActivity().findViewById(R.id.tv1_1);
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
                int k = getResources().getIdentifier("tv" + i + "_1", "id", getActivity().getPackageName());
                int j = getResources().getIdentifier("tv" + i + "_1_1", "id", getActivity().getPackageName());
                tvj = getActivity().findViewById(j);
                tvj.setText(strT1);

                strT1 = st2.nextToken();
                st3 = new StringTokenizer(strT1, "뉅");
                String strT2 = st3.nextToken();
                tvk = getActivity().findViewById(k);
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
    }

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
            address = "http://211.112.85.72:8000/polls/crawl/지역/" + longitude + "/" + latitude;
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

