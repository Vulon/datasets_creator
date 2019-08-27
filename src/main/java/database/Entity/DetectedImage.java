package database.Entity;


import javax.persistence.*;

@Entity
@Table(name = "detected_image")
public class DetectedImage {
    @Id
    @Column(name = "path", unique = true)
    private String path;

    @Column(name = "object_x1")
    private int object_x1;

    @Column(name = "object_y1")
    private int object_y1;

    @Column(name = "object_x2")
    private int object_x2;

    @Column(name = "object_y2")
    private int object_y2;

    public DetectedImage(String path, int object_x1, int object_y1, int object_x2, int object_y2) {
        this.path = path;
        this.object_x1 = object_x1;
        this.object_y1 = object_y1;
        this.object_x2 = object_x2;
        this.object_y2 = object_y2;
    }

    public void updateData(DetectedImage other){
        this.object_x1 = other.object_x1;
        this.object_y1 = other.object_y1;
        this.object_x2 = other.object_x2;
        this.object_y2 = other.object_y2;
    }

    public DetectedImage() {
    }



    public void setFirstPoint(int x, int y){
        object_x1 = x;
        object_y1 = y;
    }

    public void setSecondPoint(int x, int y){
        object_x2 = x;
        object_y2 = y;
    }

    @Override
    public String toString() {
        return "DetectedImage{" +
                "path='" + path + '\'' +
                ", object_x1=" + object_x1 +
                ", object_y1=" + object_y1 +
                ", object_x2=" + object_x2 +
                ", object_y2=" + object_y2 +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DetectedImage image = (DetectedImage) o;

        if (object_x1 != image.object_x1) return false;
        if (object_y1 != image.object_y1) return false;
        if (object_x2 != image.object_x2) return false;
        if (object_y2 != image.object_y2) return false;
        return path.equals(image.path);

    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + object_x1;
        result = 31 * result + object_y1;
        result = 31 * result + object_x2;
        result = 31 * result + object_y2;
        return result;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getObject_x1() {
        return object_x1;
    }

    public void setObject_x1(int object_x1) {
        this.object_x1 = object_x1;
    }

    public int getObject_y1() {
        return object_y1;
    }

    public void setObject_y1(int object_y1) {
        this.object_y1 = object_y1;
    }

    public int getObject_x2() {
        return object_x2;
    }

    public void setObject_x2(int object_x2) {
        this.object_x2 = object_x2;
    }

    public int getObject_y2() {
        return object_y2;
    }

    public void setObject_y2(int object_y2) {
        this.object_y2 = object_y2;
    }
}
