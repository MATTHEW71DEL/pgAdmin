package org.matv.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Client {
    private String phone;
    private String name;
    private String surname;
    private String patronymic;
}