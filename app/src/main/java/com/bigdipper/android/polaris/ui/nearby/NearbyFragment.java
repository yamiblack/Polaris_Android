package com.bigdipper.android.polaris.ui.nearby;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bigdipper.android.polaris.POI.POILocation;
import com.bigdipper.android.polaris.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NearbyFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback {

    private static final String TAG = "NearbyFragment";

    static String API_Key = "l7xx57fa48d037ad47f6bfdadc9ff4b5e33c";

    FrameLayout tMap;
    TMapView tMapView = null;
    TMapData tmapData;

    // View
    EditText searchText;
    Button cancelBtn;
    Button backBtn;
    Button directionBtn;

    // Dynamic View
    LinearLayout searchResultLayout;

    static double longitude, latitude;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nearby, container, false);

        // View
        searchText = root.findViewById(R.id.et_search);
        cancelBtn = root.findViewById(R.id.btn_nearby_cancel);
        backBtn = root.findViewById(R.id.btn_nearby_back);
        directionBtn = root.findViewById(R.id.btn_direction);

        // Dynamic View
        searchResultLayout = (LinearLayout) root.findViewById(R.id.searchList);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
                searchText.clearFocus();
                cancelBtn.setVisibility(View.GONE);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResultLayout.removeAllViews();
                searchResultLayout.setVisibility(View.GONE);
                searchText.clearFocus();
                searchText.setText("");
                backBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                InputMethodManager mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        directionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchResultLayout.removeAllViews();
                cancelBtn.setVisibility(View.VISIBLE);
                try {
                    String searchKeyword = searchText.getText().toString();

                    URL searchUrl = new URL("https://apis.openapi.sk.com/tmap/pois?appKey=" + API_Key + "&version=1&searchKeyword=" + searchKeyword + "&searchtypCd=R&radius=0&centerLon=" + longitude + "&centerLat=" + latitude);
                    Runnable search = new SearchPOI(searchUrl);
                    Thread searchThread = new Thread(search);
                    searchThread.start();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                searchResultLayout.setVisibility(View.VISIBLE);
            }
        });

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    backBtn.setVisibility(View.VISIBLE);
                }else{

                }
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            Toast.makeText(container.getContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
//        }

//        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                Toast.makeText(container.getContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
//            }
//        }

        tMap = (FrameLayout) root.findViewById(R.id.ll_tmap);

        tMapView = new TMapView(getActivity().getApplicationContext());

        tMapView.setSKTMapApiKey(API_Key);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setCompassMode(true);
        tMapView.setTrackingMode(true);
        tMap.addView(tMapView);

        tmapData = new TMapData();

        final LocationListener mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    tMapView.setLocationPoint(longitude, latitude);
                    tMapView.setCenterPoint(longitude, latitude);

                }

            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        final LocationManager lm = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(container.getContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000,
                1,
                mLocationListener);


        return root;
    }

    @Override
    public void onLocationChange(Location location) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
    }

    public class SearchPOI implements Runnable{
        private URL url;
        // POI
        ArrayList<POILocation> searchResult = new ArrayList<>();
        SearchPOI(URL url){
            this.url = url;
        }

        @Override
        public void run(){
            try {
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");

                if(huc.getResponseCode() == HttpURLConnection.HTTP_OK){
                    InputStreamReader streamReader = new InputStreamReader(huc.getInputStream());
                    BufferedReader buffered = new BufferedReader(streamReader);

                    StringBuilder content = new StringBuilder();
                    while(true){
                        String data = buffered.readLine();
                        if(data == null){
                            break;
                        }else{
                            // result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getString("centerLat");
                            // result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getString("centerLon");
                            JSONArray result = new JSONObject(data).getJSONObject("searchPoiInfo").getJSONObject("pois").getJSONArray("poi");
                            for(int i = 0; i < result.length(); i++){
                                String resultName = result.getJSONObject(i).getString("name");
                                double resultRadius = result.getJSONObject(i).getDouble("radius");
                                String resultLowerBizName = result.getJSONObject(i).getString("lowerBizName");
                                String resultFullAddressRoad = result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getString("fullAddressRoad");
                                String resultFullAddress = result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getString("fullAddressRoad");
                                double resultCenterLat = result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getDouble("centerLat");
                                double resultCenterLon = result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getDouble("centerLon");

                                POILocation poiLocation = new POILocation(resultName, resultRadius, resultLowerBizName, resultFullAddressRoad, resultFullAddress, resultCenterLat, resultCenterLon);
                                searchResult.add(poiLocation);
                                getActivity().runOnUiThread((new Runnable(){

                                    @Override
                                    public void run() {
                                        showSearchResult(searchResult, result.length());

                                    }
                                }));
//                                System.out.println("--------------------------------------------------------------------------------------------------");
//                                System.out.println("이름 : " + searchResult.get(0).getName());
//                                System.out.println("거리: " + searchResult.get(0).getRadius());
//                                System.out.println("종류: " + searchResult.get(0).getBizName());
//                                System.out.println("도로명 주소: " + searchResult.get(0).getRoadAddress());
//                                System.out.println("지번 주소: " + searchResult.get(0).getAddress());
//                                System.out.println("위도: " + searchResult.get(0).getLatitude());
//                                System.out.println("경도: " + searchResult.get(0).getLongitude());
                            }
                            //System.out.println(data);
                            content.append(data); // 검색 결과: data

                        }
                    }

                    buffered.close();
                    huc.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch(JSONException e){
                e.printStackTrace();
            }

        }
    }

    private void showSearchResult(ArrayList<POILocation> searchResult, int size){

        for(int i = 0; i < size; i++){
            LinearLayout searchElementLayout = new LinearLayout(getContext());
            LinearLayout searchResultNameLayout = new LinearLayout(getContext());
            LinearLayout searchResultAddressLayout = new LinearLayout(getContext());
            searchElementLayout.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_bottom_stroke));

            searchElementLayout.setOrientation(LinearLayout.VERTICAL);
            searchElementLayout.setId(i);
            int id = i;
            searchElementLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchResultLayout.setVisibility(View.GONE);
                    tMapView.setLocationPoint(searchResult.get(id).getLongitude(), searchResult.get(id).getLatitude());
                    tMapView.setCenterPoint(searchResult.get(id).getLongitude(), searchResult.get(id).getLatitude());
                }
            });

            searchResultNameLayout.setOrientation(LinearLayout.HORIZONTAL);
            searchResultAddressLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView resultName = new TextView(getContext());
            TextView resultRadius = new TextView(getContext());
            TextView resultBizName = new TextView(getContext());
            TextView resultRoadAddress = new TextView(getContext());
            TextView resultAddress = new TextView(getContext());
            TextView resultLatitude = new TextView(getContext());
            TextView resultLongitude = new TextView(getContext());

            resultName.setText(searchResult.get(i).getName());
            resultRadius.setText(String.valueOf(searchResult.get(i).getRadius()));
            resultBizName.setText(searchResult.get(i).getBizName());
            resultRoadAddress.setText(searchResult.get(i).getRoadAddress());

            searchResultNameLayout.addView(resultName);
            searchResultNameLayout.addView(resultBizName);
            searchResultAddressLayout.addView(resultRoadAddress);
            searchResultAddressLayout.addView(resultRadius);

            searchElementLayout.addView(searchResultNameLayout);
            searchElementLayout.addView(searchResultAddressLayout);

            searchResultLayout.addView(searchElementLayout);
        }
    }

}