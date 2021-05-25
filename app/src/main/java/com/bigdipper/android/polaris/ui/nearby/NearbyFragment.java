package com.bigdipper.android.polaris.ui.nearby;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import android.os.Looper;
=======

import android.text.Editable;
import android.text.TextWatcher;


>>>>>>> Stashed changes
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bigdipper.android.polaris.R;
import com.bigdipper.android.polaris.entity.NavPath;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

<<<<<<< Updated upstream
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
=======
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes

import java.net.URL;
<<<<<<< Updated upstream
=======
import java.util.ArrayList;
import java.util.List;
>>>>>>> Stashed changes

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class NearbyFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback {

<<<<<<< Updated upstream
    String API_Key = "l7xx57fa48d037ad47f6bfdadc9ff4b5e33c";
=======
    //    static String API_Key = "l7xx57fa48d037ad47f6bfdadc9ff4b5e33c";
//    static String API_Key = "l7xx23e9a48d31d54d329e28dde4fce61161";
//    static String API_Key = "l7xx8587ca243f924b9996e7c5b8ea0a6075";
    static String API_Key = "l7xxf744614311fb4f578cb721f90eedc763";

>>>>>>> Stashed changes

    FrameLayout tMap;
    TMapView tMapView = null;
    TMapGpsManager tMapGPS = null;
    TMapPoint tMapPoint = null;

    double longitude, latitude;

<<<<<<< Updated upstream
    //add for find path
    Document doc = null;
    TMapData tmapdata;
    boolean navFlag = false;
    TextView txtTest;
=======
    //addd for find path
    boolean navFlag = false;
    double destinationLatitude, destinationLongitude;
<<<<<<< Updated upstream

=======
    TextView showPath;
    NodeList nodeListPlacemark; // placemark data from kml
    List<NavPath> navPaths;
>>>>>>> Stashed changes

    static double longitude, latitude;
>>>>>>> Stashed changes

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nearby, container, false);

<<<<<<< Updated upstream
=======
        // View
        searchText = root.findViewById(R.id.et_search);
        cancelBtn = root.findViewById(R.id.btn_nearby_cancel);
        backBtn = root.findViewById(R.id.btn_nearby_back);
        directionBtn = root.findViewById(R.id.btn_direction);

        // Dynamic View
        searchResultLayout = (LinearLayout) root.findViewById(R.id.searchList);

        //add for find path
        tmapData = new TMapData();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
                searchText.clearFocus();
                cancelBtn.setVisibility(View.GONE);
                directionBtn.setVisibility(View.VISIBLE);
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
                directionBtn.setVisibility(View.VISIBLE);
                InputMethodManager mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        directionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tMapView.setLocationPoint(longitude, latitude);
                tMapView.setCenterPoint(longitude, latitude);
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
                        getActivity().runOnUiThread((new Runnable() {
                            @Override
                            public void run() {
                                searchResultLayout.setVisibility(View.VISIBLE);
                                directionBtn.setVisibility(View.GONE);
                            }
                        }));
                        super.run();
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

>>>>>>> Stashed changes
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

        //add for find path
        tmapdata = new TMapData();

        tMap = (FrameLayout) root.findViewById(R.id.ll_tmap);

        tMapView = new TMapView(getActivity().getApplicationContext());

        tMapView.setSKTMapApiKey(API_Key);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setCompassMode(true);
        tMapView.setTrackingMode(true);
        tMap.addView(tMapView);

        final LocationListener mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    tMapView.setLocationPoint(longitude, latitude);
                    tMapView.setCenterPoint(longitude, latitude);
                    //add for find path
                    if(navFlag){
                        txtTest.setText("위치 변경");
                        getPathDataXML();
                    }
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

