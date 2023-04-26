package org.spbstu.aleksandrov.billingsystem.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_callee")
    private List<Call> incomingCalls;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_caller")
    private List<Call> outgoingCalls;

    @Column(name = "phone_number")
    private long phone;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_tariff")
    private Tariff tariff;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_operator")
    private Operator operator;

    @Column(name = "balance")
    private int balance;

    @Column(name = "remain_minutes")
    private int minutesLeft;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "comment")
    private String comment;

    public Customer() {
    }

    public Customer(long phone, Tariff tariff, Operator operator, int balance,
                    int minutesLeft, Date updateTime, String comment) {
        this.phone = phone;
        this.tariff = tariff;
        this.operator = operator;
        this.balance = balance;
        this.minutesLeft = minutesLeft;
        this.updateTime = updateTime;
        this.comment = comment;
    }
}
