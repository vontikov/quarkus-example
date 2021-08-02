package com.github.vontikov.greeting.extension.deployment;

import com.github.vontikov.greeting.extension.GreetingExtensionServlet;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.undertow.deployment.ServletBuildItem;

class GreetingExtensionProcessor {

    private static final String FEATURE = "greeting-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    ServletBuildItem createServlet() {
        ServletBuildItem servletBuildItem = ServletBuildItem
                .builder("greeting-extension",
                        GreetingExtensionServlet.class.getName())
                .addMapping("/greeting-extension")
                .build();
        return servletBuildItem;
    }
}
