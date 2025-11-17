package cue.edu.co.eventcore.application.dtos.participant;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Participant creation/update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantRequestDto {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Pattern(regexp = "^[0-9]{10,20}$", message = "Phone must be a valid number with 10-20 digits")
    private String phone;

    @NotBlank(message = "Document number is required")
    @Size(min = 5, max = 50, message = "Document number must be between 5 and 50 characters")
    private String documentNumber;
}
