package com.Scoders.BankingApp.controller.finance;

import com.Scoders.BankingApp.model.MarketData;
import com.Scoders.BankingApp.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @GetMapping("/finance")
    public String finance(Model model) {
        List<MarketData> stocks = financeService.getStockData();
        List<MarketData> goldData = financeService.getGoldData();
        List<MarketData> futures = financeService.getFutureData();

        model.addAttribute("stocks", stocks);
        model.addAttribute("goldData", goldData);
        model.addAttribute("futures", futures);
        model.addAttribute("lastUpdateTime", financeService.getLastUpdateTime());
        model.addAttribute("isUsingMockData", financeService.isUsingMockData());

        return "finance";
    }

    @Scheduled(fixedRate = 3600000)
    public void scheduledRefresh() {
        financeService.refreshMarketData();
    }
}
