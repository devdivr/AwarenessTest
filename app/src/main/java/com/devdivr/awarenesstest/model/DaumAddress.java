package com.devdivr.awarenesstest.model;

import com.google.gson.annotations.SerializedName;

/**
 * 좌표로 주소를 얻는 api를 위한 모델
 * https://developers.daum.net/services/apis/local/geo/coord2addr
 *
 * {
 "type": "H",
 "code": "3102374",
 "name": "삼평동",
 "fullName": "경기도 성남시 분당구 삼평동",
 "regionId": "B22330900",
 "name0": "대한민국",
 "code1": "31",
 "name1": "경기도",
 "code2": "31023",
 "name2": "성남시 분당구",
 "code3": "3102374",
 "name3": "삼평동",
 "x": 127.1163593869371,
 "y": 37.40612091848614
 }
 */
public class DaumAddress {

    @SerializedName("fullName")
    private String address;

    @SerializedName("name2")
    private String guDong;

    @SerializedName("x")
    private float longitude;

    @SerializedName("y")
    private float latitude;

    public String getAddress() {
        return address != null ? address : "";
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public String getGu() {
        if (guDong != null) {
            String[] gu = guDong.split(" ");
            return gu[0];
        }
        return "";
    }

    @Override
    public String toString() {
        return "DaumAddress{" +
                "address='" + address + '\'' +
                ", guDong='" + guDong + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}