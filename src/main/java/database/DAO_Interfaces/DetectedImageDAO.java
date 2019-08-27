package database.DAO_Interfaces;

import database.Entity.DetectedImage;

import java.util.List;

public interface DetectedImageDAO {
    void save(DetectedImage image);

    DetectedImage get(Integer id);

    void startSession();

    void endSession();

    void saveAll(List<DetectedImage> imageList);

    DetectedImage getByPath(String path);

    List<DetectedImage> getAll();

    int deleteByPath(String path);
}
