
package com.tollplaza.repository;

import com.tollplaza.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    boolean existsByVehicleNumber(String vehicleNumber);

    boolean existsByFasTagNumber(String fasTagNumber);

    boolean existsByVehicleNumberAndVehicleIdNot(
            String vehicleNumber, Integer vehicleId);

    boolean existsByFasTagNumberAndVehicleIdNot(
            String fasTagNumber, Integer vehicleId);
}
