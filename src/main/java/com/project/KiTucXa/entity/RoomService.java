package com.project.KiTucXa.entity;

import jakarta.persistence.*;
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
public class RoomService extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String roomServiceId;
    @ManyToOne
    @JoinColumn(name = "room_id")
    Room room;
    @ManyToOne
    @JoinColumn(name = "utility_service_id")
    UtilityService utilityService;
    BigDecimal price;

}
