package com.example.scheduler.model;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer appointmentId;
    private Integer operatorId;
    private String customerName;
    private int time;

    public Appointment(Integer operatorId, String customerName, int time) {
        this.operatorId = operatorId;
        this.customerName = customerName;
        this.time = time;
    }

}
