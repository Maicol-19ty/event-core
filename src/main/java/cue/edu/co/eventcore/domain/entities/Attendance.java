package cue.edu.co.eventcore.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity representing an Attendance (relationship between Event and Participant)
 * This is a pure domain object with no framework dependencies
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    private Long id;
    private Long eventId;
    private Long participantId;
    private AttendanceStatus status;
    private LocalDateTime registrationDate;
    private LocalDateTime checkInDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Check if participant is registered
     * @return true if participant is registered
     */
    public boolean isRegistered() {
        return AttendanceStatus.REGISTERED.equals(status);
    }

    /**
     * Business logic: Check if participant has checked in
     * @return true if participant has checked in
     */
    public boolean isCheckedIn() {
        return checkInDate != null && AttendanceStatus.CHECKED_IN.equals(status);
    }

    /**
     * Business logic: Check if attendance is cancelled
     * @return true if attendance is cancelled
     */
    public boolean isCancelled() {
        return AttendanceStatus.CANCELLED.equals(status);
    }

    /**
     * Business logic: Perform check-in
     */
    public void checkIn() {
        this.status = AttendanceStatus.CHECKED_IN;
        this.checkInDate = LocalDateTime.now();
    }

    /**
     * Business logic: Cancel attendance
     */
    public void cancel() {
        this.status = AttendanceStatus.CANCELLED;
    }
}
