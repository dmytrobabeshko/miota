package org.fox;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.fox.domain.GraphBuilder;
import org.fox.domain.Properties;
import org.fox.domain.SearchInfoHolder;
import org.fox.http.UriBuilder;
import org.fox.presentation.ConsoleUI;
import org.fox.presentation.ResultFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Miota {

    public static void main(String[] args) throws IOException {

        ApplicationContext context = null;
        ForkJoinPool forkJoinPool = null;

        try {
            context = new AnnotationConfigApplicationContext(SpringConfig.class);
            forkJoinPool = context.getBean(ForkJoinPool.class);
            ConsoleUI consoleUI = context.getBean(ConsoleUI.class);
            GraphBuilder graphBuilder = context.getBean(GraphBuilder.class);
            UriBuilder uriBuilder = context.getBean(UriBuilder.class);
            ResultFileWriter resultFileWriter = context.getBean(ResultFileWriter.class);
            Properties properties = context.getBean(Properties.class);
            SearchInfoHolder searchInfoHolder = new SearchInfoHolder(properties);

            long startTime = System.currentTimeMillis();
            consoleUI.startUI(searchInfoHolder, startTime);
            graphBuilder.buildTree(uriBuilder, searchInfoHolder);

            forkJoinPool.awaitQuiescence(1, TimeUnit.HOURS);
            consoleUI.stopUI(searchInfoHolder, startTime);
            resultFileWriter.writeResult(searchInfoHolder, startTime);

        } catch (Throwable e) {
            if (context != null) {
                ScheduledExecutorService uiExecutor;
                uiExecutor = context.getBean(ScheduledExecutorService.class);

                if (uiExecutor != null) {
                    uiExecutor.shutdown();
                }
            }

            System.out.println(e);
            System.out.println(Arrays.toString(e.getStackTrace()));
        } finally {
            if (forkJoinPool != null) {
                forkJoinPool.shutdown();
            }
        }

        System.in.read();
    }
}
