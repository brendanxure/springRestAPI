package com.codewithmosh.store.dtos;


import lombok.Data;

@Data
public class ChangePasswordRequest {
    String oldPassword;
    String newPassword;
}
