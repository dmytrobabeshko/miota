package org.fox.presentation;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.fox.domain.Build;
import org.fox.domain.GraphTransformer;
import org.fox.domain.SearchInfoHolder;
import org.fox.http.UriBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class InfoBuilder {

    @NotNull
    private final static String n = System.lineSeparator();

    @NotNull
    private final GraphTransformer graphTransformer;

    @NotNull
    private final ForkJoinPool forkJoinPool;

    @NotNull
    private final UriBuilder uriBuilder;


    @Autowired
    public InfoBuilder(@NotNull GraphTransformer graphTransformer, @NotNull ForkJoinPool forkJoinPool, @NotNull UriBuilder uriBuilder) {
        this.graphTransformer = graphTransformer;
        this.forkJoinPool = forkJoinPool;
        this.uriBuilder = uriBuilder;
    }


    @NotNull
    public StringBuilder getProperties(@NotNull SearchInfoHolder searchInfoHolder) {
        StringBuilder sb = new StringBuilder();

        sb.append("MiOTA by Fox, Version 1.0\t");

        sb.append(String.format("w: %s, ce: %s(%s), nf: %s(%s)" + n + n,
                searchInfoHolder.getWorkers(),
                searchInfoHolder.getConnectionErrorRetry(),
                searchInfoHolder.getConnectionErrorRetryDelay(),
                searchInfoHolder.getHttpNotFoundRetry(),
                searchInfoHolder.getConnectionErrorRetryDelay()));

        sb.append("Device:        " + searchInfoHolder.getDevice()).append(n);
        sb.append("Platform ID:   " + searchInfoHolder.getPlatformId()).append(n);
        sb.append("Android Ver:   " + searchInfoHolder.getAndroidVer()).append(n);
        sb.append("Current build: " + searchInfoHolder.getCurrentBuild()).append(n);
        sb.append("Build delta:   " + searchInfoHolder.getBuildDelta()).append(n);

        return sb;
    }

    @NotNull
    public StringBuilder getInfo(@NotNull SearchInfoHolder searchInfoHolder, @NotNull Long startTime) {

        long stopTime = System.currentTimeMillis();
        double elapsedTime = ((stopTime - startTime) / 1000d);
        int queuedSubmissionCount = forkJoinPool.getQueuedSubmissionCount();
        int processedCount = searchInfoHolder.getProcessedCount().get();
        StringBuilder sb = new StringBuilder();

        sb.append("Processed: " + processedCount).append(n);
        sb.append("Queued:    " + (queuedSubmissionCount + forkJoinPool.getActiveThreadCount())).append(n);

        List<Build> allBuilds = graphTransformer.getAllBuilds(searchInfoHolder);
        int found = allBuilds.size();

        sb.append("Found:     " + (found)).append(n).append(n);
        sb.append("Speed:     " + (long) (processedCount / elapsedTime) + " builds/sec").append(n);
        sb.append("Elapsed:   " + (long) elapsedTime + " sec").append(n);

        if (found > 0) {

            sb.append(n).append(n);
            sb.append("Builds found:").append(n);
            sb.append(allBuilds.stream().
                    map(Object::toString).
                    collect(joining(" ")));
        }

        return sb;
    }

    @NotNull
    public StringBuilder getPathsInfo(@NotNull SearchInfoHolder searchInfoHolder) {
        StringBuilder sb = new StringBuilder();

        if (searchInfoHolder.getGraph().vertexSet().size() > 1) {
            List<List<Build>> result = graphTransformer.getAllPaths(searchInfoHolder);
            sb.append(n);
            sb.append(n);
            sb.append(n);
            sb.append("Update paths:").append(n);

            for (List<Build> path : result) {
                sb.append("> " + path.stream().map(Object::toString).collect(joining(", "))).append(n);
            }
        } else {
            sb.append(n).append(n);
            sb.append("Nothing is found");
        }

        return sb;
    }

    @NotNull
    public StringBuilder getPathInfoWithURI(@NotNull SearchInfoHolder searchInfoHolder) {

        StringBuilder sb = new StringBuilder();
        List<List<Build>> allPaths = graphTransformer.getAllPaths(searchInfoHolder);

        for (List<Build> path : allPaths) {
            int pathSize = path.size();
            sb.append("> " + path.stream().map(Object::toString).collect(joining(", "))).append(n);

            for (int i = 0; i < pathSize; i++) {
                Build currentBuild = path.get(i);

                if (i != pathSize - 1) {
                    Build otaBuild = path.get(i + 1);
                    if (!otaBuild.hasError()) {
                        sb.append(uriBuilder.buildURI(searchInfoHolder, currentBuild.getBuild(), otaBuild.getBuild()).toString()).append(n);
                    }
                }
            }

            sb.append(n);
        }

        return sb;
    }
}
