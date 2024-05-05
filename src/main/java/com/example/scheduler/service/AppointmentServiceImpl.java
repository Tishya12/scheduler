package com.example.scheduler.service;

import com.example.scheduler.model.Appointment;
import com.example.scheduler.model.AppointmentBookRequest;
import com.example.scheduler.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service(value = "appointmentService")
public class AppointmentServiceImpl implements IAppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Override
    public String bookAppointment(AppointmentBookRequest appointmentBookRequest) {
        if (appointmentBookRequest.getOperatorId() != null) {
            boolean operatorAvailable = checkOperatorAvailable(appointmentBookRequest);
            if (operatorAvailable) {
                Integer appointmentId = saveAppointmentDetails(appointmentBookRequest, appointmentBookRequest.getOperatorId());
                return "Appointment booked with operator " + appointmentBookRequest.getOperatorId() + " and Appointment Id is " + appointmentId;
            } else {
                return "Requested operator is not available please try with some other operator or try some other slot";
            }
        } else {
            Integer operatorId = findAvailableOperator(appointmentBookRequest.getTime());
            if (operatorId != null) {
                Integer appointmentId = saveAppointmentDetails(appointmentBookRequest, operatorId);
                return "Appointment booked with " + operatorId + "and Appointment Id is " + appointmentId;
            } else {
                return "No operator is available please try with some other slot";
            }
        }
    }

    boolean checkOperatorAvailable(AppointmentBookRequest appointmentBookRequest) {
        List<Appointment> appointments = appointmentRepository.findAll();
        if (CollectionUtils.isEmpty(appointments)) {
            return true;
        }
        for (Appointment appointment : appointments) {
            if (appointment.getOperatorId().equals(appointmentBookRequest.getOperatorId()) && appointment.getTime() == appointmentBookRequest.getTime()) {
                return false;
            }
        }
        return true;
    }

    Integer findAvailableOperator(Integer time) {
        List<Appointment> appointments = appointmentRepository.findAll();
        boolean op1 = true, op2 = true, op3 = true;

        if (CollectionUtils.isEmpty(appointments)) {
            return 1;
        }

        for (Appointment appointment : appointments) {
            if (appointment.getTime() == time) {
                if (appointment.getOperatorId() == 1) {
                    op1 = false;
                }
                if (appointment.getOperatorId() == 2) {
                    op2 = false;
                }
                if (appointment.getOperatorId() == 3) {
                    op3 = false;
                }
            }
        }
        if (op1 == true) return 1;
        if (op2 == true) return 2;
        if (op3 == true) return 3;
        return null;
    }


    Integer saveAppointmentDetails(AppointmentBookRequest appointmentBookRequest, Integer operatorId) {
        Appointment appointment = new Appointment(operatorId, appointmentBookRequest.getCustomerName(), appointmentBookRequest.getTime());
        appointmentRepository.save(appointment);
        return appointment.getAppointmentId();
    }

    @Override
    public String rescheduleAppointment(AppointmentBookRequest appointmentBookRequest) {
        try {
            if (appointmentBookRequest.getAppointmentId() != null) {
                appointmentRepository.findById(appointmentBookRequest.getAppointmentId()).ifPresent(appointment -> {
                    appointment.setTime(appointmentBookRequest.getTime());
                    appointmentRepository.save(appointment);
                });
                return "Appointment booked with " + appointmentBookRequest.getOperatorId() + " and Appointment id is " + appointmentBookRequest.getAppointmentId();
            } else return "Operator is not available please try with some other slot";
        } catch (Exception ex) {
            return "Facing some issues, please try after sometime.";
        }
    }

    @Override
    public String cancelAppointment(Integer appointmentId) {
        try {
            appointmentRepository.deleteById(appointmentId);
            return "Appointment cancelled successfully";
        } catch (Exception e) {
            return "Appointment not found";
        }
    }

    @Override
    public Map<String, List<String>> getAllAppointmentsByOperatorId(Integer operatorId) {
        List<Appointment> appointments = appointmentRepository.findAll();
        Map<String, List<String>> allAppointments = new HashMap<>();
        List<String> timeSlots = new ArrayList<>();
        try {
            for (Appointment appointment : appointments) {
                if (Objects.equals(appointment.getOperatorId(), operatorId)) {
                    String timeSlot = appointment.getTime() + "-" + (appointment.getTime() + 1);
                    timeSlots.add(timeSlot);
                }
            }
            allAppointments.put("Booked Time Slots", timeSlots);
            return allAppointments;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Map<String, List<String>> getAllAvailableSlotsByOperatorId(Integer operatorId) {
        List<Appointment> appointments = appointmentRepository.findAll();
        List<Integer> bookedTimeSlots = new ArrayList<>();
        try {
            for (Appointment appointment : appointments) {
                if (Objects.equals(appointment.getOperatorId(), operatorId)) {
                    bookedTimeSlots.add(appointment.getTime());
                }
            }
            Collections.sort(bookedTimeSlots);
            return findOpenSlots(bookedTimeSlots);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public static Map<String, List<String>> findOpenSlots(List<Integer> bookedSlots) {
        List<String> openSlots = new ArrayList<>();
        Map<String, List<String>> allOpenSlots = new HashMap<>();
        int start = 1;
        for (int end : bookedSlots) {
            if (start < end) {
                openSlots.add(start + "-" + end);
            }
            start = end + 1;
        }
        if (start <= 24) {
            openSlots.add(start + "-" + 24);
        }
        allOpenSlots.put("Available Slots", openSlots);
        return allOpenSlots;
    }


}
