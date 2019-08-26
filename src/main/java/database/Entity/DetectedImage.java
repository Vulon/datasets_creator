package database.Entity;


import javax.persistence.*;

@Entity
@Table(name = "detected_image")
public class DetectedImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @Column(name = "path", unique = true)
    private String path;

    @Column(name = "object_x")
    private int object_x;

    @Column(name = "object_y")
    private int object_y;

    @Column(name = "object_w")
    private int object_w;

    @Column(name = "object_h")
    private int object_h;

    public DetectedImage(String path, int object_x, int object_y, int object_w, int object_h) {
        this.path = path;
        this.object_x = object_x;
        this.object_y = object_y;
        this.object_w = object_w;
        this.object_h = object_h;
    }

    public void updateData(DetectedImage other){
        this.object_x = other.object_x;
        this.object_y = other.object_y;
        this.object_w = other.object_w;
        this.object_h = other.object_h;
    }

    public DetectedImage() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetectedImage that = (DetectedImage) o;
        if (object_x != that.object_x) return false;
        if (object_y != that.object_y) return false;
        if (object_w != that.object_w) return false;
        if (object_h != that.object_h) return false;
        if (!id.equals(that.id)) return false;
        return path.equals(that.path);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + object_x;
        result = 31 * result + object_y;
        result = 31 * result + object_w;
        result = 31 * result + object_h;
        return result;
    }

    @Override
    public String toString() {
        return "DetectedImage{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", object_x=" + object_x +
                ", object_y=" + object_y +
                ", object_w=" + object_w +
                ", object_h=" + object_h +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getObject_x() {
        return object_x;
    }

    public void setObject_x(int object_x) {
        this.object_x = object_x;
    }

    public int getObject_y() {
        return object_y;
    }

    public void setObject_y(int object_y) {
        this.object_y = object_y;
    }

    public int getObject_w() {
        return object_w;
    }

    public void setObject_w(int object_w) {
        this.object_w = object_w;
    }

    public int getObject_h() {
        return object_h;
    }

    public void setObject_h(int object_h) {
        this.object_h = object_h;
    }
}
