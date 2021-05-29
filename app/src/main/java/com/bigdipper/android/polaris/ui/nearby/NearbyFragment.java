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
import android.view.Gravity;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bigdipper.android.polaris.entity.POILocation;
import com.bigdipper.android.polaris.R;
import com.bigdipper.android.polaris.entity.NavPath;
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
import java.util.List;

public class NearbyFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback {

    private static final String TAG = "NearbyFragment";

    static String API_Key;

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
    double destinationLatitude, destinationLongitude;
    String destinationName;
    TextView showPath;
    NodeList nodeListPlacemark; // placemark data from kml
    List<NavPath> navPaths;

    // Keyboard
    InputMethodManager mInputMethodManager;

    static double longitude, latitude;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        API_Key = getResources().getString(R.string.API_KEY_SUB_3);
    }

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
//                searchText.clearFocus();
                cancelBtn.setVisibility(View.GONE);
                directionBtn.setVisibility(View.VISIBLE);
                zoomBtnLayout.setVisibility(View.VISIBLE);
                searchResultScrollView.setVisibility(View.GONE);
                tMapView.removeAllMarkerItem();
                searchResultLayout.removeAllViews();
//                new Thread(){
//                    @Override
//                    public void run() {
//                        mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                    }
//                }.start();
//                selectedSearchName.setText("");
//                selectedSearchBizName.setText("");
//                selectedSearchAddress.setText("");
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
                        getPathDataXML();
                    }
                }.start();
            }
        });


        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                searchResultLayout.removeAllViews();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchResultLayout.removeAllViews();
                searchResultScrollView.setVisibility(View.VISIBLE);
                directionBtn.setVisibility(View.GONE);
                zoomBtnLayout.setVisibility(View.GONE);
                searchSelectedLayout.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.VISIBLE);
                String searchKeyword = s.toString();
                try {
                    URL searchUrl = new URL("https://apis.openapi.sk.com/tmap/pois?appKey=" + API_Key + "&version=1&searchKeyword=" + searchKeyword + "&searchtypCd=R&radius=0&centerLon=" + longitude + "&centerLat=" + latitude);
                    Runnable search = new SearchPOI(searchUrl);
                    Thread searchThread = new Thread(search);
                    searchThread.start();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
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
//                    getActivity().runOnUiThread((new Runnable() {
//
//                        @Override
//                        public void run() {
//
//                        }
//                    }));

                }
            });

            searchResultNameLayout.setOrientation(LinearLayout.HORIZONTAL);
            searchResultAddressLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView resultName = new TextView(getContext());
            TextView resultRadius = new TextView(getContext());
            TextView resultBizName = new TextView(getContext());
            TextView resultRoadAddress = new TextView(getContext());

            resultName.setTextColor(Color.BLACK);
            resultName.setTextSize(15);
            resultBizName.setGravity(Gravity.RIGHT);
            resultBizName.setTextColor(ContextCompat.getColor(getContext(), R.color.biz_radius_color));
            resultRadius.setGravity(Gravity.RIGHT);
            resultRadius.setTextColor(ContextCompat.getColor(getContext(), R.color.biz_radius_color));

            searchResultNameLayout.addView(resultName);
            searchResultNameLayout.addView(resultBizName);
            searchResultAddressLayout.addView(resultRoadAddress);
            searchResultAddressLayout.addView(resultRadius);

            searchElementLayout.addView(searchResultNameLayout);
            searchElementLayout.addView(searchResultAddressLayout);

            searchResultLayout.addView(searchElementLayout);

            try {
                resultName.setText(searchResult.get(i).getName());
                resultRadius.setText(String.valueOf(searchResult.get(i).getRadius()));
                resultBizName.setText(" " + searchResult.get(i).getBizName());
                resultRoadAddress.setText(searchResult.get(i).getRoadAddress());
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "not found result");
                continue;
            }
        }
    }

    //add for find path
    private void drawPoly() {
        new Thread() {
            @Override
            public void run() {
                try {
                    TMapPolyLine tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, new TMapPoint(latitude, longitude), new TMapPoint(destinationLatitude, destinationLongitude));

                    tMapPolyLine.setLineColor(Color.BLUE);
                    tMapPolyLine.setLineWidth(2);
//                    tMapPolyLine.addLinePoint(new TMapPoint(latitude, longitude));
//                    for(int i = 0; i<navPaths.size(); i++){
//                        tMapPolyLine.addLinePoint(new TMapPoint(Double.parseDouble(navPaths.get(i).getLatitude()), Double.parseDouble(navPaths.get(i).getLongitude())));
//                        Log.e("addLinePoint", "lat "+ navPaths.get(i).getLatitude() + " lon " + navPaths.get(i).getLongitude());
//                    }
                    tMapView.addTMapPolyLine("path", tMapPolyLine);

                    Log.e("pathPoints_getPAss", "" + tMapPolyLine.getPassPoint().size());
                    for(int i = 0; i<tMapPolyLine.getPassPoint().size(); i++){
                        Log.e("pathPoints_Pass", "lat "+tMapPolyLine.getPassPoint().get(i).getLatitude() + " lon " + tMapPolyLine.getPassPoint().get(i).getLongitude());
                    }

                    Log.e("pathPoints_getPAss", "" + tMapPolyLine.getLinePoint().size());
                    for(int i = 0; i<tMapPolyLine.getLinePoint().size(); i++){
                        Log.e("pathPoints_Line", "lat "+tMapPolyLine.getLinePoint().get(i).getLatitude() + " lon " + tMapPolyLine.getLinePoint().get(i).getLongitude());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
    }

    //add for find path
    //if choose poi -> call this
    private void getPathDataXML() {
        TMapPoint endPoint = new TMapPoint(destinationLatitude, destinationLongitude);
        new Thread() {
            @Override
            public void run() {
                try {
                    tmapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapView.getLocationPoint(), endPoint, new TMapData.FindPathDataAllListenerCallback() {
                        @Override
                        public void onFindPathDataAll(Document document) {
                            Element root = document.getDocumentElement();
                            nodeListPlacemark = root.getElementsByTagName("Placemark");  //get placemarks in kml
                            Log.e("getPAthDataXML", "호출됨");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    while (true) {
                        if (nodeListPlacemark != null) {
                            navigatePath();
                            break;
                        }
                    }
                }
                super.run();
            }
        }.start();
    }

    //add for find path
    private void navigatePath() {
        drawPoly();
        navPaths = new ArrayList<>();
        try {
            for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                String index = null, pathLongitude = null, pathLatitude = null, turntype =null;
                for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {

//                    Log.e("datas", ""+nodeListPlacemarkItem.item(j).getNodeName().trim() + " "+nodeListPlacemarkItem.item(j).getTextContent().trim());

                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:index")) {
                        index = nodeListPlacemarkItem.item(j).getTextContent().trim();
                    }
                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("LineString")) {
                        String tmp = nodeListPlacemarkItem.item(j).getTextContent().trim();
                        int findSpace = tmp.indexOf(' ');
                        if(findSpace == -1){ //1개만 존재
                            int findComma = tmp.indexOf(',');
                            pathLongitude = tmp.substring(0, findComma);
                            pathLatitude = tmp.substring(findComma+1);
                        }
                        else{
                            String firstPosition = tmp.substring(0, findSpace);
                            int findComma = firstPosition.indexOf(',');
                            pathLongitude = firstPosition.substring(0, findComma);
                            pathLatitude = firstPosition.substring(findComma+1);
                        }
                    }
                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                        String tmp = nodeListPlacemarkItem.item(j).getTextContent().trim();
                        int findSpace = tmp.indexOf(' ');
                        if(findSpace == -1){ //1개만 존재
                            int findComma = tmp.indexOf(',');
                            pathLongitude = tmp.substring(0, findComma);
                            pathLatitude = tmp.substring(findComma+1);
                        }
                        else{
                            String firstPosition = tmp.substring(0, findSpace);
                            int findComma = firstPosition.indexOf(',');
                            pathLongitude = firstPosition.substring(0, findComma);
                            pathLatitude = firstPosition.substring(findComma+1);
                        }
                    }
                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:turnType")) {
                        turntype = nodeListPlacemarkItem.item(j).getTextContent().trim();
                    }
                }
                Log.e("navPathData", "index " + index + "  lat " + pathLatitude + " lon " + pathLongitude + " turn " + turntype);
                navPaths.add(new NavPath(index, pathLatitude, pathLongitude, turntype));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {
                int index = 0;
                Log.e("nvaleng", ""+navPaths.size());
                while (index+2 < navPaths.size()) {
                    TMapPoint curPoint = new TMapPoint(latitude, longitude);
                    double curNavDistance = 0;
                    try {
                        curNavDistance = distance(latitude, longitude, Double.parseDouble(navPaths.get(index).getLatitude()), Double.parseDouble(navPaths.get(index).getLongitude())) * 1000;
                        if(curNavDistance < 2)
                            index+=2;
//                        drawPoly();
                        Log.e("navigation", "목적지 까지 남은 거리: " + (int)curNavDistance + " 현재 방향: " + navPaths.get(index).getTurnType());
                        Log.e("navigation", "다음 방향: " + navPaths.get(index+2).getTurnType());
                        index+=2;
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }
    private double rad2deg(double rad) {

        return (rad * 180.0 / Math.PI);

    }
    private double deg2rad(double deg) {

        return (deg * Math.PI / 180.0);

    }

}