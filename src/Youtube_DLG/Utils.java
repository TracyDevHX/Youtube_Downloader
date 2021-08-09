package Youtube_DLG;

import java.text.MessageFormat;

public class Utils {
    public static String setCommand(String url) {
        InformationObj temp = InformationObj.getInstance();
        StringBuilder stringBuilder;
        if (temp.getDefaultMap().get("youtube_dl/location").equals("global")) {
            stringBuilder = new StringBuilder("youtube-dl ");
        } else {
            stringBuilder = new StringBuilder(temp.getDefaultMap().get("youtube_dl/location"));
            stringBuilder.append("youtube-dl ");
        }

        if (!temp.getDefaultMap().get("ffmpeg/location").equals("global")) {
            stringBuilder.append("--ffmpeg-location ");
            stringBuilder.append(temp.getDefaultMap().get("ffmpeg/location")).append(" ");
        }

        stringBuilder.append("\"").append(url).append("\" ");
        for (String command : temp.getDefaultMap().get("default/commandLine").split("\n")) {
            stringBuilder.append(command).append(" ");
        }

        if (temp.getType().equals("audio")) {

            if (Boolean.parseBoolean(temp.getAudioArgsMap("audio/playlist"))) {
                stringBuilder.append("--yes-playlist ");
            } else {
                stringBuilder.append("--no-playlist ");
            }
            stringBuilder.append("--extract-audio ");
            stringBuilder.append("--audio-format ");
            stringBuilder.append(temp.getAudioArgsMap("audio/format")).append(" ");
            stringBuilder.append("-o \"").append(temp.getAudioArgsMap("audio/directory")).append("\\audio\\%(title)s.%(ext)s\" ");
            for (String command : temp.getAudioArgsMap("audio/commandLine").split("\n")) {
                stringBuilder.append(command.trim()).append(" ");
            }
        } else {
            if (Boolean.parseBoolean(temp.getVideoArgsMap("video/playlist"))) {
                stringBuilder.append("--yes-playlist ");
            } else {
                stringBuilder.append("--no-playlist ");
            }

            String text;
            Object[] arguments;
            if (temp.getVideoArgsMap("video/resolution").equals("best")) {
                stringBuilder.append("-f \"bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best\" ");
            } else {
                text = "-f \"bestvideo[height<={0}]+bestaudio[ext=m4a]/best[height<={0}]/best\" ";
                arguments = new Object[]{temp.getVideoArgsMap("video/resolution").
                        substring(0, temp.getVideoArgsMap("video/resolution").length() - 1)};
                stringBuilder.append(MessageFormat.format(text, arguments));
            }
            stringBuilder.append("--merge-output-format ").append(temp.getVideoArgsMap("video/format")).append(" ");
            stringBuilder.append("-o \"").append(temp.getVideoArgsMap("video/directory")).append("\\video\\%(title)s.%(ext)s\" ");
            for (String command : temp.getVideoArgsMap("video/commandLine").split("\n")) {
                stringBuilder.append(command.trim()).append(" ");
            }
        }

        return stringBuilder.toString();
    }

    public static double toPercent(String line) {
        //System.out.println(line);
        String[] words = line.split(" ");
        if (words[0].equals("[download]")) {
            String percent = null;
            for (String word : words) {
                if (word.contains("%")) {
                    percent = word;
                }
            }

            if (percent == null) {
                return -1;
            }
            //System.out.println(percent);
            String p = percent.substring(0, percent.length() - 1);
            //System.out.println(Double.parseDouble(p));
            return Double.parseDouble(p);
        }
        return -1;
    }

    public static boolean isHome(String line) {
        return line.contains("Home");
    }

    public static double toOverallPercent(String line) {
        if (line.contains("Downloading video")) {
            String[] words = line.split(" ");
            int total = Integer.parseInt(words[words.length - 1]);
            int single = Integer.parseInt(words[words.length - 3]);
            return (double) single / total;
        }
        return -1;
    }

    public static String getDestination(String line, boolean isAudio) {
        if (line.contains("Destination: ")) {
            String[] words = line.split("Destination:");
            String[] paths = words[words.length - 1].split("\\\\");
            String name = paths[paths.length - 1];
            String Name;
            if (isAudio) {
                Name = name.substring(0, name.length() - 4);
            } else {
                if (name.contains("webm")) {
                    Name = name.substring(0, name.length() - 10);
                } else {
                    Name = name.substring(0, name.length() - 9);
                }
            }
            return "Downloading: " + Name;
        }
        return "none";
    }
}
