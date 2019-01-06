package org.fox.presentation;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.fox.domain.SearchInfoHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ResultFileWriter {

    @NotNull
    private static final String n = System.lineSeparator();

    @NotNull
    private final static String logFile = System.getProperty("user.dir") + "\\output.txt";

    @NotNull
    private final InfoBuilder infoBuilder;


    @Autowired
    public ResultFileWriter(@NotNull InfoBuilder infoBuilder) {
        this.infoBuilder = infoBuilder;
    }

    public void writeResult(@NotNull SearchInfoHolder searchInfoHolder, long startTime) {
        try {
            if (searchInfoHolder.getGraph().vertexSet().size() > 1) {

                System.out.println("Output");
                System.out.println(logFile);

                StringBuilder sb = infoBuilder.getProperties(searchInfoHolder);
                sb.append(n);
                sb.append(infoBuilder.getInfo(searchInfoHolder, startTime));
                sb.append(infoBuilder.getPathsInfo(searchInfoHolder));
                sb.append(n).append(n).append(n);
                sb.append(infoBuilder.getPathInfoWithURI(searchInfoHolder));

                BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, false));
                writer.write(sb.toString());
                writer.close();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }
}
