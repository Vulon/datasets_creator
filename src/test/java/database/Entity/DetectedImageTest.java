package database.Entity;


import database.DAO_Implemented.DetectedImageDAOImpl;
import database.HibernateSessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assert;


import java.util.ArrayList;

public class DetectedImageTest {
    private static DetectedImageDAOImpl detectedImageDAO;

    @BeforeClass
    public static void loadDAO(){
        detectedImageDAO = DetectedImageDAOImpl.getInstance();
    }

    @Test
    public void testSaveAndLoad(){
        DetectedImage image1 = new DetectedImage("image1.png", 1, 1, 1, 4);
        DetectedImage image2 = new DetectedImage("image2.png", 2, 1, 1, 3);
        DetectedImage image3 = new DetectedImage("image3.png", 1, 1, 4, 1);
        DetectedImage image4 = new DetectedImage("image4.png", 3, 4, 1, 2);
        DetectedImage image5 = new DetectedImage("image5.png", 2, 1, 0, 1);
        ArrayList<DetectedImage> images = new ArrayList<DetectedImage>();
        images.add(image1);
        images.add(image2);
        images.add(image3);
        detectedImageDAO.saveAll(images);
        detectedImageDAO.save(image4);
        detectedImageDAO.save(image5);
        assertEquals(image1, detectedImageDAO.get(image1.getId()));
        assertEquals(image2, detectedImageDAO.get(image2.getId()));
        assertEquals(image3, detectedImageDAO.get(image3.getId()));
        assertEquals(image4, detectedImageDAO.get(image4.getId()));
        assertEquals(image5, detectedImageDAO.get(image5.getId()));
        System.out.println("SaveAndLoad test completed");
    }

    @AfterClass
    public static void shutdown(){
        HibernateSessionFactory.shutdown();
    }
}