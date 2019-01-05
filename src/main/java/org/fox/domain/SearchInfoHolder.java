package org.fox.domain;

import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.concurrent.AsSynchronizedGraph;

public class SearchInfoHolder {

    @NotNull
    private final Properties properties;

    @NotNull
    private final Graph<Build, DefaultEdge> graph;

    @NotNull
    private final AtomicInteger processedCount;


    public SearchInfoHolder(@NotNull Properties properties) {

        this.properties = properties;
        this.processedCount = new AtomicInteger();

        SimpleDirectedGraph<Build, DefaultEdge> simpleDirectedGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
        this.graph = new AsSynchronizedGraph.Builder<Build, DefaultEdge>().cacheEnable().build(simpleDirectedGraph);
    }


    @NotNull
    public String getDevice() {
        return properties.getDevice();
    }

    @NotNull
    public String getPlatformId() {
        return properties.getPlatformId();
    }

    @NotNull
    public String getAndroidVer() {
        return properties.getAndroidVer();
    }

    @NotNull
    public Integer getCurrentBuild() {
        return properties.getCurrentBuild();
    }

    @NotNull
    public Integer getBuildDelta() {
        return properties.getBuildDelta();
    }

    @NotNull
    public String getUpdateUriTemplate() {
        return properties.getUpdateUriTemplate();
    }

    @NotNull
    public Integer getWorkers() {
        return properties.getWorkers();
    }

    @NotNull
    public Integer getConnectionErrorRetry() {
        return properties.getConnectionErrorRetry();
    }

    @NotNull
    public Integer getConnectionErrorRetryDelay() {
        return properties.getConnectionErrorRetryDelay();
    }

    @NotNull
    public Integer getHttpNotFoundRetry() {
        return properties.getHttpNotFoundRetry();
    }

    @NotNull
    public Integer getHttpNotFoundRetryDelay() {
        return properties.getHttpNotFoundRetryDelay();
    }

    @NotNull
    public Integer getScreenRefreshInterval() {
        return properties.getScreenRefreshInterval();
    }

    @NotNull
    public Graph<Build, DefaultEdge> getGraph() {
        return graph;
    }


    @NotNull
    public AtomicInteger getProcessedCount() {
        return processedCount;
    }
}

