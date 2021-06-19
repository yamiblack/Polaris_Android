package com.bigdipper.android.polaris.ui.nearby;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bigdipper.android.polaris.MessageConsumer;
import com.bigdipper.android.polaris.entity.POILocation;
import com.bigdipper.android.polaris.R;
import com.bigdipper.android.polaris.entity.NavPath;
import com.bigdipper.android.polaris.ui.NavigationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.samsung.android.sdk.accessory.SAAgentV2;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.jetbrains.annotations.NotNull;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback {

    private static final String TAG = "NearbyFragment";
    private static String API_Key;

    private FrameLayout tMap;
    private TMapView tMapView = null;
    private TMapData tmapData;

    // Value
    private int compassMode = 0; // 0: 현재위치, 1: 현재위치&나침반&시야각
    private String selectedName;
    private String selectedBizName;
    private String selectedAddress;

    // View
    private EditText searchText;
    private TextView selectedSearchName;
    private TextView selectedSearchBizName;
    private TextView selectedSearchAddress;
    private Button cancelBtn;
    private Button backBtn;
    private Button directionBtn;
    private Button zoomInBtn;
    private Button zoomOutBtn;
    private Button startNaviBtn;
    private ImageView ivFavorite;
    private LinearLayout zoomBtnLayout;
    private LinearLayout searchSelectedLayout;
    private ScrollView searchResultScrollView;

    // Dynamic View
    private LinearLayout searchResultLayout;

    // add for find path
    private static double destinationLatitude, destinationLongitude;
    private String destinationName;
    private TextView showPath;
    private NodeList nodeListPlacemark; // placemark data from kml
    private List<NavPath> navPaths;

    // Keyboard
    private InputMethodManager mInputMethodManager;

    // for draw path by kim
    private List<TMapPoint> drawPolyList;
    private int drawPathCount = 0;

    private static double longitude, latitude;

    // add for watch Connection
    private MessageConsumer mMessageConsumer = null;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String email;
    private Boolean isFavorite = false;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        API_Key = getResources().getString(R.string.tmap_app_key_sub_1);
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
        ivFavorite = root.findViewById(R.id.iv_favorite);
        zoomBtnLayout = root.findViewById(R.id.btn_zoom_layout);
        searchSelectedLayout = root.findViewById(R.id.layout_search_selected);
        searchResultScrollView = root.findViewById(R.id.sv_search_result);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        // for draw path by kim
        drawPolyList = new ArrayList<>();

        // showPath = root.findViewById(R.id.tv_show_path);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        //add for watch Connection
        initConnection();

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
                new Thread() {
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

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite();
            }
        });

        startNaviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NavigationActivity.class);

                intent.putExtra("destLati", String.valueOf(destinationLatitude));
                intent.putExtra("destLong", String.valueOf(destinationLongitude));
                intent.putExtra("lati", String.valueOf(latitude));
                intent.putExtra("long", String.valueOf(longitude));
                startActivity(intent);
