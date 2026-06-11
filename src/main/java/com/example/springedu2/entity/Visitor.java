package com.example.springedu2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50)
    private String name;

    @CreationTimestamp
    @Column(name = "writedate", nullable = false, updatable = false)
    private LocalDate writedate;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000)
    private String memo;
}
