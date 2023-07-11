package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    Long id;
    @NotBlank(groups = {CreateObject.class}, message = "Name cannot be blank")
    String name;
    @NotBlank(groups = {CreateObject.class}, message = "Email cannot be blank")
    @Email(groups = {CreateObject.class, UpdateObject.class}, message = "Invalid email address")
    String email;
}
