package com.payline.payment.ideal.utils.http;


import com.payline.payment.ideal.exception.HttpCallException;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.properties.ConfigProperties;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * This utility class provides a basic HTTP client to send requests, using OkHttp library.
 * It must be extended to match each payment method needs.
 */
@Log4j2
public abstract class AbstractHttpClient {

    private CloseableHttpClient client;
    private int retries;
    /**
     * Instantiate a HTTP client.
     */

    protected AbstractHttpClient() {

        int connectionRequestTimeout;
        int connectTimeout;
        int socketTimeout;
        try {
            // request config timeouts (in seconds)
            ConfigProperties config = ConfigProperties.getInstance();
            connectionRequestTimeout = Integer.parseInt(config.get("http.connectionRequestTimeout"));
            connectTimeout = Integer.parseInt(config.get("http.connectTimeout"));
            socketTimeout = Integer.parseInt(config.get("http.socketTimeout"));
            // retries
            this.retries = Integer.parseInt(config.get("http.retries"));
        } catch (NumberFormatException e) {
            throw new PluginException("plugin error: http.* properties must be integers", e);
        }

        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout * 1000)
                .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
                .setSocketTimeout(socketTimeout * 1000)
                .build();

        this.client = HttpClientBuilder.create()
                .useSystemProperties()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(new BasicCredentialsProvider())
                .setSSLSocketFactory(new SSLConnectionSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), SSLConnectionSocketFactory.getDefaultHostnameVerifier())).build();
    }


    /**
     * Send a GET request
     *
     * @param url  URL RL scheme + host
     * @param path URL path
     * @return The response returned from the HTTP call
     */

    StringResponse doGet(String url, String path, Header[] headers){
        try {
            URI uri = new URI(url + path);

            final HttpGet httpGetRequest = new HttpGet(uri);
            httpGetRequest.setHeaders(headers);

            return getStringResponse(url, httpGetRequest);
        } catch (URISyntaxException e) {
            throw new HttpCallException("AbstractHttpClient.doGet.URISyntaxException", e);
        }
    }

    /**
     * Send a POST request.
     *
     * @param url  URL scheme + host
     * @param path URL path
     * @param body Request body
     * @return The response returned from the HTTP call
     */
    StringResponse doPost(String url, String path, Header[] headers, HttpEntity body) {

        try {
            URI uri = new URI(url + path);

            final HttpPost httpPostRequest = new HttpPost(uri);
            httpPostRequest.setHeaders(headers);
            httpPostRequest.setEntity(body);

            return getStringResponse(url, httpPostRequest);

        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new HttpCallException("AbstractHttpClient.doPost.URISyntaxException", e);
        }
    }

    StringResponse getStringResponse(String url, HttpRequestBase httpPostRequest) {
        final long start = System.currentTimeMillis();
        int count = 0;
        StringResponse strResponse = null;
        String errMsg = null;
        while (count < this.retries && strResponse == null) {
            try (CloseableHttpResponse httpResponse = this.client.execute(httpPostRequest)) {

                log.info("Start partner call... [URL: {}]", url);
                strResponse = StringResponse.fromHttpResponse( httpResponse );

                final long end = System.currentTimeMillis();

                log.info("End partner call [T: {}ms] [CODE: {}]", end - start, strResponse.getStatusCode());

            } catch (final IOException e) {
                log.error("Error while partner call [T: {}ms]", System.currentTimeMillis() - start, e);
                errMsg = e.getMessage();
            } finally {
                count++;
            }
        }

        if (strResponse == null) {
            if (errMsg == null) {
                throw new HttpCallException("Http response is empty");
            }
            throw new HttpCallException(errMsg);
        }
        log.info("Response obtained from partner API [{} {}]", strResponse.getStatusCode(), strResponse.getStatusMessage() );
        return strResponse;
    }


}
