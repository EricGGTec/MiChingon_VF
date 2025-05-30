/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Reporte;
import modelo.Trabajador;

/**
 *
 * @author magal
 */
public class ReporteJpaController {
    
    public ReporteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
}
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Reporte reporte) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Trabajador generadoPor = reporte.getGeneradoPor();
            if (generadoPor != null) {
                generadoPor = em.getReference(generadoPor.getClass(), generadoPor.getIdTrabajador());
                reporte.setGeneradoPor(generadoPor);
            }
            em.persist(reporte);
            if (generadoPor != null) {
                generadoPor.getReporteCollection().add(reporte);
                generadoPor = em.merge(generadoPor);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Reporte reporte) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reporte persistentReporte = em.find(Reporte.class, reporte.getIdReporte());
            Trabajador generadoPorOld = persistentReporte.getGeneradoPor();
            Trabajador generadoPorNew = reporte.getGeneradoPor();
            if (generadoPorNew != null) {
                generadoPorNew = em.getReference(generadoPorNew.getClass(), generadoPorNew.getIdTrabajador());
                reporte.setGeneradoPor(generadoPorNew);
            }
            reporte = em.merge(reporte);
            if (generadoPorOld != null && !generadoPorOld.equals(generadoPorNew)) {
                generadoPorOld.getReporteCollection().remove(reporte);
                generadoPorOld = em.merge(generadoPorOld);
            }
            if (generadoPorNew != null && !generadoPorNew.equals(generadoPorOld)) {
                generadoPorNew.getReporteCollection().add(reporte);
                generadoPorNew = em.merge(generadoPorNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = reporte.getIdReporte();
                if (findReporte(id) == null) {
                    throw new NonexistentEntityException("The reporte with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reporte reporte;
            try {
                reporte = em.getReference(Reporte.class, id);
                reporte.getIdReporte();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The reporte with id " + id + " no longer exists.", enfe);
            }
            Trabajador generadoPor = reporte.getGeneradoPor();
            if (generadoPor != null) {
                generadoPor.getReporteCollection().remove(reporte);
                generadoPor = em.merge(generadoPor);
            }
            em.remove(reporte);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Reporte> findReporteEntities() {
        return findReporteEntities(true, -1, -1);
    }

    public List<Reporte> findReporteEntities(int maxResults, int firstResult) {
        return findReporteEntities(false, maxResults, firstResult);
    }

    private List<Reporte> findReporteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Reporte.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Reporte findReporte(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Reporte.class, id);
        } finally {
            em.close();
        }
    }

    public int getReporteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Reporte> rt = cq.from(Reporte.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
