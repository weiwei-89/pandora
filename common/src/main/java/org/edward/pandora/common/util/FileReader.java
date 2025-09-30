package org.edward.pandora.common.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileReader {
    private final TextFilter filter;

    public FileReader() {
        this.filter = new DefaultFilter();
    }

    public FileReader(TextFilter filter) {
        this.filter = filter;
    }

    public String read(String path) throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(path),
                            StandardCharsets.UTF_8
                    )
            );
            StringBuilder sb = new StringBuilder();
            String line = null;
            while(true) {
                line = reader.readLine();
                if(line == null) {
                    break;
                }
                if(this.filter != null) {
                    line = this.filter.process(line);
                }
                sb.append(line);
            }
            return sb.toString();
        } finally {
            if(reader != null) {
                reader.close();
            }
        }
    }

    public static class DefaultFilter implements TextFilter {
        private static final String[] DEFAULT_COMMENT_MARKERS = {"//", "#", "--", "**"};

        @Override
        public String process(String text) {
            int index = FileReader.search(text, DEFAULT_COMMENT_MARKERS);
            if(index < 0) {
                return text;
            }
            return text.substring(0, index);
        }
    }

    public static int search(String data, String[] markers) {
        int index = -1;
        for(String marker : markers) {
            index = data.indexOf(marker);
            if(index >= 0) {
                return index;
            }
        }
        return -1;
    }
}