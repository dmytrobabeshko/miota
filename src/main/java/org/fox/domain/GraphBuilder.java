package org.fox.domain;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

import org.fox.http.RestTemplateWrapper;
import org.fox.http.UriBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public class GraphBuilder {

    @NotNull
    private final ForkJoinPool forkJoinPool;

    @NotNull
    private final RestTemplateWrapper restTemplateWrapper;


    @Autowired
    public GraphBuilder(@NotNull ForkJoinPool forkJoinPool, @NotNull RestTemplateWrapper restTemplateWrapper) {
        this.forkJoinPool = forkJoinPool;
        this.restTemplateWrapper = restTemplateWrapper;
    }


    public void buildTree(@NotNull UriBuilder uriBuilder, @NotNull SearchInfoHolder searchInfoHolder) {
        Graph<Build, DefaultEdge> graph = searchInfoHolder.getGraph();
        Integer currentBuild = searchInfoHolder.getCurrentBuild();
        Build currentBuildVertex = new Build(currentBuild);

        graph.addVertex(new Build(currentBuild));
        traverse(searchInfoHolder, currentBuildVertex);
    }

    private void traverse(@NotNull SearchInfoHolder searchInfoHolder, @NotNull final Build currentBuildVertex) {

        Integer currentBuild = currentBuildVertex.getBuild();
        Integer maxBuild = currentBuild + searchInfoHolder.getBuildDelta();
        Graph<Build, DefaultEdge> graph = searchInfoHolder.getGraph();
        List<CompletableFuture<Void>> stages = new CopyOnWriteArrayList<>();

        for (Integer build = currentBuild + 1; build <= maxBuild; build++) {
            final Integer otaBuild = build;

            CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> isUpdateAvailable(searchInfoHolder, currentBuild, otaBuild), forkJoinPool).
                    thenApplyAsync(otaBuildVertex -> {
                        searchInfoHolder.getProcessedCount().incrementAndGet();

                        if (otaBuildVertex != null) {
                            boolean existing = graph.containsVertex(otaBuildVertex);

                            if (!existing) {
                                graph.addVertex(otaBuildVertex);
                            }

                            graph.addEdge(currentBuildVertex, otaBuildVertex);

                            if (!existing) {
                                traverse(searchInfoHolder, otaBuildVertex);
                            }
                        }

                        return null;
                    }, forkJoinPool);

            stages.add(completableFuture);
        }

        forkJoinPool.execute(() ->
                CompletableFuture.allOf(stages.toArray(new CompletableFuture[0])));
    }

    @Nullable
    private Build isUpdateAvailable(@NotNull SearchInfoHolder searchInfoHolder, @NotNull Integer currentBuild, @NotNull Integer otaBuild) {
        Build result = null;

        try {
            HttpStatus httpStatus = restTemplateWrapper.tryGet(searchInfoHolder, currentBuild, otaBuild);

            if (httpStatus == OK) {
                result = new Build(otaBuild);
            } else if (httpStatus != NOT_FOUND) {
                result = new Build(otaBuild, httpStatus.name());
            }

        } catch (Throwable e) {
            result = new Build(otaBuild, "ERROR");
        }

        return result;
    }
}
