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
import modelo.Reporte;
import modelo.Trabajador;

/**
 *
 * @author magal
 */
public class TrabajadorJpaController {
    
    public TrabajadorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
}
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Trabajador trabajador) {
        if (trabajador.getTicketCollection() == null) {
            trabajador.setTicketCollection(new ArrayList<Ticket>());
        }
        if (trabajador.getReporteCollection() == null) {
            trabajador.setReporteCollection(new ArrayList<Reporte>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Ticket> attachedTicketCollection = new ArrayList<Ticket>();
            for (Ticket ticketCollectionTicketToAttach : trabajador.getTicketCollection()) {
                ticketCollectionTicketToAttach = em.getReference(ticketCollectionTicketToAttach.getClass(), ticketCollectionTicketToAttach.getIdTicket());
                attachedTicketCollection.add(ticketCollectionTicketToAttach);
            }
            trabajador.setTicketCollection(attachedTicketCollection);
            Collection<Reporte> attachedReporteCollection = new ArrayList<Reporte>();
            for (Reporte reporteCollectionReporteToAttach : trabajador.getReporteCollection()) {
                reporteCollectionReporteToAttach = em.getReference(reporteCollectionReporteToAttach.getClass(), reporteCollectionReporteToAttach.getIdReporte());
                attachedReporteCollection.add(reporteCollectionReporteToAttach);
            }
            trabajador.setReporteCollection(attachedReporteCollection);
            em.persist(trabajador);
            for (Ticket ticketCollectionTicket : trabajador.getTicketCollection()) {
                Trabajador oldIdCajeroOfTicketCollectionTicket = ticketCollectionTicket.getIdCajero();
                ticketCollectionTicket.setIdCajero(trabajador);
                ticketCollectionTicket = em.merge(ticketCollectionTicket);
                if (oldIdCajeroOfTicketCollectionTicket != null) {
                    oldIdCajeroOfTicketCollectionTicket.getTicketCollection().remove(ticketCollectionTicket);
                    oldIdCajeroOfTicketCollectionTicket = em.merge(oldIdCajeroOfTicketCollectionTicket);
                }
            }
            for (Reporte reporteCollectionReporte : trabajador.getReporteCollection()) {
                Trabajador oldGeneradoPorOfReporteCollectionReporte = reporteCollectionReporte.getGeneradoPor();
                reporteCollectionReporte.setGeneradoPor(trabajador);
                reporteCollectionReporte = em.merge(reporteCollectionReporte);
                if (oldGeneradoPorOfReporteCollectionReporte != null) {
                    oldGeneradoPorOfReporteCollectionReporte.getReporteCollection().remove(reporteCollectionReporte);
                    oldGeneradoPorOfReporteCollectionReporte = em.merge(oldGeneradoPorOfReporteCollectionReporte);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Trabajador trabajador) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Trabajador persistentTrabajador = em.find(Trabajador.class, trabajador.getIdTrabajador());
            Collection<Ticket> ticketCollectionOld = persistentTrabajador.getTicketCollection();
            Collection<Ticket> ticketCollectionNew = trabajador.getTicketCollection();
            Collection<Reporte> reporteCollectionOld = persistentTrabajador.getReporteCollection();
            Collection<Reporte> reporteCollectionNew = trabajador.getReporteCollection();
            List<String> illegalOrphanMessages = null;
            for (Ticket ticketCollectionOldTicket : ticketCollectionOld) {
                if (!ticketCollectionNew.contains(ticketCollectionOldTicket)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ticket " + ticketCollectionOldTicket + " since its idCajero field is not nullable.");
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
            trabajador.setTicketCollection(ticketCollectionNew);
            Collection<Reporte> attachedReporteCollectionNew = new ArrayList<Reporte>();
            for (Reporte reporteCollectionNewReporteToAttach : reporteCollectionNew) {
                reporteCollectionNewReporteToAttach = em.getReference(reporteCollectionNewReporteToAttach.getClass(), reporteCollectionNewReporteToAttach.getIdReporte());
                attachedReporteCollectionNew.add(reporteCollectionNewReporteToAttach);
            }
            reporteCollectionNew = attachedReporteCollectionNew;
            trabajador.setReporteCollection(reporteCollectionNew);
            trabajador = em.merge(trabajador);
            for (Ticket ticketCollectionNewTicket : ticketCollectionNew) {
                if (!ticketCollectionOld.contains(ticketCollectionNewTicket)) {
                    Trabajador oldIdCajeroOfTicketCollectionNewTicket = ticketCollectionNewTicket.getIdCajero();
                    ticketCollectionNewTicket.setIdCajero(trabajador);
                    ticketCollectionNewTicket = em.merge(ticketCollectionNewTicket);
                    if (oldIdCajeroOfTicketCollectionNewTicket != null && !oldIdCajeroOfTicketCollectionNewTicket.equals(trabajador)) {
                        oldIdCajeroOfTicketCollectionNewTicket.getTicketCollection().remove(ticketCollectionNewTicket);
                        oldIdCajeroOfTicketCollectionNewTicket = em.merge(oldIdCajeroOfTicketCollectionNewTicket);
                    }
                }
            }
            for (Reporte reporteCollectionOldReporte : reporteCollectionOld) {
                if (!reporteCollectionNew.contains(reporteCollectionOldReporte)) {
                    reporteCollectionOldReporte.setGeneradoPor(null);
                    reporteCollectionOldReporte = em.merge(reporteCollectionOldReporte);
                }
            }
            for (Reporte reporteCollectionNewReporte : reporteCollectionNew) {
                if (!reporteCollectionOld.contains(reporteCollectionNewReporte)) {
                    Trabajador oldGeneradoPorOfReporteCollectionNewReporte = reporteCollectionNewReporte.getGeneradoPor();
                    reporteCollectionNewReporte.setGeneradoPor(trabajador);
                    reporteCollectionNewReporte = em.merge(reporteCollectionNewReporte);
                    if (oldGeneradoPorOfReporteCollectionNewReporte != null && !oldGeneradoPorOfReporteCollectionNewReporte.equals(trabajador)) {
                        oldGeneradoPorOfReporteCollectionNewReporte.getReporteCollection().remove(reporteCollectionNewReporte);
                        oldGeneradoPorOfReporteCollectionNewReporte = em.merge(oldGeneradoPorOfReporteCollectionNewReporte);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = trabajador.getIdTrabajador();
                if (findTrabajador(id) == null) {
                    throw new NonexistentEntityException("The trabajador with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Trabajador trabajador;
            try {
                trabajador = em.getReference(Trabajador.class, id);
                trabajador.getIdTrabajador();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The trabajador with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ticket> ticketCollectionOrphanCheck = trabajador.getTicketCollection();
            for (Ticket ticketCollectionOrphanCheckTicket : ticketCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Trabajador (" + trabajador + ") cannot be destroyed since the Ticket " + ticketCollectionOrphanCheckTicket + " in its ticketCollection field has a non-nullable idCajero field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Reporte> reporteCollection = trabajador.getReporteCollection();
            for (Reporte reporteCollectionReporte : reporteCollection) {
                reporteCollectionReporte.setGeneradoPor(null);
                reporteCollectionReporte = em.merge(reporteCollectionReporte);
            }
            em.remove(trabajador);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Trabajador> findTrabajadorEntities() {
        return findTrabajadorEntities(true, -1, -1);
    }

    public List<Trabajador> findTrabajadorEntities(int maxResults, int firstResult) {
        return findTrabajadorEntities(false, maxResults, firstResult);
    }

    private List<Trabajador> findTrabajadorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Trabajador.class));
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

    public Trabajador findTrabajador(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Trabajador.class, id);
        } finally {
            em.close();
        }
    }

    public int getTrabajadorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Trabajador> rt = cq.from(Trabajador.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
