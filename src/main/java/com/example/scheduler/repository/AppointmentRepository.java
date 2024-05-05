package com.example.scheduler.repository;

import com.example.scheduler.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByOperatorIdAndTime(Integer operatorId, int time);

    List<Appointment> findByTime(int time);

    List<Appointment> findByOperatorId(Integer operatorId);
}
