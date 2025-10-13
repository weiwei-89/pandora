package org.edward.pandora.event_bus;

import org.edward.pandora.common.task.IntervalTask;
import org.edward.pandora.common.task.Processor;
import org.edward.pandora.common.task.TaskPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Order(1)
@Component
public class StartupRunner implements ApplicationRunner {
    private TaskPool taskPool = TaskPool.getInstance();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.taskPool.addTask("scanning", new IntervalTask(new ScanningProcessor(), 5*1000));
    }

    private static class ScanningProcessor implements Processor {
        private static final Logger logger = LoggerFactory.getLogger(ScanningProcessor.class);
        private static final String APP_BASE_FOLDER_PATH = "D:\\edward\\test\\pandora\\event-bus\\app";

        @Override
        public void process() throws Exception {
            File appBaseFolder = new File(APP_BASE_FOLDER_PATH);
            if(!appBaseFolder.exists()) {
                logger.warn("app base folder does not exist");
                return;
            }
            if(!appBaseFolder.isDirectory()) {
                logger.warn("{} is not a folder", APP_BASE_FOLDER_PATH);
                return;
            }
            File[] appFolderArray = appBaseFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });
            if(appFolderArray==null || appFolderArray.length==0) {
                logger.info("there are no apps");
                return;
            }
            logger.info("there are {} apps", appFolderArray.length);
            for(int i=0; i<appFolderArray.length; i++) {
                File appFolder = appFolderArray[i];
                logger.info("{}.handling app \"{}\"", i+1, appFolder.getName());
                this.handleApp(appFolder);
            }
        }

        private void handleApp(File file) throws Exception {
            File[] eventFileArray = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".txt");
                }
            });
            if(eventFileArray==null || eventFileArray.length==0) {
                logger.info("there are no events");
                return;
            }
            logger.info("there are {} events", eventFileArray.length);
            for(int i=0; i<eventFileArray.length; i++) {
                File eventFile = eventFileArray[i];
                logger.info("{}.processing event \"{}\"", i+1, eventFile.getName());
                this.handleEvent(eventFile);
            }
        }

        private void handleEvent(File file) throws Exception {
            Path targetPath = file.toPath().getParent().resolve("in_progress"+File.separator+file.getName());
            Files.createDirectories(targetPath.getParent());
            Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}