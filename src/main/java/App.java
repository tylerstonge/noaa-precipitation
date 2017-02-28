import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the layout
        Parent root = FXMLLoader.load(getClass().getResource("/mainlayout.xml"));

        // Create and show scene
        Scene scene = new Scene(root);
        stage.setTitle("NOAA Precipitation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