<<<<<<< Updated upstream
=======
    public class SearchPOI implements Runnable {
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

                    StringBuilder content = new StringBuilder();
                    while (true) {
                        String data = buffered.readLine();
                        if (data == null) {
                            break;
                        } else {
                            // result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getString("centerLat");
                            // result.getJSONObject(i).getJSONObject("newAddressList").getJSONArray("newAddress").getJSONObject(0).getString("centerLon");
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
                                getActivity().runOnUiThread((new Runnable() {

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
>>>>>>> Stashed changes

    //add for find path
    private void drawPoly(){
        TMapPoint startPoint = new TMapPoint(latitude, longitude); //현재 위치
        TMapPoint endPoint = new TMapPoint(36.0522751, 127.1354746); // (목적지)
        Log.e("draw poly", "" + latitude +" " +  longitude);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    TMapPolyLine tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint);
                    tMapPolyLine.setLineColor(Color.BLUE);
                    tMapPolyLine.setLineWidth(2);
                    tMapView.addTMapPolyLine("TestLine1", tMapPolyLine);
                    //POI lat,lon info
//                    Log.e("polyline", ""+tMapPolyLine.getLinePoint().toString());
                }
<<<<<<< Updated upstream
                catch (Exception e){
                    e.printStackTrace();;
                }
                super.run();
=======
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
>>>>>>> Stashed changes
            }
        };
        thread.start();
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }

<<<<<<< Updated upstream
<<<<<<< Updated upstream
    //add for find fath
    private void getPathDataXML(){
        TMapPoint endPoint = new TMapPoint(36.0522751, 127.1354746);
        Thread getPathThread = new Thread(){
            @Override
            public void run(){
                try {
                    drawPoly();
                    tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapView.getLocationPoint(), endPoint, new TMapData.FindPathDataAllListenerCallback() {
                        @Override
                        public void onFindPathDataAll(Document document) {
                            Element root = document.getDocumentElement();

                            NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                            StringBuilder navInfo = new StringBuilder("이동정보:\n");

                            for( int i=0; i<nodeListPlacemark.getLength(); i++ ) {
                                NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                                for( int j=0; j<nodeListPlacemarkItem.getLength(); j++ ) {
                                    if( nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:distance") ) {
                                        Log.e("distance", nodeListPlacemarkItem.item(j).getTextContent().trim() +"미터");
                                    }
                                    if( nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:turntype") ) {
                                        Log.e("turntype", nodeListPlacemarkItem.item(j).getTextContent().trim() +"방향");
                                    }
//                        description
                                    if( nodeListPlacemarkItem.item(j).getNodeName().equals("description") ) {
                                        navInfo.append(nodeListPlacemarkItem.item(j).getTextContent().trim() + "\n");
                                        Log.e("description", nodeListPlacemarkItem.item(j).getTextContent().trim() );
                                    }
                                }
                            }
                            Log.e("navInfo", "info: "+ navInfo);
                            txtTest.setText("lat " + Double.toString(latitude) + " \nlong " + Double.toString(longitude) + "\n" +navInfo);
                        }
                    });
=======
    private void showSearchResult(ArrayList<POILocation> searchResult, int size){

        for(int i = 0; i < size; i++){
=======
    private void showSearchResult(ArrayList<POILocation> searchResult, int size) {
        for (int i = 0; i < size; i++) {
>>>>>>> Stashed changes
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
                    directionBtn.setVisibility(View.VISIBLE);
                    destinationLatitude = searchResult.get(id).getLatitude();
                    destinationLongitude = searchResult.get(id).getLongitude();
<<<<<<< Updated upstream
                    tMapView.setLocationPoint(destinationLongitude, destinationLatitude);
                    tMapView.setCenterPoint(destinationLongitude, destinationLatitude);
                    getPathDataXML();
>>>>>>> Stashed changes
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        getPathThread.start();
        txtTest.setText(" ");
    }
<<<<<<< Updated upstream
=======

    private void drawPoly(){
        TMapPoint startPoint = new TMapPoint(latitude, longitude); //현재 위치
        TMapPoint endPoint = new TMapPoint(destinationLatitude, destinationLongitude); // (목적지)
        Log.e("draw poly", "" + latitude +" " +  longitude);
        Thread thread = new Thread(){
=======
//                    tMapView.setLocationPoint(destinationLongitude, destinationLatitude);
//                    tMapView.setCenterPoint(destinationLongitude, destinationLatitude);
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


            resultName.setText(searchResult.get(id).getName());
            resultRadius.setText(String.valueOf(searchResult.get(id).getRadius()));
            resultBizName.setText(searchResult.get(id).getBizName());
            resultRoadAddress.setText(searchResult.get(id).getRoadAddress());


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
    private void drawPoly(int index) {
        new Thread() {
>>>>>>> Stashed changes
            @Override
            public void run() {
                try {
                    TMapPolyLine tMapPolyLine = new TMapPolyLine();
                    tMapPolyLine.setLineColor(Color.BLUE);
                    tMapPolyLine.setLineWidth(2);
                    tMapPolyLine.addLinePoint(new TMapPoint(latitude, longitude));
                    for(int i = index; i<navPaths.size(); i+=2){
                        tMapPolyLine.addLinePoint(new TMapPoint(Double.parseDouble(navPaths.get(i).getLatitude()), Double.parseDouble(navPaths.get(i).getLongitude())));
                        Log.e("addLinePoint", "lat "+ navPaths.get(i).getLatitude() + " lon " + navPaths.get(i).getLongitude());
                    }
                    tMapView.addTMapPolyLine("path",tMapPolyLine);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.run();
            }
<<<<<<< Updated upstream
        };
        thread.start();
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }

    //add for find fath
    private void getPathDataXML(){
        TMapPoint endPoint = new TMapPoint(destinationLatitude, destinationLongitude);
        Thread getPathThread = new Thread(){
            @Override
            public void run(){
                try {
                    drawPoly();
=======
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
>>>>>>> Stashed changes
                    tmapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapView.getLocationPoint(), endPoint, new TMapData.FindPathDataAllListenerCallback() {
                        @Override
                        public void onFindPathDataAll(Document document) {
                            Element root = document.getDocumentElement();
<<<<<<< Updated upstream

                            NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                            StringBuilder navInfo = new StringBuilder("이동정보:\n");

                            for( int i=0; i<nodeListPlacemark.getLength(); i++ ) {
                                NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                                for( int j=0; j<nodeListPlacemarkItem.getLength(); j++ ) {
                                    if( nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:distance") ) {
                                        Log.e("distance", nodeListPlacemarkItem.item(j).getTextContent().trim() +"미터");
                                    }
                                    if( nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:turntype") ) {
                                        Log.e("turntype", nodeListPlacemarkItem.item(j).getTextContent().trim() +"방향");
                                    }
//                        description
                                    if( nodeListPlacemarkItem.item(j).getNodeName().equals("description") ) {
                                        navInfo.append(nodeListPlacemarkItem.item(j).getTextContent().trim() + "\n");
                                        Log.e("description", nodeListPlacemarkItem.item(j).getTextContent().trim() );
                                    }
                                }
                            }
                            Log.e("navInfo", "info: "+ navInfo);
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        getPathThread.start();
    }

>>>>>>> Stashed changes
=======
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
                        drawPoly(index);
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
>>>>>>> Stashed changes
}