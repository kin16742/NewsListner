package com.example.kin16.newslistener;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class locationNews extends Fragment {
    String address = "";
    String chk = "1";
    String result = "";
    BackgroundTask task;
    TextView tvk, tvj, tvh;
    double longitude;
    double latitude;
    LocationManager lm;
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
        latitude = 37.45;
        longitude = 126.65;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("위도 경도", longitude + " " + latitude);
        } else {
            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location loc) {
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                    Log.d("위도 경도", latitude + " " + longitude);
                    lm.removeUpdates(this);
                    if (result == "") {
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
                            int h = getResources().getIdentifier("tv" + i + "_2_1", "id", getActivity().getPackageName());
                            tvj = getActivity().findViewById(j);
                            tvj.setText(strT1);

                            strT1 = st2.nextToken();
                            st3 = new StringTokenizer(strT1, "뉅");
                            String strT2 = st3.nextToken();
                            tvk = getActivity().findViewById(k);
                            tvk.setText(strT2);

                            final String strLink = st3.nextToken();
                            tvh = getActivity().findViewById(h);
                            tvh.setText(strLink);
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

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, mLocationListener);
        }

        String test;
        tvk = getActivity().findViewById(R.id.tv1_1);
        test = tvk.getText().toString();
        if (test.equals(chk)) {
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

        Log.d("지역뉴스", "완료");
    }

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
            address = "http://211.112.85.72:8000/polls/gps/" + latitude + "/" + longitude;
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

