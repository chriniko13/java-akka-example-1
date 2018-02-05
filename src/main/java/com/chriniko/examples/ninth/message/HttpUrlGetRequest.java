package com.chriniko.examples.ninth.message;


/*
    Note ---> this message is used from the following actors:

    1) AskDemoActor.java

 */
public class HttpUrlGetRequest {

    private final String url;

    public HttpUrlGetRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
