package com.smurfhouse.domain;

import com.smurfhouse.domain.enumeration.HouseUpdateOperation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A HouseUpdate.
 */
@Entity
@Table(name = "house_update")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "houseupdate")
public class HouseUpdate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false)
    private HouseUpdateOperation operation;

    @ManyToOne
    private Update update;

    @ManyToOne
    private House house;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HouseUpdateOperation getOperation() {
        return operation;
    }

    public void setOperation(HouseUpdateOperation operation) {
        this.operation = operation;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HouseUpdate houseUpdate = (HouseUpdate) o;
        if(houseUpdate.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, houseUpdate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "HouseUpdate{" +
            "id=" + id +
            ", operation='" + operation + "'" +
            '}';
    }
}
