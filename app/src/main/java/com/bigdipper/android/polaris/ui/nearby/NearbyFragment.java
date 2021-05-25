package com.bigdipper.android.polaris.ui.nearby;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NearbyFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback {

    private static final String TAG = "NearbyFragment";

    //    static String API_Key = "l7xx57fa48d037ad47f6bfdadc9ff4b5e33c"; // 메인
//    static String API_Key = "l7xx23e9a48d31d54d329e28dde4fce61161"; // test 1
//    static String API_Key = "l7xx8587ca243f924b9996e7c5b8ea0a6075"; // test 2
    static String API_Key = "l7xxf744614311fb4f578cb721f90eedc763"; // test 3


    FrameLayout tMap;
    TMapView tMapView = null;
    TMapData tmapData;

    // Value
    int compassMode = 0; // 0: 현재위치, 1: 현재위치&나침반&시야각
    String selectedName;
    String selectedBizName;
    String selectedAddress;

    // View
    EditText searchText;
    TextView selectedSearchName;
    TextView selectedSearchBizName;
    TextView selectedSearchAddress;
    Button cancelBtn;
    Button backBtn;
    Button directionBtn;
    Button zoomInBtn;
    Button zoomOutBtn;
    Button startNaviBtn;
    LinearLayout zoomBtnLayout;
    LinearLayout searchSelectedLayout;
    ScrollView searchResultScrollView;

    // Dynamic View
    LinearLayout searchResultLayout;

    //addd for find path
    String destinationName;
    double destinationLatitude;
    double destinationLongitude;
    TextView showPath;

    // Keyboard
    InputMethodManager mInputMethodManager;

    static double longitude, latitude;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nearby, container, false);

        // View
        searchText = root.findViewById(R.id.et_search);
        selectedSearchName = root.findViewById(R.id.t_search_selected_name);
        selectedSearchBizName = root.findViewById(R.id.t_search_selected_bizname);
        selectedSearchAddress = root.findViewById(R.id.t_search_selected_address);
        cancelBtn = root.findViewById(R.id.btn_nearby_cancel);
        backBtn = root.findViewById(R.id.btn_nearby_back);
        directionBtn = root.findViewById(R.id.btn_direction);
        zoomInBtn = root.findViewById(R.id.btn_zoom_in);
        zoomOutBtn = root.findViewById(R.id.btn_zoom_out);
        startNaviBtn = root.findViewById(R.id.btn_selected_start_navi);
        zoomBtnLayout = root.findViewById(R.id.btn_zoom_layout);
        searchSelectedLayout = root.findViewById(R.id.layout_search_selected);
        searchResultScrollView = root.findViewById(R.id.sv_search_result);

        // showPath = root.findViewById(R.id.tv_show_path);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // Dynamic View
        searchResultLayout = (LinearLayout) root.findViewById(R.id.searchList);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tMapView.setTrackingMode(false);
                searchText.setText("");
                searchText.clearFocus();
                cancelBtn.setVisibility(View.GONE);
                directionBtn.setVisibility(View.VISIBLE);
                zoomBtnLayout.setVisibility(View.VISIBLE);
                searchResultScrollView.setVisibility(View.GONE);
                searchSelectedLayout.setVisibility(View.GONE);
                tMapView.removeAllMarkerItem();
                searchResultLayout.removeAllViews();
                new Thread(){
                    @Override
                    public void run() {
                        mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }.start();
                selectedSearchName.setText("");
                selectedSearchBizName.setText("");
                selectedSearchAddress.setText("");
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tMapView.setTrackingMode(false);
                searchResultLayout.removeAllViews();
                searchText.clearFocus();
                searchText.setText("");
                backBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                searchResultScrollView.setVisibility(View.GONE);
                searchSelectedLayout.setVisibility(View.GONE);
                directionBtn.setVisibility(View.VISIBLE);
                zoomBtnLayout.setVisibility(View.VISIBLE);
                tMapView.removeAllMarkerItem();
                new Thread(){
                    @Override
                    public void run() {
                        mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }.start();
                selectedSearchName.setText("");
                selectedSearchBizName.setText("");
                selectedSearchAddress.setText("");

            }
        });
        directionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tMapView.setTrackingMode(true);
                tMapView.setLocationPoint(longitude, latitude);
                tMapView.setCenterPoint(longitude, latitude);

                if (compassMode == 0) {
                    tMapView.setCompassMode(true);
                    tMapView.setSightVisible(true);
                    compassMode++;
                } else if (compassMode == 1) {
                    tMapView.setCompassMode(false);
                    tMapView.setSightVisible(false);
                    compassMode = 0;
                }
            }
        });
        zoomInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        tMapView.MapZoomIn();
                    }
                }.start();
            }
        });
        zoomOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        tMapView.MapZoomOut();
                    }
                }.start();
            }
        });

        startNaviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        // 해당 위치로 길안내 시작 kim 연동 필요
                    }
                }.start();
            }
        });


        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searchResultLayout.removeAllViews();
                cancelBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchResultScrollView.setVisibility(View.VISIBLE);
                directionBtn.setVisibility(View.GONE);
                zoomBtnLayout.setVisibility(View.GONE);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            String searchKeyword = searchText.getText().toString();

                            URL searchUrl = new URL("https://apis.openapi.sk.com/tmap/pois?appKey=" + API_Key + "&version=1&searchKeyword=" + searchKeyword + "&searchtypCd=R&radius=0&centerLon=" + longitude + "&centerLat=" + latitude);
                            Runnable search = new SearchPOI(searchUrl);
                            Thread searchThread = new Thread(search);
                            searchThread.start();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    backBtn.setVisibility(View.VISIBLE);
                } else {

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
        tMapView.setTrackingMode(true);
        tMapView.setCompassMode(false);
        tMapView.setZoomLevel(17);
        tMap.addView(tMapView);


        tMapView.setLocationPoint(longitude, latitude);
        tMapView.setCenterPoint(longitude, latitude);

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

    private class SearchPOI implements Runnable {
        private URL url;
        // POI
        ArrayList<POILocation> searchResult = new ArrayList<>();

        SearchPOI(URL url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");

                if (huc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader streamReader = new InputStreamReader(huc.getInputStream());
                    BufferedReader buffered = new BufferedReader(streamReader);

                    while (true) {
                        String data = buffered.readLine();
                        if (data == null) {
                            break;
                        } else {
                            JSONArray result = new JSONObject(data).getJSONObject("searchPoiInfo").getJSONObject("pois").getJSONArray("poi");
                            for (int i = 0; i < result.length(); i++) {
                                String resultName = result.getJSONObject(i).getString("name");
                                double resultRadius = result.getJSONObject(i).getDouble("radius");
                                String resultLowerBizName = result.getJSONObject(i).getString("lowerBizName");
                                String resultFullAddressRoad = result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getString("fullAddressRoad");
                                String resultFullAddress = result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getString("fullAddressRoad");
                                double resultCenterLat = result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getDouble("centerLat");
                                double resultCenterLon = result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getDouble("centerLon");

                                POILocation poiLocation = new POILocation(resultName, resultRadius, resultLowerBizName, resultFullAddressRoad, resultFullAddress, resultCenterLat, resultCenterLon);
                                searchResult.add(poiLocation);
                                if (result.length() != 0) {
                                    getActivity().runOnUiThread((new Runnable() {
                                        @Override
                                        public void run() {
                                            showSearchResult(searchResult, result.length());
                                        }
                                    }));
                                }
                            }
                        }
                    }

                    buffered.close();
                    huc.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void showSearchResult(ArrayList<POILocation> searchResult, int size) {

        for (int i = 0; i < size; i++) {
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
                    compassMode = 1;
                    tMapView.setTrackingMode(false);
                    mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    searchResultScrollView.setVisibility(View.GONE);
                    directionBtn.setVisibility(View.VISIBLE);
                    zoomBtnLayout.setVisibility(View.VISIBLE);
                    destinationLatitude = searchResult.get(id).getLatitude();
                    destinationLongitude = searchResult.get(id).getLongitude();
                    selectedName = searchResult.get(id).getName();
                    selectedBizName = searchResult.get(id).getBizName();
                    selectedAddress = searchResult.get(id).getRoadAddress();

                    destinationName = searchResult.get(id).getName();
                    tMapView.setCenterPoint(destinationLongitude, destinationLatitude);

                    // 마커 생성
                    TMapMarkerItem markerItem = new TMapMarkerItem();

                    TMapPoint tMapPoint1 = new TMapPoint(destinationLatitude, destinationLongitude); // 검색 결과

                    // 마커 아이콘
                    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.navi_search_result_marker);

                    markerItem.setIcon(bitmap); // 마커 아이콘 지정
                    markerItem.setCanShowCallout(true);
                    markerItem.setCalloutTitle(destinationName);
                    markerItem.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
                    markerItem.setTMapPoint(tMapPoint1); // 마커의 좌표 지정
                    markerItem.setName(destinationName); // 마커의 타이틀 지정
                    tMapView.addMarkerItem("markerItem1", markerItem); // 지도에 마커 추가

                    getActivity().runOnUiThread((new Runnable() {

                        @Override
                        public void run() {
                            selectedSearchName.setText(selectedName);
                            selectedSearchBizName.setText(selectedBizName);
                            selectedSearchAddress.setText(selectedAddress);

                            searchSelectedLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    compassMode = 1;
                                    tMapView.setTrackingMode(false);
                                    tMapView.setCenterPoint(destinationLongitude, destinationLatitude);
                                }
                            });
                            searchSelectedLayout.setVisibility(View.VISIBLE);
                        }
                    }));

                }
            });

            searchResultNameLayout.setOrientation(LinearLayout.HORIZONTAL);
            searchResultAddressLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView resultName = new TextView(getContext());
            TextView resultRadius = new TextView(getContext());
            TextView resultBizName = new TextView(getContext());
            TextView resultRoadAddress = new TextView(getContext());

            try {
                resultName.setText(searchResult.get(i).getName());
                resultRadius.setText(" " + String.valueOf(searchResult.get(i).getRadius()));
                resultBizName.setText(" " + searchResult.get(i).getBizName());
                resultRoadAddress.setText(searchResult.get(i).getRoadAddress());
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "not found result");
            }

            searchResultNameLayout.addView(resultName);
            searchResultNameLayout.addView(resultBizName);
            searchResultAddressLayout.addView(resultRoadAddress);
            searchResultAddressLayout.addView(resultRadius);

            searchElementLayout.addView(searchResultNameLayout);
            searchElementLayout.addView(searchResultAddressLayout);

            searchResultLayout.addView(searchElementLayout);
        }
    }

    //add for find path
    private void drawPoly() {
        TMapPoint startPoint = new TMapPoint(latitude, longitude); //현재 위치
        TMapPoint endPoint = new TMapPoint(destinationLatitude, destinationLongitude); // (목적지)
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    TMapPolyLine tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint);
                    tMapPolyLine.setLineColor(Color.BLUE);
                    tMapPolyLine.setLineWidth(2);
                    tMapView.addTMapPolyLine("TestLine1", tMapPolyLine);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        thread.start();
    }

    private class GetPath implements Runnable {

        TMapPoint endPoint = new TMapPoint(destinationLatitude, destinationLongitude);
        TMapPoint startPint = new TMapPoint(latitude, longitude);

        @Override
        public void run() {
            tmapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPint, endPoint, new TMapData.FindPathDataAllListenerCallback() {
                @Override
                public void onFindPathDataAll(Document document) {
                    Element root = document.getDocumentElement();
                    NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                    for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                        NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                        for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
                            if (nodeListPlacemarkItem.item(j).getNodeName().equals("description")) {
                                Log.d("debug", nodeListPlacemarkItem.item(j).getTextContent().trim());
                            }
                        }
                    }
                }
            });
        }
    }

    //add for find fath
