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
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Visitor {

    @Id // 기본키 : Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 번호 자동증가
    private Integer id; // 방명록 고유식별번호 : 기본키

    // NotBlank : null, ""(빈문자열), " " : 공백포함된 문자열 전부 허용 X
    // Size(max=50) : 문자열(50문자), 배열(50개), ArrayList(50개)
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50)
    private String name;

    // data 등록일(Created Date) 자동입력 일일이 LocalDateTime.now() 를 넣지 않아도 된다.
    @CreationTimestamp
    @Column(name = "writedate", nullable = false, updatable = false)
    private LocalDate writeDate;

    @PrePersist
    public void prePersist() {
        writeDate = LocalDate.now();
    }

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000)
    private String memo;
}
