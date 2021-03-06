package com.selfcoders.matterbukkit.matterbridgeapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

public class API {
    private final String url;
    private final String gateway;
    private final String systemUsername;
    private final String systemAvatarUrl;
    private final String token;

    public class Response {
        private HttpUriRequest httpUriRequest;
        private HttpEntity httpEntity;

        Response(HttpUriRequest httpUriRequest, HttpEntity httpEntity) {
            this.httpUriRequest = httpUriRequest;
            this.httpEntity = httpEntity;
        }

        public HttpUriRequest getHttpUriRequest() {
            return httpUriRequest;
        }

        public HttpEntity getHttpEntity() {
            return httpEntity;
        }

        public InputStream getContent() throws IOException {
            if (httpEntity == null) {
                return null;
            }

            return httpEntity.getContent();
        }
    }

    public API(String url, String gateway, String systemUsername, String systemAvatarUrl, String token) {
        this.url = url;
        this.gateway = gateway;
        this.systemUsername = systemUsername;
        this.systemAvatarUrl = systemAvatarUrl;
        this.token = token;
    }

    public void sendMessage(Message message) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url + "/api/message");

        if (token != null && !token.isEmpty()) {
            httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        Gson gson = new Gson();

        JsonObject messageJson = new JsonObject();
        messageJson.addProperty("gateway", gateway);
        messageJson.addProperty("username", message.getUsername());
        messageJson.addProperty("text", message.getText());
        messageJson.addProperty("avatar", message.getAvatar());

        httpPost.setEntity(new StringEntity(gson.toJson(messageJson), "application/json", "utf-8"));

        HttpResponse response = httpClient.execute(httpPost);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200) {
            throw new APIException(response.getStatusLine().getReasonPhrase());
        }

        HttpEntity entity = response.getEntity();

        if (entity == null) {
            return;
        }

        EntityUtils.consume(entity);
    }

    public void sendMessage(String username, String text, String avatarUrl) throws IOException {
        sendMessage(new Message(username, text, avatarUrl));
    }

    public void sendMessage(String text) throws IOException {
        sendMessage(systemUsername, text, systemAvatarUrl);
    }

    public void clearMessages() throws IOException {
        Response response = get("/api/messages");
        HttpEntity entity = response.getHttpEntity();

        if (entity == null) {
            return;
        }

        EntityUtils.consume(entity);
    }

    public Response streamMessages() throws IOException {
        return get("/api/stream");
    }

    private Response get(String path) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url + path);

        if (token != null && !token.isEmpty()) {
            httpGet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        HttpResponse response = httpClient.execute(httpGet);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200) {
            throw new APIException(response.getStatusLine().getReasonPhrase());
        }

        return new Response(httpGet, response.getEntity());
    }
}
