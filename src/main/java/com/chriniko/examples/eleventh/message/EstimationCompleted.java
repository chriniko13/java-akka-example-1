package com.chriniko.examples.eleventh.message;

public class EstimationCompleted {

    private final int storyPoints;

    public EstimationCompleted(int storyPoints) {
        this.storyPoints = storyPoints;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    @Override
    public String toString() {
        return "EstimationCompleted{" +
                "storyPoints=" + storyPoints +
                '}';
    }
}
