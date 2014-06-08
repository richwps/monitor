/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity;

import java.io.Serializable;
import java.net.URI;
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
public class WpsEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Version
    private long version;
    
    @Id
    private String identifier;
    private URI route;
    
    public WpsEntity() {
    }

    public WpsEntity(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public URI getRoute() {
        return route;
    }

    public void setRoute(URI route) {
        this.route = route;
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
