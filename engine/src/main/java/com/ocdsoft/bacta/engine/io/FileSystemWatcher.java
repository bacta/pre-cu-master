package com.ocdsoft.bacta.engine.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;
import java.util.function.Consumer;

import static com.ocdsoft.bacta.engine.io.FileSystemWatcherEventType.CREATE;
import static com.ocdsoft.bacta.engine.io.FileSystemWatcherEventType.DELETE;
import static com.ocdsoft.bacta.engine.io.FileSystemWatcherEventType.MODIFY;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by crush on 6/6/2016.
 */
public final class FileSystemWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemWatcher.class);

    private final Path filePath;
    private final Consumer<Path> createCallback;
    private final Consumer<Path> deleteCallback;
    private final Consumer<Path> modifyCallback;
    private final Consumer<FileSystemWatcherEvent> genericCallback;
    private volatile boolean watching;

    /**
     * Creates a new FileSystemWatcher which monitors a directory for changes to files. If a file is created, added,
     * or deleted, then the appropriate callback is executed. Call {@link #start()} to start watching. An infinite loop
     * will run until it is told to {@link #stop()} or an exception is thrown.
     *
     * @param filePath       The directory to be watched.
     * @param createCallback The callback to be executed when a file is created in the directory.
     * @param deleteCallback The callback to be executed when a file is deleted in the directory.
     * @param modifyCallback The callback to be executed when a file is modified in the directory.
     */
    public FileSystemWatcher(final Path filePath,
                             final Consumer<Path> createCallback,
                             final Consumer<Path> deleteCallback,
                             final Consumer<Path> modifyCallback,
                             final Consumer<FileSystemWatcherEvent> genericCallback) {
        this.filePath = filePath;
        this.createCallback = createCallback;
        this.deleteCallback = deleteCallback;
        this.modifyCallback = modifyCallback;
        this.genericCallback = genericCallback;
        this.watching = false;
    }

    /**
     * Starts an infinite loop monitoring the configured directory. Will continue to monitor and call the configured
     * callbacks until either {@link #stop()} is called or an exception is thrown.
     */
    public void start() {
        if (watching) {
            LOGGER.error("This instance is already watching. Create a new instance.");
            return;
        }

        new Thread(() -> {
            try (final WatchService watcher = FileSystems.getDefault().newWatchService()) {
                LOGGER.debug("Watching {} for file changes.", filePath.toString());

                filePath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

                watching = true;

                while (watching) {
                    final WatchKey watchKey = watcher.take();

                    for (final WatchEvent<?> event : watchKey.pollEvents()) {
                        final WatchEvent.Kind<?> kind = event.kind();

                        if (StandardWatchEventKinds.OVERFLOW.equals(kind))
                            continue;

                        final WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;
                        final Path filePath = watchEvent.context();

                        final FileSystemWatcherEvent fileSystemWatcherEvent;

                        //It has to be one of these three events.
                        if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind)) {
                            fileSystemWatcherEvent = new FileSystemWatcherEvent(CREATE, filePath.toString());
                        } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind)) {
                            fileSystemWatcherEvent = new FileSystemWatcherEvent(DELETE, filePath.toString());
                        } else {
                            fileSystemWatcherEvent = new FileSystemWatcherEvent(MODIFY, filePath.toString());
                        }



                        if (genericCallback != null)
                            genericCallback.accept(fileSystemWatcherEvent);

                        if (CREATE.equals(fileSystemWatcherEvent.getEventType()) && createCallback != null)
                            createCallback.accept(filePath);
                        else if (DELETE.equals(fileSystemWatcherEvent.getEventType()) && deleteCallback != null)
                            deleteCallback.accept(filePath);
                        else if (MODIFY.equals(fileSystemWatcherEvent.getEventType()) && modifyCallback != null)
                            modifyCallback.accept(filePath);
                    }

                    final boolean valid = watchKey.reset();

                    if (!valid) {
                        watching = false;
                        break;
                    }
                }

                watching = false;
                LOGGER.debug("Not watching for file changes anymore.");
            } catch (final Exception ex) {
                LOGGER.error("Unexpectedly stopped watching: {}", ex.getMessage());
            }
        }).start();
    }

    /**
     * Stops the infinite loop monitoring for file changes.
     */
    public void stop() {
        LOGGER.debug("Requested to stop watching for file changes.");
        watching = false;
    }
}
