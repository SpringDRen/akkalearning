package com.rlc.test;

import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

import java.util.Set;

/**
 * Created by renlc on 2016/6/30.
 */
public class JettyUtilTest {

    public static void main(String[] args) {
//        String query = "28/Jun/2016:02:45:02 +0800|218.107.7.211|GET /info.php?f=0&v=imgolive-aphone-2.1.0.22&u=866963020732923&t=0&b=1&s=3&c=2&a=0&p=3&l=%2Fmp4%2F2016%2Fzongyi%2F2016mnssrcjnskhych_55927%2F2337A9CC93AF0A803148FFAF4D276A25_20160627_1_1_1104.mp4%2Fplaylist.m3u8%3Fuuid%3D1f24c4a5c7fb4ee2a39252bb9fb2681b%26t%3D5771e4ad%26pno%3D1021%26sign%3D3c008df4621a3e4fad2b19f1108d0c25%26win%3D300%26srgid%3D25012%26urgid%3D595%26srgids%3D25012%26nid%3D25012%26payload%3Dusertoken%253dhit%253d0%255eruip%253d3664447443%26rdur%3D21600%26arange%3D0%26limitrate%3D0%26fid%3D2337A9CC93AF0A803148FFAF4D276A25%26ver%3D0x03&cv=20160205&z=1&h=pcvideogs.titan.mgtv.com HTTP/1.1|200";
        String query = "si=&s=3&a=&t=0&cv=20160205&b=2&u=&p=4&v=imgotv-pcweb-Mgtv_Play_V5.0.1&l=mp4%2F2016%2Fzongyi%2Fwxhnc_52431%2FA0970E4748E622532776F9437B77E627_20160619_1_1_780.mp4%2Fplaylist.m3u8%3Fuuid%3D77695355899c4018becd0ad039cbca3d%26t%3D5771e495%26pno%3D1000%26sign%3De8a1c119636331e6a0d37e6e1755b456%26win%3D3600%26srgid%3D25012%26urgid%3D2823%26srgids%3D25012%26nid%3D25012%26payload%3Dusertoken%253dhit%253d0%255eruip%253d2026001940%26rdur%3D21600%26arange%3D0%26limitrate%3D0%26fid%3DA0970E4748E622532776F9437B77E627%26ver%3D0x03%26r%3D40837444195375&e=302000&c=1&f=-1&z=0&h=pcvideogs.titan.mgtv.com HTTP/1.1|200\";";
        MultiMap<String> values = new MultiMap<>();
        UrlEncoded.decodeTo(query, values, "UTF-8");
        Set<String> set = values.keySet();
        for (String key : set) {
            System.out.println(key + ":" + values.get(key).size() + ":" + values.get(key).get(0));
        }
    }
}
