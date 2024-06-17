package com.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class NaverService {

    private static final String NAVER_PROFILE_API_URL = "https://openapi.naver.com/v1/nid/me";

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    public String getNaverProfile(String accessToken) {
        String header = "Bearer " + accessToken;

        try {
            URL url = new URL(NAVER_PROFILE_API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", header);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else { // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAccessToken(String code, String state, String redirectURI) {
        try {
            String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&redirect_uri=" + redirectURI +
                    "&code=" + code +
                    "&state=" + state;

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            // JSON 파싱을 통해 access_token 추출
            String responseBody = response.toString();
            return extractAccessToken(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractAccessToken(String responseBody) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        return rootNode.path("access_token").asText();
    }

    public String extractNicknameFromNaverProfile(String naverProfile) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(naverProfile);
        return rootNode.path("response").path("nickname").asText();
    }
}
