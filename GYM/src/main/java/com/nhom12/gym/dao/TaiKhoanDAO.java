package com.nhom12.gym.dao;

import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class TaiKhoanDAO {

    public List<TaiKhoan> layTatCa() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM TaiKhoan ORDER BY tenDangNhap ASC", TaiKhoan.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TaiKhoan timTheoTenDangNhap(String tenDangNhap) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TaiKhoan.class, tenDangNhap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<TaiKhoan> query = session.createQuery(
                "FROM TaiKhoan WHERE tenDangNhap = :user AND matKhau = :pass", TaiKhoan.class
            );
            query.setParameter("user", tenDangNhap);
            query.setParameter("pass", matKhau);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean them(TaiKhoan tk) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(tk);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhat(TaiKhoan tk) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(tk);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(String tenDangNhap) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            TaiKhoan tk = session.get(TaiKhoan.class, tenDangNhap);
            if (tk != null) {
                session.remove(tk);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}
