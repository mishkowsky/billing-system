package org.spbstu.aleksandrov.billingsystem.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tariff")
@Getter
@Setter
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "tariff_code")
    private String code;

    @Column(name = "tariff_name")
    private String name;

    @Column(name = "minutes_limit")
    private int minutesLimit;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_tariff")
    private List<Price> prices;

    @Column(name = "price")
    private int price;

    public Tariff() {
    }

    public Tariff(int id, String code, String name, int minutesLimit, int price) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.minutesLimit = minutesLimit;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format(
                "%d %s %s %s %d",
                id, code, name, price, price
        );
    }
}
