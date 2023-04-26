package org.spbstu.aleksandrov.billingsystem.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "price_per_minute")
@Getter
@Setter
public class Price {

    public enum CallType {INCOMING, OUTGOING}

    public enum PriceType {BELOW_LIMIT, OVER_LIMIT}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_tariff")
    private Tariff tariff;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_operator")
    private Operator operator;

    @Column(name = "call_type")
    @Enumerated(EnumType.STRING)
    private CallType callType;

    @Column(name = "price_type")
    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    @Column(name = "price")
    private int price;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "comment")
    private String comment;

    public Price() {
    }

    public Price(Tariff tariff, Operator operator, CallType callType, PriceType priceType,
                 int price, Date updateTime, String comment) {
        this.tariff = tariff;
        this.operator = operator;
        this.callType = callType;
        this.priceType = priceType;
        this.price = price;
        this.updateTime = updateTime;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s %s %s %d",
                tariff.getName(), operator.getName(), callType.toString(), priceType.toString(), price
        );
    }
}
