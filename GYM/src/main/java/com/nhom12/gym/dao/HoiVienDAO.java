package com.nhom12.gym.dao;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class HoiVienDAO {
   public List<HoiVien> layTatCa() {
      try {
         Session session = HibernateUtil.getSessionFactory().openSession();

         List var2;
         try {
            var2 = session.createQuery("FROM HoiVien ORDER BY maHoiVien DESC", HoiVien.class).list();
         } catch (Throwable var5) {
            if (session != null) {
               try {
                  session.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (session != null) {
            session.close();
         }

         return var2;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public boolean them(HoiVien hv) {
      Transaction transaction = null;

      try {
         Session session = HibernateUtil.getSessionFactory().openSession();

         boolean var4;
         try {
            transaction = session.beginTransaction();
            session.persist(hv);
            transaction.commit();
            var4 = true;
         } catch (Throwable var7) {
            if (session != null) {
               try {
                  session.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (session != null) {
            session.close();
         }

         return var4;
      } catch (Exception e) {
         if (transaction != null) {
            transaction.rollback();
         }

         e.printStackTrace();
         return false;
      }
   }

   public boolean capNhat(HoiVien hv) {
      Transaction transaction = null;

      try {
         Session session = HibernateUtil.getSessionFactory().openSession();

         boolean var4;
         try {
            transaction = session.beginTransaction();
            session.merge(hv);
            transaction.commit();
            var4 = true;
         } catch (Throwable var7) {
            if (session != null) {
               try {
                  session.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (session != null) {
            session.close();
         }

         return var4;
      } catch (Exception e) {
         if (transaction != null) {
            transaction.rollback();
         }

         e.printStackTrace();
         return false;
      }
   }

   public boolean xoa(int maHoiVien) {
      Transaction transaction = null;

      try {
         Session session = HibernateUtil.getSessionFactory().openSession();

         boolean var10;
         label56: {
            try {
               transaction = session.beginTransaction();
               HoiVien hv = (HoiVien)session.get(HoiVien.class, maHoiVien);
               if (hv != null) {
                  session.remove(hv);
                  transaction.commit();
                  var10 = true;
                  break label56;
               }

               var10 = false;
            } catch (Throwable var7) {
               if (session != null) {
                  try {
                     session.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (session != null) {
               session.close();
            }

            return var10;
         }

         if (session != null) {
            session.close();
         }

         return var10;
      } catch (Exception e) {
         if (transaction != null) {
            transaction.rollback();
         }

         e.printStackTrace();
         return false;
      }
   }

   public HoiVien timTheoMaQR(String maQR) {
      try {
         Session session = HibernateUtil.getSessionFactory().openSession();

         HoiVien var4;
         try {
            Query<HoiVien> query = session.createQuery("FROM HoiVien v WHERE v.maTheQR = :ma", HoiVien.class);
            query.setParameter("ma", maQR);
            var4 = (HoiVien)query.uniqueResult();
         } catch (Throwable var6) {
            if (session != null) {
               try {
                  session.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (session != null) {
            session.close();
         }

         return var4;
      } catch (Exception var7) {
         return null;
      }
   }
}
