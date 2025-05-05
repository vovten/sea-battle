package vovten;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import vovten.gui.MainController;

/**
 * Main
 */
public class Main extends Application {
    private static final double SCENE_WIDTH = 569;
    private static final double SCENE_HEIGHT = 465;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setMainStage(stage);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Морской бой");
        stage.getIcons().add(new Image("ship.png"));
        stage.setResizable(false);
        stage.setOnCloseRequest((event) -> {
            controller.showConfirmDialogExitGame();
            event.consume();
        });
        stage.show();
    }
}
