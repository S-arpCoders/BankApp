package com.Scoders.BankingApp.controller.ViewBalance;

import com.Scoders.BankingApp.database.AccountDatabase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewBalContr {

    AccountDatabase database = new AccountDatabase();
    @GetMapping("/ViewBal")
    public String index(Model model) {
        model.addAttribute("Ngwane", "Money site");
        return "ViewBal";
    }

    @PostMapping("/ViewBal")
    public String viewBalance(Model model, @RequestParam("test") String data
    ) {


        return data;
    }
}
