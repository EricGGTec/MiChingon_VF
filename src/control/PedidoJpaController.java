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
import modelo.Cliente;
import modelo.UbicacionPedido;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Devolucion;
import modelo.Pedido;
import modelo.Transaccion;

/**
 *
 * @author magal
 */
public class PedidoJpaController {
    
    public PedidoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
}
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pedido pedido) {
        if (pedido.getUbicacionPedidoCollection() == null) {
            pedido.setUbicacionPedidoCollection(new ArrayList<UbicacionPedido>());
        }
        if (pedido.getDevolucionCollection() == null) {
            pedido.setDevolucionCollection(new ArrayList<Devolucion>());
        }
        if (pedido.getTransaccionCollection() == null) {
            pedido.setTransaccionCollection(new ArrayList<Transaccion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente idCliente = pedido.getIdCliente();
            if (idCliente != null) {
                idCliente = em.getReference(idCliente.getClass(), idCliente.getIdCliente());
                pedido.setIdCliente(idCliente);
            }
            Collection<UbicacionPedido> attachedUbicacionPedidoCollection = new ArrayList<UbicacionPedido>();
            for (UbicacionPedido ubicacionPedidoCollectionUbicacionPedidoToAttach : pedido.getUbicacionPedidoCollection()) {
                ubicacionPedidoCollectionUbicacionPedidoToAttach = em.getReference(ubicacionPedidoCollectionUbicacionPedidoToAttach.getClass(), ubicacionPedidoCollectionUbicacionPedidoToAttach.getIdUbicacion());
                attachedUbicacionPedidoCollection.add(ubicacionPedidoCollectionUbicacionPedidoToAttach);
            }
            pedido.setUbicacionPedidoCollection(attachedUbicacionPedidoCollection);
            Collection<Devolucion> attachedDevolucionCollection = new ArrayList<Devolucion>();
            for (Devolucion devolucionCollectionDevolucionToAttach : pedido.getDevolucionCollection()) {
                devolucionCollectionDevolucionToAttach = em.getReference(devolucionCollectionDevolucionToAttach.getClass(), devolucionCollectionDevolucionToAttach.getIdDevolucion());
                attachedDevolucionCollection.add(devolucionCollectionDevolucionToAttach);
            }
            pedido.setDevolucionCollection(attachedDevolucionCollection);
            Collection<Transaccion> attachedTransaccionCollection = new ArrayList<Transaccion>();
            for (Transaccion transaccionCollectionTransaccionToAttach : pedido.getTransaccionCollection()) {
                transaccionCollectionTransaccionToAttach = em.getReference(transaccionCollectionTransaccionToAttach.getClass(), transaccionCollectionTransaccionToAttach.getIdTransaccion());
                attachedTransaccionCollection.add(transaccionCollectionTransaccionToAttach);
            }
            pedido.setTransaccionCollection(attachedTransaccionCollection);
            em.persist(pedido);
            if (idCliente != null) {
                idCliente.getPedidoCollection().add(pedido);
                idCliente = em.merge(idCliente);
            }
            for (UbicacionPedido ubicacionPedidoCollectionUbicacionPedido : pedido.getUbicacionPedidoCollection()) {
                Pedido oldIdPedidoOfUbicacionPedidoCollectionUbicacionPedido = ubicacionPedidoCollectionUbicacionPedido.getIdPedido();
                ubicacionPedidoCollectionUbicacionPedido.setIdPedido(pedido);
                ubicacionPedidoCollectionUbicacionPedido = em.merge(ubicacionPedidoCollectionUbicacionPedido);
                if (oldIdPedidoOfUbicacionPedidoCollectionUbicacionPedido != null) {
                    oldIdPedidoOfUbicacionPedidoCollectionUbicacionPedido.getUbicacionPedidoCollection().remove(ubicacionPedidoCollectionUbicacionPedido);
                    oldIdPedidoOfUbicacionPedidoCollectionUbicacionPedido = em.merge(oldIdPedidoOfUbicacionPedidoCollectionUbicacionPedido);
                }
            }
            for (Devolucion devolucionCollectionDevolucion : pedido.getDevolucionCollection()) {
                Pedido oldIdPedidoOfDevolucionCollectionDevolucion = devolucionCollectionDevolucion.getIdPedido();
                devolucionCollectionDevolucion.setIdPedido(pedido);
                devolucionCollectionDevolucion = em.merge(devolucionCollectionDevolucion);
                if (oldIdPedidoOfDevolucionCollectionDevolucion != null) {
                    oldIdPedidoOfDevolucionCollectionDevolucion.getDevolucionCollection().remove(devolucionCollectionDevolucion);
                    oldIdPedidoOfDevolucionCollectionDevolucion = em.merge(oldIdPedidoOfDevolucionCollectionDevolucion);
                }
            }
            for (Transaccion transaccionCollectionTransaccion : pedido.getTransaccionCollection()) {
                Pedido oldIdPedidoOfTransaccionCollectionTransaccion = transaccionCollectionTransaccion.getIdPedido();
                transaccionCollectionTransaccion.setIdPedido(pedido);
                transaccionCollectionTransaccion = em.merge(transaccionCollectionTransaccion);
                if (oldIdPedidoOfTransaccionCollectionTransaccion != null) {
                    oldIdPedidoOfTransaccionCollectionTransaccion.getTransaccionCollection().remove(transaccionCollectionTransaccion);
                    oldIdPedidoOfTransaccionCollectionTransaccion = em.merge(oldIdPedidoOfTransaccionCollectionTransaccion);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pedido pedido) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pedido persistentPedido = em.find(Pedido.class, pedido.getIdPedido());
            Cliente idClienteOld = persistentPedido.getIdCliente();
            Cliente idClienteNew = pedido.getIdCliente();
            Collection<UbicacionPedido> ubicacionPedidoCollectionOld = persistentPedido.getUbicacionPedidoCollection();
            Collection<UbicacionPedido> ubicacionPedidoCollectionNew = pedido.getUbicacionPedidoCollection();
            Collection<Devolucion> devolucionCollectionOld = persistentPedido.getDevolucionCollection();
            Collection<Devolucion> devolucionCollectionNew = pedido.getDevolucionCollection();
            Collection<Transaccion> transaccionCollectionOld = persistentPedido.getTransaccionCollection();
            Collection<Transaccion> transaccionCollectionNew = pedido.getTransaccionCollection();
            List<String> illegalOrphanMessages = null;
            for (UbicacionPedido ubicacionPedidoCollectionOldUbicacionPedido : ubicacionPedidoCollectionOld) {
                if (!ubicacionPedidoCollectionNew.contains(ubicacionPedidoCollectionOldUbicacionPedido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UbicacionPedido " + ubicacionPedidoCollectionOldUbicacionPedido + " since its idPedido field is not nullable.");
                }
            }
            for (Devolucion devolucionCollectionOldDevolucion : devolucionCollectionOld) {
                if (!devolucionCollectionNew.contains(devolucionCollectionOldDevolucion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Devolucion " + devolucionCollectionOldDevolucion + " since its idPedido field is not nullable.");
                }
            }
            for (Transaccion transaccionCollectionOldTransaccion : transaccionCollectionOld) {
                if (!transaccionCollectionNew.contains(transaccionCollectionOldTransaccion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Transaccion " + transaccionCollectionOldTransaccion + " since its idPedido field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idClienteNew != null) {
                idClienteNew = em.getReference(idClienteNew.getClass(), idClienteNew.getIdCliente());
                pedido.setIdCliente(idClienteNew);
            }
            Collection<UbicacionPedido> attachedUbicacionPedidoCollectionNew = new ArrayList<UbicacionPedido>();
            for (UbicacionPedido ubicacionPedidoCollectionNewUbicacionPedidoToAttach : ubicacionPedidoCollectionNew) {
                ubicacionPedidoCollectionNewUbicacionPedidoToAttach = em.getReference(ubicacionPedidoCollectionNewUbicacionPedidoToAttach.getClass(), ubicacionPedidoCollectionNewUbicacionPedidoToAttach.getIdUbicacion());
                attachedUbicacionPedidoCollectionNew.add(ubicacionPedidoCollectionNewUbicacionPedidoToAttach);
            }
            ubicacionPedidoCollectionNew = attachedUbicacionPedidoCollectionNew;
            pedido.setUbicacionPedidoCollection(ubicacionPedidoCollectionNew);
            Collection<Devolucion> attachedDevolucionCollectionNew = new ArrayList<Devolucion>();
            for (Devolucion devolucionCollectionNewDevolucionToAttach : devolucionCollectionNew) {
                devolucionCollectionNewDevolucionToAttach = em.getReference(devolucionCollectionNewDevolucionToAttach.getClass(), devolucionCollectionNewDevolucionToAttach.getIdDevolucion());
                attachedDevolucionCollectionNew.add(devolucionCollectionNewDevolucionToAttach);
            }
            devolucionCollectionNew = attachedDevolucionCollectionNew;
            pedido.setDevolucionCollection(devolucionCollectionNew);
            Collection<Transaccion> attachedTransaccionCollectionNew = new ArrayList<Transaccion>();
            for (Transaccion transaccionCollectionNewTransaccionToAttach : transaccionCollectionNew) {
                transaccionCollectionNewTransaccionToAttach = em.getReference(transaccionCollectionNewTransaccionToAttach.getClass(), transaccionCollectionNewTransaccionToAttach.getIdTransaccion());
                attachedTransaccionCollectionNew.add(transaccionCollectionNewTransaccionToAttach);
            }
            transaccionCollectionNew = attachedTransaccionCollectionNew;
            pedido.setTransaccionCollection(transaccionCollectionNew);
            pedido = em.merge(pedido);
            if (idClienteOld != null && !idClienteOld.equals(idClienteNew)) {
                idClienteOld.getPedidoCollection().remove(pedido);
                idClienteOld = em.merge(idClienteOld);
            }
            if (idClienteNew != null && !idClienteNew.equals(idClienteOld)) {
                idClienteNew.getPedidoCollection().add(pedido);
                idClienteNew = em.merge(idClienteNew);
            }
            for (UbicacionPedido ubicacionPedidoCollectionNewUbicacionPedido : ubicacionPedidoCollectionNew) {
                if (!ubicacionPedidoCollectionOld.contains(ubicacionPedidoCollectionNewUbicacionPedido)) {
                    Pedido oldIdPedidoOfUbicacionPedidoCollectionNewUbicacionPedido = ubicacionPedidoCollectionNewUbicacionPedido.getIdPedido();
                    ubicacionPedidoCollectionNewUbicacionPedido.setIdPedido(pedido);
                    ubicacionPedidoCollectionNewUbicacionPedido = em.merge(ubicacionPedidoCollectionNewUbicacionPedido);
                    if (oldIdPedidoOfUbicacionPedidoCollectionNewUbicacionPedido != null && !oldIdPedidoOfUbicacionPedidoCollectionNewUbicacionPedido.equals(pedido)) {
                        oldIdPedidoOfUbicacionPedidoCollectionNewUbicacionPedido.getUbicacionPedidoCollection().remove(ubicacionPedidoCollectionNewUbicacionPedido);
                        oldIdPedidoOfUbicacionPedidoCollectionNewUbicacionPedido = em.merge(oldIdPedidoOfUbicacionPedidoCollectionNewUbicacionPedido);
                    }
                }
            }
            for (Devolucion devolucionCollectionNewDevolucion : devolucionCollectionNew) {
                if (!devolucionCollectionOld.contains(devolucionCollectionNewDevolucion)) {
                    Pedido oldIdPedidoOfDevolucionCollectionNewDevolucion = devolucionCollectionNewDevolucion.getIdPedido();
                    devolucionCollectionNewDevolucion.setIdPedido(pedido);
                    devolucionCollectionNewDevolucion = em.merge(devolucionCollectionNewDevolucion);
                    if (oldIdPedidoOfDevolucionCollectionNewDevolucion != null && !oldIdPedidoOfDevolucionCollectionNewDevolucion.equals(pedido)) {
                        oldIdPedidoOfDevolucionCollectionNewDevolucion.getDevolucionCollection().remove(devolucionCollectionNewDevolucion);
                        oldIdPedidoOfDevolucionCollectionNewDevolucion = em.merge(oldIdPedidoOfDevolucionCollectionNewDevolucion);
                    }
                }
            }
            for (Transaccion transaccionCollectionNewTransaccion : transaccionCollectionNew) {
                if (!transaccionCollectionOld.contains(transaccionCollectionNewTransaccion)) {
                    Pedido oldIdPedidoOfTransaccionCollectionNewTransaccion = transaccionCollectionNewTransaccion.getIdPedido();
                    transaccionCollectionNewTransaccion.setIdPedido(pedido);
                    transaccionCollectionNewTransaccion = em.merge(transaccionCollectionNewTransaccion);
                    if (oldIdPedidoOfTransaccionCollectionNewTransaccion != null && !oldIdPedidoOfTransaccionCollectionNewTransaccion.equals(pedido)) {
                        oldIdPedidoOfTransaccionCollectionNewTransaccion.getTransaccionCollection().remove(transaccionCollectionNewTransaccion);
                        oldIdPedidoOfTransaccionCollectionNewTransaccion = em.merge(oldIdPedidoOfTransaccionCollectionNewTransaccion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pedido.getIdPedido();
                if (findPedido(id) == null) {
                    throw new NonexistentEntityException("The pedido with id " + id + " no longer exists.");
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
            Pedido pedido;
            try {
                pedido = em.getReference(Pedido.class, id);
                pedido.getIdPedido();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pedido with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<UbicacionPedido> ubicacionPedidoCollectionOrphanCheck = pedido.getUbicacionPedidoCollection();
            for (UbicacionPedido ubicacionPedidoCollectionOrphanCheckUbicacionPedido : ubicacionPedidoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pedido (" + pedido + ") cannot be destroyed since the UbicacionPedido " + ubicacionPedidoCollectionOrphanCheckUbicacionPedido + " in its ubicacionPedidoCollection field has a non-nullable idPedido field.");
            }
            Collection<Devolucion> devolucionCollectionOrphanCheck = pedido.getDevolucionCollection();
            for (Devolucion devolucionCollectionOrphanCheckDevolucion : devolucionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pedido (" + pedido + ") cannot be destroyed since the Devolucion " + devolucionCollectionOrphanCheckDevolucion + " in its devolucionCollection field has a non-nullable idPedido field.");
            }
            Collection<Transaccion> transaccionCollectionOrphanCheck = pedido.getTransaccionCollection();
            for (Transaccion transaccionCollectionOrphanCheckTransaccion : transaccionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pedido (" + pedido + ") cannot be destroyed since the Transaccion " + transaccionCollectionOrphanCheckTransaccion + " in its transaccionCollection field has a non-nullable idPedido field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cliente idCliente = pedido.getIdCliente();
            if (idCliente != null) {
                idCliente.getPedidoCollection().remove(pedido);
                idCliente = em.merge(idCliente);
            }
            em.remove(pedido);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pedido> findPedidoEntities() {
        return findPedidoEntities(true, -1, -1);
    }

    public List<Pedido> findPedidoEntities(int maxResults, int firstResult) {
        return findPedidoEntities(false, maxResults, firstResult);
    }

    private List<Pedido> findPedidoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pedido.class));
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

    public Pedido findPedido(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pedido.class, id);
        } finally {
            em.close();
        }
    }

    public int getPedidoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pedido> rt = cq.from(Pedido.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
