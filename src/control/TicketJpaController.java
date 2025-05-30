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
import modelo.Cliente;
import modelo.Ticket;
import modelo.Trabajador;

/**
 *
 * @author magal
 */
public class TicketJpaController {
    
    public TicketJpaController(EntityManagerFactory emf) {
        this.emf = emf;
}
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ticket ticket) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente idCliente = ticket.getIdCliente();
            if (idCliente != null) {
                idCliente = em.getReference(idCliente.getClass(), idCliente.getIdCliente());
                ticket.setIdCliente(idCliente);
            }
            Trabajador idCajero = ticket.getIdCajero();
            if (idCajero != null) {
                idCajero = em.getReference(idCajero.getClass(), idCajero.getIdTrabajador());
                ticket.setIdCajero(idCajero);
            }
            em.persist(ticket);
            if (idCliente != null) {
                idCliente.getTicketCollection().add(ticket);
                idCliente = em.merge(idCliente);
            }
            if (idCajero != null) {
                idCajero.getTicketCollection().add(ticket);
                idCajero = em.merge(idCajero);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ticket ticket) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ticket persistentTicket = em.find(Ticket.class, ticket.getIdTicket());
            Cliente idClienteOld = persistentTicket.getIdCliente();
            Cliente idClienteNew = ticket.getIdCliente();
            Trabajador idCajeroOld = persistentTicket.getIdCajero();
            Trabajador idCajeroNew = ticket.getIdCajero();
            if (idClienteNew != null) {
                idClienteNew = em.getReference(idClienteNew.getClass(), idClienteNew.getIdCliente());
                ticket.setIdCliente(idClienteNew);
            }
            if (idCajeroNew != null) {
                idCajeroNew = em.getReference(idCajeroNew.getClass(), idCajeroNew.getIdTrabajador());
                ticket.setIdCajero(idCajeroNew);
            }
            ticket = em.merge(ticket);
            if (idClienteOld != null && !idClienteOld.equals(idClienteNew)) {
                idClienteOld.getTicketCollection().remove(ticket);
                idClienteOld = em.merge(idClienteOld);
            }
            if (idClienteNew != null && !idClienteNew.equals(idClienteOld)) {
                idClienteNew.getTicketCollection().add(ticket);
                idClienteNew = em.merge(idClienteNew);
            }
            if (idCajeroOld != null && !idCajeroOld.equals(idCajeroNew)) {
                idCajeroOld.getTicketCollection().remove(ticket);
                idCajeroOld = em.merge(idCajeroOld);
            }
            if (idCajeroNew != null && !idCajeroNew.equals(idCajeroOld)) {
                idCajeroNew.getTicketCollection().add(ticket);
                idCajeroNew = em.merge(idCajeroNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ticket.getIdTicket();
                if (findTicket(id) == null) {
                    throw new NonexistentEntityException("The ticket with id " + id + " no longer exists.");
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
            Ticket ticket;
            try {
                ticket = em.getReference(Ticket.class, id);
                ticket.getIdTicket();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ticket with id " + id + " no longer exists.", enfe);
            }
            Cliente idCliente = ticket.getIdCliente();
            if (idCliente != null) {
                idCliente.getTicketCollection().remove(ticket);
                idCliente = em.merge(idCliente);
            }
            Trabajador idCajero = ticket.getIdCajero();
            if (idCajero != null) {
                idCajero.getTicketCollection().remove(ticket);
                idCajero = em.merge(idCajero);
            }
            em.remove(ticket);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ticket> findTicketEntities() {
        return findTicketEntities(true, -1, -1);
    }

    public List<Ticket> findTicketEntities(int maxResults, int firstResult) {
        return findTicketEntities(false, maxResults, firstResult);
    }

    private List<Ticket> findTicketEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ticket.class));
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

    public Ticket findTicket(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ticket.class, id);
        } finally {
            em.close();
        }
    }

    public int getTicketCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ticket> rt = cq.from(Ticket.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
