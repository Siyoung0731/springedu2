package com.example.springedu2.repository;

import com.example.springedu2.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitorRepository extends JpaRepository<Visitor, Integer> {
    // CRUD
    // 데이터 저장(create/update) -- save(entity) 데이터 저장
    // 데이터 단건 조회
    // e=
    List<Visitor> findByMemoContainingIgnoreCaseOrderByIdDesc(String keyword);
}
