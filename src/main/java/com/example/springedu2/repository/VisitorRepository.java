package com.example.springedu2.repository;

import com.example.springedu2.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitorRepository extends JpaRepository<Visitor, Integer> {
    List<Visitor> findByMemoContainingIgnoreCaseOrderByIdDesc(String keyword);
}
