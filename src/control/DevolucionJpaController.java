/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import control.exceptions.IllegalOrphanException;
import control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Pedido;
import modelo.ValeDevolucion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Devolucion;

/**
 *
 * @author magal
 */
public class DevolucionJpaController implements Serializable {

    public DevolucionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Devolucion devolucion) {
        if (devolucion.getValeDevolucionCollection() == null) {
            devolucion.setValeDevolucionCollection(new ArrayList<ValeDevolucion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pedido idPedido = devolucion.getIdPedido();
            if (idPedido != null) {
                idPedido = em.getReference(idPedido.getClass(), idPedido.getIdPedido());
                devolucion.setIdPedido(idPedido);
            }
            Collection<ValeDevolucion> attachedValeDevolucionCollection = new ArrayList<ValeDevolucion>();
            for (ValeDevolucion valeDevolucionCollectionValeDevolucionToAttach : devolucion.getValeDevolucionCollection()) {
                valeDevolucionCollectionValeDevolucionToAttach = em.getReference(valeDevolucionCollectionValeDevolucionToAttach.getClass(), valeDevolucionCollectionValeDevolucionToAttach.getIdVale());
                attachedValeDevolucionCollection.add(valeDevolucionCollectionValeDevolucionToAttach);
            }
            devolucion.setValeDevolucionCollection(attachedValeDevolucionCollection);
            em.persist(devolucion);
            if (idPedido != null) {
                idPedido.getDevolucionCollection().add(devolucion);
                idPedido = em.merge(idPedido);
            }
            for (ValeDevolucion valeDevolucionCollectionValeDevolucion : devolucion.getValeDevolucionCollection()) {
                Devolucion oldIdDevolucionOfValeDevolucionCollectionValeDevolucion = valeDevolucionCollectionValeDevolucion.getIdDevolucion();
                valeDevolucionCollectionValeDevolucion.setIdDevolucion(devolucion);
                valeDevolucionCollectionValeDevolucion = em.merge(valeDevolucionCollectionValeDevolucion);
                if (oldIdDevolucionOfValeDevolucionCollectionValeDevolucion != null) {
                    oldIdDevolucionOfValeDevolucionCollectionValeDevolucion.getValeDevolucionCollection().remove(valeDevolucionCollectionValeDevolucion);
                    oldIdDevolucionOfValeDevolucionCollectionValeDevolucion = em.merge(oldIdDevolucionOfValeDevolucionCollectionValeDevolucion);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Devolucion devolucion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Devolucion persistentDevolucion = em.find(Devolucion.class, devolucion.getIdDevolucion());
            Pedido idPedidoOld = persistentDevolucion.getIdPedido();
            Pedido idPedidoNew = devolucion.getIdPedido();
            Collection<ValeDevolucion> valeDevolucionCollectionOld = persistentDevolucion.getValeDevolucionCollection();
            Collection<ValeDevolucion> valeDevolucionCollectionNew = devolucion.getValeDevolucionCollection();
            List<String> illegalOrphanMessages = null;
            for (ValeDevolucion valeDevolucionCollectionOldValeDevolucion : valeDevolucionCollectionOld) {
                if (!valeDevolucionCollectionNew.contains(valeDevolucionCollectionOldValeDevolucion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ValeDevolucion " + valeDevolucionCollectionOldValeDevolucion + " since its idDevolucion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idPedidoNew != null) {
                idPedidoNew = em.getReference(idPedidoNew.getClass(), idPedidoNew.getIdPedido());
                devolucion.setIdPedido(idPedidoNew);
            }
            Collection<ValeDevolucion> attachedValeDevolucionCollectionNew = new ArrayList<ValeDevolucion>();
            for (ValeDevolucion valeDevolucionCollectionNewValeDevolucionToAttach : valeDevolucionCollectionNew) {
                valeDevolucionCollectionNewValeDevolucionToAttach = em.getReference(valeDevolucionCollectionNewValeDevolucionToAttach.getClass(), valeDevolucionCollectionNewValeDevolucionToAttach.getIdVale());
                attachedValeDevolucionCollectionNew.add(valeDevolucionCollectionNewValeDevolucionToAttach);
            }
            valeDevolucionCollectionNew = attachedValeDevolucionCollectionNew;
            devolucion.setValeDevolucionCollection(valeDevolucionCollectionNew);
            devolucion = em.merge(devolucion);
            if (idPedidoOld != null && !idPedidoOld.equals(idPedidoNew)) {
                idPedidoOld.getDevolucionCollection().remove(devolucion);
                idPedidoOld = em.merge(idPedidoOld);
            }
            if (idPedidoNew != null && !idPedidoNew.equals(idPedidoOld)) {
                idPedidoNew.getDevolucionCollection().add(devolucion);
                idPedidoNew = em.merge(idPedidoNew);
            }
            for (ValeDevolucion valeDevolucionCollectionNewValeDevolucion : valeDevolucionCollectionNew) {
                if (!valeDevolucionCollectionOld.contains(valeDevolucionCollectionNewValeDevolucion)) {
                    Devolucion oldIdDevolucionOfValeDevolucionCollectionNewValeDevolucion = valeDevolucionCollectionNewValeDevolucion.getIdDevolucion();
                    valeDevolucionCollectionNewValeDevolucion.setIdDevolucion(devolucion);
                    valeDevolucionCollectionNewValeDevolucion = em.merge(valeDevolucionCollectionNewValeDevolucion);
                    if (oldIdDevolucionOfValeDevolucionCollectionNewValeDevolucion != null && !oldIdDevolucionOfValeDevolucionCollectionNewValeDevolucion.equals(devolucion)) {
                        oldIdDevolucionOfValeDevolucionCollectionNewValeDevolucion.getValeDevolucionCollection().remove(valeDevolucionCollectionNewValeDevolucion);
                        oldIdDevolucionOfValeDevolucionCollectionNewValeDevolucion = em.merge(oldIdDevolucionOfValeDevolucionCollectionNewValeDevolucion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = devolucion.getIdDevolucion();
                if (findDevolucion(id) == null) {
                    throw new NonexistentEntityException("The devolucion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Devolucion devolucion;
            try {
                devolucion = em.getReference(Devolucion.class, id);
                devolucion.getIdDevolucion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The devolucion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<ValeDevolucion> valeDevolucionCollectionOrphanCheck = devolucion.getValeDevolucionCollection();
            for (ValeDevolucion valeDevolucionCollectionOrphanCheckValeDevolucion : valeDevolucionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Devolucion (" + devolucion + ") cannot be destroyed since the ValeDevolucion " + valeDevolucionCollectionOrphanCheckValeDevolucion + " in its valeDevolucionCollection field has a non-nullable idDevolucion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Pedido idPedido = devolucion.getIdPedido();
            if (idPedido != null) {
                idPedido.getDevolucionCollection().remove(devolucion);
                idPedido = em.merge(idPedido);
            }
            em.remove(devolucion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Devolucion> findDevolucionEntities() {
        return findDevolucionEntities(true, -1, -1);
    }

    public List<Devolucion> findDevolucionEntities(int maxResults, int firstResult) {
        return findDevolucionEntities(false, maxResults, firstResult);
    }

    private List<Devolucion> findDevolucionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Devolucion.class));
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

    public Devolucion findDevolucion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Devolucion.class, id);
        } finally {
            em.close();
        }
    }

    public int getDevolucionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Devolucion> rt = cq.from(Devolucion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
