
package com.tollplaza;

import com.tollplaza.repository.PaymentRepository;
import com.tollplaza.repository.TollBoothRepository;
import com.tollplaza.repository.TollPlazaRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportsController {

    private final PaymentRepository paymentRepository;
    private final TollPlazaRepository tollPlazaRepository;
    private final TollBoothRepository tollBoothRepository;

    public ReportsController(
            PaymentRepository paymentRepository,
            TollPlazaRepository tollPlazaRepository,
            TollBoothRepository tollBoothRepository) {

        this.paymentRepository = paymentRepository;
        this.tollPlazaRepository = tollPlazaRepository;
        this.tollBoothRepository = tollBoothRepository;
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("activePage", "reports");

        model.addAttribute("plazaCount", tollPlazaRepository.count());
        model.addAttribute("boothCount", tollBoothRepository.count());
        model.addAttribute("paymentCount", paymentRepository.count());

        model.addAttribute(
                "completedPaymentCount",
                paymentRepository.countByPaymentStatus("Completed"));

        model.addAttribute(
                "totalRevenue",
                paymentRepository.getTotalRevenueByStatus("Completed"));

        model.addAttribute(
                "revenueByMode",
                paymentRepository.getRevenueByPaymentMode("Completed"));

        model.addAttribute(
                "dailyRevenue",
                paymentRepository.getDailyRevenueByStatus("Completed"));

        model.addAttribute(
                "revenueByPlaza",
                paymentRepository.getRevenueByPlazaAndStatus("Completed"));

        return "reports";
    }
}
