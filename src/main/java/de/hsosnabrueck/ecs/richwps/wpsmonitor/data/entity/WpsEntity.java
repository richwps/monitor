/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.persistence.Column;
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
     * Selects a specific wps that matches the given identifier parameter.
     */
    @NamedQuery(name = "wps.findByIdentifier", query = "SELECT t FROM WpsEntity t WHERE t.identifier = :identifier"),})
public class WpsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String identifier;

    private URI route;

    public WpsEntity() {
    }

    public WpsEntity(String identifier, URI route) {
        this.identifier = identifier;
        this.route = route;
    }

    public WpsEntity(String identifier, String route) throws MalformedURLException, URISyntaxException {
        this.identifier = identifier;
        this.setUri(route);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public URI getUri() {
        return route;
    }

    public void setUri(URI route) {
        this.route = route;
    }

    public final void setUri(String route) throws MalformedURLException, URISyntaxException {
        URL url = new URL(route);
        URI uri = url.toURI();

        this.route = uri;
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
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "WpsEntity{" + "identifier=" + identifier + ", route=" + route + '}';
    }
}
