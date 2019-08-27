package Controllers;

import Utils.CSVFileTool;
import database.DAO_Implemented.DetectedImageDAOImpl;
import database.Entity.DetectedImage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private Label name_label;

    @FXML
    private ImageView imageView;

    public static final int IMG_HEIGHT = 144;
    public static final int IMG_WIDTH = 176;



    private FileChooser fileChooser;

    private Stage mainStage;

    public ArrayList<File> fileArrayList;

    private DetectedImage currentDetectedImage;

    private int currentFile = 0;

    private Image currentImage;

    @FXML
    private Button webcam_button;

    @FXML
    private void handle_webcam(){
        try{
            Stage stage = new Stage();
            stage.setTitle("Webcam Feed");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/webcam.fxml"));
            Parent parent = loader.load();
            WebcamFeedController controller = loader.getController();
            String path;
            if(fileArrayList.size() < 1){
                path = "../datasets_creator/images";
            }else{
                path = fileArrayList.get(0).getParent();
            }

            controller.init(path, this);
            Scene scene = new Scene(parent, 850, 600);
            stage.setScene(scene);
            stage.setMinHeight(600);
            stage.setMinWidth(850);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    public void init(Stage mainStage){

        fileChooser = new FileChooser();
        this.mainStage = mainStage;
        fileArrayList = new ArrayList<File>();
        imageView.setFitHeight(IMG_HEIGHT);
        imageView.setFitWidth(IMG_WIDTH);
        imageView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            System.out.println("First point: " + event.getX() + " " + event.getY());
            currentDetectedImage.setFirstPoint((int)event.getX(), (int)event.getY());
        });
        imageView.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            System.out.println("Second point: " + event.getX() + " " + event.getY());
            currentDetectedImage.setSecondPoint((int)event.getX(), (int)event.getY());
            drawRectangle();
        });
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
        if (currentFile > 0){
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
//
//    private File saveWebCamImage(BufferedImage bufferedImage){
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
//        String now = dateTimeFormatter.format(LocalDateTime.now());
//        System.out.println("Calendar value " + now);
//        if(fileArrayList.size() > 0){
//            String directory = fileArrayList.get(0).getParent();
//            System.out.println("WebCam image save directory: " + directory);
//
//            try{
//                File output = new File(directory + "/" + now + ".png");
//                output.createNewFile();
//                ImageIO.write(bufferedImage, "PNG", output);
//                return output;
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//        }else{
//            try{
//                File output = new File("../datasets_creator/images/" + now + ".png");
//                output.createNewFile();
//                ImageIO.write(bufferedImage, "PNG", output);
//                return output;
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//        return  null;
//    }



    private void drawRectangle(){
        if(currentDetectedImage == null || currentImage == null){
            return;
        }

        WritableImage writableImage = new WritableImage(currentImage.getPixelReader(), (int)currentImage.getWidth(), (int)currentImage.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        int top = Math.min(currentDetectedImage.getObject_y1(), currentDetectedImage.getObject_y2());
        int bot = Math.max(currentDetectedImage.getObject_y1(), currentDetectedImage.getObject_y2());
        int left = Math.min(currentDetectedImage.getObject_x1(), currentDetectedImage.getObject_x2());
        int right = Math.max(currentDetectedImage.getObject_x1(), currentDetectedImage.getObject_x2());
        top = Math.max(top, 0);
        bot = Math.min(bot, (int)currentImage.getHeight() - 1);
        left = Math.max(left, 0);
        right = Math.min(right, (int)currentImage.getWidth() - 1);
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

    public void update(){

        updateImageView();
        updateLabel();
        drawRectangle();
    }

    private void updateLabel(){
        if(fileArrayList.size() < 1 || currentDetectedImage == null){
            info_label.setText("No files selected");
            name_label.setText("-");
        }else{
            info_label.setText((currentFile + 1) + " file out of " + fileArrayList.size());
            name_label.setText(
                    currentDetectedImage.getPath());
        }

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
            currentImage = new Image(inputStream, IMG_WIDTH, IMG_HEIGHT, true, false);
            imageView.setImage(currentImage);
            int x1 = (int)currentImage.getWidth() / 2;
            int y1 = (int)currentImage.getHeight() / 2;
            int x2 = (int)currentImage.getWidth() - 1;
            int y2 = (int)currentImage.getHeight() - 1;
            currentDetectedImage = DetectedImageDAOImpl.getInstance().getByPath(fileArrayList.get(currentFile).getAbsolutePath());
            if(currentDetectedImage == null){
                currentDetectedImage = new DetectedImage(fileArrayList.get(currentFile).getPath(), x1, y1, x2, y2);
                System.out.println("CREATED new DetectedImage " + currentDetectedImage);
            }else{
                System.out.println("LOADED old DetectedImage" + currentDetectedImage);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }



}
