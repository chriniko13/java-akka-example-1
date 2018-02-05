package com.chriniko.examples.ninth.message;

/*
    Note ---> this message is used from the following actors:

    1) CacheActor.java

 */

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
