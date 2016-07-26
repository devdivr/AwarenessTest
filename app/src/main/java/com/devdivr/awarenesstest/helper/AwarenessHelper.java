package com.devdivr.awarenesstest.helper;

import com.google.android.gms.awareness.state.Weather;

/**
 * Created by devdivr on 7/26/16.
 */
public class AwarenessHelper {

    public static String getActivityType(int type) {
        switch(type) {
            case 0:
                return "IN_VEHICLE";
            case 1:
                return "ON_BICYCLE";
            case 2:
                return "ON_FOOT";
            case 3:
                return "STILL";
            case 4:
                return "UNKNOWN";
            case 5:
                return "TILTING";
            case 6:
            default:
                return Integer.toString(type);
            case 7:
                return "WALKING";
            case 8:
                return "RUNNING";
        }
    }

    public static String getWeatherInfo(Weather weather) {

        StringBuilder sb = new StringBuilder();
        for (int condition : weather.getConditions()) {
            sb.append(getWeatherCondition(condition));
            sb.append(", ");
        }

        int temperature = (int) weather.getTemperature(Weather.CELSIUS);
        int feelsLikeTemperature = (int) weather.getFeelsLikeTemperature(Weather.CELSIUS);
        int humidity = weather.getHumidity();
        int dewPoint = (int) weather.getDewPoint(Weather.CELSIUS);
        String info = String.format("기온 %d˚C, 체감온도 %d˚C, 습도 %d%%, 이슬점 %d˚C", temperature, feelsLikeTemperature, humidity, dewPoint);
        return sb.toString() + info;
    }

    private static String getWeatherCondition(int condition) {

        switch (condition) {
            case Weather.CONDITION_CLEAR:
                return "Clear";

            case Weather.CONDITION_CLOUDY:
                return "Cloudy";

            case Weather.CONDITION_FOGGY:
                return "Foggy";

            case Weather.CONDITION_HAZY:
                return "Hazy";

            case Weather.CONDITION_ICY:
                return "Icy";

            case Weather.CONDITION_RAINY:
                return "Rainy";

            case Weather.CONDITION_SNOWY:
                return "Snowy";

            case Weather.CONDITION_STORMY:
                return "Stormy";

            case Weather.CONDITION_WINDY:
                return "Windy";

            default :
                // Weather.CONDITION_UNKNOWN
                return "Unknown";
        }
    }
}
