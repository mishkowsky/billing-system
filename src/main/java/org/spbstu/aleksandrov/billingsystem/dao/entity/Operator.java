package org.spbstu.aleksandrov.billingsystem.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "operator")
@Getter
@Setter
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "operator_name")
    private String name;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "comment")
    private String comment;

    public Operator() {
    }

    public Operator(String name, Date updateTime, String comment) {
        this.name = name;
        this.updateTime = updateTime;
        this.comment = comment;
    }
}
