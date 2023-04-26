package org.spbstu.aleksandrov.billingsystem.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "call")
@Getter
@Setter
public class Call implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_caller")
    private Customer caller;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_callee")
    private Customer callee;

    @Column(name = "price")
    private int price;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "comment")
    private String comment;

    public Call() {
    }

    public Call(Customer caller, Customer callee, int price,
                Date startDate, Date endDate, String comment) {
        this.caller = caller;
        this.callee = callee;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.comment = comment;
    }
}
