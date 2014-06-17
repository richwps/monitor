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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

/**
 *
 * @author FloH
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "wps.getAllWps", query = "SELECT t FROM WpsEntity t")
})
public final class WpsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Version
    private long version;

    @Id
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

    public URI getUri() {
        return route;
    }

    public void setUri(URI route) {
        this.route = route;
    }

    public void setUri(String route) throws MalformedURLException, URISyntaxException {
        URL url = new URL(route);
        URI uri = url.toURI();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
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
        if ((this.identifier == null) ? (other.identifier != null) : !this.identifier.equals(other.identifier)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WpsEntity{" + "identifier=" + identifier + ", route=" + route + '}';
    }
}
