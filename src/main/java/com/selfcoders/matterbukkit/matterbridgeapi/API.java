package com.selfcoders.matterbukkit.matterbridgeapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class API {
    private final String url;
    private final String gateway;
    private final String systemUsername;
    private final String token;

    public API(String url, String gateway, String systemUsername, String token) {
        this.url = url;
        this.gateway = gateway;
        this.systemUsername = systemUsername;
        this.token = token;
    }

    public void sendMessage(Message message) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(url + "/api/message");

        if (token != null && !token.isEmpty()) {
            httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        Gson gson = new Gson();

        JsonObject messageJson = new JsonObject();
        messageJson.addProperty("gateway", gateway);
        messageJson.addProperty("username", message.getUsername());
        messageJson.addProperty("text", message.getText());

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

        entity.consumeContent();
    }

    public void sendMessage(String username, String text) throws IOException {
        sendMessage(new Message(username, text));
    }

    public void sendMessage(String text) throws IOException {
        sendMessage(systemUsername, text);
    }

    public void clearMessages() throws IOException {
        HttpEntity entity = get("/api/messages");

        if (entity == null) {
            return;
        }

        entity.consumeContent();
    }

    public InputStream streamMessages() throws IOException {
        HttpEntity entity = get("/api/stream");

        if (entity == null) {
            return null;
        }

        return entity.getContent();
    }

    private HttpEntity get(String path) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(url + path);

        if (token != null && !token.isEmpty()) {
            httpGet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        HttpResponse response = httpClient.execute(httpGet);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200) {
            throw new APIException(response.getStatusLine().getReasonPhrase());
        }

        return response.getEntity();
    }
}
