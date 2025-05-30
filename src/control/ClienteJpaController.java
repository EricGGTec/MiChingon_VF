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
import modelo.Ticket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Cliente;
import modelo.Transaccion;
import modelo.Pedido;

/**
 *
 * @author magal
 */
public class ClienteJpaController implements Serializable {

    public ClienteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cliente cliente) {
        if (cliente.getTicketCollection() == null) {
            cliente.setTicketCollection(new ArrayList<Ticket>());
        }
        if (cliente.getTransaccionCollection() == null) {
            cliente.setTransaccionCollection(new ArrayList<Transaccion>());
        }
        if (cliente.getPedidoCollection() == null) {
            cliente.setPedidoCollection(new ArrayList<Pedido>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Ticket> attachedTicketCollection = new ArrayList<Ticket>();
            for (Ticket ticketCollectionTicketToAttach : cliente.getTicketCollection()) {
                ticketCollectionTicketToAttach = em.getReference(ticketCollectionTicketToAttach.getClass(), ticketCollectionTicketToAttach.getIdTicket());
                attachedTicketCollection.add(ticketCollectionTicketToAttach);
            }
            cliente.setTicketCollection(attachedTicketCollection);
            Collection<Transaccion> attachedTransaccionCollection = new ArrayList<Transaccion>();
            for (Transaccion transaccionCollectionTransaccionToAttach : cliente.getTransaccionCollection()) {
                transaccionCollectionTransaccionToAttach = em.getReference(transaccionCollectionTransaccionToAttach.getClass(), transaccionCollectionTransaccionToAttach.getIdTransaccion());
                attachedTransaccionCollection.add(transaccionCollectionTransaccionToAttach);
            }
            cliente.setTransaccionCollection(attachedTransaccionCollection);
            Collection<Pedido> attachedPedidoCollection = new ArrayList<Pedido>();
            for (Pedido pedidoCollectionPedidoToAttach : cliente.getPedidoCollection()) {
                pedidoCollectionPedidoToAttach = em.getReference(pedidoCollectionPedidoToAttach.getClass(), pedidoCollectionPedidoToAttach.getIdPedido());
                attachedPedidoCollection.add(pedidoCollectionPedidoToAttach);
            }
            cliente.setPedidoCollection(attachedPedidoCollection);
            em.persist(cliente);
            for (Ticket ticketCollectionTicket : cliente.getTicketCollection()) {
                Cliente oldIdClienteOfTicketCollectionTicket = ticketCollectionTicket.getIdCliente();
                ticketCollectionTicket.setIdCliente(cliente);
                ticketCollectionTicket = em.merge(ticketCollectionTicket);
                if (oldIdClienteOfTicketCollectionTicket != null) {
                    oldIdClienteOfTicketCollectionTicket.getTicketCollection().remove(ticketCollectionTicket);
                    oldIdClienteOfTicketCollectionTicket = em.merge(oldIdClienteOfTicketCollectionTicket);
                }
            }
            for (Transaccion transaccionCollectionTransaccion : cliente.getTransaccionCollection()) {
                Cliente oldIdClienteOfTransaccionCollectionTransaccion = transaccionCollectionTransaccion.getIdCliente();
                transaccionCollectionTransaccion.setIdCliente(cliente);
                transaccionCollectionTransaccion = em.merge(transaccionCollectionTransaccion);
                if (oldIdClienteOfTransaccionCollectionTransaccion != null) {
                    oldIdClienteOfTransaccionCollectionTransaccion.getTransaccionCollection().remove(transaccionCollectionTransaccion);
                    oldIdClienteOfTransaccionCollectionTransaccion = em.merge(oldIdClienteOfTransaccionCollectionTransaccion);
                }
            }
            for (Pedido pedidoCollectionPedido : cliente.getPedidoCollection()) {
                Cliente oldIdClienteOfPedidoCollectionPedido = pedidoCollectionPedido.getIdCliente();
                pedidoCollectionPedido.setIdCliente(cliente);
                pedidoCollectionPedido = em.merge(pedidoCollectionPedido);
                if (oldIdClienteOfPedidoCollectionPedido != null) {
                    oldIdClienteOfPedidoCollectionPedido.getPedidoCollection().remove(pedidoCollectionPedido);
                    oldIdClienteOfPedidoCollectionPedido = em.merge(oldIdClienteOfPedidoCollectionPedido);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cliente cliente) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente persistentCliente = em.find(Cliente.class, cliente.getIdCliente());
            Collection<Ticket> ticketCollectionOld = persistentCliente.getTicketCollection();
            Collection<Ticket> ticketCollectionNew = cliente.getTicketCollection();
            Collection<Transaccion> transaccionCollectionOld = persistentCliente.getTransaccionCollection();
            Collection<Transaccion> transaccionCollectionNew = cliente.getTransaccionCollection();
            Collection<Pedido> pedidoCollectionOld = persistentCliente.getPedidoCollection();
            Collection<Pedido> pedidoCollectionNew = cliente.getPedidoCollection();
            List<String> illegalOrphanMessages = null;
            for (Ticket ticketCollectionOldTicket : ticketCollectionOld) {
                if (!ticketCollectionNew.contains(ticketCollectionOldTicket)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ticket " + ticketCollectionOldTicket + " since its idCliente field is not nullable.");
                }
            }
            for (Transaccion transaccionCollectionOldTransaccion : transaccionCollectionOld) {
                if (!transaccionCollectionNew.contains(transaccionCollectionOldTransaccion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Transaccion " + transaccionCollectionOldTransaccion + " since its idCliente field is not nullable.");
                }
            }
            for (Pedido pedidoCollectionOldPedido : pedidoCollectionOld) {
                if (!pedidoCollectionNew.contains(pedidoCollectionOldPedido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pedido " + pedidoCollectionOldPedido + " since its idCliente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Ticket> attachedTicketCollectionNew = new ArrayList<Ticket>();
            for (Ticket ticketCollectionNewTicketToAttach : ticketCollectionNew) {
                ticketCollectionNewTicketToAttach = em.getReference(ticketCollectionNewTicketToAttach.getClass(), ticketCollectionNewTicketToAttach.getIdTicket());
                attachedTicketCollectionNew.add(ticketCollectionNewTicketToAttach);
            }
            ticketCollectionNew = attachedTicketCollectionNew;
            cliente.setTicketCollection(ticketCollectionNew);
            Collection<Transaccion> attachedTransaccionCollectionNew = new ArrayList<Transaccion>();
            for (Transaccion transaccionCollectionNewTransaccionToAttach : transaccionCollectionNew) {
                transaccionCollectionNewTransaccionToAttach = em.getReference(transaccionCollectionNewTransaccionToAttach.getClass(), transaccionCollectionNewTransaccionToAttach.getIdTransaccion());
                attachedTransaccionCollectionNew.add(transaccionCollectionNewTransaccionToAttach);
            }
            transaccionCollectionNew = attachedTransaccionCollectionNew;
            cliente.setTransaccionCollection(transaccionCollectionNew);
            Collection<Pedido> attachedPedidoCollectionNew = new ArrayList<Pedido>();
            for (Pedido pedidoCollectionNewPedidoToAttach : pedidoCollectionNew) {
                pedidoCollectionNewPedidoToAttach = em.getReference(pedidoCollectionNewPedidoToAttach.getClass(), pedidoCollectionNewPedidoToAttach.getIdPedido());
                attachedPedidoCollectionNew.add(pedidoCollectionNewPedidoToAttach);
            }
            pedidoCollectionNew = attachedPedidoCollectionNew;
            cliente.setPedidoCollection(pedidoCollectionNew);
            cliente = em.merge(cliente);
            for (Ticket ticketCollectionNewTicket : ticketCollectionNew) {
                if (!ticketCollectionOld.contains(ticketCollectionNewTicket)) {
                    Cliente oldIdClienteOfTicketCollectionNewTicket = ticketCollectionNewTicket.getIdCliente();
                    ticketCollectionNewTicket.setIdCliente(cliente);
                    ticketCollectionNewTicket = em.merge(ticketCollectionNewTicket);
                    if (oldIdClienteOfTicketCollectionNewTicket != null && !oldIdClienteOfTicketCollectionNewTicket.equals(cliente)) {
                        oldIdClienteOfTicketCollectionNewTicket.getTicketCollection().remove(ticketCollectionNewTicket);
                        oldIdClienteOfTicketCollectionNewTicket = em.merge(oldIdClienteOfTicketCollectionNewTicket);
                    }
                }
            }
            for (Transaccion transaccionCollectionNewTransaccion : transaccionCollectionNew) {
                if (!transaccionCollectionOld.contains(transaccionCollectionNewTransaccion)) {
                    Cliente oldIdClienteOfTransaccionCollectionNewTransaccion = transaccionCollectionNewTransaccion.getIdCliente();
                    transaccionCollectionNewTransaccion.setIdCliente(cliente);
                    transaccionCollectionNewTransaccion = em.merge(transaccionCollectionNewTransaccion);
                    if (oldIdClienteOfTransaccionCollectionNewTransaccion != null && !oldIdClienteOfTransaccionCollectionNewTransaccion.equals(cliente)) {
                        oldIdClienteOfTransaccionCollectionNewTransaccion.getTransaccionCollection().remove(transaccionCollectionNewTransaccion);
                        oldIdClienteOfTransaccionCollectionNewTransaccion = em.merge(oldIdClienteOfTransaccionCollectionNewTransaccion);
                    }
                }
            }
            for (Pedido pedidoCollectionNewPedido : pedidoCollectionNew) {
                if (!pedidoCollectionOld.contains(pedidoCollectionNewPedido)) {
                    Cliente oldIdClienteOfPedidoCollectionNewPedido = pedidoCollectionNewPedido.getIdCliente();
                    pedidoCollectionNewPedido.setIdCliente(cliente);
                    pedidoCollectionNewPedido = em.merge(pedidoCollectionNewPedido);
                    if (oldIdClienteOfPedidoCollectionNewPedido != null && !oldIdClienteOfPedidoCollectionNewPedido.equals(cliente)) {
                        oldIdClienteOfPedidoCollectionNewPedido.getPedidoCollection().remove(pedidoCollectionNewPedido);
                        oldIdClienteOfPedidoCollectionNewPedido = em.merge(oldIdClienteOfPedidoCollectionNewPedido);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cliente.getIdCliente();
                if (findCliente(id) == null) {
                    throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.");
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
            Cliente cliente;
            try {
                cliente = em.getReference(Cliente.class, id);
                cliente.getIdCliente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ticket> ticketCollectionOrphanCheck = cliente.getTicketCollection();
            for (Ticket ticketCollectionOrphanCheckTicket : ticketCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cliente (" + cliente + ") cannot be destroyed since the Ticket " + ticketCollectionOrphanCheckTicket + " in its ticketCollection field has a non-nullable idCliente field.");
            }
            Collection<Transaccion> transaccionCollectionOrphanCheck = cliente.getTransaccionCollection();
            for (Transaccion transaccionCollectionOrphanCheckTransaccion : transaccionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cliente (" + cliente + ") cannot be destroyed since the Transaccion " + transaccionCollectionOrphanCheckTransaccion + " in its transaccionCollection field has a non-nullable idCliente field.");
            }
            Collection<Pedido> pedidoCollectionOrphanCheck = cliente.getPedidoCollection();
            for (Pedido pedidoCollectionOrphanCheckPedido : pedidoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cliente (" + cliente + ") cannot be destroyed since the Pedido " + pedidoCollectionOrphanCheckPedido + " in its pedidoCollection field has a non-nullable idCliente field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cliente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    public List<Cliente> findClienteEntities(int maxResults, int firstResult) {
        return findClienteEntities(false, maxResults, firstResult);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cliente.class));
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

    public Cliente findCliente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    public int getClienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cliente> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
