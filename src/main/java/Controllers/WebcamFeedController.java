package Controllers;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebcamFeedController {

    @FXML
    private ImageView image_view;

    private Webcam webcam;

    private Thread videoThread;

    private Thread recordThread;

    private AtomicBoolean running;

    private String savePath;

    private MainController mainController;


    public void init(String savePath, MainController mainController){
        this.mainController = mainController;
        running = new AtomicBoolean(true);
        recording = new AtomicBoolean(false);

        webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(MainController.IMG_WIDTH, MainController.IMG_HEIGHT));
        webcam.open();
        this.savePath = savePath;
        image_view.setFitWidth(MainController.IMG_WIDTH);
        image_view.setFitHeight(MainController.IMG_HEIGHT);
        videoThread = new Thread(()->{
            while (running.get()){
                try{
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    e.printStackTrace();
                    break;
                }

                Platform.runLater(()->{
                    if(running.get()){
                        Image image = SwingFXUtils
                                .toFXImage(
                                        webcam.getImage(), null);
                        image_view.setImage(image);
                    }
                });
            }
        });
         videoThread.start();
        recordThread = new Thread(()->{
            while (running.get()){
                try{
                    Thread.sleep(1500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                    break;
                }
                if(recording.get()){
                    Platform.runLater(()-> save_button.fire());
                }
            }
        });
        recordThread.start();
        System.out.println("Thread started");
    }

    @FXML
    private void handleSave(){
        BufferedImage image = webcam.getImage();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss_SSS");
        String now = dateTimeFormatter.format(LocalDateTime.now());
        try{
            File file = new File(savePath + "/" + now + ".png");
            file.createNewFile();
            ImageIO.write(image, "PNG", file);
            mainController.fileArrayList.add(file);
            mainController.update();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(){
        closeWebCam();
        image_view.getScene().getWindow().hide();
    }

    @FXML
    private Button record_button;

    @FXML
    private Button save_button;

    private AtomicBoolean recording;

    @FXML
    public void handleRecord(){
        if(recording.get()){
            System.out.println("Stop recording");
            record_button.setStyle("-fx-background-color: grey");
            recording.set(false);
        }else{
            System.out.println("Start recording");
            record_button.setStyle("-fx-background-color: red");
            recording.set(true);
        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeWebCam();
    }

    public void closeWebCam(){
        webcam.close();
        running.set(false);
        recording.set(false);
        recordThread.interrupt();
        videoThread.interrupt();
    }


}
