package com.chriniko.examples.ninth.message;

public class ParseBodyRequest {

    private final String url;
    private final String bodyToClean;

    public ParseBodyRequest(String url, String bodyToClean) {
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
