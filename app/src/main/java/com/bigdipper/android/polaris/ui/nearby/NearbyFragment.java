package com.bigdipper.android.polaris.ui.nearby;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
<<<<<<< Updated upstream
import android.os.Build;
import android.os.Bundle;
=======
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
>>>>>>> Stashed changes
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< Updated upstream
=======
import android.widget.Button;
>>>>>>> Stashed changes
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
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
<<<<<<< Updated upstream
import com.skt.Tmap.TMapView;

public class NearbyFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback {

=======
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class NearbyFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback {

>>>>>>> Stashed changes
    String API_Key = "l7xx57fa48d037ad47f6bfdadc9ff4b5e33c";

    FrameLayout tMap;
    TMapView tMapView = null;
    TMapGpsManager tMapGPS = null;
    TMapPoint tMapPoint = null;

    double longitude, latitude;
<<<<<<< Updated upstream
=======

    //add for find path
    Document doc = null;
    TMapData tmapdata;
    boolean navFlag = false;
    TextView txtTest;
>>>>>>> Stashed changes

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nearby, container, false);

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
                catch (Exception e){
                    e.printStackTrace();;
                }
                super.run();
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
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        getPathThread.start();
        txtTest.setText(" ");
    }
>>>>>>> Stashed changes
}