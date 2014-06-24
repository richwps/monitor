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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Version;

/**
 * Entity that group up various AbstractQosEntity objects by measurement
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@Entity
@NamedQueries({
    /**
     * Selects all MeasuredDataEntity objects be process
     */
    @NamedQuery(name = "qos.getQosByProcess", query = "SELECT t FROM MeasuredDataEntity t WHERE t.process.identifier = :identifier AND "
            + "t.process.wps.identifier = :wpsIdentifier "
            + "ORDER BY t.createTime"),

    /**
     * Select all MeasuredDataEntity objects by wps
     */
    @NamedQuery(name = "qos.getQosByWps", query = "SELECT t FROM MeasuredDataEntity t WHERE t.process.wps.identifier = :identifier"),
    @NamedQuery(name = "qos.deleteByWps", query = "DELETE FROM MeasuredDataEntity t WHERE t.process.wps.identifier = :wpsIdentifier"),
    @NamedQuery(name = "qos.deleteByWpsProcess", query = "DELETE FROM MeasuredDataEntity t WHERE t.process.identifier = :processIdentifier AND t.process.wps.identifier = :wpsIdentifier")
})
public class MeasuredDataEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private long version;

    @Column(nullable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date createTime;

    @JoinColumn(nullable = false)
    @OneToOne
    private WpsProcessEntity process;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private List<AbstractQosEntity> data;

    public MeasuredDataEntity() {
        this.data = new ArrayList<AbstractQosEntity>();
    }

    public MeasuredDataEntity(List<AbstractQosEntity> qosEntities) {
        this.data = qosEntities;
        this.createTime = new Date();
    }

    public boolean add(AbstractQosEntity e) {
        return data.add(e);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public WpsProcessEntity getProcess() {
        return process;
    }

    public void setProcess(WpsProcessEntity process) {
        this.process = process;
    }

    public List<AbstractQosEntity> getData() {
        return data;
    }

    public void setData(List<AbstractQosEntity> data) {
        this.data = data;
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
        if (!(object instanceof MeasuredDataEntity)) {
            return false;
        }
        MeasuredDataEntity other = (MeasuredDataEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (AbstractQosEntity e : data) {
            builder.append(e.getDataAsString());
            builder.append("\n");
        }

        return builder.toString();
    }

}
