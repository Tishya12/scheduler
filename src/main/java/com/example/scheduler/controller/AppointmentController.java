package com.example.scheduler.controller;

import com.example.scheduler.model.AppointmentBookRequest;
import com.example.scheduler.service.IAppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
public class AppointmentController {

    @Autowired
    IAppointmentService appointmentService;

    @PostMapping(value = "/bookAppointment")
    public ResponseEntity<String> bookAppointment(@RequestBody @Valid AppointmentBookRequest appointmentBookRequest) {
        return new ResponseEntity<>(appointmentService.bookAppointment(appointmentBookRequest), HttpStatus.OK);
    }

    @PutMapping(value = "/rescheduleAppointment")
    public ResponseEntity<String> rescheduleAppointment(@RequestBody AppointmentBookRequest appointmentBookRequest) {
        return new ResponseEntity<>(appointmentService.rescheduleAppointment(appointmentBookRequest), HttpStatus.OK);
    }

    @DeleteMapping(value = "/cancelAppointment")
    public ResponseEntity<String> deleteAppointment(@RequestParam Integer appointmentId) {
        return new ResponseEntity<>(appointmentService.cancelAppointment(appointmentId), HttpStatus.OK);
    }

    @GetMapping(value = "/showAllAppointments")
    public ResponseEntity<List<String>> getAllAppointments(@RequestParam Integer operatorId) {
        return new ResponseEntity<>(appointmentService.getAllAppointmentsByOperatorId(operatorId), HttpStatus.OK);

    }

    @GetMapping(value = "/getAvailableSlots")
    public ResponseEntity<List<String>> getAllAvailableSlots(@RequestParam Integer operatorId) {
        return new ResponseEntity<>(appointmentService.getAllAvailableSlotsByOperatorId(operatorId), HttpStatus.OK);
    }

}
