package com.chriniko.examples.eleventh.message;

public class UserStory {

    private final String title;

    public UserStory(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "UserStory{" +
                "title='" + title + '\'' +
                '}';
    }
}
