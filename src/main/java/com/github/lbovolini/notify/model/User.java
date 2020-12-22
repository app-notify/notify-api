package com.github.lbovolini.notify.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Document
public class User {

    @Id
    private String id;
    @NotBlank(message = "{firstName.not.blank}")
    private String firstName;
    @NotBlank(message = "{lastName.not.blank}")
    private String lastName;
    @Pattern(regexp = "^(\\w){3,20}\\b", message = "{userName.regexp}")
    private String userName;
    @Email(message = "{email.address}")
    private String email;
    @Pattern(regexp = "^(\\d){11,20}\\b", message = "{phone.regexp}")
    private String phone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthday;
    @Pattern(regexp = "^(?=.*[\\d])(?=.*[a-z])[\\w!@#$%^&*()-=+,.;:]{8,}$", message = "{password.regexp}")
    private String password;

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getPassword() {
        return password;
    }
}
