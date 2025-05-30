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
import modelo.Catalogo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Inventario;
import modelo.Producto;

/**
 *
 * @author magal
 */
public class ProductoJpaController {
    
    public ProductoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
}
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Producto producto) {
        if (producto.getCatalogoCollection() == null) {
            producto.setCatalogoCollection(new ArrayList<Catalogo>());
        }
        if (producto.getInventarioCollection() == null) {
            producto.setInventarioCollection(new ArrayList<Inventario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Catalogo> attachedCatalogoCollection = new ArrayList<Catalogo>();
            for (Catalogo catalogoCollectionCatalogoToAttach : producto.getCatalogoCollection()) {
                catalogoCollectionCatalogoToAttach = em.getReference(catalogoCollectionCatalogoToAttach.getClass(), catalogoCollectionCatalogoToAttach.getIdCatalogo());
                attachedCatalogoCollection.add(catalogoCollectionCatalogoToAttach);
            }
            producto.setCatalogoCollection(attachedCatalogoCollection);
            Collection<Inventario> attachedInventarioCollection = new ArrayList<Inventario>();
            for (Inventario inventarioCollectionInventarioToAttach : producto.getInventarioCollection()) {
                inventarioCollectionInventarioToAttach = em.getReference(inventarioCollectionInventarioToAttach.getClass(), inventarioCollectionInventarioToAttach.getIdInventario());
                attachedInventarioCollection.add(inventarioCollectionInventarioToAttach);
            }
            producto.setInventarioCollection(attachedInventarioCollection);
            em.persist(producto);
            for (Catalogo catalogoCollectionCatalogo : producto.getCatalogoCollection()) {
                catalogoCollectionCatalogo.getProductoCollection().add(producto);
                catalogoCollectionCatalogo = em.merge(catalogoCollectionCatalogo);
            }
            for (Inventario inventarioCollectionInventario : producto.getInventarioCollection()) {
                Producto oldIdProductoOfInventarioCollectionInventario = inventarioCollectionInventario.getIdProducto();
                inventarioCollectionInventario.setIdProducto(producto);
                inventarioCollectionInventario = em.merge(inventarioCollectionInventario);
                if (oldIdProductoOfInventarioCollectionInventario != null) {
                    oldIdProductoOfInventarioCollectionInventario.getInventarioCollection().remove(inventarioCollectionInventario);
                    oldIdProductoOfInventarioCollectionInventario = em.merge(oldIdProductoOfInventarioCollectionInventario);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Producto producto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto persistentProducto = em.find(Producto.class, producto.getIdProducto());
            Collection<Catalogo> catalogoCollectionOld = persistentProducto.getCatalogoCollection();
            Collection<Catalogo> catalogoCollectionNew = producto.getCatalogoCollection();
            Collection<Inventario> inventarioCollectionOld = persistentProducto.getInventarioCollection();
            Collection<Inventario> inventarioCollectionNew = producto.getInventarioCollection();
            List<String> illegalOrphanMessages = null;
            for (Inventario inventarioCollectionOldInventario : inventarioCollectionOld) {
                if (!inventarioCollectionNew.contains(inventarioCollectionOldInventario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Inventario " + inventarioCollectionOldInventario + " since its idProducto field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Catalogo> attachedCatalogoCollectionNew = new ArrayList<Catalogo>();
            for (Catalogo catalogoCollectionNewCatalogoToAttach : catalogoCollectionNew) {
                catalogoCollectionNewCatalogoToAttach = em.getReference(catalogoCollectionNewCatalogoToAttach.getClass(), catalogoCollectionNewCatalogoToAttach.getIdCatalogo());
                attachedCatalogoCollectionNew.add(catalogoCollectionNewCatalogoToAttach);
            }
            catalogoCollectionNew = attachedCatalogoCollectionNew;
            producto.setCatalogoCollection(catalogoCollectionNew);
            Collection<Inventario> attachedInventarioCollectionNew = new ArrayList<Inventario>();
            for (Inventario inventarioCollectionNewInventarioToAttach : inventarioCollectionNew) {
                inventarioCollectionNewInventarioToAttach = em.getReference(inventarioCollectionNewInventarioToAttach.getClass(), inventarioCollectionNewInventarioToAttach.getIdInventario());
                attachedInventarioCollectionNew.add(inventarioCollectionNewInventarioToAttach);
            }
            inventarioCollectionNew = attachedInventarioCollectionNew;
            producto.setInventarioCollection(inventarioCollectionNew);
            producto = em.merge(producto);
            for (Catalogo catalogoCollectionOldCatalogo : catalogoCollectionOld) {
                if (!catalogoCollectionNew.contains(catalogoCollectionOldCatalogo)) {
                    catalogoCollectionOldCatalogo.getProductoCollection().remove(producto);
                    catalogoCollectionOldCatalogo = em.merge(catalogoCollectionOldCatalogo);
                }
            }
            for (Catalogo catalogoCollectionNewCatalogo : catalogoCollectionNew) {
                if (!catalogoCollectionOld.contains(catalogoCollectionNewCatalogo)) {
                    catalogoCollectionNewCatalogo.getProductoCollection().add(producto);
                    catalogoCollectionNewCatalogo = em.merge(catalogoCollectionNewCatalogo);
                }
            }
            for (Inventario inventarioCollectionNewInventario : inventarioCollectionNew) {
                if (!inventarioCollectionOld.contains(inventarioCollectionNewInventario)) {
                    Producto oldIdProductoOfInventarioCollectionNewInventario = inventarioCollectionNewInventario.getIdProducto();
                    inventarioCollectionNewInventario.setIdProducto(producto);
                    inventarioCollectionNewInventario = em.merge(inventarioCollectionNewInventario);
                    if (oldIdProductoOfInventarioCollectionNewInventario != null && !oldIdProductoOfInventarioCollectionNewInventario.equals(producto)) {
                        oldIdProductoOfInventarioCollectionNewInventario.getInventarioCollection().remove(inventarioCollectionNewInventario);
                        oldIdProductoOfInventarioCollectionNewInventario = em.merge(oldIdProductoOfInventarioCollectionNewInventario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = producto.getIdProducto();
                if (findProducto(id) == null) {
                    throw new NonexistentEntityException("The producto with id " + id + " no longer exists.");
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
            Producto producto;
            try {
                producto = em.getReference(Producto.class, id);
                producto.getIdProducto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The producto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Inventario> inventarioCollectionOrphanCheck = producto.getInventarioCollection();
            for (Inventario inventarioCollectionOrphanCheckInventario : inventarioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the Inventario " + inventarioCollectionOrphanCheckInventario + " in its inventarioCollection field has a non-nullable idProducto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Catalogo> catalogoCollection = producto.getCatalogoCollection();
            for (Catalogo catalogoCollectionCatalogo : catalogoCollection) {
                catalogoCollectionCatalogo.getProductoCollection().remove(producto);
                catalogoCollectionCatalogo = em.merge(catalogoCollectionCatalogo);
            }
            em.remove(producto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Producto> findProductoEntities() {
        return findProductoEntities(true, -1, -1);
    }

    public List<Producto> findProductoEntities(int maxResults, int firstResult) {
        return findProductoEntities(false, maxResults, firstResult);
    }

    private List<Producto> findProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Producto.class));
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

    public Producto findProducto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Producto> rt = cq.from(Producto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
