/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author FloH
 */
@Entity
public class WpsEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private List<WpsProcessEntity> wpsProcess;
    
    @Id
    private String identifier;
    
    public WpsEntity() {
        this(null, new ArrayList<WpsProcessEntity>());
    }

    public WpsEntity(String identifier) {
        this(identifier, new ArrayList<WpsProcessEntity>());
    }

    public WpsEntity(String identifier, List<WpsProcessEntity> wpsProcess) {
        this.wpsProcess = wpsProcess;
        this.identifier = identifier;
    }
    
    public List<WpsProcessEntity> getWpsProcess() {
        return wpsProcess;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean add(WpsProcessEntity e) {
        return wpsProcess.add(e);
    }

    public boolean remove(WpsProcessEntity o) {
        return wpsProcess.remove(o);
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
        return "WpsEntity{" + "wpsProcess=" + wpsProcess + ", identifier=" + identifier + '}';
    }
    
}
