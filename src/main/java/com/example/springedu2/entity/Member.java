package com.example.springedu2.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Entity
@Table(name = "members")    // table 이름 변경
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Member { // 회원
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)   // NotNull, UNIQUE, varchar(30)
    private String username;

    @Column(nullable = false) // BCrypt 암호화 통가하면 길이가 길어진다 length 지정안함
    private String password;

    @Column(nullable = false, length = 50)
    private String name; // 사용자 이름

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role = Role.USER;  // 권한

    @Column(nullable = false)
    private boolean enabled = true;           // 계정 사용 가능

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;           // 계정 생성일 , 가입일

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;            // 계정 수정일
}
