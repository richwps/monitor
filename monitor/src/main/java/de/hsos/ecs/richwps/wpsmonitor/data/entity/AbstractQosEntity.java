/*
 * Copyright 2014 Florian Vogelpohl <floriantobias@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hsos.ecs.richwps.wpsmonitor.data.entity;

import de.hsos.ecs.richwps.wpsmonitor.boundary.restful.strategy.Hide;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * General entity for qos-measurements. All qos entity must extends this entity.
 * Otherwise they can't persisted in the database
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@NamedQueries({
    /**
     * Deletes all AbstractQosEntity instances which are associated with the
     * given :wpsId.
     */
    @NamedQuery(name = "abstractQos.deleteByWps", query = "DELETE FROM AbstractQosEntity q WHERE q.id IN(SELECT md.id FROM MeasuredDataEntity m JOIN m.data md WHERE m.process.wps.id = :wpsId)"),

    /**
     * Deletes all AbstractQosEntity instances which are associated with the
     * given :wpsId and :processIdentifier.
     */
    @NamedQuery(name = "abstractQos.deleteByWpsProcess", query = "DELETE FROM AbstractQosEntity t WHERE t.id "
            + "IN(SELECT md.id FROM MeasuredDataEntity m JOIN m.data md WHERE m.process.wps.id = :wpsId AND m.process.identifier = :processIdentifier)"),
    
    /**
     * Deletes all AbstractQosEntity instances which are associated with the
     * given :wpsId and :processIdentifier.
     */
    @NamedQuery(name = "abstractQos.deleteByWpsProcessEndpoint", query = "DELETE FROM AbstractQosEntity t WHERE t.id "
            + "IN(SELECT md.id FROM MeasuredDataEntity m JOIN m.data md WHERE m.process.wps.endpoint = :endpoint AND m.process.identifier = :processIdentifier)"),

    /**
     * Deletes all AbstractQosEntity instances which are associated with the
     * given :wpsId and :processIdentifier and are older as :date.
     */
    @NamedQuery(name = "abstractQos.deleteByWpsProcessOlderAs", query = "DELETE FROM AbstractQosEntity t WHERE t.id "
            + "IN(SELECT md.id FROM MeasuredDataEntity m JOIN m.data md WHERE m.process.wps.id = :wpsId AND m.process.identifier = :processIdentifier AND m.createTime < :date)"),

    /**
     * Deletes all AbstractQosEntity instances which are associated with the
     * given :wpsId and :processIdentifier and are older as :date.
     */
    @NamedQuery(name = "abstractQos.deleteByWpsProcessOlderAsEndpoint", query = "DELETE FROM AbstractQosEntity t WHERE t.id "
            + "IN(SELECT md.id FROM MeasuredDataEntity m JOIN m.data md WHERE m.process.wps.endpoint = :endpoint AND m.process.identifier = :processIdentifier AND m.createTime < :date)"),
    
    /**
     * Deletes all AbstractQosEntity instances which are older as :date.
     */
    @NamedQuery(name = "abstractQos.deleteOlderAs", query = "DELETE FROM AbstractQosEntity t WHERE t.id IN(SELECT md.id FROM MeasuredDataEntity m JOIN m.data md WHERE m.createTime < :date)")
})
@Entity
public abstract class AbstractQosEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Hide
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    /**
     * Important method to indicate which entity-type it is
     *
     * @return Name of the extended entity
     */
    public abstract String getEntityName();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AbstractQosEntity)) {
            return false;
        }
        AbstractQosEntity other = (AbstractQosEntity) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "de.hsos.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity[ id=" + id + " ]";
    }
}
