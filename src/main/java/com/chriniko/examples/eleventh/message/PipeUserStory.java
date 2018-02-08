package com.chriniko.examples.eleventh.message;

public class PipeUserStory {

    private final String title;

    public PipeUserStory(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "PipeUserStory{" +
                "title='" + title + '\'' +
                '}';
    }
}
