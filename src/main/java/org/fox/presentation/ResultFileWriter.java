package org.fox.presentation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.fox.domain.SearchInfoHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ResultFileWriter {

    @NotNull
    private static final String n = System.lineSeparator();

    @NotNull
    private final static String outputFileTemplate = System.getProperty("user.dir") + "\\%s_%s_b%s_%s.txt";

    @NotNull
    private final InfoBuilder infoBuilder;


    @Autowired
    public ResultFileWriter(@NotNull InfoBuilder infoBuilder) {
        this.infoBuilder = infoBuilder;
    }

    public void writeResult(@NotNull SearchInfoHolder searchInfoHolder, long startTime) {
        try {
            if (searchInfoHolder.getGraph().vertexSet().size() > 1) {

                System.out.print("Output ");
                String outputFile = getOutputFilePath(searchInfoHolder);
                System.out.println(outputFile);

                StringBuilder sb = new StringBuilder();
                sb.append(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now())).append(n).append(n);
                sb.append(infoBuilder.getProperties(searchInfoHolder));
                sb.append(n);
                sb.append(infoBuilder.getInfo(searchInfoHolder, startTime));
                sb.append(infoBuilder.getPathsInfo(searchInfoHolder));
                sb.append(n).append(n).append(n);
                sb.append(infoBuilder.getPathInfoWithURI(searchInfoHolder));

                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, false));
                writer.write(sb.toString());
                writer.close();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    private String getOutputFilePath(@NotNull SearchInfoHolder searchInfoHolder) {
        String path;
        int index = 1;

        do {
            path = String.format(outputFileTemplate,
                    searchInfoHolder.getDevice(),
                    searchInfoHolder.getPlatformId(),
                    searchInfoHolder.getCurrentBuild(),
                    index++);

        } while (new File(path).exists());

        return path;
    }
}
