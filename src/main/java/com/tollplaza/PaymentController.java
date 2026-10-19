
package com.tollplaza;

import com.tollplaza.model.Payment;
import com.tollplaza.model.TollBooth;
import com.tollplaza.repository.PaymentRepository;
import com.tollplaza.repository.TollBoothRepository;
import com.tollplaza.repository.VehicleRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final VehicleRepository vehicleRepository;
    private final TollBoothRepository tollBoothRepository;

    public PaymentController(
            PaymentRepository paymentRepository,
            VehicleRepository vehicleRepository,
            TollBoothRepository tollBoothRepository) {

        this.paymentRepository = paymentRepository;
        this.vehicleRepository = vehicleRepository;
        this.tollBoothRepository = tollBoothRepository;
    }

    // LIST AND SEARCH PAYMENTS
    @GetMapping("/payments")
    public String listPayments(
            @RequestParam(required = false) Integer paymentId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate paymentDate,
            @RequestParam(required = false) String paymentMode,
            @RequestParam(required = false) String paymentStatus,
            Model model) {

        if (paymentMode != null && paymentMode.isBlank()) {
            paymentMode = null;
        }

        if (paymentStatus != null && paymentStatus.isBlank()) {
            paymentStatus = null;
        }

        List<Payment> payments;

        if (paymentDate != null && paymentDate.isAfter(LocalDate.now())) {
            payments = List.of();
            model.addAttribute(
                    "error", "Search date cannot be in the future.");
        } else {
            payments = paymentRepository.searchPayments(
                    paymentId, paymentDate, paymentMode, paymentStatus);
        }

        model.addAttribute("payments", payments);
        model.addAttribute("filterPaymentId", paymentId);
        model.addAttribute("filterPaymentDate", paymentDate);
        model.addAttribute("filterPaymentMode", paymentMode);
        model.addAttribute("filterPaymentStatus", paymentStatus);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("activePage", "payments");           

        return "payments";
    }

    // SHOW ADD PAYMENT FORM
    @GetMapping("/payments/new")
    public String showAddForm(Model model) {

        model.addAttribute("payment", new Payment());
        model.addAttribute("vehicles", vehicleRepository.findAll());

        model.addAttribute(
                "booths",
                tollBoothRepository.findAll().stream()
                        .filter(booth -> "Active".equalsIgnoreCase(
                                booth.getBoothStatus()))
                        .toList());

        model.addAttribute("formTitle", "Record Payment");
        model.addAttribute("formAction", "/payments");
        model.addAttribute("today", LocalDate.now());

        return "payment-form";
    }

    // ADD PAYMENT
    @PostMapping("/payments")
    public String addPayment(
            @ModelAttribute Payment payment,
            RedirectAttributes redirectAttributes) {

        if (payment.getPaymentId() == null
                || payment.getPaymentMode() == null
                || payment.getPaymentMode().isBlank()
                || payment.getTollAmount() == null
                || payment.getTollAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Enter a Payment ID, Payment Mode, and a positive Toll Amount.");

            return "redirect:/payments/new";
        }

        if (payment.getPaymentDate() != null
                && payment.getPaymentDate().isAfter(LocalDate.now())) {

            redirectAttributes.addFlashAttribute(
                    "error", "Payment date cannot be in the future.");

            return "redirect:/payments/new";
        }

        if (paymentRepository.existsById(payment.getPaymentId())) {
            redirectAttributes.addFlashAttribute(
                    "error", "Payment ID already exists.");

            return "redirect:/payments/new";
        }

        if (payment.getVehicleId() != null
                && !vehicleRepository.existsById(payment.getVehicleId())) {

            redirectAttributes.addFlashAttribute(
                    "error", "Selected vehicle does not exist.");

            return "redirect:/payments/new";
        }

        if (payment.getBoothId() != null) {
            TollBooth booth = tollBoothRepository
                    .findById(payment.getBoothId())
                    .orElse(null);

            if (booth == null) {
                redirectAttributes.addFlashAttribute(
                        "error", "Selected toll booth does not exist.");

                return "redirect:/payments/new";
            }

            if (!"Active".equalsIgnoreCase(booth.getBoothStatus())) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Payment cannot be recorded at a booth that is not Active.");

                return "redirect:/payments/new";
            }
        }

        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }

        if (payment.getPaymentStatus() == null
                || payment.getPaymentStatus().isBlank()) {
            payment.setPaymentStatus("Completed");
        }

        payment.setPaymentMode(payment.getPaymentMode().trim());
        payment.setPaymentStatus(payment.getPaymentStatus().trim());

        paymentRepository.save(payment);

        redirectAttributes.addFlashAttribute(
                "success", "Payment recorded successfully.");

        return "redirect:/payments";
    }

    // DELETE PAYMENT
    @PostMapping("/payments/delete/{id}")
    public String deletePayment(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        if (!paymentRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Payment not found.");

            return "redirect:/payments";
        }

        paymentRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success", "Payment deleted successfully.");

        return "redirect:/payments";
    }

    // SHOW EDIT PAYMENT FORM
    @GetMapping("/payments/edit/{id}")
    public String showEditForm(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Payment payment = paymentRepository.findById(id).orElse(null);

        if (payment == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Payment not found.");

            return "redirect:/payments";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("vehicles", vehicleRepository.findAll());

        model.addAttribute(
                "booths",
                tollBoothRepository.findAll().stream()
                        .filter(booth ->
                                "Active".equalsIgnoreCase(
                                        booth.getBoothStatus())
                                || (payment.getBoothId() != null
                                && booth.getBoothId().equals(
                                        payment.getBoothId())))
                        .toList());

        model.addAttribute("formTitle", "Edit Payment");
        model.addAttribute("formAction", "/payments/update/" + id);

        return "payment-form";
    }

    // UPDATE PAYMENT
    @PostMapping("/payments/update/{id}")
    public String updatePayment(
            @PathVariable Integer id,
            @ModelAttribute Payment submittedPayment,
            RedirectAttributes redirectAttributes) {

        Payment existingPayment =
                paymentRepository.findById(id).orElse(null);

        if (existingPayment == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Payment not found.");

            return "redirect:/payments";
        }

        if (submittedPayment.getPaymentDate() != null
                && submittedPayment.getPaymentDate()
                        .isAfter(LocalDate.now())) {

            redirectAttributes.addFlashAttribute(
                    "error", "Payment date cannot be in the future.");

            return "redirect:/payments/edit/" + id;
        }

        if (submittedPayment.getPaymentMode() == null
                || submittedPayment.getPaymentMode().isBlank()
                || submittedPayment.getTollAmount() == null
                || submittedPayment.getTollAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Enter a Payment Mode and a positive Toll Amount.");

            return "redirect:/payments/edit/" + id;
        }

        if (submittedPayment.getVehicleId() != null
                && !vehicleRepository.existsById(
                        submittedPayment.getVehicleId())) {

            redirectAttributes.addFlashAttribute(
                    "error", "Selected vehicle does not exist.");

            return "redirect:/payments/edit/" + id;
        }

        if (submittedPayment.getBoothId() != null) {
            TollBooth booth = tollBoothRepository
                    .findById(submittedPayment.getBoothId())
                    .orElse(null);

            if (booth == null
                    || (!"Active".equalsIgnoreCase(
                            booth.getBoothStatus())
                    && !submittedPayment.getBoothId().equals(
                            existingPayment.getBoothId()))) {

                redirectAttributes.addFlashAttribute(
                        "error", "Select an active toll booth.");

                return "redirect:/payments/edit/" + id;
            }
        }

        if (submittedPayment.getPaymentDate() != null) {
            existingPayment.setPaymentDate(
                    submittedPayment.getPaymentDate());
        }

        existingPayment.setPaymentMode(
                submittedPayment.getPaymentMode().trim());

        existingPayment.setPaymentStatus(
                submittedPayment.getPaymentStatus() == null
                        || submittedPayment.getPaymentStatus().isBlank()
                        ? "Completed"
                        : submittedPayment.getPaymentStatus().trim());

        existingPayment.setTollAmount(submittedPayment.getTollAmount());
        existingPayment.setVehicleId(submittedPayment.getVehicleId());
        existingPayment.setBoothId(submittedPayment.getBoothId());

        paymentRepository.save(existingPayment);

        redirectAttributes.addFlashAttribute(
                "success", "Payment updated successfully.");

        return "redirect:/payments";
    }
}
