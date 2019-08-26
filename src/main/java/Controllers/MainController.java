package Controllers;

import Utils.CSVFileTool;
import com.github.sarxos.webcam.Webcam;
import database.DAO_Implemented.DetectedImageDAOImpl;
import database.Entity.DetectedImage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainController {

    @FXML
    private Button browse_button;

    @FXML
    private Button prev_button;

    @FXML
    private Button save_button;

    @FXML
    private Button next_button;

    @FXML
    private Label info_label;

    @FXML
    private ImageView imageView;

    @FXML
    private Button cancel_button;

    @FXML
    private Slider slider_w;

    @FXML
    private Slider slider_h;



    private FileChooser fileChooser;

    private Stage mainStage;

    private ArrayList<File> fileArrayList;

    private DetectedImage currentDetectedImage;

    private int currentFile = 0;

    private Image currentImage;

    @FXML
    private Button webcam_button;

    public void init(Stage mainStage){
        fileChooser = new FileChooser();
        this.mainStage = mainStage;
        fileArrayList = new ArrayList<File>();
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            currentDetectedImage.setObject_x((int)event.getX());
            currentDetectedImage.setObject_y((int)event.getY());
            System.out.println("Click coords: " + event.getX() + " " + event.getY());
            updateSliders();
            drawRectangle();
            event.consume();
        });
        slider_h.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentDetectedImage.setObject_h(newValue.intValue());
            drawRectangle();
        });

        slider_w.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentDetectedImage.setObject_w(newValue.intValue());
            drawRectangle();
        });
        cancel_button.setVisible(false);
        update();
    }

    @FXML
    private void openSaveToCSVDialog(){
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV file with dataset", "*.csv");
        chooser.getExtensionFilters().add(filter);
        File saveFile = chooser.showSaveDialog(mainStage);
        try{
            List<DetectedImage> images = DetectedImageDAOImpl.getInstance().getAll();
            if(images != null){
                CSVFileTool.writeToCSVFile(images, saveFile);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void openFileDialog(){
        fileArrayList.clear();
        List<File> fileList = fileChooser.showOpenMultipleDialog(mainStage);
        if(fileList != null){
            System.out.println("Found " + fileList.size() + " files");
            fileArrayList.addAll(fileList);
            fileArrayList.removeIf(file -> !file.getName().contains(".png") && !file.getName().contains(".jpg"));
        }
        currentFile = 0;
        update();
    }

    @FXML
    private void handlePrev(){
        if (currentFile > 1){
            currentFile -= 1;
        }
        update();
    }

    @FXML
    private void handleNext(){
        if(currentFile + 1 < fileArrayList.size()){
            currentFile += 1;
        }
        update();
    }

    @FXML
    private void handle_save(){
        if(fileArrayList.size() < 1){
            return;
        }
        DetectedImage image = DetectedImageDAOImpl.getInstance().getByPath(fileArrayList.get(currentFile).getPath());
        if (image != null){
            image.updateData(currentDetectedImage);
        }else{
            image = currentDetectedImage;
        }
        DetectedImageDAOImpl.getInstance().save(image);
    }

    @FXML
    private void handle_removeEntries(){
        try{
            Stage stage = new Stage();
            stage.setTitle("Remove Entries");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/database_clear.fxml"));
            Parent parent = loader.load();
            DatabaseClearController controller = loader.getController();
            controller.init();
            Scene scene = new Scene(parent, 800, 500);
            stage.setScene(scene);
            stage.setMinHeight(500);
            stage.setMinWidth(800);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    private File saveWebCamImage(BufferedImage bufferedImage){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
        String now = dateTimeFormatter.format(LocalDateTime.now());
        System.out.println("Calendar value " + now);
        if(fileArrayList.size() > 0){
            String directory = fileArrayList.get(0).getParent();
            System.out.println("WebCam image save directory: " + directory);

            try{
                File output = new File(directory + "/" + now + ".png");
                output.createNewFile();
                ImageIO.write(bufferedImage, "PNG", output);
                return output;
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            try{
                File output = new File("../datasets_creator/images/" + now + ".png");
                output.createNewFile();
                ImageIO.write(bufferedImage, "PNG", output);
                return output;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return  null;
    }

    @FXML
    private void handleWebCam(){
        webcam = Webcam.getDefault();

        if(!webcam.isOpen()){
            webcam.open();
            webcam_button.setText("Save Photo");
            cancel_button.setVisible(true);
        }else{ //Save Photo
            File output = saveWebCamImage(webcam.getImage());
            if(output != null){
                fileArrayList.add(output);
                update();
            }
        }
    }

    @FXML
    private void handleCancel(){
        cancel_button.setVisible(false);
        webcam_button.setText("WebCam");
        webcam.close();
    }

    Webcam webcam;

    private void drawRectangle(){
        if(currentDetectedImage == null || currentImage == null){
            return;
        }

        WritableImage writableImage = new WritableImage(currentImage.getPixelReader(), (int)currentImage.getWidth(), (int)currentImage.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int top = currentDetectedImage.getObject_y();
        int bot = Math.min((int)currentImage.getHeight() - 1, top + currentDetectedImage.getObject_h());
        int left = currentDetectedImage.getObject_x();
        int right = Math.min((int)currentImage.getWidth() - 1, left + currentDetectedImage.getObject_w());

        for(int i = left; i <= right; i++){
            pixelWriter.setColor(i, top, Color.rgb(255, 0, 0));
            pixelWriter.setColor(i, bot, Color.rgb(255, 0, 0));
        }
        for(int i = top; i <= bot; i++){
            pixelWriter.setColor(left, i, Color.rgb(255, 0, 0));
            pixelWriter.setColor(right, i, Color.rgb(255, 0, 0));
        }
        imageView.setImage(writableImage);
    }

    private void update(){
        updateLabel();
        updateImageView();
        updateSliders();
        drawRectangle();
    }

    private void updateLabel(){
        info_label.setText((currentFile + 1) + " file out of " + fileArrayList.size());
    }

    private void updateImageView(){
        System.out.println("FileArrayList size: " + fileArrayList.size());
        currentDetectedImage = null;
        currentImage = null;
        if(fileArrayList.size() < 1){
            return;
        }
        System.out.println("ImageView update for file " + fileArrayList.get(currentFile).getName() + " and path " + fileArrayList.get(currentFile).getPath());
        try(FileInputStream inputStream = new FileInputStream(fileArrayList.get(currentFile))){

            currentImage = new Image(inputStream, 512, 512, true, false);
            System.out.println("Loaded image size " + currentImage.getWidth() + " " + currentImage.getHeight());
            imageView.setImage(currentImage);
            int x = (int)currentImage.getWidth() / 2;
            int y = (int)currentImage.getHeight() / 2;
            int w = (int)slider_w.getValue();
            int h = (int)slider_h.getValue();
            currentDetectedImage = DetectedImageDAOImpl.getInstance().getByPath(fileArrayList.get(currentFile).getAbsolutePath());
            if(currentDetectedImage == null){
                currentDetectedImage = new DetectedImage(fileArrayList.get(currentFile).getPath(), x, y, w, h);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void updateSliders(){
        int max_w;
        int max_h;
        int w;
        int h;
        if (currentImage == null || currentDetectedImage == null){
            max_h = 100;
            max_w = 100;
            w = 40;
            h = 40;
        }else{
            max_w = (int)currentImage.getWidth() - currentDetectedImage.getObject_x();
            max_h = (int)currentImage.getHeight() - currentDetectedImage.getObject_y();
            w = currentDetectedImage.getObject_w();
            h = currentDetectedImage.getObject_h();

        }
        slider_w.setMax(max_w);
        slider_h.setMax(max_h);
        slider_w.setValue(w);
        slider_h.setValue(h);
    }


}
