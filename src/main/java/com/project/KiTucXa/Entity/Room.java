package com.project.KiTucXa.Entity;

import jakarta.persistence.*;
import com.project.KiTucXa.Enum.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

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
