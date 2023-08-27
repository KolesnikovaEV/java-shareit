package ru.practicum.gate.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.gate.validation.CreateObject;
import ru.practicum.gate.validation.UpdateObject;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateUserDto {
    @NotBlank(groups = {CreateObject.class}, message = "Name cannot be blank")
    @Size(max = 255)
    private String name;
    @NotBlank(groups = {CreateObject.class}, message = "Email cannot be blank")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", groups = {CreateObject.class, UpdateObject.class},
            message = "Invalid email address")
    @Size(max = 512)
    private String email;
}
