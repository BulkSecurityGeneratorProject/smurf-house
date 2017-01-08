package com.smurfhouse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Update.
 */
@Entity
@Table(name = "update",
        indexes = {
            @Index(name = "INDX_UPDATE_UPDATEDATE",  columnList="update_date", unique = false)
        }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "update")
public class Update implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;

    @Column(name = "news")
    private Integer news;

    @Column(name = "deletes")
    private Integer deletes;

    @Column(name = "price_updates")
    private Integer priceUpdates;

    @Column(name = "with_error")
    private Boolean error;

    @ManyToOne
    private GroupSearch groupSearch;

    @OneToMany(mappedBy = "update")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<HouseUpdate> houseUpdates = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getNews() {
        return news;
    }

    public void setNews(Integer news) {
        this.news = news;
    }

    public Integer getDeletes() {
        return deletes;
    }

    public void setDeletes(Integer deletes) {
        this.deletes = deletes;
    }

    public Integer getPriceUpdates() {
        return priceUpdates;
    }

    public void setPriceUpdates(Integer priceUpdates) {
        this.priceUpdates = priceUpdates;
    }

    public GroupSearch getGroupSearch() {
        return groupSearch;
    }

    public void setGroupSearch(GroupSearch groupSearch) {
        this.groupSearch = groupSearch;
    }

    public Set<HouseUpdate> getHouseUpdates() {
        return houseUpdates;
    }

    public void setHouseUpdates(Set<HouseUpdate> houseUpdates) {
        this.houseUpdates = houseUpdates;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Update update = (Update) o;
        if(update.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, update.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Update{" +
            "id=" + id +
            ", updateDate='" + updateDate + "'" +
            ", news='" + news + "'" +
            ", deletes='" + deletes + "'" +
            ", priceUpdates='" + priceUpdates + "'" +
            '}';
    }
}
