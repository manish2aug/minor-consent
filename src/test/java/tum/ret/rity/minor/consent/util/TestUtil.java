package tum.ret.rity.minor.consent.util;

import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestUtil {

    public String getQueryString(Map<String, String> keyValue) {
        StringBuilder queryParam = new StringBuilder();
        if (keyValue != null) {
            keyValue.forEach((k, v) -> {
                if (StringUtils.isNoneBlank(k, v)) {
                    if (queryParam.length() > 0) queryParam.append("&");
                    queryParam.append(k).append("=").append(v);
                }
            });
        }
        return queryParam.toString();
    }

    public static String getRequestUrl(String app_base_path, String resourcePath, String identifierType,
                                       String identifierValue, String issuingCountry) {
        StringBuilder queryParam = new StringBuilder();
        if (identifierType != null) {
            queryParam.append("identifier_type=").append(identifierType);
        }
        if (identifierValue != null) {
            if (queryParam.length() > 0) queryParam.append("&");
            queryParam.append("identifier_value=").append(identifierValue);
        }
        if (issuingCountry != null) {
            if (queryParam.length() > 0) queryParam.append("&");
            queryParam.append("identifier_issuing_country=").append(issuingCountry);
        }
        if (queryParam.length() > 0) {
            return StringUtils.join(app_base_path, resourcePath, "?", queryParam);
        }
        return StringUtils.join(app_base_path, resourcePath);
    }

    public static String getRequestUrl(String app_base_path, String resourcePath, Map<String, String> queryParams) {
        StringBuilder queryParamString = new StringBuilder();
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.entrySet()
                    .stream()
                    .filter(a -> StringUtils.isNotBlank(a.getValue()))
                    .forEach(a -> {
                        if (queryParamString.length() > 0) queryParamString.append("&");
                        queryParamString.append(a.getKey()).append("=").append(a.getValue());
                    });
        }
        if (queryParamString.length() > 0) {
            return StringUtils.join(app_base_path, resourcePath, "?", queryParamString);
        }
        return StringUtils.join(app_base_path, resourcePath);
    }

    public static String getKeyclockToken(String... scope) throws Exception {
        HttpPost post = new HttpPost("https://localhost:8443/auth/realms/test/protocol/openid-connect/token");
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("client_id", "test"));
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        urlParameters.add(new BasicNameValuePair("client_secret", ApplicationConstants.CLIENT_SECRET));
        if (scope.length > 0) {
            urlParameters.add(new BasicNameValuePair("scope", String.join(",", scope)));
        }
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        String access_token = null;
        try (CloseableHttpClient httpClient = getClient();
             CloseableHttpResponse response = httpClient.execute(post)) {
            JsonObject entries = new JsonObject(EntityUtils.toString(response.getEntity()));
            access_token = entries.getString("access_token");
            if (StringUtils.isNotBlank(access_token)) return access_token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private static CloseableHttpClient getClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory> create()
                        .register("https", sslsf)
                        .register("http", new PlainConnectionSocketFactory())
                        .build();

        BasicHttpClientConnectionManager connectionManager =
                new BasicHttpClientConnectionManager(socketFactoryRegistry);
        return HttpClients.custom().setSSLSocketFactory(sslsf)
                .setConnectionManager(connectionManager).build();

    }
}
