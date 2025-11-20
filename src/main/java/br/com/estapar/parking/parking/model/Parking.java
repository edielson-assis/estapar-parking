package br.com.estapar.parking.parking.model;

import java.io.Serializable;
import java.time.Instant;

import br.com.estapar.parking.spot.model.Spot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = "parkingId")
@Setter
@Getter
@Entity
public class Parking implements Serializable {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parkingId;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "spot_id")
    private Spot spot;

    @Column(name = "sector", nullable = false)
    private String sector;

    @Column(name = "entry_time", nullable = false)
    private Instant entryTime;

    @Column(name = "exit_time")
    private Instant exitTime;

    @Column(name = "base_price_at_entry")
    private Double basePriceAtEntry;

    @Column(name = "dynamic_factor")
    private Double dynamicFactor;

    @Column(name = "total_price")
    private Double totalPrice;
}