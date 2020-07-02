package com.springboot.demo.Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.Consts;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.*;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import javax.net.ssl.*;


/**
 * Http请求
 *
 * @author mszhou
 */
public class HttpsUtils {

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


    public static RestTemplate getInstance(String charset) {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : list) {
            if(httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName(charset));
                break;
            }
        }
        return restTemplate;
    }

    public static String serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        String string = byteArrayOutputStream.toString("ISO-8859-1");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return string;
    }

    //====================================================================
    //============================= 测试调用   ============================
    //====================================================================
    public static void main(String[] args) {
        //
        Object JSESSIONID = new Object();
        Object LtpaToken2 = new Object();
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


            String str = "//ST016A      JOB ACCT#,'TRAINER',CLASS=A,NOTIFY=ST016\n" +
                    "//STEP1       EXEC PGM=IDCAMS\n" +
                    "//SYSPRINT    DD SYSOUT=*\n" +
                    "//SYSIN       DD *\n" +
                    "  DELETE ST016.VSAM.ESDS1\n" +
                    "  SET MAXCC=0\n" +
                    "  DEFINE CLUSTER-\n" +
                    "     (NAME(ST016.VSAM.ESDS1)-\n" +
                    "     NONINDEXED-\n" +
                    "     VOLUME(BYWK00)-\n" +
                    "     RECORDS(100,10)-\n" +
                    "     RECORDSIZE(10 20)-\n" +
                    "     CONTROLINTERVALSIZE(4096)\n" +
                    "//\n";
            HttpEntity<String> formEntity = new HttpEntity<String>(str, openHeaders);


            ResponseEntity<String > openResponse = new RestTemplate(requestFactory).exchange(openClientUrl, HttpMethod.PUT, formEntity, String.class);

            System.out.println(openResponse);

            //处理数据进行返回即可



            //---------------从主机端提交作业
/*
            //设置请求头
            HttpHeaders hostHeaders = new HttpHeaders();
            hostHeaders.setContentType(MediaType.APPLICATION_JSON);
            hostHeaders.add("Cookie", LtpaToken2.toString());
            hostHeaders.add("Referer", zosmfUrlOverHttps);


            RestTemplate restTemplate = new RestTemplate(requestFactory);
            MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
            jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            restTemplate.getMessageConverters().add(jsonHttpMessageConverter);

            //request
//            MultiValueMap<String, String> hostMap = new LinkedMultiValueMap<>();
            //这里添加作业内容
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file","232");
            String str = JSON.toJSONString(jsonObject);



            ResponseEntity<String > hostResponse = restTemplate.exchange(openClientUrl, HttpMethod.PUT, new HttpEntity<>(str, hostHeaders), String.class);
            //处理数据进行返回即可*/

        } catch (Exception e) {
            // TODO 异常
            e.printStackTrace();
        }

    }

}