package Youtube_DLG;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;


public class AppMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        InformationObj.saveProperties();
        InformationObj.initProperties();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("RootWindow.fxml")));
        primaryStage.setTitle("Youtube Downloader -by Tracy-");
        primaryStage.setScene(new Scene(root, 1280, 750));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
