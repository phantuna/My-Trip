package org.example.demo_datn.Util;

import java.time.LocalDate;

public class SunCalcUtil {

    // Trả về mảng 2 phần tử: [Giờ mọc (phút), Giờ lặn (phút)]
    // Ví dụ: [330, 1080] tuc la 5:30 sang va 18:00 chieu
    public static int[] calculateSunriseSunset(double lat, double lng, LocalDate date) {
        int dayOfYear = date.getDayOfYear();

        // 1. Tính toán sơ bộ
        double lngHour = lng / 15.0;
        double t = dayOfYear + ((6 - lngHour) / 24.0);

        // 2. Mean anomaly
        double M = (0.9856 * t) - 3.289;

        // 3. True longitude
        double L = M + (1.916 * Math.sin(Math.toRadians(M))) + (0.020 * Math.sin(Math.toRadians(2 * M))) + 282.634;
        L = constrainAngle(L);

        // 4. Right ascension
        double RA = Math.toDegrees(Math.atan(0.91764 * Math.tan(Math.toRadians(L))));
        RA = constrainAngle(RA);
        double Lquadrant  = (Math.floor(L/90.0)) * 90.0;
        double RAquadrant = (Math.floor(RA/90.0)) * 90.0;
        RA = RA + (Lquadrant - RAquadrant);
        RA = RA / 15.0;

        // 5. Tính cosH
        double sinDec = 0.39782 * Math.sin(Math.toRadians(L));
        double cosDec = Math.cos(Math.asin(sinDec));
        double cosH = (Math.cos(Math.toRadians(90.833)) - (sinDec * Math.sin(Math.toRadians(lat)))) / (cosDec * Math.cos(Math.toRadians(lat)));

        if (cosH > 1 || cosH < -1) return new int[] {360, 1080}; // Mặc định nếu vùng cực

        // 6. Tính giờ
        double H = (360 - Math.toDegrees(Math.acos(cosH))) / 15.0;
        double T = H + RA - (0.06571 * t) - 6.622;
        double UT = T - lngHour;

        double localOffset = 7.0; // Múi giờ Việt Nam UTC+7
        double sunsetHours = UT + localOffset;
        double sunriseHours = sunsetHours - (2 * (Math.toDegrees(Math.acos(cosH)) / 15.0));

        sunriseHours = (sunriseHours + 24) % 24;
        sunsetHours = (sunsetHours + 24) % 24;

        return new int[] {
                (int) (sunriseHours * 60),
                (int) (sunsetHours * 60)
        };
    }

    private static double constrainAngle(double x){
        x = x % 360;
        if (x < 0) x += 360;
        return x;
    }
}