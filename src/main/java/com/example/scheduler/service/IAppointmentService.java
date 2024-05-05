package com.example.scheduler.service;

import com.example.scheduler.model.Appointment;
import com.example.scheduler.model.AppointmentBookRequest;

import java.util.List;
import java.util.Map;

public interface IAppointmentService {
    public String bookAppointment(AppointmentBookRequest appointmentBookRequest);

    public String rescheduleAppointment(AppointmentBookRequest appointmentBookRequest);

    public String cancelAppointment(Integer appointmentId);

    public Map<String, List<String>> getAllAvailableSlotsByOperatorId(Integer operatorId);

    public Map<String, List<String>> getAllAppointmentsByOperatorId(Integer operatorId);

}
