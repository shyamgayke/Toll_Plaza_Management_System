package com.tollplaza;

import com.tollplaza.model.TollBooth;
import com.tollplaza.repository.PaymentRepository;
import com.tollplaza.repository.TollBoothRepository;
import com.tollplaza.repository.TollPlazaRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TollBoothController {

    private final TollBoothRepository tollBoothRepository;
    private final TollPlazaRepository tollPlazaRepository;
    private final PaymentRepository paymentRepository;

    public TollBoothController(
            TollBoothRepository tollBoothRepository,
            TollPlazaRepository tollPlazaRepository,
            PaymentRepository paymentRepository) {
        this.tollBoothRepository = tollBoothRepository;
        this.tollPlazaRepository = tollPlazaRepository;
        this.paymentRepository = paymentRepository;
    }

    // View all booths
        @GetMapping("/booths")
        public String listBooths(
                @RequestParam(required = false) Integer boothId,
                @RequestParam(required = false) String boothStatus,
                @RequestParam(required = false) Integer plazaId,
                Model model) {

        if (boothStatus != null && boothStatus.isBlank()) {
                boothStatus = null;
        }

        model.addAttribute(
                "booths",
                tollBoothRepository.searchBooths(
                        boothId, boothStatus, plazaId));

        model.addAttribute("plazas", tollPlazaRepository.findAll());
        model.addAttribute("filterBoothId", boothId);
        model.addAttribute("filterBoothStatus", boothStatus);
        model.addAttribute("filterPlazaId", plazaId);
        model.addAttribute("activePage", "booths");

        return "booths";
        }


    // Show Add Booth form
    @GetMapping("/booths/new")
    public String showAddForm(Model model) {
        model.addAttribute("booth", new TollBooth());
        model.addAttribute("plazas", tollPlazaRepository.findAll());
        model.addAttribute("formTitle", "Add Toll Booth");
        model.addAttribute("formAction", "/booths");
        return "booth-form";
    }

    // Save a new booth
    @PostMapping("/booths")
    public String addBooth(
            @ModelAttribute("booth") TollBooth booth,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (booth.getBoothId() == null) {
            return showFormError(model, booth, "Booth ID is required.",
                    "Add Toll Booth", "/booths");
        }

        if (tollBoothRepository.existsById(booth.getBoothId())) {
            return showFormError(model, booth, "This Booth ID already exists.",
                    "Add Toll Booth", "/booths");
        }

        if (!validStatus(booth.getBoothStatus())) {
            return showFormError(model, booth, "Select a valid booth status.",
                    "Add Toll Booth", "/booths");
        }

        if (booth.getPlazaId() == null
                || !tollPlazaRepository.existsById(booth.getPlazaId())) {
            return showFormError(model, booth, "Select a valid toll plaza.",
                    "Add Toll Booth", "/booths");
        }

        booth.setBoothStatus(booth.getBoothStatus().trim());
        tollBoothRepository.save(booth);

        redirectAttributes.addFlashAttribute(
                "success", "Toll booth added successfully.");

        return "redirect:/booths";
    }

    // Show Edit Booth form
    @GetMapping("/booths/edit/{id}")
    public String showEditForm(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        TollBooth booth = tollBoothRepository.findById(id).orElse(null);

        if (booth == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Toll booth not found.");
            return "redirect:/booths";
        }

        model.addAttribute("booth", booth);
        model.addAttribute("plazas", tollPlazaRepository.findAll());
        model.addAttribute("formTitle", "Edit Toll Booth");
        model.addAttribute("formAction", "/booths/update/" + id);

        return "booth-form";
    }

    // Update an existing booth
    @PostMapping("/booths/update/{id}")
    public String updateBooth(
            @PathVariable Integer id,
            @ModelAttribute("booth") TollBooth submittedBooth,
            Model model,
            RedirectAttributes redirectAttributes) {

        TollBooth existingBooth =
                tollBoothRepository.findById(id).orElse(null);

        if (existingBooth == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Toll booth not found.");
            return "redirect:/booths";
        }

        if (!validStatus(submittedBooth.getBoothStatus())) {
            return showFormError(model, submittedBooth,
                    "Select a valid booth status.",
                    "Edit Toll Booth", "/booths/update/" + id);
        }

        if (submittedBooth.getPlazaId() == null
                || !tollPlazaRepository.existsById(
                        submittedBooth.getPlazaId())) {
            return showFormError(model, submittedBooth,
                    "Select a valid toll plaza.",
                    "Edit Toll Booth", "/booths/update/" + id);
        }

        existingBooth.setBoothStatus(
                submittedBooth.getBoothStatus().trim());
        existingBooth.setPlazaId(submittedBooth.getPlazaId());

        tollBoothRepository.save(existingBooth);

        redirectAttributes.addFlashAttribute(
                "success", "Toll booth updated successfully.");

        return "redirect:/booths";
    }

    // Delete a booth
    @PostMapping("/booths/delete/{id}")
    public String deleteBooth(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        if (!tollBoothRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Toll booth not found.");
            return "redirect:/booths";
        }

        if (paymentRepository.existsByBoothId(id)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "This booth cannot be deleted because payment records "
                    + "are linked to it. You can change its status instead.");
            return "redirect:/booths";
        }

        tollBoothRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success", "Toll booth deleted successfully.");

        return "redirect:/booths";
    }

    private boolean validStatus(String status) {
        return status != null
                && (status.equals("Active")
                || status.equals("Maintenance")
                || status.equals("Inactive"));
    }

    private String showFormError(
            Model model,
            TollBooth booth,
            String error,
            String title,
            String action) {

        model.addAttribute("booth", booth);
        model.addAttribute("plazas", tollPlazaRepository.findAll());
        model.addAttribute("formTitle", title);
        model.addAttribute("formAction", action);
        model.addAttribute("error", error);

        return "booth-form";
    }
}