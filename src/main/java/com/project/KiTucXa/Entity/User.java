package com.project.KiTucXa.Entity;

import jakarta.persistence.*;
import com.project.KiTucXa.Enum.Gender;
import com.project.KiTucXa.Enum.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String userId;
    String maSV;
    String userName;
    String passWord;
    String fullName;
    @Enumerated(EnumType.STRING)
    Gender gender;
//    String roomNameStudent;
    String cccd;
    String phoneNumber;
    @Enumerated(EnumType.STRING)
     Status status;
     String country;
    Set<String> roles;

}
