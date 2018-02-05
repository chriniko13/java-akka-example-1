package com.chriniko.examples.ninth.message;

/*
    Note ---> this message is used from the following actors:

    1) AskDemoActor.java

 */
public class HttpBodyResultResponse {

    private final String url;
    private final String body;

    public HttpBodyResultResponse(String url, String body) {
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
