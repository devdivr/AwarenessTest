package com.devdivr.awarenesstest.network;

import android.net.Uri;

/**
 * Created by devdivr on 7/26/16.
 */
public class Url {

    private static final String EXPORT_FORMAT = "json";

    private static final String GET_ADDRESS_BY_COORD = "https://apis.daum.net/local/geo/coord2addr";

    public static String getDaumApiUrl(String apiKey, double longitude, double latitude) {

        Uri builder = Uri.parse(Url.GET_ADDRESS_BY_COORD)
                .buildUpon()
                .appendQueryParameter("apiKey", apiKey)
                .appendQueryParameter("longitude", String.valueOf(longitude))
                .appendQueryParameter("latitude", String.valueOf(latitude))
                .appendQueryParameter("output", EXPORT_FORMAT)
                .build();
        return builder.toString();

    }
}
