package com.springboot.demo.controller;


import ch.qos.logback.core.util.FileUtil;
//import com.springboot.demo.Utils.HttpsUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@CrossOrigin
@RestController

public class BasicController {
    private static CloseableHttpClient getHttpClient() throws Exception {
        //忽略ssl验证
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {

            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }

        }).build();

        //创建httpClient
        CloseableHttpClient client = HttpClients.custom().setSSLContext(sslContext).
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        return client;
    }

    Object LtpaToken2 = new Object();

    @RequestMapping("login")
    public ResponseEntity<String> login(String userId, String pwd) throws Exception {
        Object JSESSIONID = new Object();
        ResponseEntity<String> response = null;
        try {
            //------------登录
            String zosmfUrlOverHttps = "https://10.60.43.8:8800/zosmf/";
            CloseableHttpClient httpClient = getHttpClient();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            HttpHeaders httpHeaders = new RestTemplate(requestFactory).headForHeaders(zosmfUrlOverHttps);
            List<String> setCookie = httpHeaders.get("Set-Cookie");
            if (setCookie != null) {
                System.out.println("===================================JSESSIONID========================");

                JSESSIONID = setCookie.get(0).split(";")[0];
                System.out.println(JSESSIONID);
            } else {

            }

            //访问zosmf获取token
            String loginUrlOverHttps = zosmfUrlOverHttps + "LoginServlet";

            //设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Cookie", JSESSIONID.toString());
            headers.add("Referer", zosmfUrlOverHttps);

            //添加表单数据
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("requestType", "Login");
            map.add("username", userId);
            map.add("password", pwd);
            //request
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            response = new RestTemplate(requestFactory).postForEntity(loginUrlOverHttps, request, String.class);
            List<String> tokenList = response.getHeaders().get("Set-Cookie");
            if (tokenList != null) {
                System.out.println("===================================LtpaToken2========================");
                LtpaToken2 = tokenList.get(0).split(";")[0];
                System.out.println(LtpaToken2);
            }
        } catch (Exception e) {
            // TODO 异常
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @module:
     * @Description: 上传
     * @author:
     * @date:
     */
//    @RequestMapping("uploadA")
//    public void uploadAPP(@RequestParam("file") MultipartFile file) throws Exception {
//        if (file != null && !file.isEmpty()) {
//            String zosmfUrlOverHttps = "https://10.60.43.8:8800/zosmf/";
//            JSONObject jsonObject=new JSONObject();
//            InputStream inputStream=file.getInputStream();
//            byte[] bytes = new byte[0];
//            bytes = new byte[inputStream.available()];
//            inputStream.read(bytes);
//            String fileContent = new String(bytes);
//            System.out.println(fileContent);
//            //---------------从开放端提交作业
//            try {
//                //访问zosmf获取token
//                String openClientUrl = zosmfUrlOverHttps + "restjobs/jobs";
//
//                //设置请求头
//                HttpHeaders openHeaders = new HttpHeaders();
//                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//                openHeaders.setContentType(type);
//                openHeaders.add("Accept", MediaType.APPLICATION_JSON.toString());
//                openHeaders.add("Cookie", LtpaToken2.toString());
//                openHeaders.add("Referer", zosmfUrlOverHttps);
//
//
//                //request
//                //这里就把要提交的作业加上
//                HttpEntity<String> formEntity = new HttpEntity<String>(fileContent, openHeaders);
//
//                CloseableHttpClient httpClient = getHttpClient();
//                HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//                requestFactory.setHttpClient(httpClient);
//                ResponseEntity<String > openResponse = new RestTemplate(requestFactory).exchange(openClientUrl, HttpMethod.PUT, formEntity, String.class);
//                System.out.println(openResponse);
//    //            new RestTemplate(requestFactory).put(openClientUrl, openRequest);
//                //处理数据进行返回即可
//
//
//
//                //---------------从主机端提交作业
//
//                //设置请求头
//                HttpHeaders hostHeaders = new HttpHeaders();
//                hostHeaders.setContentType(MediaType.APPLICATION_JSON);
//                hostHeaders.add("Accept", "application/json");
//                hostHeaders.add("Cookie", LtpaToken2.toString());
//                hostHeaders.add("Referer", zosmfUrlOverHttps);
//
//                //request
////                MultiValueMap<String, String> hostMap = new LinkedMultiValueMap<>();
////                //这里添加作业内容
////                hostMap.add("file",file.toString());
//
//                jsonObject.put("file",fileContent);
//                /*MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//                map.put("file", Collections.singletonList(fileContent));*/
//                HttpEntity<String> hostRequest = new HttpEntity<>(JSON.toJSONString(jsonObject), hostHeaders);
//                ResponseEntity<String > hostResponse = new RestTemplate(requestFactory).exchange(openClientUrl, HttpMethod.PUT, hostRequest, String.class);
//                //处理数据进行返回即可
//
//            } catch (Exception e) {
//                    // TODO 异常
//                    e.printStackTrace();
//            }
//        }
//    }

    /**
     * @module:
     * @Description: 开放端上传
     * @author:
     * @date:
     */
    @RequestMapping("uploadA")
    public ResponseEntity<String> uploadAPP(@RequestParam("file") MultipartFile file) throws Exception {
        Object JSESSIONID = new Object();
        Object LtpaToken2 = new Object();
        //file文件
        JSONObject jsonObject = new JSONObject();
        InputStream inputStream = file.getInputStream();
        byte[] bytes = new byte[0];
        bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String fileContent = new String(bytes);
        System.out.println(fileContent);

        String zosmfUrlOverHttps = "https://10.60.43.8:8800/zosmf/";
        CloseableHttpClient httpClient = getHttpClient();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        HttpHeaders httpHeaders = new RestTemplate(requestFactory).headForHeaders(zosmfUrlOverHttps);
        List<String> setCookie = httpHeaders.get("Set-Cookie");
        if (setCookie != null) {
            System.out.println("===================================JSESSIONID========================");

            JSESSIONID = setCookie.get(0).split(";")[0];
            System.out.println(JSESSIONID);
        } else {

        }

        //访问zosmf获取token
        String loginUrlOverHttps = zosmfUrlOverHttps + "LoginServlet";

        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Cookie", JSESSIONID.toString());
        headers.add("Referer", zosmfUrlOverHttps);

        //添加表单数据
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("requestType", "Login");
        map.add("username","ST006");
        map.add("password", "123456");
        //request
        HttpEntity<MultiValueMap<String, String>>request = new HttpEntity<>(map, headers);
        ResponseEntity<String > response = new RestTemplate(requestFactory).postForEntity(loginUrlOverHttps, request, String.class);
        List<String> tokenList = response.getHeaders().get("Set-Cookie");
        if(tokenList != null){
            System.out.println("===================================LtpaToken2========================");
            LtpaToken2 = tokenList.get(0).split(";")[0];
            System.out.println(LtpaToken2);
        }
        //---------------从开放端提交作业
        String openClientUrl = zosmfUrlOverHttps + "restjobs/jobs";

        //request
        HttpHeaders openHeaders = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        openHeaders.add("Cookie", LtpaToken2.toString());
        openHeaders.add("Referer", zosmfUrlOverHttps);



        HttpEntity<String> formEntity = new HttpEntity<String>(fileContent, openHeaders);
        ResponseEntity<String > openResponse = new RestTemplate(requestFactory).exchange(openClientUrl, HttpMethod.PUT, formEntity, String.class);
        System.out.println(openResponse);
        return openResponse;
    }
}
