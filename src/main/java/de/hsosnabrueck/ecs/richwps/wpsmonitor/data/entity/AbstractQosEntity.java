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
package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

/**
 * General entity for qos-measurements. All qos entity musst extends this
 * entity. Otherwise they can't persisted in the database
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "abstractQos.deleteByWps", query = "DELETE FROM AbstractQosEntity t WHERE t.id IN(SELECT m.id FROM MeasuredDataEntity m WHERE m.process.wps.identifier = :wpsIdentifier)"),
    @NamedQuery(name = "abstractQos.deleteByWpsProcess", query = "DELETE FROM AbstractQosEntity t WHERE t.id IN(SELECT m.id FROM MeasuredDataEntity m WHERE m.process.wps.identifier = :wpsIdentifier AND m.process.identifier = :processIdentifier)")
})
public abstract class AbstractQosEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Version
    private long version;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AbstractQosEntity)) {
            return false;
        }
        AbstractQosEntity other = (AbstractQosEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public abstract String getDataAsString();

    @Override
    public String toString() {
        return "de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.AbstractQosEntity[ id=" + id + " ]";
    }

}
