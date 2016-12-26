package com.smurfhouse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A House.
 */
@Entity
@Table(name = "house")
@Document(indexName = "house")
public class House extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 2048)
    @Column(name = "title", length = 2048, nullable = false)
    private String title;

    @NotNull
    @Size(max = 2048)
    @Column(name = "key", length = 2048, nullable = false)
    private String key;


    @NotNull
    @Size(max = 2048)
    @Column(name = "external_link", length = 2048, nullable = false)
    private String externalLink;

    @Column(name = "price", precision=10, scale=2)
    private BigDecimal price;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "meters")
    private Integer meters;

    @Column(name = "numrooms")
    private Integer numrooms;

    @Column(name = "floor")
    private Integer floor;

    @Size(max = 4096)
    @Column(name = "details", length = 4096)
    private String details;

    @Column(name = "elevator")
    private Boolean elevator;

    @Column(name = "facing_outside")
    private Boolean facingOutside;

    @Column(name = "garage")
    private Boolean garage;

    @OneToMany(mappedBy = "house")
    @JsonIgnore
    private Set<PriceHouse> priceHouses = new HashSet<>();

    @ManyToOne
    private GroupSearch groupSearch;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getMeters() {
        return meters;
    }

    public void setMeters(Integer meters) {
        this.meters = meters;
    }

    public Integer getNumrooms() {
        return numrooms;
    }

    public void setNumrooms(Integer numrooms) {
        this.numrooms = numrooms;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean isElevator() {
        return elevator;
    }

    public void setElevator(Boolean elevator) {
        this.elevator = elevator;
    }

    public Boolean isFacingOutside() {
        return facingOutside;
    }

    public void setFacingOutside(Boolean facingOutside) {
        this.facingOutside = facingOutside;
    }

    public Boolean isGarage() {
        return garage;
    }

    public void setGarage(Boolean garage) {
        this.garage = garage;
    }

    public Set<PriceHouse> getPriceHouses() {
        return priceHouses;
    }

    public void setPriceHouses(Set<PriceHouse> priceHouses) {
        this.priceHouses = priceHouses;
    }

    public GroupSearch getGroupSearch() {
        return groupSearch;
    }

    public void setGroupSearch(GroupSearch groupSearch) {
        this.groupSearch = groupSearch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        House house = (House) o;
        if(house.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, house.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "House{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", key='" + key + "'" +
            ", externalLink='" + externalLink + "'" +
            ", price='" + price + "'" +
            ", startDate='" + startDate + "'" +
            ", endDate='" + endDate + "'" +
            ", meters='" + meters + "'" +
            ", numrooms='" + numrooms + "'" +
            ", floor='" + floor + "'" +
            ", details='" + details + "'" +
            ", elevator='" + elevator + "'" +
            ", facingOutside='" + facingOutside + "'" +
            ", garage='" + garage + "'" +
            '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
