package launch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public Stage getMainStage() {
        return mainStage;
    }

    Stage mainStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        mainStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/fs.fxml"));
        primaryStage.setTitle("Search files");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(1000);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
