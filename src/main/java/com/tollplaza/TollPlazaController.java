
package com.tollplaza;

import com.tollplaza.model.TollPlaza;
import com.tollplaza.repository.TollPlazaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TollPlazaController {

    private final TollPlazaRepository tollPlazaRepository;

    public TollPlazaController(TollPlazaRepository tollPlazaRepository) {
        this.tollPlazaRepository = tollPlazaRepository;
    }

    
    @GetMapping("/plazas")
    public String listPlazas(Model model) {
        model.addAttribute("tollPlazas", tollPlazaRepository.findAll());
        model.addAttribute("activePage", "plazas");
        return "plazas";
    }

    @GetMapping("/plazas/new")
    public String showAddForm(Model model) {
        model.addAttribute("tollPlaza", new TollPlaza());
        return "add-plaza";
    }


    @PostMapping("/plazas")
    public String addPlaza(
            @ModelAttribute("tollPlaza") TollPlaza tollPlaza,
            RedirectAttributes redirectAttributes) {

        if (tollPlaza.getPlazaId() == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Plaza ID is required.");
            return "redirect:/plazas/new";
        }

        if (tollPlazaRepository.existsById(tollPlaza.getPlazaId())) {
            redirectAttributes.addFlashAttribute(
                    "error", "A toll plaza with this ID already exists.");
            return "redirect:/plazas/new";
        }

        if (tollPlaza.getPlazaStatus() == null ||
                tollPlaza.getPlazaStatus().isBlank()) {
            tollPlaza.setPlazaStatus("Operational");
        }

        tollPlazaRepository.save(tollPlaza);

        redirectAttributes.addFlashAttribute(
                "success", "Toll plaza added successfully!");

        return "redirect:/";
    }

    @GetMapping("/plazas/edit/{id}")
    public String showEditForm(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        TollPlaza tollPlaza = tollPlazaRepository.findById(id).orElse(null);

        if (tollPlaza == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Toll plaza not found.");
            return "redirect:/";
        }

        model.addAttribute("tollPlaza", tollPlaza);
        return "edit-plaza";
    }

    @PostMapping("/plazas/update/{id}")
    public String updatePlaza(
            @PathVariable Integer id,
            @ModelAttribute("tollPlaza") TollPlaza formPlaza,
            RedirectAttributes redirectAttributes) {

        TollPlaza existingPlaza =
                tollPlazaRepository.findById(id).orElse(null);

        if (existingPlaza == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Toll plaza not found.");
            return "redirect:/";
        }

        existingPlaza.setPlazaName(formPlaza.getPlazaName());
        existingPlaza.setHighwayName(formPlaza.getHighwayName());
        existingPlaza.setCity(formPlaza.getCity());
        existingPlaza.setState(formPlaza.getState());
        existingPlaza.setPlazaStatus(formPlaza.getPlazaStatus());

        tollPlazaRepository.save(existingPlaza);

        redirectAttributes.addFlashAttribute(
                "success", "Toll plaza updated successfully!");

        return "redirect:/";
    }

    
    @PostMapping("/plazas/delete/{id}")
    public String deletePlaza(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        TollPlaza tollPlaza = tollPlazaRepository.findById(id).orElse(null);

        if (tollPlaza == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Toll plaza not found.");
            return "redirect:/";
        }

        if ((tollPlaza.getBooths() != null && !tollPlaza.getBooths().isEmpty())
                || (tollPlaza.getContacts() != null
                && !tollPlaza.getContacts().isEmpty())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete this plaza because it has associated booths or contacts.");

            return "redirect:/";
        }

        tollPlazaRepository.delete(tollPlaza);

        redirectAttributes.addFlashAttribute(
                "success", "Toll plaza deleted successfully!");

        return "redirect:/";
    }

    @GetMapping("/plazas/details/{id}")
    public String viewDetails(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        TollPlaza tollPlaza = tollPlazaRepository.findById(id).orElse(null);

        if (tollPlaza == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Toll plaza not found.");
            return "redirect:/";
        }

        model.addAttribute("tollPlaza", tollPlaza);
        return "plaza-details";
    }

}
