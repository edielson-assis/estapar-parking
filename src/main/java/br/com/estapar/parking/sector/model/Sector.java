package br.com.estapar.parking.sector.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = "sectorId")
@Setter
@Getter
@Entity
public class Sector implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sectorId;

    @Column(name = "sector_name", nullable = false, unique = true)
    private String sectorName;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;
}