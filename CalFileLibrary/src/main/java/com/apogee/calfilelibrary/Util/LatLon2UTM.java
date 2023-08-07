package com.apogee.calfilelibrary.Util;

import android.content.Context;


import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class LatLon2UTM {
    private final double a;
    private final double e;
    private final double esq;
    private final double e0sq;
    final char[] digraphArrayN;
    public LatLon2UTM(Context context) {
        double equatorialRadius = 6378137;
        double flattening = 298.2572235630;
        a = equatorialRadius;
        double f = 1 / flattening;
        double b = a * (1 - f);   // polar radius
        e = Math.sqrt(1 - Math.pow(b, 2) / Math.pow(a, 2));
        esq = (1 - (b / a) * (b / a));
        e0sq = e * e / (1 - Math.pow(e, 2));
        digraphArrayN = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        };
    }
    private static final double SCALE_FACTOR = 0.9996;
    private static final double MAJOR_RADIUS = 6378137.0;
    private static final double MINOR_RADIUS = 6356752.314;
    /*LatLong conversion DDMM to degree*/
    public String latlngcnvrsn(String lat , String lon){
        String latitude1 = lat;
        int length = latitude1.length();
        String firsthalf = latitude1.substring(0, 2);
        String secondhalf = latitude1.substring(2, length);
        double secondhalfff = Double.parseDouble(secondhalf);
        double getval = secondhalfff/60;
        double finalval = Double.parseDouble(firsthalf)+getval;
        String finalString = String.valueOf(finalval);
        String longitude1 = lon;
        int length2 = longitude1.length();
        String firsthalf2 = longitude1.substring(0, 3);
        String secondhalf2 = longitude1.substring(3, length2);
        double secondhalfff2 = Double.parseDouble(secondhalf2);
        double getval2 = secondhalfff2/60;
        double finalval2 = Double.parseDouble(firsthalf2)+getval2;
        String finalString2 = String.valueOf(finalval2);
        return finalString+"_"+finalString2;
    }
    public List<Double> degreeToUTM2(double latitude, double longitude) {
        List<Double> northEastList = new ArrayList<Double>();
        try {

            int force_zone_number = 0;
            String force_zone_letter = "";

            double Zone;
            char Letter;

            Zone = (double) Math.floor(longitude / 6 + 31);

            double K0 = 0.9996;

            double E = 0.00669438;
            double E2 = E * E;
            double E3 = E2 * E;
            double E_P2 = E / (1 - E);

            double SQRT_E = Math.sqrt(1 - E);
            double _E = (1 - SQRT_E) / (1 + SQRT_E);
            double _E2 = _E * _E;
            double _E3 = _E2 * _E;
            double _E4 = _E3 * _E;
            double _E5 = _E4 * _E;

            double M1 = (1 - E / 4 - 3 * E2 / 64 - 5 * E3 / 256);
            double M2 = (3 * E / 8 + 3 * E2 / 32 + 45 * E3 / 1024);
            double M3 = (15 * E2 / 256 + 45 * E3 / 1024);
            double M4 = (35 * E3 / 3072);

            double P2 = (3 / 2 * _E - 27 / 32 * _E3 + 269 / 512 * _E5);
            double P3 = (21 / 16 * _E2 - 55 / 32 * _E4);
            double P4 = (151 / 96 * _E3 - 417 / 128 * _E5);
            double P5 = (1097 / 512 * _E4);

            double R = 6378137;

            String ZONE_LETTERS = "CDEFGHJKLMNPQRSTUVWXX";
            double lat_rad = Math.toRadians(latitude);
            double lat_sin = Math.sin(lat_rad);
            double lat_cos = Math.cos(lat_rad);

            double lat_tan = lat_sin / lat_cos;
            double lat_tan2 = lat_tan * lat_tan;
            double lat_tan4 = lat_tan2 * lat_tan2;
            double lon_rad = Math.toRadians(longitude);
            double central_lon = zone_number_to_central_longitude(Zone);
            double central_lon_rad = Math.toRadians(central_lon);

            double n = R / Math.sqrt(1 - E * Math.pow(lat_sin, 2));
            double c = E_P2 * Math.pow(lat_cos, 2);

            double a = lat_cos * mod_angle(lon_rad - central_lon_rad);
            double a2 = a * a;
            double a3 = a2 * a;
            double a4 = a3 * a;
            double a5 = a4 * a;
            double a6 = a5 * a;

            double m = R * (M1 * lat_rad
                    - M2 * Math.sin(2 * lat_rad)
                    + M3 * Math.sin(4 * lat_rad)
                    - M4 * Math.sin(6 * lat_rad));

            double easting = K0 * n * (a
                    + a3 / 6 * (1 - lat_tan2 + c)
                    + a5 / 120 * (5 - 18 * lat_tan2 + lat_tan4 + 72 * c - 58 * E_P2)) + 500000;

            double northing = K0 * (m + n * lat_tan * (a2 / 2 + a4 / 24 * (5 - lat_tan2 + 9 * c + 4 * Math.pow(c, 2))
                    + a6 / 720 * (61 - 58 * lat_tan2 + lat_tan4 + 600 * c - 330 * E_P2)));

            northEastList.add(easting);
            northEastList.add(northing);
            northEastList.add(Zone);

        } catch (Exception e) {
            System.out.println("Model.WebServiceModel.degreeToUTM2()- " + e);
        }
        return northEastList;
    }
    public Double zone_number_to_central_longitude(double zone_number) {
        double result = 0.0;
        try {
            result = (zone_number - 1) * 6 - 180 + 3;
        } catch (Exception e) {
            System.out.println("Model.WebServiceModel.zone_number_to_central_longitude()- " + e);
        }
        return result;
    }
    public Double mod_angle(double value) {
        double result = 0.0;
        try {
            result = (value + Math.PI) % (2 * Math.PI) - Math.PI;
        } catch (Exception e) {
            System.out.println("Model.WebServiceModel.mod_angle()- " + e);
        }
        return result;
    }
    public LatLng convertToLatLng(double x, double y, int zone, boolean southhemi) {

        x -= 500000.0;
        x /= SCALE_FACTOR;

        /* If in southern hemisphere, adjust y accordingly. */
        if (southhemi) {
            y -= 10000000.0;
        }

        y /= SCALE_FACTOR;

        double cmeridian = getCentralMeridian(zone);
        return mapPointToLatLng(x, y, cmeridian);
    }
    public LatLng mapPointToLatLng(double x, double y, double lambda0) {

        double phif = getFootpointLatitude(y);
        double ep2 = (Math.pow(MAJOR_RADIUS, 2.0) - Math.pow(MINOR_RADIUS, 2.0))
                / Math.pow(MINOR_RADIUS, 2.0);
        double cf = Math.cos(phif);
        double nuf2 = ep2 * Math.pow(cf, 2.0);
        double Nf = Math.pow(MAJOR_RADIUS, 2.0) / (MINOR_RADIUS * Math.sqrt(1 + nuf2));
        double Nfpow = Nf;
        double tf = Math.tan(phif);
        double tf2 = tf * tf;
        double tf4 = tf2 * tf2;
        double x1frac = 1.0 / (Nfpow * cf);

        Nfpow *= Nf;
        double x2frac = tf / (2.0 * Nfpow);
        Nfpow *= Nf;
        double x3frac = 1.0 / (6.0 * Nfpow * cf);
        Nfpow *= Nf;
        double x4frac = tf / (24.0 * Nfpow);
        Nfpow *= Nf;
        double x5frac = 1.0 / (120.0 * Nfpow * cf);
        Nfpow *= Nf;
        double x6frac = tf / (720.0 * Nfpow);
        Nfpow *= Nf;
        double x7frac = 1.0 / (5040.0 * Nfpow * cf);
        Nfpow *= Nf;
        double x8frac = tf / (40320.0 * Nfpow);
        double x2poly = -1.0 - nuf2;
        double x3poly = -1.0 - 2 * tf2 - nuf2;
        double x4poly = 5.0 + 3.0 * tf2 + 6.0 * nuf2 - 6.0 * tf2 * nuf2
                - 3.0 * (nuf2 * nuf2) - 9.0 * tf2 * (nuf2 * nuf2);
        double x5poly = 5.0 + 28.0 * tf2 + 24.0 * tf4 + 6.0 * nuf2 + 8.0 * tf2 * nuf2;
        double x6poly = -61.0 - 90.0 * tf2 - 45.0 * tf4 - 107.0 * nuf2
                + 162.0 * tf2 * nuf2;
        double x7poly = -61.0 - 662.0 * tf2 - 1320.0 * tf4 - 720.0 * (tf4 * tf2);
        double x8poly = 1385.0 + 3633.0 * tf2 + 4095.0 * tf4 + 1575 * (tf4 * tf2);

        double lat_rad = phif + x2frac * x2poly * (x * x)
                + x4frac * x4poly * Math.pow(x, 4.0)
                + x6frac * x6poly * Math.pow(x, 6.0)
                + x8frac * x8poly * Math.pow(x, 8.0);

        double lng_rad = lambda0 + x1frac * x
                + x3frac * x3poly * Math.pow(x, 3.0)
                + x5frac * x5poly * Math.pow(x, 5.0)
                + x7frac * x7poly * Math.pow(x, 7.0);

        double latt = 0.0;
        double lonn = 0.0;
        latt = Math.toDegrees(lat_rad);
        lonn = Math.toDegrees(lng_rad);

        BigDecimal bdLat = BigDecimal.valueOf(latt);
        BigDecimal bdLon = BigDecimal.valueOf(lonn);
        bdLat = bdLat.setScale(8, RoundingMode.DOWN);
        bdLon = bdLon.setScale(8, RoundingMode.DOWN);
        return new LatLng(bdLat.doubleValue(),bdLon.doubleValue());
    }
    public  double getFootpointLatitude(double y) {

        /* Precalculate n (Eq. 10.18) */
        double n = (MAJOR_RADIUS - MINOR_RADIUS) / (MAJOR_RADIUS + MINOR_RADIUS);

        /* Precalculate alpha_ (Eq. 10.22) */
        /* (Same as alpha in Eq. 10.17) */
        double alpha_ = ((MAJOR_RADIUS + MINOR_RADIUS) / 2.0)
                * (1 + (Math.pow(n, 2.0) / 4) + (Math.pow(n, 4.0) / 64));

        /* Precalculate y_ (Eq. 10.23) */
        double y_ = y / alpha_;

        /* Precalculate beta_ (Eq. 10.22) */
        double beta_ = (3.0 * n / 2.0) + (-27.0 * Math.pow(n, 3.0) / 32.0)
                + (269.0 * Math.pow(n, 5.0) / 512.0);

        /* Precalculate gamma_ (Eq. 10.22) */
        double gamma_ = (21.0 * Math.pow(n, 2.0) / 16.0)
                + (-55.0 * Math.pow(n, 4.0) / 32.0);

        /* Precalculate delta_ (Eq. 10.22) */
        double delta_ = (151.0 * Math.pow(n, 3.0) / 96.0)
                + (-417.0 * Math.pow(n, 5.0) / 128.0);

        /* Precalculate epsilon_ (Eq. 10.22) */
        double epsilon_ = (1097.0 * Math.pow(n, 4.0) / 512.0);

        /* Now calculate the sum of the series (Eq. 10.21) */
        double result = y_ + (beta_ * Math.sin(2.0 * y_))
                + (gamma_ * Math.sin(4.0 * y_))
                + (delta_ * Math.sin(6.0 * y_))
                + (epsilon_ * Math.sin(8.0 * y_));

        return result;
    }
    public  double getCentralMeridian(int zone) {
        return Math.toRadians(-183.0 + (zone * 6.0));
    }

}