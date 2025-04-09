package com.rest.server.models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String userId;

    private String userTitle;

    private String userFirstName;

    private String userLastName;

    private String userGender;

    private String userEmail;

    private String userDateOfBirth;

    private String userRegisterDate;

    private String userPhone;

    private String userPicture;

    private String userLocationId;
}