//    private void getPathDataXML(){
//        showPath.setVisibility(View.VISIBLE);
//        TMapPoint endPoint = new TMapPoint(destinationLatitude, destinationLongitude);
//        TMapPoint startPint = new TMapPoint(latitude, longitude);
//        StringBuilder navInfo = new StringBuilder("이동정보:\n");
//
//        new Thread(){
//            @Override
//            public void run() {
//                while (true){
//                    try {
//                        drawPoly();
//                        tmapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPint, endPoint, new TMapData.FindPathDataAllListenerCallback() {
//                            @Override
//                            public void onFindPathDataAll(Document document) {
//                                Element root = document.getDocumentElement();
//
//                                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
//
//                                for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
//                                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
//                                    for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
//                                        if (nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:distance")) {
//                                            Log.e("distance", nodeListPlacemarkItem.item(j).getTextContent().trim() + "미터");
//                                        }
//                                        if (nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:turntype")) {
//                                            Log.e("turntype", nodeListPlacemarkItem.item(j).getTextContent().trim() + "방향");
//                                        }
//                                        //description
//                                        if (nodeListPlacemarkItem.item(j).getNodeName().equals("description")) {
//                                            navInfo.append(nodeListPlacemarkItem.item(j).getTextContent().trim() + "\n");
//                                            Log.e("description", nodeListPlacemarkItem.item(j).getTextContent().trim());
//                                        }
//                                    }
//                                }
//                                Log.e("navInfo", "info: " + navInfo);
//                                getActivity().runOnUiThread((new Runnable(){
//
//                                    @Override
//                                    public void run() {
//                                        showPath.setText(navInfo);
//
//                                    }
//                                }));
//                            }
//                        });
//
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                    super.run();
//                }
//            }
//        }.start();
//        try {
//            Thread.sleep(1000);
//        } catch(InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

}