import Controllers.MainController;
import database.HibernateSessionFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public void start(Stage primaryStage) throws Exception {
        HibernateSessionFactory.getSessionFactory();
        Stage stage = new Stage();
        stage.setTitle("Main");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/mainscene.fxml"));
        Parent parent = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        controller.init(stage);
        Scene scene = new Scene(parent, 800, 700);
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(700);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
