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

/**
 * Entity that group up various AbstractQosEntity objects by measurement
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@Entity
@NamedQueries({
    /**
     * Selects all MeasuredDataEntity objects by process and wps identifier.
     */
    @NamedQuery(name = "qos.getQosByProcess", query = "SELECT t FROM MeasuredDataEntity t WHERE t.process.identifier = :processIdentifier AND "
            + "t.process.wps.id = :wpsId "
            + "ORDER BY t.createTime DESC"),
    
    /**
     * Selects all MeasuredDataEntity objects by process and wps identifier.
     */
    @NamedQuery(name = "qos.getQosByProcessEndpoint", query = "SELECT t FROM MeasuredDataEntity t WHERE t.process.identifier = :processIdentifier "
            + "AND t.process.wps.endpoint = :endpoint "
            + "ORDER BY t.createTime DESC"),

    /**
     * Selects all MeasuredDataEntity objects by wps identifier.
     */
    @NamedQuery(name = "qos.getQosByWps", query = "SELECT t FROM MeasuredDataEntity t WHERE t.process.wps.id = :wpsId ORDER BY t.createTime DESC"),
    
     /**
     * Selects all MeasuredDataEntity objects by wps identifier.
     */
    @NamedQuery(name = "qos.getQosByWpsEndpoint", query = "SELECT t FROM MeasuredDataEntity t WHERE t.process.wps.endpoint = :endpoint ORDER BY t.createTime DESC"),

    /**
     * Deletes all MeasuredDataEntity instances which are associated by the
     * given :wpsId parameter.
     */
    @NamedQuery(name = "qos.deleteByWps", query = "DELETE FROM MeasuredDataEntity t WHERE t.process.wps.id = :wpsId"),

    /**
     * Deletes all MeasuredDataEntity instances which are associated by the
     * given :wpsId and :processIdentifier parameters.
     */
    @NamedQuery(name = "qos.deleteByWpsProcess", query = "DELETE FROM MeasuredDataEntity t WHERE "
            + "t.process.identifier = :processIdentifier AND t.process.wps.id = :wpsId"),
    
    /**
     * Deletes all MeasuredDataEntity instances which are associated by the
     * given :wpsId and :processIdentifier parameters.
     */
    @NamedQuery(name = "qos.deleteByWpsProcessEndpoint", query = "DELETE FROM MeasuredDataEntity t WHERE "
            + "t.process.identifier = :processIdentifier AND t.process.wps.endpoint = :endpoint"),

    /**
     * Deletes all MeasuredDataEntity instances which are associated by the
     * given :wpsId and :processIdentifier parameters and are older as
     * :date.
     */
    @NamedQuery(name = "qos.deleteByWpsProcessOlderAs", query = "DELETE FROM MeasuredDataEntity t WHERE "
            + "t.process.identifier = :processIdentifier AND t.process.wps.id = :wpsId AND t.createTime < :date"),
    
    /**
     * Deletes all MeasuredDataEntity instances which are associated by the
     * given :wpsId and :processIdentifier parameters and are older as
     * :date.
     */
    @NamedQuery(name = "qos.deleteByWpsProcessOlderAsEndpoint", query = "DELETE FROM MeasuredDataEntity t WHERE "
            + "t.process.identifier = :processIdentifier AND t.process.wps.endpoint = :endpoint AND t.createTime < :date"),

    /**
     * Deletes all MeasuredDataEntity instances which are older as :date.
     */
    @NamedQuery(name = "qos.deleteOlderAs", query = "DELETE FROM MeasuredDataEntity t WHERE t.createTime < :date")
})
public class MeasuredDataEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "measureddata_id")
    private Long id;

    @Column(nullable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createTime;

    @JoinColumn(nullable = false)
    @OneToOne
    private WpsProcessEntity process;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "owner_id", referencedColumnName = "measureddata_id")
    private List<AbstractQosEntity> data;

    public MeasuredDataEntity() {
        this.data = new ArrayList<>();
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
        if (!(object instanceof MeasuredDataEntity)) {
            return false;
        }
        MeasuredDataEntity other = (MeasuredDataEntity) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (AbstractQosEntity e : data) {
            builder.append(e);
            builder.append("\n");
        }

        return builder.toString();
    }

}
