package com.example.springedu2.repository;

import com.example.springedu2.entity.Visitor;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Integer> {
    // CRUD
    // 데이터 저장(create/update) -- save(entity) 데이터 저장
    // 데이터 단건 조회
    //
    List<Visitor> findByMemoContainingIgnoreCaseOrderByIdDesc(String keyword);
    Optional<Visitor> findById(@RequestParam Long id);
    @Query(value = "select v from Visitor v where v.name = ?1", nativeQuery = false)
    List<Visitor> findByIrum(String key);
}
