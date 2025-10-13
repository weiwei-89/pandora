package org.edward.pandora.event_bus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class EventListener {
    private final String path;

    private EventListener(String path) {
        this.path = path;
    }

    public static void register(String path) throws Exception {
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
    }

    public static EventListener acquire(String path) throws Exception {
        File file = new File(path);
        if(!file.exists()) {
            throw new Exception(String.format("path does not exist [%s]", path));
        }
        return new EventListener(path);
    }

    public synchronized void add(Event event) throws Exception {
        OutputStream out = null;
        try {
            out = new FileOutputStream(this.path+File.separator+event.getName()+".txt");
            out.write(event.getName().getBytes());
        } finally {
            if(out != null) {
                out.flush();
                out.close();
            }
        }
    }
}