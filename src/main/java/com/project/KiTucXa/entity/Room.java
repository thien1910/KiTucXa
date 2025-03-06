package com.project.KiTucXa.entity;

import com.project.KiTucXa.Enum.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Room extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
     String roomId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
     String roomName;
     String department;
     int maximumOccupancy;
     int currentOccupancy;
     String roomType;
     BigDecimal roomPrice;
    @Enumerated(EnumType.STRING)
     RoomStatus roomStatus;

     String note;

}
