
package com.tollplaza;

import com.tollplaza.repository.TollPlazaRepository;
import com.tollplaza.repository.TollBoothRepository;
import com.tollplaza.repository.TollPlazaContactRepository;
import com.tollplaza.repository.PaymentRepository;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;


@Controller
public class HomeController {

    private final TollPlazaRepository tollPlazaRepository;
    private final TollBoothRepository tollBoothRepository;
    private final TollPlazaContactRepository tollPlazaContactRepository;
    private final PaymentRepository paymentRepository;

    public HomeController(
            TollPlazaRepository tollPlazaRepository,
            TollBoothRepository tollBoothRepository,
            TollPlazaContactRepository tollPlazaContactRepository,
            PaymentRepository paymentRepository) {

        this.tollPlazaRepository = tollPlazaRepository;
        this.tollBoothRepository = tollBoothRepository;
        this.tollPlazaContactRepository = tollPlazaContactRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute(
                "title",
                "Toll Plaza Management System"
        );

        model.addAttribute(
                "tollPlazas",
                tollPlazaRepository.findAll()
        );

        model.addAttribute(
                "plazaCount",
                tollPlazaRepository.count()
        );

        model.addAttribute(
                "boothCount",
                tollBoothRepository.count()
        );

        model.addAttribute(
                "contactCount",
                tollPlazaContactRepository.count()
        );

        // Payment statistics
        model.addAttribute("paymentCount", paymentRepository.count());

        model.addAttribute(
                "completedPaymentCount",
                paymentRepository.countByPaymentStatus("Completed"));

        model.addAttribute(
                "totalRevenue",
                paymentRepository.getTotalRevenueByStatus("Completed"));

        LocalDate today = LocalDate.now();

        model.addAttribute(
                "todayPaymentCount",
                paymentRepository.countByPaymentDateAndPaymentStatus(
                        today, "Completed"));

        model.addAttribute(
                "todayRevenue",
                paymentRepository.getRevenueByDateAndStatus(
                        today, "Completed"));

        // Booth status statistics
        model.addAttribute(
                "maintenanceBoothCount",
                tollBoothRepository.countByBoothStatus("Maintenance"));

        // Revenue grouped by payment mode
        List<Object[]> revenueRows =
                paymentRepository.getRevenueByPaymentMode("Completed");

        Map<String, BigDecimal> revenueByMode = new LinkedHashMap<>();

        for (Object[] row : revenueRows) {
                revenueByMode.put(
                        (String) row[0],
                        (BigDecimal) row[1]);
        }

        model.addAttribute("revenueByMode", revenueByMode);
        model.addAttribute("activePage", "dashboard");

        return "index";
    }
}
