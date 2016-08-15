package com.smurfhouse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smurfhouse.domain.enumeration.Provider;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A GroupSearch.
 */
@Entity
@Table(name = "group_search")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "groupsearch")
public class GroupSearch extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "title", length = 256, nullable = false)
    private String title;

    @NotNull
    @Size(max = 2048)
    @Column(name = "url", length = 2048, nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Provider provider;

    @NotNull
    @Column(name = "max_limit_price", precision=10, scale=2, nullable = false)
    private BigDecimal maxLimitPrice;

    @Column(name = "activated")
    private Boolean activated;

    @OneToMany(mappedBy = "groupSearch")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<House> houses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public BigDecimal getMaxLimitPrice() {
        return maxLimitPrice;
    }

    public void setMaxLimitPrice(BigDecimal maxLimitPrice) {
        this.maxLimitPrice = maxLimitPrice;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Set<House> getHouses() {
        return houses;
    }

    public void setHouses(Set<House> houses) {
        this.houses = houses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupSearch groupSearch = (GroupSearch) o;
        if(groupSearch.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, groupSearch.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "GroupSearch{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", url='" + url + "'" +
            ", provider='" + provider + "'" +
            ", maxLimitPrice='" + maxLimitPrice + "'" +
            ", activated='" + activated + "'" +
            '}';
    }
}
