
package com.tollplaza;

import com.tollplaza.model.Vehicle;
import com.tollplaza.repository.VehicleRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // Display all vehicles
    @GetMapping("/vehicles")
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", vehicleRepository.findAll());
        model.addAttribute("activePage", "vehicles");
        return "vehicles";
    }

    // Show add form
    @GetMapping("/vehicles/new")
    public String showAddForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("formTitle", "Add Vehicle");
        model.addAttribute("formAction", "/vehicles");
        return "vehicle-form";
    }

    // Add vehicle
    @PostMapping("/vehicles")
    public String addVehicle(
            @ModelAttribute Vehicle vehicle,
            RedirectAttributes redirectAttributes) {

        if (vehicle.getVehicleId() == null
                || vehicle.getVehicleNumber() == null
                || vehicle.getVehicleNumber().isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "error", "Vehicle ID and Vehicle Number are required.");

            return "redirect:/vehicles/new";
        }

        vehicle.setVehicleNumber(vehicle.getVehicleNumber().trim());
        normalizeOptionalFields(vehicle);

        if (vehicleRepository.existsById(vehicle.getVehicleId())) {
            redirectAttributes.addFlashAttribute(
                    "error", "Vehicle ID already exists.");

            return "redirect:/vehicles/new";
        }

        if (vehicleRepository.existsByVehicleNumber(
                vehicle.getVehicleNumber())) {

            redirectAttributes.addFlashAttribute(
                    "error", "This vehicle number is already registered.");

            return "redirect:/vehicles/new";
        }

        if (vehicle.getFasTagNumber() != null
                && vehicleRepository.existsByFasTagNumber(
                        vehicle.getFasTagNumber())) {

            redirectAttributes.addFlashAttribute(
                    "error", "This FASTag number is already registered.");

            return "redirect:/vehicles/new";
        }

        vehicleRepository.save(vehicle);

        redirectAttributes.addFlashAttribute(
                "success", "Vehicle added successfully.");

        return "redirect:/vehicles";
    }

    // Show edit form
    @GetMapping("/vehicles/edit/{id}")
    public String showEditForm(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Vehicle vehicle = vehicleRepository.findById(id).orElse(null);

        if (vehicle == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Vehicle not found.");

            return "redirect:/vehicles";
        }

        model.addAttribute("vehicle", vehicle);
        model.addAttribute("formTitle", "Edit Vehicle");
        model.addAttribute("formAction", "/vehicles/update/" + id);

        return "vehicle-form";
    }

    // Update vehicle
    @PostMapping("/vehicles/update/{id}")
    public String updateVehicle(
            @PathVariable Integer id,
            @ModelAttribute Vehicle formVehicle,
            RedirectAttributes redirectAttributes) {

        Vehicle vehicle = vehicleRepository.findById(id).orElse(null);

        if (vehicle == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Vehicle not found.");

            return "redirect:/vehicles";
        }

        if (formVehicle.getVehicleNumber() == null
                || formVehicle.getVehicleNumber().isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "error", "Vehicle Number is required.");

            return "redirect:/vehicles/edit/" + id;
        }

        formVehicle.setVehicleNumber(
                formVehicle.getVehicleNumber().trim());

        normalizeOptionalFields(formVehicle);

        if (vehicleRepository.existsByVehicleNumberAndVehicleIdNot(
                formVehicle.getVehicleNumber(), id)) {

            redirectAttributes.addFlashAttribute(
                    "error", "This vehicle number is already registered.");

            return "redirect:/vehicles/edit/" + id;
        }

        if (formVehicle.getFasTagNumber() != null
                && vehicleRepository.existsByFasTagNumberAndVehicleIdNot(
                        formVehicle.getFasTagNumber(), id)) {

            redirectAttributes.addFlashAttribute(
                    "error", "This FASTag number is already registered.");

            return "redirect:/vehicles/edit/" + id;
        }

        vehicle.setVehicleNumber(formVehicle.getVehicleNumber());
        vehicle.setFasTagNumber(formVehicle.getFasTagNumber());
        vehicle.setPassId(formVehicle.getPassId());
        vehicle.setPassType(formVehicle.getPassType());
        vehicle.setVehicleCategory(formVehicle.getVehicleCategory());

        vehicleRepository.save(vehicle);

        redirectAttributes.addFlashAttribute(
                "success", "Vehicle updated successfully.");

        return "redirect:/vehicles";
    }

    // Delete vehicle
    @PostMapping("/vehicles/delete/{id}")
    public String deleteVehicle(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        if (!vehicleRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Vehicle not found.");

            return "redirect:/vehicles";
        }

        try {
            vehicleRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "success", "Vehicle deleted successfully.");

        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Vehicle could not be deleted because it may have associated payments.");
        }

        return "redirect:/vehicles";
    }

    // Convert blank optional fields to null
    private void normalizeOptionalFields(Vehicle vehicle) {

        if (vehicle.getFasTagNumber() != null) {
            vehicle.setFasTagNumber(
                    vehicle.getFasTagNumber().trim());

            if (vehicle.getFasTagNumber().isEmpty()) {
                vehicle.setFasTagNumber(null);
            }
        }

        if (vehicle.getPassType() != null) {
            vehicle.setPassType(vehicle.getPassType().trim());

            if (vehicle.getPassType().isEmpty()) {
                vehicle.setPassType(null);
            }
        }

        if (vehicle.getVehicleCategory() != null) {
            vehicle.setVehicleCategory(
                    vehicle.getVehicleCategory().trim());

            if (vehicle.getVehicleCategory().isEmpty()) {
                vehicle.setVehicleCategory(null);
            }
        }
    }
}
