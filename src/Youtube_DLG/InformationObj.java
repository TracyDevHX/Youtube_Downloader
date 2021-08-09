package Youtube_DLG;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class InformationObj implements Serializable {
    private final static InformationObj INSTANCE = new InformationObj();

    private Map<String, String> defaultMap = new HashMap<String, String>() {
        {
            put("audio/format", "mp3");
            put("audio/directory", "");
            put("audio/commandLine", "");
            put("audio/playlist", "false");
            put("video/format", "mp4");
            put("video/resolution", "best");
            put("video/directory", "");
            put("video/commandLine", "");
            put("video/playlist", "false");
            put("default/commandLine", "--ignore-errors\n--no-overwrites\n--continue");
            put("youtube_dl/location", "global");
            put("ffmpeg/location", "global");
        }
    };

    private Map<String, String> audioArgsMap = new HashMap<String, String>() {
        {
            put("audio/format", "mp3");
            put("audio/directory", "");
            put("audio/commandLine", "");
            put("audio/playlist", "false");
        }
    };

    private Map<String, String> videoArgsMap = new HashMap<String, String>() {
        {
            put("video/format", "mp4");
            put("video/resolution", "best");
            put("video/directory", "");
            put("video/commandLine", "");
            put("video/playlist", "false");
        }
    };

    private String[] commands;
    private String urls;
    private String type;

    private InformationObj() {
    }

    public static InformationObj getInstance() {
        return INSTANCE;
    }

    public static void initProperties() {
        Properties properties = new Properties();
        File path = new File("./logs/default.properties");
        try {
            FileInputStream input = new FileInputStream(path);
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            Map<String, String> map = (Map<String, String>) ((Map) properties);

            InformationObj obj = InformationObj.getInstance();
            obj.setDefaultMap(map);
            obj.initArgsMap(map);
        } catch (Exception ignored) {
        }
    }

    public static void saveProperties() {
        Properties properties = new Properties();
        File directory = new File("./logs");
        File path = new File("./logs/default.properties");
        if (path.exists()) {
            return;
        }

        if (!directory.exists()) {
            Object o = directory.mkdir();
        }

        try {
            InformationObj obj = InformationObj.getInstance();
            properties.putAll(obj.getDefaultMap());
            FileOutputStream output = new FileOutputStream(path);
            properties.store(new OutputStreamWriter(output, StandardCharsets.UTF_8), "Youtube_Downloader -by Tracy- Default Settings\n" +
                    "Please restart or refresh the program to let modification take effect");
        } catch (Exception ignored) {
        }
    }

    public String getAudioArgsMap(String key) {
        return this.audioArgsMap.get(key);
    }

    public void addAudioArgsMap(String key, String value) {
        this.audioArgsMap.put(key, value);
    }

    public Map<String, String> exportAudioArgsMap() {
        return this.audioArgsMap;
    }

    public void importAudioArgsMap(Map<String, String> audioArgsMap) {
        this.audioArgsMap = audioArgsMap;
    }

    public String getVideoArgsMap(String key) {
        return this.videoArgsMap.get(key);
    }

    public void addVideoArgsMap(String key, String value) {
        this.videoArgsMap.put(key, value);
    }

    public void importVideoArgsMap(Map<String, String> videoArgsMap) {
        this.videoArgsMap = videoArgsMap;
    }

    public Map<String, String> exportVideoArgsMap() {
        return this.videoArgsMap;
    }

    public String[] getCommands() {
        return this.commands;
    }

    public void setCommands(String[] commands) {
        this.commands = commands;
    }

    public String getUrls() {
        return this.urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getDefaultMap() {
        return this.defaultMap;
    }

    public void setDefaultMap(Map<String, String> defaultMap) {
        this.defaultMap = defaultMap;
    }

    public void initArgsMap(Map<String, String> map) {
        this.audioArgsMap.put("audio/format", map.get("audio/format"));
        this.audioArgsMap.put("audio/directory", map.get("audio/directory"));
        this.audioArgsMap.put("audio/commandLine", map.get("audio/commandLine"));
        this.audioArgsMap.put("audio/playlist", map.get("audio/playlist"));

        this.videoArgsMap.put("video/format", map.get("video/format"));
        this.videoArgsMap.put("video/resolution", map.get("video/resolution"));
        this.videoArgsMap.put("video/directory", map.get("video/directory"));
        this.videoArgsMap.put("video/commandLine", map.get("video/commandLine"));
        this.videoArgsMap.put("video/playlist", map.get("video/playlist"));
    }
}
