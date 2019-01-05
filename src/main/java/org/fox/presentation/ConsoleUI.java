package org.fox.presentation;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.fox.domain.SearchInfoHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ConsoleUI {

    @NotNull
    private final ScheduledExecutorService uiExecutor;

    @NotNull
    private static final String n = System.lineSeparator();

    @NotNull
    private final InfoBuilder infoBuilder;

    @NotNull
    private final static String os = System.getProperty("os.name");


    @Autowired
    public ConsoleUI(@NotNull ScheduledExecutorService uiExecutor, @NotNull InfoBuilder infoBuilder) {
        this.uiExecutor = uiExecutor;
        this.infoBuilder = infoBuilder;
    }


    public void startUI(@NotNull SearchInfoHolder searchInfoHolder, long startTime) {
        uiExecutor.scheduleWithFixedDelay(() ->
                        printInfo(searchInfoHolder, startTime),
                0, searchInfoHolder.getScreenRefreshInterval(), TimeUnit.MILLISECONDS);
    }

    public void stopUI(@NotNull SearchInfoHolder searchInfoHolder, long startTime) {
        uiExecutor.shutdown();
        printInfo(searchInfoHolder, startTime);
        printPaths(searchInfoHolder);
    }

    private void printInfo(@NotNull SearchInfoHolder searchInfoHolder, @NotNull Long startTime) {
        StringBuilder sb = infoBuilder.getProperties(searchInfoHolder);
        sb.append(n);
        sb.append(infoBuilder.getInfo(searchInfoHolder, startTime));

        clearConsole();
        System.out.print(sb);
    }


    private void printPaths(@NotNull SearchInfoHolder searchInfoHolder) {
        StringBuilder sb = infoBuilder.getPathsInfo(searchInfoHolder);
        System.out.println(sb);
    }


    private void clearConsole() {
        try {
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
