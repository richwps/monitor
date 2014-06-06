/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;

/**
 *
 * @author FloH
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "wpsprocess.getAllOf", query = "SELECT t FROM WpsProcessEntity t WHERE t.wps.identifier = :identifier")
})
public class WpsProcessEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Version
    private long version;
    
    @Id
    private String identifier;
    
    @Id
    @ManyToOne(fetch=FetchType.LAZY)
    private WpsEntity wps;
    @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private List<AbstractQosEntity> qosData;

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
    
    /**
     * ATTENTION! If u use this method, lazy load will load ALL associated
     * AbstractQosEntities ...
     * 
     * @param e
     * @return 
     */
    public boolean add(AbstractQosEntity e) {
        return qosData.add(e);
    }

    public boolean remove(AbstractQosEntity o) {
        return qosData.remove(o);
    }

    public List<AbstractQosEntity> getQosData() {
        return qosData;
    }

    public void setQosData(List<AbstractQosEntity> qosData) {
        this.qosData = qosData;
    }
}
