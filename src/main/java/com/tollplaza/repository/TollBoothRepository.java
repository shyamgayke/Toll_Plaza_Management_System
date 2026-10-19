
package com.tollplaza.repository;

import com.tollplaza.model.TollBooth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TollBoothRepository extends JpaRepository<TollBooth, Integer> {

    @Query("""
        SELECT b FROM TollBooth b
        WHERE (:boothId IS NULL OR b.boothId = :boothId)
          AND (:boothStatus IS NULL OR b.boothStatus = :boothStatus)
          AND (:plazaId IS NULL OR b.plazaId = :plazaId)
        ORDER BY b.boothId
        """)
    List<TollBooth> searchBooths(
            @Param("boothId") Integer boothId,
            @Param("boothStatus") String boothStatus,
            @Param("plazaId") Integer plazaId);

    // Count booths by status
    long countByBoothStatus(String boothStatus);
}