//                new Thread() {
//                    @Override
//                    public void run() {
//                        getPathDataXML();
//                    }
//                }.start();

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
                    getFavorite();

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

    //change for drawPoly by kim
    private void drawPoly() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    Log.e("drawPoly 호출됨 ", "ㅇㅇㅇ");
                    try {
                        if (drawPolyList.isEmpty()) {
                            TMapPolyLine tMapPolyLineData = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, new TMapPoint(latitude, longitude), new TMapPoint(destinationLatitude, destinationLongitude));
                            for (int i = 0; i < tMapPolyLineData.getLinePoint().size(); i++) {
                                Log.e("drawPolyAdd", "add " + i);
                                Log.e("drawPolyAdd", " " + tMapPolyLineData.getLinePoint().get(i).getLatitude() + " " + tMapPolyLineData.getLinePoint().get(i).getLongitude());
                                drawPolyList.add(new TMapPoint(tMapPolyLineData.getLinePoint().get(i).getLatitude(), tMapPolyLineData.getLinePoint().get(i).getLongitude()));
                            }
                        }
                        Log.e("drawPoly nousage", " " + drawPathCount);
                        TMapPolyLine tMapPolyLine = new TMapPolyLine();
                        tMapPolyLine.setLineColor(Color.BLUE);
                        tMapPolyLine.setLineWidth(2);
                        if (distance(latitude, longitude, drawPolyList.get(drawPathCount).getLatitude(), drawPolyList.get(drawPathCount).getLongitude()) * 1000 == 2) {
                            drawPathCount++;
                        }
                        tMapPolyLine.addLinePoint(new TMapPoint(latitude, longitude));
                        for (int i = drawPathCount; i < drawPolyList.size() - 1; i++) {
                            tMapPolyLine.addLinePoint(new TMapPoint(drawPolyList.get(i).getLatitude(), drawPolyList.get(i).getLongitude()));
                        }
                        tMapView.addTMapPolyLine("path", tMapPolyLine);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    super.run();
                }
            }
        }.start();
    }

    //add for find path
    //if choose poi -> call this
    private void getPathDataXML() {
        TMapPoint endPoint = new TMapPoint(destinationLatitude, destinationLongitude);
        drawPolyList.clear(); // 경로 삭제
        drawPathCount = 0;
        drawPoly();

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
        navPaths = new ArrayList<>();
        try {
            for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                String index = null, pathLongitude = null, pathLatitude = null, turntype = null;
                for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {

//                    Log.e("datas", ""+nodeListPlacemarkItem.item(j).getNodeName().trim() + " "+nodeListPlacemarkItem.item(j).getTextContent().trim());

                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:index")) {
                        index = nodeListPlacemarkItem.item(j).getTextContent().trim();
                    }
                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("LineString")) {
                        String tmp = nodeListPlacemarkItem.item(j).getTextContent().trim();
                        int findSpace = tmp.indexOf(' ');
                        if (findSpace == -1) { //1개만 존재
                            int findComma = tmp.indexOf(',');
                            pathLongitude = tmp.substring(0, findComma);
                            pathLatitude = tmp.substring(findComma + 1);
                        } else {
                            String firstPosition = tmp.substring(0, findSpace);
                            int findComma = firstPosition.indexOf(',');
                            pathLongitude = firstPosition.substring(0, findComma);
                            pathLatitude = firstPosition.substring(findComma + 1);
                        }
                    }
                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                        String tmp = nodeListPlacemarkItem.item(j).getTextContent().trim();
                        int findSpace = tmp.indexOf(' ');
                        if (findSpace == -1) { //1개만 존재
                            int findComma = tmp.indexOf(',');
                            pathLongitude = tmp.substring(0, findComma);
                            pathLatitude = tmp.substring(findComma + 1);
                        } else {
                            String firstPosition = tmp.substring(0, findSpace);
                            int findComma = firstPosition.indexOf(',');
                            pathLongitude = firstPosition.substring(0, findComma);
                            pathLatitude = firstPosition.substring(findComma + 1);
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
                Log.e("nvaleng", "" + navPaths.size());
                while (index + 2 < navPaths.size()) {
                    TMapPoint curPoint = new TMapPoint(latitude, longitude);
                    double curNavDistance = 0;
                    try {
                        curNavDistance = distance(latitude, longitude, Double.parseDouble(navPaths.get(index).getLatitude()), Double.parseDouble(navPaths.get(index).getLongitude())) * 1000;
                        if (curNavDistance < 2) {
                            index += 2;
                        }
                        Log.e("navigation", "목적지 까지 남은 거리: " + (int) curNavDistance + " 현재 방향: " + navPaths.get(index).getTurnType());
                        Log.e("navigation", "다음 방향: " + navPaths.get(index + 2).getTurnType());
                        //add for watch Connection
                        try {
                            mMessageConsumer.sendData("nav/" + Integer.toString((int) curNavDistance) + "m/" + navPaths.get(index + 2).getTurnType());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
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


    //add for watch Connection
    private SAAgentV2.RequestAgentCallback mAgentCallback2 = new SAAgentV2.RequestAgentCallback() {
        @Override
        public void onAgentAvailable(SAAgentV2 agent) {
            mMessageConsumer = (MessageConsumer) agent;
        }

        @Override
        public void onError(int errorCode, String message) {
            Log.e(TAG, "Agent initialization error: " + errorCode + ". ErrorMsg: " + message);
        }
    };

    //add for watch Connection
    private void initConnection() {
        Log.e("연결연결1", "연결연결1");
        SAAgentV2.requestAgent(getActivity().getApplicationContext(), MessageConsumer.class.getName(), mAgentCallback2);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (mMessageConsumer != null) {
                        mMessageConsumer.findPeers();
                        Log.e("연결연결2", "연결연결2");
                        break;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    super.run();
                }
            }
        }.start();
    }

    public void setFavorite() {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("searchName", selectedName);
        data.put("businessName", selectedBizName);
        data.put("address", selectedAddress);
        data.put("latitude", String.valueOf(destinationLatitude));
        data.put("longitude", String.valueOf(destinationLongitude));

        db.collection("FAVORITE").document(email + selectedName)
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getFavorite();

                if (isFavorite) {
                    deleteFavorite();
                } else {
                    Toast.makeText(getContext(), "즐겨찾기에 추가됐습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getFavorite() {
        db.collection("FAVORITE").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.get("email").toString().equals(email) && documentSnapshot.get("searchName").equals(selectedName)) {
                            ivFavorite.setImageResource(R.drawable.ic_favorite_select);
                            isFavorite = true;
                        } else {
                            ivFavorite.setImageResource(R.drawable.ic_favorite_unselect);
                            isFavorite = false;
                        }
                    }
                }
            }
        });
    }

    public void deleteFavorite() {
        db.collection("FAVORITE").document(email + selectedName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "즐겨찾기가 히제됐습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

}