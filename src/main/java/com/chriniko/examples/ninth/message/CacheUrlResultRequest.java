package com.chriniko.examples.ninth.message;


public class CacheUrlResultRequest {

    private final String url;
    private final String body;

    public CacheUrlResultRequest(String url, String body) {
        this.url = url;
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }
}
