package com.example.order.repository;

import com.example.order.dto.enums.OrderStatus;
import com.example.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);


    // refactor the method use jpa method insted
    List<Order> findByStatus(OrderStatus status);
}
