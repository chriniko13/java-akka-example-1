package com.chriniko.examples.ninth.message;


/*
    Note ---> this message is used from the following actors:

    1) ParserActor.java

 */
public class CleanBodyRequest {

    private final String url;
    private final String bodyToClean;

    public CleanBodyRequest(String url, String bodyToClean) {
        this.url = url;
        this.bodyToClean = bodyToClean;
    }

    public String getUrl() {
        return url;
    }

    public String getBodyToClean() {
        return bodyToClean;
    }
}
