package com.app.smartkeyboard.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.app.smartkeyboard.listeners.LocationAreaListener;


import java.io.IOException;
import java.util.List;

import timber.log.Timber;

public class LocationUtils {


    private final double kaabaLat = 21.25683d;
    private final double kaabaLng = 39.493488d;


    //定位
    private static LocationManager locationManager;
    private static Context mContext;

    private LocationAreaListener locationAreaListener;

    public void startLocation(Context context, LocationAreaListener listener) {
        mContext = context;
        this.locationAreaListener = listener;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastLocation != null){
            Geocoder geocoder = new Geocoder(mContext);
            try {
                List<Address> addressList = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 5);
                if (addressList == null || addressList.size() == 0) {
                    return;
                }

                Address add = addressList.get(0);
                String cityName = add.getLocality();
                String cityArea = add.getSubLocality();
                String cityStr = cityName + cityArea;
                String province = add.getAdminArea();

                String resultStr = cityName + " " + province + " " + add.getCountryName();
                if (locationAreaListener != null) {
                    locationAreaListener.backCityStr(resultStr);
                    locationAreaListener.backLatLon(add.getLatitude(), add.getLongitude());
                }

                Timber.e("---------city=" + cityArea + " " + cityStr + " " + province);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0.1f, locationListener);
    }


    public void stopLocation() {
        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Timber.e("---localton=" + location.getBearing() + " " + location.getAccuracy() + "\n" + location.getLatitude() + "\n" + location.getLongitude() + "\n" + location.getExtras() + " " + location.getAltitude());

            Geocoder geocoder = new Geocoder(mContext);
            try {
                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                if (addressList == null || addressList.size() == 0) {
                    return;
                }

                Address add = addressList.get(0);
                String cityName = add.getLocality();
                String cityArea = add.getSubLocality();
                String cityStr = cityName + cityArea;
                String province = add.getAdminArea();

                String resultStr = cityName + " " + province + " " + add.getCountryName();
                if (locationAreaListener != null) {
                    locationAreaListener.backCityStr(resultStr);
                    locationAreaListener.backLatLon(add.getLatitude(), add.getLongitude());
                }

                Timber.e("---------city=" + cityArea + " " + cityStr + " " + province);
                stopLocation();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFlushComplete(int requestCode) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }
    };


    /**
     * distanceInMeters 通过经纬度获取距离(单位：米)
     *
     * @param pLatitude1  表示A点纬度
     * @param pLongitude1 表示A点经度
     * @param pLatitude2  表示B点纬度
     * @param pLongitud2  表示B点经度
     */
    public static double getDistances(double pLatitude1, double pLongitude1, double pLatitude2, double pLongitud2) {
        Location loc1 = new Location("");
        loc1.setLatitude(pLatitude1);
        loc1.setLongitude(pLongitude1);


        Location loc2 = new Location("");
        loc2.setLatitude(pLatitude2);
        loc2.setLongitude(pLongitud2);


        float direction = loc1.bearingTo(loc2);

        Timber.e("-----方向=" + direction);

        float distanceInMeters = loc1.distanceTo(loc2);    //单位：米
        float v = distanceInMeters / 1000;    //单位：千米
        double distance = Math.round(v * 100) / 100.0;    //保留两位小数
        return distance;
    }

    /**
     * distanceInMeters 通过经纬度获取距离(单位：米)
     *
     * @param pLatitude1  表示A点纬度
     * @param pLongitude1 表示A点经度
     * @param pLatitude2  表示B点纬度
     * @param pLongitud2  表示B点经度
     */
    public static double[] getDistancesAndDirection(double pLatitude1, double pLongitude1, double pLatitude2, double pLongitud2) {
        Location loc1 = new Location("");
        loc1.setLatitude(pLatitude1);
        loc1.setLongitude(pLongitude1);


        Location loc2 = new Location("");
        loc2.setLatitude(pLatitude2);
        loc2.setLongitude(pLongitud2);


        float direction = (float) calculateQiblaDirection(pLatitude1,pLongitude1);//loc1.bearingTo(loc2);

        Timber.e("-----方向=" + direction);

        float distanceInMeters = loc1.distanceTo(loc2);    //单位：米
        float v = distanceInMeters / 1000;    //单位：千米
        double distance = Math.round(v * 100) / 100.0;    //保留两位小数
        return new double[]{distance,direction};
    }


    public static double calculateQiblaDirection(double latitude, double longitude) {
        double meccaLatitude = 21.4225; // 麦加的纬度
        double meccaLongitude = 39.8262; // 麦加的经度

        double phiK = Math.toRadians(meccaLatitude);
        double lambdaK = Math.toRadians(meccaLongitude);
        double phi = Math.toRadians(latitude);
        double lambda = Math.toRadians(longitude);

        double deltaLambda = lambdaK - lambda;

        double y = Math.sin(deltaLambda);
        double x = Math.cos(phi) * Math.tan(phiK) - Math.sin(phi) * Math.cos(deltaLambda);

        double qiblaAngle = Math.atan2(y, x);
        qiblaAngle = Math.toDegrees(qiblaAngle);

        return (qiblaAngle + 360) % 360; // 将角度限制在 0 到 360 度之间
    }


    public static float getDirection(double sLan, double sLng, double eLan, double eLng) {
        Location loc1 = new Location("");
        loc1.setLatitude(sLan);
        loc1.setLongitude(sLng);


        Location loc2 = new Location("");
        loc2.setLatitude(eLan);
        loc2.setLongitude(eLng);
        float direction = loc1.bearingTo(loc2);
        return direction;
    }


    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * Lat1 Lung1 表示A点经纬度，Lat2 Lung2 表示B点经纬度； a=Lat1 – Lat2 为两点纬度之差 b=Lung1
     * -Lung2 为两点经度之差； 6378.137为地球半径，单位为千米；  计算出来的结果单位为千米。
     * 通过经纬度获取距离(单位：千米)
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        // s = s * 1000;    乘以1000是换算成米
//        s = Math.round(s/10d) /100d;//单位：千米 保留两位小数
//        s = Math.round(s / 100d) / 10d;//单位：千米 保留一位小数
        return s;
    }


    // 计算方位角pab。

    public static double gps2d(double lat_a, double lng_a, double lat_b, double lng_b) {

        double d = 0;

        lat_a = lat_a * Math.PI / 180;

        lng_a = lng_a * Math.PI / 180;

        lat_b = lat_b * Math.PI / 180;

        lng_b = lng_b * Math.PI / 180;

        d = Math.sin(lat_a) * Math.sin(lat_b) + Math.cos(lat_a) * Math.cos(lat_b) * Math.cos(lng_b - lng_a);

        d = Math.sqrt(1 - d * d);

        d = Math.cos(lat_b) * Math.sin(lng_b - lng_a) / d;

        d = Math.asin(d) * 180 / Math.PI;

//     d = Math.round(d*10000);

        return d;

    }


    public static double bearing(double lat1, double lon1, double lat2, double lon2){
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff = Math.toRadians(longitude2-longitude1);
        double y = Math.sin(longDiff)*Math.cos(latitude2);
        double x = Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);
        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }


}
