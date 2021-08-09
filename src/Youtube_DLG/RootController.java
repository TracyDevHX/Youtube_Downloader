package Youtube_DLG;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class RootController implements Initializable {
    private final List<String> logs = new ArrayList<>();
    @FXML
    public RadioButton audio;
    @FXML
    public RadioButton video;
    @FXML
    public TextArea urls;
    @FXML
    public ProgressBar threadProgressBar;
    @FXML
    public ProgressBar entireProgressBar;
    @FXML
    public Label threadMessage;
    @FXML
    public Label entireMessage;
    @FXML
    public Label title;
    @FXML
    public Label openedTitle;

    private Downloader downloader;
    private File fileSaved;
    private boolean stopped = false;
    private boolean saved = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void optionWindow(ActionEvent actionEvent) {
        Parent root;
        try {
            Button button = (Button) actionEvent.getSource();
            Stage primaryStage = (Stage) button.getScene().getWindow();
            Stage stage = new Stage();

            if (audio.isSelected()) {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("AudioOptionWindow.fxml")));
                stage.setScene(new Scene(root, 585, 400));
                stage.setTitle("Audio Options");
            } else {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("VideoOptionWindow.fxml")));
                stage.setScene(new Scene(root, 585, 450));
                stage.setTitle("Video Options");
            }

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(primaryStage);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download(ActionEvent actionEvent) {
        this.stopped = false;
        InformationObj temp = InformationObj.getInstance();
        if (audio.isSelected()) {
            temp.setType("audio");
        } else {
            temp.setType("video");
        }
        String[] urlList = urls.getText().split("\n");
        boolean check = true;
        for (String url : urlList) {
            if (!url.equals("")) {
                check = false;
                break;
            }
        }
        if (check) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-font-family: 'Malgun Gothic';");
            alert.setTitle("Input Parameter Warning");
            alert.setHeaderText("Missing Parameters");
            alert.setContentText("Please fill in the URLs area");
            alert.showAndWait();
            return;
        }

        List<String> list = new ArrayList<>();
        for (String url : urlList) {
            String command = Utils.setCommand(url);
            list.add(command);
        }
        String[] commands = new String[list.size()];
        list.toArray(commands);
        //System.out.println(Arrays.toString(commands));
        temp.setCommands(commands);
        this.downloader = new Downloader();
        this.downloader.start();
    }

    public void restart(ActionEvent actionEvent) {
        if (this.downloader != null) {
            if (!this.stopped) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-font-family: 'Malgun Gothic';");
                alert.setTitle("Risky thread access warning");
                alert.setHeaderText("Restarting before halting is not allowed");
                alert.setContentText("Please click the stop button before restarting the thread");
                alert.showAndWait();
            } else {
                this.downloader.restart();
                this.stopped = false;
            }
        }
    }

    public void stop(ActionEvent actionEvent) {
        this.stopped = true;
        if (this.downloader != null) {
            this.downloader.cancel();
        }
    }

    public void newWindow(ActionEvent actionEvent) throws IOException {
        Stage dialog = new Stage(StageStyle.DECORATED);
        dialog.initModality(Modality.NONE);

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("RootWindow.fxml")));
        dialog.setTitle("Youtube Downloader -by Tracy-");
        dialog.setScene(new Scene(root, 1280, 750));
        dialog.setResizable(false);
        dialog.show();
    }

    public void save(ActionEvent actionEvent) {
        if (!saved) {
            this.saveAs(actionEvent);
        } else {
            File file = this.fileSaved;
            try {
                InformationObj obj = InformationObj.getInstance();
                obj.setUrls(urls.getText());
                OutputStream outputStream = new FileOutputStream(file.toString());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(obj);
                objectOutputStream.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAs(ActionEvent actionEvent) {
        MenuItem item = (MenuItem) actionEvent.getSource();
        Stage stage = (Stage) item.getParentPopup().getOwnerWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Youtube Downloader Cache Files", "*.ydc")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                InformationObj obj = InformationObj.getInstance();
                if (audio.isSelected()) {
                    obj.setType("audio");
                } else {
                    obj.setType("video");
                }
                obj.setUrls(urls.getText());
                OutputStream outputStream = new FileOutputStream(file.toString());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(obj);
                this.saved = true;
                this.fileSaved = file;
                String format = "{0}  [{1}]";
                Object[] arg = {this.fileSaved.getName(), this.fileSaved.getParent()};
                this.openedTitle.setText(MessageFormat.format(format, arg));
                objectOutputStream.close();
                outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void open(ActionEvent actionEvent) {
        MenuItem item = (MenuItem) actionEvent.getSource();
        Stage stage = (Stage) item.getParentPopup().getOwnerWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Youtube Downloader Cache Files", "*.ydc")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                InputStream inputStream = new FileInputStream(file.toString());
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                InformationObj newObj = (InformationObj) objectInputStream.readObject();
                InformationObj obj = InformationObj.getInstance();
                obj.importAudioArgsMap(newObj.exportAudioArgsMap());
                obj.importVideoArgsMap(newObj.exportVideoArgsMap());
                obj.setDefaultMap(newObj.getDefaultMap());
                obj.setCommands(newObj.getCommands());
                obj.setType(newObj.getType());
                obj.setUrls(newObj.getUrls());

                this.urls.setText(newObj.getUrls());
                if (newObj.getType().equals("audio")) {
                    this.audio.setSelected(true);
                    this.video.setSelected(false);
                } else {
                    this.audio.setSelected(false);
                    this.video.setSelected(true);
                }
                this.saved = true;
                this.fileSaved = file;
                String format = "{0}  [{1}]";
                Object[] arg = {this.fileSaved.getName(), this.fileSaved.getParent()};
                this.openedTitle.setText(MessageFormat.format(format, arg));
                objectInputStream.close();
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void viewLogs(ActionEvent actionEvent) {
        File file = new File("./logs/ydl_log.txt");
        if (!file.exists()) {
            try {
                File directory = new File("./logs");
                if (!directory.exists()) {
                    Object o = directory.mkdir();
                }

                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Runtime rs = Runtime.getRuntime();
        try {
            rs.exec("notepad " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearLogs(ActionEvent actionEvent) {
        File file = new File("./logs/ydl_log.txt");
        Alert alert;
        if (file.exists()) {
            boolean success = file.delete();
            if (success) {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-font-family: 'Malgun Gothic';");
                alert.setTitle("Log removal success");
                alert.setHeaderText("Download logs has been removed");
                alert.setContentText("Successfully removed all logs from the ./logs folder");
            } else {
                alert = new Alert(Alert.AlertType.WARNING);
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-font-family: 'Malgun Gothic';");
                alert.setTitle("Log removal error");
                alert.setHeaderText("Unable to delete logs");
                alert.setContentText("Please shut down all applications that use the log file, and try again");
            }
        } else {
            alert = new Alert(Alert.AlertType.WARNING);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-font-family: 'Malgun Gothic';");
            alert.setTitle("Log removal error");
            alert.setHeaderText("Unable to delete logs");
            alert.setContentText("Log files not existing in the ./logs folder");
        }
        alert.showAndWait();
    }

    public void aboutTheProgram(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: 'Malgun Gothic';");
        alert.setTitle("About Youtube Downloader -by Tracy-");
        alert.setHeaderText("Information about the application");
        alert.setContentText("Based on youtube-dl executable, this application is developed to let users easily download videos in youtube with diverse options. " +
                "This program is programmed with Java 16.0.1. and JavaFX.");
        alert.showAndWait();
    }

    public void openConsole(ActionEvent actionEvent) {
        try {
            Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"youtube-dl --help\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh(ActionEvent actionEvent) {
        InformationObj.saveProperties();
        InformationObj.initProperties();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: 'Malgun Gothic';");
        alert.setTitle("Refresh completed");
        alert.setHeaderText("Load successful");
        alert.setContentText("Successfully loaded default information and generated .properties file");
        alert.showAndWait();
    }

    public void openProperties(ActionEvent actionEvent) {
        File file = new File("./logs/default.properties");
        if (!file.exists()) {
            InformationObj.saveProperties();
        }

        Runtime rs = Runtime.getRuntime();
        try {
            rs.exec("notepad " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Downloader extends Service<Void> {
        InformationObj temp = InformationObj.getInstance();

        @Override
        protected Task<Void> createTask() {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    Platform.runLater(() -> {
                        entireProgressBar.setProgress(0);
                        updateProgress(0, 100);
                        entireMessage.setText("Overall Download Status: [READY]");
                        threadMessage.setText("Thread Download Status: [READY]");
                    });

                    String[] commands = temp.getCommands();
                    boolean isAudio = true;
                    for (String command : commands) {
                        if (!command.contains("--extract-audio")) {
                            isAudio = false;
                        }
                        RootController.this.logs.add("\n<Command>\n" + command);
                        RootController.this.logs.add("==========================\n");
                        RootController.this.logs.add("<Download Logs>");
                        RootController.this.logs.add("==========================\n");
                        try {
                            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
                            builder.redirectErrorStream(true);
                            Process p = builder.start();
                            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                            String line;
                            int check = 0;
                            while (true) {
                                line = r.readLine();
                                if (line == null) {
                                    break;
                                }

                                RootController.this.logs.add(line);

                                if (!Utils.getDestination(line, isAudio).equals("none")) {
                                    String result = Utils.getDestination(line, isAudio);
                                    Platform.runLater(() -> title.setText(result));
                                }

                                if (check != 1 && Utils.toOverallPercent(line) != -1) {
                                    String finalLine = line;
                                    Platform.runLater(() -> {
                                        entireProgressBar.setProgress(Utils.toOverallPercent(finalLine));
                                        entireMessage.setText(finalLine);
                                    });
                                }

                                if (Utils.isHome(line)) {
                                    check = 1;
                                } else {
                                    check = 0;
                                }

                                if (Utils.toPercent(line) != -1) {
                                    updateProgress(Utils.toPercent(line), 100.0);
                                }
                                String finalLine1 = line;
                                Platform.runLater(() -> threadMessage.setText(finalLine1));

                                if (isCancelled()) {
                                    p.destroy();
                                    r.close();
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            File directory = new File("./logs");
                            if (!directory.exists()) {
                                Object o = directory.mkdir();
                            }

                            FileWriter fw = new FileWriter("./logs/ydl_log.txt", true);
                            BufferedWriter bw = new BufferedWriter(fw);
                            for (String log_line : RootController.this.logs) {
                                bw.write(log_line + "\n");
                            }
                            bw.close();
                            fw.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        threadProgressBar.progressProperty().unbind();
                        threadMessage.setText("Completed!");
                        entireMessage.setText("Completed!");
                        threadProgressBar.setProgress(100);
                        entireProgressBar.setProgress(100);
                    });
                }

                @Override
                protected void cancelled() {
                    Platform.runLater(() -> {
                        threadProgressBar.progressProperty().unbind();
                        threadProgressBar.setProgress(0);
                        entireProgressBar.setProgress(0);
                    });
                }
            };

            threadProgressBar.progressProperty().bind(task.progressProperty());
            return task;
        }
    }
}



