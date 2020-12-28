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
    @NotBlank(message = "{NotBlank.user.firstName}")
    private String firstName;
    @NotBlank(message = "{NotBlank.user.lastName}")
    private String lastName;
    @Pattern(regexp = "^(\\w){3,20}\\b", message = "{Pattern.user.userName}")
    private String userName;
    @Email(message = "{Email.user.email}")
    private String email;
    @Pattern(regexp = "^(\\d){11,20}\\b", message = "{Pattern.user.phone}")
    private String phone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthday;
    @Pattern(regexp = "^(?=.*[\\d])(?=.*[a-z])[\\w!@#$%^&*()-=+,.;:]{8,}$", message = "{Pattern.user.password}")
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
