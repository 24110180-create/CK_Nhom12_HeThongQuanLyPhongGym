package com.nhom12.gym.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
   private static final SessionFactory sessionFactory = buildSessionFactory();

   private static SessionFactory buildSessionFactory() {
      try {
         return (new Configuration()).configure().buildSessionFactory();
      } catch (Throwable ex) {
         System.err.println("Lỗi khởi tạo SessionFactory: " + String.valueOf(ex));
         throw new ExceptionInInitializerError(ex);
      }
   }

   public static SessionFactory getSessionFactory() {
      return sessionFactory;
   }
}
