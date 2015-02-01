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
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity for WpsProcesses.
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
@Entity
@NamedQueries({
    /**
     * Gets all WpsProcess-entities by wps identifier.
     */
    @NamedQuery(name = "wpsprocess.getAllOf", query = "SELECT t FROM WpsProcessEntity t WHERE t.wps.id = :wpsId"),

    /**
     * Gets all WpsProcess-entities by wps endpoint.
     */
    @NamedQuery(name = "wpsprocess.getAllOfEndpoint", query = "SELECT t FROM WpsProcessEntity t WHERE t.wps.endpoint = :endpoint"),

    /**
     * Gets all WpsProcess-entities.
     */
    @NamedQuery(name = "wpsprocess.getAll", query = "SELECT t FROM WpsProcessEntity t"),

    /**
     * Gets a specific process entity by wps.identifier and process.identifier.
     */
    @NamedQuery(name = "wpsprocess.get", query = "SELECT t FROM WpsProcessEntity t WHERE t.wps.id = :wpsId AND t.identifier = :processIdentifier"),

    /**
     * Gets a specific process entity by wps.endpoint and process.identifier.
     */
    @NamedQuery(name = "wpsprocess.getByEndpoint", query = "SELECT t FROM WpsProcessEntity t WHERE t.wps.endpoint = :endpoint AND t.identifier = :processIdentifier"),

    /**
     * Deletes all process entities that match the given wps identifier.
     */
    @NamedQuery(name = "wpsprocess.deleteByWps", query = "DELETE FROM WpsProcessEntity t WHERE t.wps.id = :wpsId"),

    /**
     * Deletes all process entities that match the given wps endpoint.
     */
    @NamedQuery(name = "wpsprocess.deleteByWpsEndpoint", query = "DELETE FROM WpsProcessEntity t WHERE t.wps.endpoint = :endpoint")
})

@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"identifier", "wps_id"})
)
public class WpsProcessEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String identifier;
    private boolean wpsException;

    @Lob
    private String rawRequest;

    @OneToOne
    @JoinColumn(name = "wps_id")
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
        return Objects.equals(this.id, other.id) || (this.id != null && this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "WpsProcessEntity{" + "id=" + id + ", identifier=" + identifier + " wps=" + wps + '}';
    }
}
