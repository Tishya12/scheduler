package com.example.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentBookRequest {
    private Integer operatorId;
    @NotNull(message = "Customer name can not be null")
    private String customerName;
    @NotNull(message = "Time can not be null")
    private int time;
    private Integer appointmentId;

}
