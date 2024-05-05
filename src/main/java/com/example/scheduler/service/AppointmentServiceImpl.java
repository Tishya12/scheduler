package com.example.scheduler.service;

import com.example.scheduler.model.Appointment;
import com.example.scheduler.model.AppointmentBookRequest;
import com.example.scheduler.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
                return "Appointment booked with operator " + operatorId + " and Appointment Id is " + appointmentId;
            } else {
                return "No operator is available please try with some other slot";
            }
        }
    }

    boolean checkOperatorAvailable(AppointmentBookRequest appointmentBookRequest) {
        List<Appointment> appointments = appointmentRepository.findByOperatorIdAndTime(appointmentBookRequest.getOperatorId(), appointmentBookRequest.getTime());
        return CollectionUtils.isEmpty(appointments);
    }

    Integer findAvailableOperator(Integer time) {
        List<Appointment> appointments = appointmentRepository.findByTime(time);

        // if it is a first appointment then returning 1st operator.
        if (CollectionUtils.isEmpty(appointments)) {
            return 1;
        }

        HashSet<Integer> bookedOperators = new HashSet<>();
        int n = appointments.size();
        int k = 0;
        while (bookedOperators.size() < 3 && k < n) {
            bookedOperators.add(appointments.get(k).getOperatorId());
            k++;
        }

        if (bookedOperators.size() == 3) return null;

        for (int i = 1; i <= 3; i++) {
            if (!bookedOperators.contains(i)) {
                return i;
            }
        }
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
            AtomicBoolean operatorAvailable = new AtomicBoolean(false);
            if (appointmentBookRequest.getAppointmentId() != null) {
                appointmentRepository.findById(appointmentBookRequest.getAppointmentId()).ifPresent(appointment -> {
                    if (checkOperatorAvailable(appointmentBookRequest)) {
                        operatorAvailable.set(true);
                        appointment.setTime(appointmentBookRequest.getTime());
                        appointmentRepository.save(appointment);
                    }
                });

                if (operatorAvailable.get()) {
                    return "Appointment rescheduled with " + appointmentBookRequest.getOperatorId() + " and Appointment id is " + appointmentBookRequest.getAppointmentId();
                } else {
                    return "Requested operator is not available please try with some other operator or try some other slot";
                }
            } else return "Please provide Appointment Id";
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
            return "Facing some issues, please try after sometime.";
        }
    }

    @Override
    public List<String> getAllAppointmentsByOperatorId(Integer operatorId) {
        List<Appointment> appointments = appointmentRepository.findByOperatorId(operatorId);
        List<String> timeSlots = new ArrayList<>();
        try {
            for (Appointment appointment : appointments) {
                String timeSlot = appointment.getTime() + "-" + (appointment.getTime() + 1);
                timeSlots.add(timeSlot);
            }
            Collections.sort(timeSlots);
            return timeSlots;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public List<String> getAllAvailableSlotsByOperatorId(Integer operatorId) {
        List<Appointment> appointments = appointmentRepository.findByOperatorId(operatorId);
        List<Integer> bookedTimeSlots = new ArrayList<>();
        try {
            for (Appointment appointment : appointments) {
                bookedTimeSlots.add(appointment.getTime());
            }
            Collections.sort(bookedTimeSlots);
            return findOpenSlots(bookedTimeSlots);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public static List<String> findOpenSlots(List<Integer> bookedSlots) {
        List<String> openSlots = new ArrayList<>();
        int start = 1;
        for (int end : bookedSlots) {
            if (start < end) {
                openSlots.add(start + "-" + end);
            }
            start = end + 1;
        }
        if (start < 24) {
            openSlots.add(start + "-" + 24);
        }
        return openSlots;
    }


}
