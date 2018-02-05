package com.chriniko.examples.ninth.message;

/*
    Note ---> this message is used from the following actors:

    1) HttpActor.java

 */
public class SearchForUrlRequest {

    private final String url;

    public SearchForUrlRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
