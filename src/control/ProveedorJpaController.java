/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.UbicacionPedido;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Proveedor;

/**
 *
 * @author magal
 */
public class ProveedorJpaController {
    
    public ProveedorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
}
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Proveedor proveedor) {
        if (proveedor.getUbicacionPedidoCollection() == null) {
            proveedor.setUbicacionPedidoCollection(new ArrayList<UbicacionPedido>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<UbicacionPedido> attachedUbicacionPedidoCollection = new ArrayList<UbicacionPedido>();
            for (UbicacionPedido ubicacionPedidoCollectionUbicacionPedidoToAttach : proveedor.getUbicacionPedidoCollection()) {
                ubicacionPedidoCollectionUbicacionPedidoToAttach = em.getReference(ubicacionPedidoCollectionUbicacionPedidoToAttach.getClass(), ubicacionPedidoCollectionUbicacionPedidoToAttach.getIdUbicacion());
                attachedUbicacionPedidoCollection.add(ubicacionPedidoCollectionUbicacionPedidoToAttach);
            }
            proveedor.setUbicacionPedidoCollection(attachedUbicacionPedidoCollection);
            em.persist(proveedor);
            for (UbicacionPedido ubicacionPedidoCollectionUbicacionPedido : proveedor.getUbicacionPedidoCollection()) {
                Proveedor oldIdProveedorOfUbicacionPedidoCollectionUbicacionPedido = ubicacionPedidoCollectionUbicacionPedido.getIdProveedor();
                ubicacionPedidoCollectionUbicacionPedido.setIdProveedor(proveedor);
                ubicacionPedidoCollectionUbicacionPedido = em.merge(ubicacionPedidoCollectionUbicacionPedido);
                if (oldIdProveedorOfUbicacionPedidoCollectionUbicacionPedido != null) {
                    oldIdProveedorOfUbicacionPedidoCollectionUbicacionPedido.getUbicacionPedidoCollection().remove(ubicacionPedidoCollectionUbicacionPedido);
                    oldIdProveedorOfUbicacionPedidoCollectionUbicacionPedido = em.merge(oldIdProveedorOfUbicacionPedidoCollectionUbicacionPedido);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Proveedor proveedor) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedor persistentProveedor = em.find(Proveedor.class, proveedor.getIdProveedor());
            Collection<UbicacionPedido> ubicacionPedidoCollectionOld = persistentProveedor.getUbicacionPedidoCollection();
            Collection<UbicacionPedido> ubicacionPedidoCollectionNew = proveedor.getUbicacionPedidoCollection();
            Collection<UbicacionPedido> attachedUbicacionPedidoCollectionNew = new ArrayList<UbicacionPedido>();
            for (UbicacionPedido ubicacionPedidoCollectionNewUbicacionPedidoToAttach : ubicacionPedidoCollectionNew) {
                ubicacionPedidoCollectionNewUbicacionPedidoToAttach = em.getReference(ubicacionPedidoCollectionNewUbicacionPedidoToAttach.getClass(), ubicacionPedidoCollectionNewUbicacionPedidoToAttach.getIdUbicacion());
                attachedUbicacionPedidoCollectionNew.add(ubicacionPedidoCollectionNewUbicacionPedidoToAttach);
            }
            ubicacionPedidoCollectionNew = attachedUbicacionPedidoCollectionNew;
            proveedor.setUbicacionPedidoCollection(ubicacionPedidoCollectionNew);
            proveedor = em.merge(proveedor);
            for (UbicacionPedido ubicacionPedidoCollectionOldUbicacionPedido : ubicacionPedidoCollectionOld) {
                if (!ubicacionPedidoCollectionNew.contains(ubicacionPedidoCollectionOldUbicacionPedido)) {
                    ubicacionPedidoCollectionOldUbicacionPedido.setIdProveedor(null);
                    ubicacionPedidoCollectionOldUbicacionPedido = em.merge(ubicacionPedidoCollectionOldUbicacionPedido);
                }
            }
            for (UbicacionPedido ubicacionPedidoCollectionNewUbicacionPedido : ubicacionPedidoCollectionNew) {
                if (!ubicacionPedidoCollectionOld.contains(ubicacionPedidoCollectionNewUbicacionPedido)) {
                    Proveedor oldIdProveedorOfUbicacionPedidoCollectionNewUbicacionPedido = ubicacionPedidoCollectionNewUbicacionPedido.getIdProveedor();
                    ubicacionPedidoCollectionNewUbicacionPedido.setIdProveedor(proveedor);
                    ubicacionPedidoCollectionNewUbicacionPedido = em.merge(ubicacionPedidoCollectionNewUbicacionPedido);
                    if (oldIdProveedorOfUbicacionPedidoCollectionNewUbicacionPedido != null && !oldIdProveedorOfUbicacionPedidoCollectionNewUbicacionPedido.equals(proveedor)) {
                        oldIdProveedorOfUbicacionPedidoCollectionNewUbicacionPedido.getUbicacionPedidoCollection().remove(ubicacionPedidoCollectionNewUbicacionPedido);
                        oldIdProveedorOfUbicacionPedidoCollectionNewUbicacionPedido = em.merge(oldIdProveedorOfUbicacionPedidoCollectionNewUbicacionPedido);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = proveedor.getIdProveedor();
                if (findProveedor(id) == null) {
                    throw new NonexistentEntityException("The proveedor with id " + id + " no longer exists.");
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
            Proveedor proveedor;
            try {
                proveedor = em.getReference(Proveedor.class, id);
                proveedor.getIdProveedor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The proveedor with id " + id + " no longer exists.", enfe);
            }
            Collection<UbicacionPedido> ubicacionPedidoCollection = proveedor.getUbicacionPedidoCollection();
            for (UbicacionPedido ubicacionPedidoCollectionUbicacionPedido : ubicacionPedidoCollection) {
                ubicacionPedidoCollectionUbicacionPedido.setIdProveedor(null);
                ubicacionPedidoCollectionUbicacionPedido = em.merge(ubicacionPedidoCollectionUbicacionPedido);
            }
            em.remove(proveedor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Proveedor> findProveedorEntities() {
        return findProveedorEntities(true, -1, -1);
    }

    public List<Proveedor> findProveedorEntities(int maxResults, int firstResult) {
        return findProveedorEntities(false, maxResults, firstResult);
    }

    private List<Proveedor> findProveedorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Proveedor.class));
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

    public Proveedor findProveedor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Proveedor.class, id);
        } finally {
            em.close();
        }
    }

    public int getProveedorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Proveedor> rt = cq.from(Proveedor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
