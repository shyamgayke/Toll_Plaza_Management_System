
package com.tollplaza.repository;

import com.tollplaza.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("""
        SELECT p FROM Payment p
        WHERE (:paymentId IS NULL OR p.paymentId = :paymentId)
          AND (:paymentDate IS NULL OR p.paymentDate = :paymentDate)
          AND (:paymentMode IS NULL OR p.paymentMode = :paymentMode)
          AND (:paymentStatus IS NULL OR p.paymentStatus = :paymentStatus)
        ORDER BY p.paymentDate DESC, p.paymentId DESC
        """)
    List<Payment> searchPayments(
            @Param("paymentId") Integer paymentId,
            @Param("paymentDate") LocalDate paymentDate,
            @Param("paymentMode") String paymentMode,
            @Param("paymentStatus") String paymentStatus);
    
    boolean existsByBoothId(Integer boothId);

    // Count all payment records
long count();

// Count completed payments
long countByPaymentStatus(String paymentStatus);

// Calculate total revenue from completed payments
@Query("""
    SELECT COALESCE(SUM(p.tollAmount), 0)
    FROM Payment p
    WHERE p.paymentStatus = :status
    """)
BigDecimal getTotalRevenueByStatus(
        @Param("status") String status);

// Count today's completed payments
long countByPaymentDateAndPaymentStatus(
        LocalDate paymentDate,
        String paymentStatus);

// Calculate today's completed revenue
@Query("""
    SELECT COALESCE(SUM(p.tollAmount), 0)
    FROM Payment p
    WHERE p.paymentDate = :date
      AND p.paymentStatus = :status
    """)
BigDecimal getRevenueByDateAndStatus(
        @Param("date") LocalDate date,
        @Param("status") String status);

// Revenue grouped by payment mode
@Query("""
    SELECT p.paymentMode, COALESCE(SUM(p.tollAmount), 0)
    FROM Payment p
    WHERE p.paymentStatus = :status
    GROUP BY p.paymentMode
    """)
List<Object[]> getRevenueByPaymentMode(
        @Param("status") String status);

// Daily completed revenue for the trend chart
@Query("""
    SELECT p.paymentDate, COALESCE(SUM(p.tollAmount), 0)
    FROM Payment p
    WHERE p.paymentStatus = :status
      AND p.paymentDate IS NOT NULL
    GROUP BY p.paymentDate
    ORDER BY p.paymentDate
    """)
List<Object[]> getDailyRevenueByStatus(
        @Param("status") String status);

// Revenue grouped by toll plaza
@Query("""
    SELECT tp.plazaName, COALESCE(SUM(p.tollAmount), 0)
    FROM Payment p
    JOIN p.tollBooth b
    JOIN b.tollPlaza tp
    WHERE p.paymentStatus = :status
    GROUP BY tp.plazaId, tp.plazaName
    ORDER BY SUM(p.tollAmount) DESC
    """)
List<Object[]> getRevenueByPlazaAndStatus(
        @Param("status") String status);

}
