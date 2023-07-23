package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class CreateUpdateUserDto {
    @NotBlank(groups = {CreateObject.class}, message = "Name cannot be blank")
    String name;
    @NotBlank(groups = {CreateObject.class}, message = "Email cannot be blank")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", groups = {CreateObject.class, UpdateObject.class},
            message = "Invalid email address")
    String email;
}
