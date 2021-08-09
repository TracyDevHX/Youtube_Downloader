package Youtube_DLG;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionController implements Initializable {
    @FXML
    public ComboBox<String> audioFormat;
    @FXML
    public TextField audioSaveDirectory;
    @FXML
    public TextArea audioCommandLine;
    @FXML
    public ComboBox<String> videoFormat;
    @FXML
    public ComboBox<String> videoResolution;
    @FXML
    public TextField videoSaveDirectory;
    @FXML
    public TextArea videoCommandLine;
    @FXML
    public Button audioDirectory;
    @FXML
    public Button videoDirectory;
    @FXML
    public RadioButton audioPlaylist;
    @FXML
    public RadioButton videoPlaylist;


    public void audioDirectoryChooser(ActionEvent actionEvent) {
        Stage stage = (Stage) audioDirectory.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            audioSaveDirectory.setText(file.toString());
        }
    }

    public void videoDirectoryChooser(ActionEvent actionEvent) {
        Stage stage = (Stage) videoDirectory.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            videoSaveDirectory.setText(file.toString());
        }
    }

    public void setOption(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        InformationObj temp = InformationObj.getInstance();
        if (stage.getTitle().equals("Audio Options")) {
            if (this.audioFormat.getValue() != null) {
                temp.addAudioArgsMap("audio/format", this.audioFormat.getValue());
            }
            temp.addAudioArgsMap("audio/directory", this.audioSaveDirectory.getText());
            temp.addAudioArgsMap("audio/commandLine", this.audioCommandLine.getText());
            temp.addAudioArgsMap("audio/playlist", String.valueOf(this.audioPlaylist.isSelected()));
            //System.out.println(Arrays.toString(temp.getAudioArgs()));
        } else {
            if (this.videoFormat.getValue() != null) {
                temp.addVideoArgsMap("video/format", this.videoFormat.getValue());
            }
            if (this.videoResolution.getValue() != null) {
                temp.addVideoArgsMap("video/resolution", this.videoResolution.getValue());
            }
            temp.addVideoArgsMap("video/directory", this.videoSaveDirectory.getText());
            temp.addVideoArgsMap("video/commandLine", this.videoCommandLine.getText());
            temp.addVideoArgsMap("video/playlist", String.valueOf(this.videoPlaylist.isSelected()));
            //System.out.println(Arrays.toString(temp.getVideoArgs()));
        }
        stage.close();
    }

    public void cancelOption(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InformationObj temp = InformationObj.getInstance();
        if (this.audioFormat != null) {
            this.audioFormat.setValue(temp.getAudioArgsMap("audio/format"));
            this.audioSaveDirectory.setText(temp.getAudioArgsMap("audio/directory"));
            this.audioCommandLine.setText(temp.getAudioArgsMap("audio/commandLine"));
            this.audioPlaylist.setSelected(Boolean.parseBoolean(temp.getAudioArgsMap("audio/playlist")));
        }

        if (this.videoFormat != null) {
            this.videoFormat.setValue(temp.getVideoArgsMap("video/format"));
            this.videoResolution.setValue(temp.getVideoArgsMap("video/resolution"));
            this.videoSaveDirectory.setText(temp.getVideoArgsMap("video/directory"));
            this.videoCommandLine.setText(temp.getVideoArgsMap("video/commandLine"));
            this.videoPlaylist.setSelected(Boolean.parseBoolean(temp.getVideoArgsMap("video/playlist")));
        }
    }
}
