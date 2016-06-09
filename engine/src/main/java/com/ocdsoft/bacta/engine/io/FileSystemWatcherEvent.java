package com.ocdsoft.bacta.engine.io;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by crush on 6/8/2016.
 */
@Getter
@AllArgsConstructor
public class FileSystemWatcherEvent {
    private final FileSystemWatcherEventType eventType;
    private final String filePath;
    private final long timestamp = System.currentTimeMillis();
}
