package com.bluedon.view.ui.cards;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Handles creating metrics Text for the posts.
 */
public class Metrics {
    /**
     * Creates a container for the provided metrics in a horizontal manner.
     * @param rawMetrics List of metrics to be put in the container.
     * @return {@link HBox} container that consists of the metrics provided.
     */
    public static HBox createMetrics(String ... rawMetrics) {
        HBox metrics = new HBox();
        metrics.getStyleClass().add("metrics");

        for(String rawMetric: rawMetrics) {
            Text metric = new Text(rawMetric);
            metric.getStyleClass().add("metric");
            metrics.getChildren().add(metric);
        }

        return metrics;
    }
}
