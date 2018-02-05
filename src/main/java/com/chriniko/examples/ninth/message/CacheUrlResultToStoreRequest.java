package com.chriniko.examples.ninth.message;

public class CacheUrlResultToStoreRequest {

    private final String url;
    private final String cleanedBody;

    public CacheUrlResultToStoreRequest(String url, String cleanedBody) {
        this.url = url;
        this.cleanedBody = cleanedBody;
    }

    public String getUrl() {
        return url;
    }

    public String getCleanedBody() {
        return cleanedBody;
    }
}
