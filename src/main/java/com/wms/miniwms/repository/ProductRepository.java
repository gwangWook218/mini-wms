package com.wms.miniwms.repository;

import com.wms.miniwms.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 비관적 락을 적용한 단건 상품 조회
    // @Query를 통해 이름 파싱 오해를 방지하고 정확한 쿼리를 날려줍니다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);
}
