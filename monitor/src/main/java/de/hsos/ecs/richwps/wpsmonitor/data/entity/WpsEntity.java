/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.ecs.richwps.wpsmonitor.data.entity;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Entity for WPS.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@Entity
@NamedQueries({
    /**
     * Selects all wps entities.
     */
    @NamedQuery(name = "wps.getAll", query = "SELECT t FROM WpsEntity t"),
    
    /**
     * Selects an WPS entry by the given endpoint parameter
     */
    @NamedQuery(name = "wps.findByEndpoint", query = "SELECT t FROM WpsEntity t WHERE t.endpoint = :endpoint"),
})
public class WpsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String endpoint;

    public WpsEntity() {
    }

    public WpsEntity(final URL endpoint) {
        this.setEndpoint(endpoint);
    }

    public WpsEntity(final String endpoint) 
            throws MalformedURLException {
        this.endpoint = new URL(endpoint).toString();
    }

    public URL getEndpoint() {
        URL realEndpoint = null;
        
        try {
            realEndpoint = new URL(endpoint);
        } catch (MalformedURLException ex) {
            // this should never happens
        }
        
        return realEndpoint;
    }

    public void setEndpoint(final URL endpoint) {
        this.endpoint = endpoint.toString();
    }
    
    public void setEndpoint(final String endpoint) 
            throws MalformedURLException {
        this.setEndpoint(new URL(endpoint));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WpsEntity other = (WpsEntity) obj;
        return Objects.equals(this.id, other.id) || (this.id != null && this.id.equals(other.id));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "WpsEntity{" + "id=" + id + ", endpoint=" + endpoint + '}';
    }

}
