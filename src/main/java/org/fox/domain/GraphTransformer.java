package org.fox.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Component;


@Component
public class GraphTransformer {

    public List<List<Build>> getAllPaths(@NotNull SearchInfoHolder searchInfoHolder) {

        Graph<Build, DefaultEdge> graph = searchInfoHolder.getGraph();
        Build currentBuild = new Build(searchInfoHolder.getCurrentBuild());

        List<Build> sinkBuilds = graph.vertexSet().stream().
                filter(vertex -> graph.outDegreeOf(vertex) == 0).
                collect(toList());

        AllDirectedPaths<Build, DefaultEdge> allDirectedPaths = new AllDirectedPaths<>(graph);

        List<List<Build>> buildPaths = sinkBuilds.stream().
                map(sinkBuild -> allDirectedPaths.getAllPaths(currentBuild, sinkBuild, true, null)).
                map(graphPathList -> graphPathList.stream().
                        map(GraphPath::getVertexList).
                        collect(toList())).
                flatMap(Collection::stream).
                collect(toList());

        buildPaths.sort((o1, o2) -> {
            Iterator<Build> i1 = o1.iterator();
            Iterator<Build> i2 = o2.iterator();
            while (i1.hasNext() || i2.hasNext()) {
                int compareTo = i1.next().compareTo(i2.next());
                if (compareTo != 0) {
                    return compareTo;
                }
            }

            return 1;
        });

        return buildPaths;
    }

    public List<Build> getAllBuilds(@NotNull SearchInfoHolder searchInfoHolder) {

        ArrayList<Build> result = new ArrayList<>(searchInfoHolder.getGraph().vertexSet());

        Collections.sort(result);
        if (!result.isEmpty()) {
            result.remove(0);
        }

        return result;
    }
}
