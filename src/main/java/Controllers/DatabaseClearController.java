package Controllers;

import database.DAO_Implemented.DetectedImageDAOImpl;
import database.Entity.DetectedImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class DatabaseClearController {

    @FXML
    private ListView list_view;

    private ObservableList<DetectedImage> observableList;

    public void init(){
        List<DetectedImage> detectedImages = DetectedImageDAOImpl.getInstance().getAll();
        observableList = FXCollections.observableArrayList();
        if(detectedImages != null){
            observableList.addAll(detectedImages);
        }
        list_view.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            DetectedImage image = (DetectedImage)newValue;
            int count = DetectedImageDAOImpl.getInstance().deleteById(image.getId());
            if(count > 0){
                updateList();
            }
        });
        list_view.setItems(observableList);
        System.out.println("Found " + observableList.size());
    }

    private void updateList(){
        observableList.clear();
        List<DetectedImage> detectedImages = DetectedImageDAOImpl.getInstance().getAll();
        if(detectedImages != null){
            observableList.addAll(detectedImages);
        }
    }


}
