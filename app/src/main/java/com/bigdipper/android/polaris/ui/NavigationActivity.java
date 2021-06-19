package com.bigdipper.android.polaris.ui;

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
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bigdipper.android.polaris.MessageConsumer;
import com.bigdipper.android.polaris.R;
import com.bigdipper.android.polaris.entity.NavPath;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.samsung.android.sdk.accessory.SAAgentV2;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private static final String TAG = "MessageActivity(C)";
    private Context context = this;
    private static String API_Key;
    private LinearLayout tMap;
    private TMapView tMapView = null;
    private TMapData tMapData;

    private static double longitude, latitude;

    private MessageConsumer messageConsumer = null;

    private List<TMapPoint> drawPolyList;
    private int drawPathCount = 0;
    private static double destinationLatitude, destinationLongitude;
    private NodeList nodeListPlacemark; // placemark data from kml
    private List<NavPath> navPaths;
    private Intent intent;
    private String destinationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        connectGalaxyWatch();

        API_Key = getResources().getString(R.string.tmap_app_key_sub_1);

        tMap = (LinearLayout) findViewById(R.id.ll_tmap);

        tMapView = new TMapView(getApplicationContext());

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

        tMapData = new TMapData();
        drawPolyList = new ArrayList<>();

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

        final LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000,
                1,
                mLocationListener);

        intent = getIntent();
        destinationName = intent.getStringExtra("destName");
        destinationLatitude = Double.parseDouble(intent.getStringExtra("destLati"));
        destinationLongitude = Double.parseDouble(intent.getStringExtra("destLong"));

        TMapMarkerItem markerItem = new TMapMarkerItem();
        TMapPoint tMapPoint = new TMapPoint(destinationLatitude, destinationLongitude);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.navi_search_result_marker);

        markerItem.setIcon(bitmap);
        markerItem.setCanShowCallout(true);
        markerItem.setCalloutTitle(destinationName);
        markerItem.setPosition(0.5f, 1.0f);
        markerItem.setTMapPoint(tMapPoint);
        markerItem.setName(destinationName);
        tMapView.addMarkerItem("markerItem", markerItem);

        Log.e("lat", String.valueOf(latitude));
        Log.e("long", String.valueOf(longitude));
        Log.e("destLat", String.valueOf(destinationLatitude));
        Log.e("destLon", String.valueOf(destinationLongitude));

        getPathDataXML();
    }

    @Override
    public void onLocationChange(Location location) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
    }

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

    private void getPathDataXML() {
        TMapPoint endPoint = new TMapPoint(destinationLatitude, destinationLongitude);
        drawPolyList.clear();
        drawPathCount = 0;
        drawPoly();

        new Thread() {
            @Override
            public void run() {
                try {
                    tMapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapView.getLocationPoint(), endPoint, new TMapData.FindPathDataAllListenerCallback() {
                        @Override
                        public void onFindPathDataAll(Document document) {
                            Element root = document.getDocumentElement();
                            nodeListPlacemark = root.getElementsByTagName("Placemark");  //get placemarks in kml
                            Log.e("getPathDataXML", "호출됨");
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
                            if(navPaths.get(index+2).getTurnType().equals("201")){
                                Toast.makeText(NavigationActivity.this, "안내종료", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            index += 2;
                        }
                        Log.e("navigation", "목적지 까지 남은 거리: " + (int) curNavDistance + " 현재 방향: " + navPaths.get(index).getTurnType());
                        Log.e("navigation", "다음 방향: " + navPaths.get(index + 2).getTurnType());
                        //add for watch Connection
                        try {
                            messageConsumer.sendData("nav/" + Integer.toString((int) curNavDistance) + "m/" + navPaths.get(index + 2).getTurnType());
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

    private SAAgentV2.RequestAgentCallback agentCallback = new SAAgentV2.RequestAgentCallback() {
        @Override
        public void onAgentAvailable(SAAgentV2 agent) {
            messageConsumer = (MessageConsumer) agent;
        }

        @Override
        public void onError(int errorCode, String message) {
            Log.e(TAG, "Agent initialization error: " + errorCode + ". ErrorMsg: " + message);
        }
    };

    private void connectGalaxyWatch() {
        Log.e("connection1", "success");
        SAAgentV2.requestAgent(getApplicationContext(), MessageConsumer.class.getName(), agentCallback);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (messageConsumer != null) {
                        messageConsumer.findPeers();
                        Log.e("connection2", "success");
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

}
