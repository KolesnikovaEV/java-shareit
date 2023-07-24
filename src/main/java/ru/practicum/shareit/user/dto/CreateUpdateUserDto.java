package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateUserDto {
    @NotBlank(groups = {CreateObject.class}, message = "Name cannot be blank")
    private String name;
    @NotBlank(groups = {CreateObject.class}, message = "Email cannot be blank")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", groups = {CreateObject.class, UpdateObject.class},
            message = "Invalid email address")
    private String email;
}
