package com.chriniko.examples.ninth.message;

/*
    Note ---> this message is used from the following actors:

    1) CacheActor.java

 */
public class NoCacheResultResponse {

    private final String url;

    public NoCacheResultResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
