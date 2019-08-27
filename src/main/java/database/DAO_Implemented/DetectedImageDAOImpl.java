package database.DAO_Implemented;

import database.DAO_Interfaces.DetectedImageDAO;
import database.Entity.DetectedImage;
import database.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class DetectedImageDAOImpl implements DetectedImageDAO {
    SessionFactory sessionFactory;
    Session session = null;

    private static DetectedImageDAOImpl self = new DetectedImageDAOImpl();

    public static DetectedImageDAOImpl getInstance(){
        return self;
    }

    private DetectedImageDAOImpl(){
        sessionFactory = HibernateSessionFactory.getSessionFactory();
    }

    public DetectedImage get(Integer id) {
        startSession();
        session.beginTransaction();
        DetectedImage image = session.get(DetectedImage.class, id);
        session.getTransaction().commit();
        return image;
    }

    public void save(DetectedImage image) {
        startSession();
        session.beginTransaction();
        session.saveOrUpdate(image);
        session.getTransaction().commit();
    }

    public void saveAll(List<DetectedImage> imageList) {
        startSession();
        session.beginTransaction();
        for(DetectedImage image : imageList){
            session.saveOrUpdate(image);
        }
        session.getTransaction().commit();
    }

    public void startSession() {
        if(session == null){
            session = sessionFactory.openSession();
        }
    }

    public void endSession() {
        if(session != null){
            session.close();
            session = null;
        }
    }

    @Override
    public DetectedImage getByPath(String path) {
        startSession();
        session.beginTransaction();
        Query query = session.createQuery("from DetectedImage where path = :param");
        query.setParameter("param", path);
        List<DetectedImage> list = query.list();
        session.getTransaction().commit();
        if(list == null || list.size() < 1){
            return null;
        }else{
            return list.get(0);
        }
    }

    @Override
    public List<DetectedImage> getAll() {
        startSession();
        session.beginTransaction();
        Query query = session.createQuery("from DetectedImage");
        List<DetectedImage> images = query.list();
        session.getTransaction().commit();
        return images;
    }

    @Override
    public int deleteById(Integer id) {
        startSession();
        session.beginTransaction();
        Query query = session.createQuery("delete DetectedImage where id = :param");
        query.setParameter("param", id);
        int count = query.executeUpdate();
        session.getTransaction().commit();
        return count;
    }

    public int deleteAll(){
        startSession();
        session.beginTransaction();
        Query query = session.createQuery("delete DetectedImage");
        int count = query.executeUpdate();
        session.getTransaction().commit();
        return count;
    }
}
