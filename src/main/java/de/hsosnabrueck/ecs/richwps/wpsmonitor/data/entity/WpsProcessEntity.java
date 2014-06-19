/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 *
 * @author FloH
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "wpsprocess.getAllOf", query = "SELECT t FROM WpsProcessEntity t WHERE t.wps.identifier = :identifier"),
    @NamedQuery(name = "wpsprocess.getAll", query = "SELECT t FROM WpsProcessEntity t"),
    @NamedQuery(name = "wpsprocess.get", query = "SELECT t FROM WpsProcessEntity t WHERE t.wps.identifier = :wpsidentifier AND t.identifier = :identifier"),
    @NamedQuery(name = "wpsprocess.deleteAllFromWps", query = "DELETE FROM WpsProcessEntity t WHERE t.wps.identifier = :wpsidentifier")
})
public class WpsProcessEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Version
    private long version;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String identifier;
    private boolean wpsException;
    
    @Lob
    private String rawRequest;
    

    @OneToOne
    private WpsEntity wps;

    public WpsProcessEntity() {
        this(null, null);
    }
    
    public WpsProcessEntity(String identifier) {
        this(identifier, null);
    }
    
    public WpsProcessEntity(String identifier, WpsEntity wps) {
        this.identifier = identifier;
        this.wps = wps;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public WpsEntity getWps() {
        return wps;
    }

    public void setWps(WpsEntity wps) {
        this.wps = wps;
    }

    public String getRawRequest() {
        return rawRequest;
    }

    public void setRawRequest(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isWpsException() {
        return wpsException;
    }

    public void setWpsException(boolean wpsException) {
        this.wpsException = wpsException;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final WpsProcessEntity other = (WpsProcessEntity) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
