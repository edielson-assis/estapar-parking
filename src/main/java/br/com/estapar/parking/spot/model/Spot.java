package br.com.estapar.parking.spot.model;

import java.io.Serializable;

import br.com.estapar.parking.sector.model.Sector;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = "spotId")
@Setter
@Getter
@Entity
public class Spot implements Serializable {

    @Id 
    private Long spotId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    @Column(name = "is_occupied", nullable = false)
    private Boolean isOccupied = false;
}