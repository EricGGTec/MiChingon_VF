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
import modelo.Pedido;
import modelo.Proveedor;
import modelo.UbicacionPedido;

/**
 *
 * @author magal
 */
public class UbicacionPedidoJpaController {
    
    public UbicacionPedidoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
}
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UbicacionPedido ubicacionPedido) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pedido idPedido = ubicacionPedido.getIdPedido();
            if (idPedido != null) {
                idPedido = em.getReference(idPedido.getClass(), idPedido.getIdPedido());
                ubicacionPedido.setIdPedido(idPedido);
            }
            Proveedor idProveedor = ubicacionPedido.getIdProveedor();
            if (idProveedor != null) {
                idProveedor = em.getReference(idProveedor.getClass(), idProveedor.getIdProveedor());
                ubicacionPedido.setIdProveedor(idProveedor);
            }
            em.persist(ubicacionPedido);
            if (idPedido != null) {
                idPedido.getUbicacionPedidoCollection().add(ubicacionPedido);
                idPedido = em.merge(idPedido);
            }
            if (idProveedor != null) {
                idProveedor.getUbicacionPedidoCollection().add(ubicacionPedido);
                idProveedor = em.merge(idProveedor);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UbicacionPedido ubicacionPedido) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UbicacionPedido persistentUbicacionPedido = em.find(UbicacionPedido.class, ubicacionPedido.getIdUbicacion());
            Pedido idPedidoOld = persistentUbicacionPedido.getIdPedido();
            Pedido idPedidoNew = ubicacionPedido.getIdPedido();
            Proveedor idProveedorOld = persistentUbicacionPedido.getIdProveedor();
            Proveedor idProveedorNew = ubicacionPedido.getIdProveedor();
            if (idPedidoNew != null) {
                idPedidoNew = em.getReference(idPedidoNew.getClass(), idPedidoNew.getIdPedido());
                ubicacionPedido.setIdPedido(idPedidoNew);
            }
            if (idProveedorNew != null) {
                idProveedorNew = em.getReference(idProveedorNew.getClass(), idProveedorNew.getIdProveedor());
                ubicacionPedido.setIdProveedor(idProveedorNew);
            }
            ubicacionPedido = em.merge(ubicacionPedido);
            if (idPedidoOld != null && !idPedidoOld.equals(idPedidoNew)) {
                idPedidoOld.getUbicacionPedidoCollection().remove(ubicacionPedido);
                idPedidoOld = em.merge(idPedidoOld);
            }
            if (idPedidoNew != null && !idPedidoNew.equals(idPedidoOld)) {
                idPedidoNew.getUbicacionPedidoCollection().add(ubicacionPedido);
                idPedidoNew = em.merge(idPedidoNew);
            }
            if (idProveedorOld != null && !idProveedorOld.equals(idProveedorNew)) {
                idProveedorOld.getUbicacionPedidoCollection().remove(ubicacionPedido);
                idProveedorOld = em.merge(idProveedorOld);
            }
            if (idProveedorNew != null && !idProveedorNew.equals(idProveedorOld)) {
                idProveedorNew.getUbicacionPedidoCollection().add(ubicacionPedido);
                idProveedorNew = em.merge(idProveedorNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ubicacionPedido.getIdUbicacion();
                if (findUbicacionPedido(id) == null) {
                    throw new NonexistentEntityException("The ubicacionPedido with id " + id + " no longer exists.");
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
            UbicacionPedido ubicacionPedido;
            try {
                ubicacionPedido = em.getReference(UbicacionPedido.class, id);
                ubicacionPedido.getIdUbicacion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ubicacionPedido with id " + id + " no longer exists.", enfe);
            }
            Pedido idPedido = ubicacionPedido.getIdPedido();
            if (idPedido != null) {
                idPedido.getUbicacionPedidoCollection().remove(ubicacionPedido);
                idPedido = em.merge(idPedido);
            }
            Proveedor idProveedor = ubicacionPedido.getIdProveedor();
            if (idProveedor != null) {
                idProveedor.getUbicacionPedidoCollection().remove(ubicacionPedido);
                idProveedor = em.merge(idProveedor);
            }
            em.remove(ubicacionPedido);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UbicacionPedido> findUbicacionPedidoEntities() {
        return findUbicacionPedidoEntities(true, -1, -1);
    }

    public List<UbicacionPedido> findUbicacionPedidoEntities(int maxResults, int firstResult) {
        return findUbicacionPedidoEntities(false, maxResults, firstResult);
    }

    private List<UbicacionPedido> findUbicacionPedidoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UbicacionPedido.class));
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

    public UbicacionPedido findUbicacionPedido(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UbicacionPedido.class, id);
        } finally {
            em.close();
        }
    }

    public int getUbicacionPedidoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UbicacionPedido> rt = cq.from(UbicacionPedido.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
