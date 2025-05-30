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
import modelo.Devolucion;
import modelo.ValeDevolucion;

/**
 *
 * @author magal
 */
public class ValeDevolucionJpaController {
    
    public ValeDevolucionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
}
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ValeDevolucion valeDevolucion) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Devolucion idDevolucion = valeDevolucion.getIdDevolucion();
            if (idDevolucion != null) {
                idDevolucion = em.getReference(idDevolucion.getClass(), idDevolucion.getIdDevolucion());
                valeDevolucion.setIdDevolucion(idDevolucion);
            }
            em.persist(valeDevolucion);
            if (idDevolucion != null) {
                idDevolucion.getValeDevolucionCollection().add(valeDevolucion);
                idDevolucion = em.merge(idDevolucion);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ValeDevolucion valeDevolucion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ValeDevolucion persistentValeDevolucion = em.find(ValeDevolucion.class, valeDevolucion.getIdVale());
            Devolucion idDevolucionOld = persistentValeDevolucion.getIdDevolucion();
            Devolucion idDevolucionNew = valeDevolucion.getIdDevolucion();
            if (idDevolucionNew != null) {
                idDevolucionNew = em.getReference(idDevolucionNew.getClass(), idDevolucionNew.getIdDevolucion());
                valeDevolucion.setIdDevolucion(idDevolucionNew);
            }
            valeDevolucion = em.merge(valeDevolucion);
            if (idDevolucionOld != null && !idDevolucionOld.equals(idDevolucionNew)) {
                idDevolucionOld.getValeDevolucionCollection().remove(valeDevolucion);
                idDevolucionOld = em.merge(idDevolucionOld);
            }
            if (idDevolucionNew != null && !idDevolucionNew.equals(idDevolucionOld)) {
                idDevolucionNew.getValeDevolucionCollection().add(valeDevolucion);
                idDevolucionNew = em.merge(idDevolucionNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = valeDevolucion.getIdVale();
                if (findValeDevolucion(id) == null) {
                    throw new NonexistentEntityException("The valeDevolucion with id " + id + " no longer exists.");
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
            ValeDevolucion valeDevolucion;
            try {
                valeDevolucion = em.getReference(ValeDevolucion.class, id);
                valeDevolucion.getIdVale();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The valeDevolucion with id " + id + " no longer exists.", enfe);
            }
            Devolucion idDevolucion = valeDevolucion.getIdDevolucion();
            if (idDevolucion != null) {
                idDevolucion.getValeDevolucionCollection().remove(valeDevolucion);
                idDevolucion = em.merge(idDevolucion);
            }
            em.remove(valeDevolucion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ValeDevolucion> findValeDevolucionEntities() {
        return findValeDevolucionEntities(true, -1, -1);
    }

    public List<ValeDevolucion> findValeDevolucionEntities(int maxResults, int firstResult) {
        return findValeDevolucionEntities(false, maxResults, firstResult);
    }

    private List<ValeDevolucion> findValeDevolucionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ValeDevolucion.class));
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

    public ValeDevolucion findValeDevolucion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ValeDevolucion.class, id);
        } finally {
            em.close();
        }
    }

    public int getValeDevolucionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ValeDevolucion> rt = cq.from(ValeDevolucion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
