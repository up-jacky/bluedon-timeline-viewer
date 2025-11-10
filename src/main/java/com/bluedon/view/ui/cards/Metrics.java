package com.bluedon.view.ui.cards;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class Metrics {
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
