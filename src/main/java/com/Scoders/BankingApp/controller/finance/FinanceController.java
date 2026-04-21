package com.Scoders.BankingApp.controller.finance;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.TradeDatabase;
import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.MarketData;
import com.Scoders.BankingApp.model.Trade;
import com.Scoders.BankingApp.model.User;
import com.Scoders.BankingApp.service.FinanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @GetMapping("/finance")
    public String finance(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        
        if (user == null) {
            model.addAttribute("errorMessage", "请先登录后再访问理财模块");
            return "login";
        }

        List<MarketData> stocks = financeService.getStockData();
        List<MarketData> goldData = financeService.getGoldData();
        List<MarketData> futures = financeService.getFutureData();

        model.addAttribute("stocks", stocks);
        model.addAttribute("goldData", goldData);
        model.addAttribute("futures", futures);
        model.addAttribute("lastUpdateTime", financeService.getLastUpdateTime());
        model.addAttribute("isUsingMockData", financeService.isUsingMockData());
        model.addAttribute("user", user);

        Account stockAccount = AccountDatabase.getStockAccountByUser(user);
        model.addAttribute("stockAccount", stockAccount);
        model.addAttribute("hasStockAccount", stockAccount != null);

        if (stockAccount != null) {
            List<Trade> trades = TradeDatabase.getTradesByUser(user);
            model.addAttribute("trades", trades);
            model.addAttribute("hasTrades", trades != null && !trades.isEmpty());
        }

        List<Account> otherAccounts = AccountDatabase.getNonStockAccountsByUser(user);
        model.addAttribute("otherAccounts", otherAccounts);
        model.addAttribute("hasOtherAccounts", otherAccounts != null && !otherAccounts.isEmpty());

        return "finance";
    }

    @PostMapping("/finance/create-stock-account")
    public String createStockAccount(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        
        if (user == null) {
            model.addAttribute("errorMessage", "请先登录后再访问理财模块");
            return "login";
        }

        Account existingAccount = AccountDatabase.getStockAccountByUser(user);
        if (existingAccount != null) {
            model.addAttribute("message", "您已经拥有股票账户");
            return "redirect:/finance";
        }

        AccountDatabase.insertAccount(user.getId(), 0.00, AccountDatabase.TYPE_STOCK);
        model.addAttribute("successMessage", "股票账户创建成功！");
        
        return "redirect:/finance";
    }

    @PostMapping("/finance/transfer-to-stock")
    public String transferToStock(
            HttpSession session,
            @RequestParam("fromAccount") long fromAccount,
            @RequestParam("amount") double amount,
            Model model) {
        User user = (User) session.getAttribute("currentUser");
        
        if (user == null) {
            model.addAttribute("errorMessage", "请先登录后再访问理财模块");
            return "login";
        }

        Account stockAccount = AccountDatabase.getStockAccountByUser(user);
        if (stockAccount == null) {
            model.addAttribute("errorMessage", "请先创建股票账户");
            return "redirect:/finance";
        }

        Account fromAcc = AccountDatabase.getAccountByAccNo(fromAccount);
        if (fromAcc == null) {
            model.addAttribute("errorMessage", "转出账户不存在");
            return "redirect:/finance";
        }

        if (fromAcc.getBalance() < amount) {
            model.addAttribute("errorMessage", "余额不足");
            return "redirect:/finance";
        }

        if (amount < 1) {
            model.addAttribute("errorMessage", "转账金额至少为1");
            return "redirect:/finance";
        }

        AccountDatabase.updateBalance(fromAccount, fromAcc.getBalance() - amount);
        TransactionDatabase.insertTransaction(fromAccount, amount, "Transfer-send");

        AccountDatabase.updateBalance(stockAccount.getAccNo(), stockAccount.getBalance() + amount);
        TransactionDatabase.insertTransaction(stockAccount.getAccNo(), amount, "Transfer-receive(Stock)");

        model.addAttribute("successMessage", "转账成功！已将 $" + amount + " 转入股票账户");
        
        return "redirect:/finance";
    }

    @PostMapping("/finance/trade")
    public String executeTrade(
            HttpSession session,
            @RequestParam("symbol") String symbol,
            @RequestParam("assetName") String assetName,
            @RequestParam("assetType") String assetType,
            @RequestParam("tradeType") String tradeType,
            @RequestParam("quantity") double quantity,
            @RequestParam("price") double price,
            Model model) {
        User user = (User) session.getAttribute("currentUser");
        
        if (user == null) {
            model.addAttribute("errorMessage", "请先登录后再访问理财模块");
            return "login";
        }

        Account stockAccount = AccountDatabase.getStockAccountByUser(user);
        if (stockAccount == null) {
            model.addAttribute("errorMessage", "请先创建股票账户");
            return "redirect:/finance";
        }

        if (quantity <= 0) {
            model.addAttribute("errorMessage", "交易数量必须大于0");
            return "redirect:/finance";
        }

        double totalAmount = price * quantity;
        String transactionType;

        if ("BUY".equals(tradeType)) {
            if (stockAccount.getBalance() < totalAmount) {
                model.addAttribute("errorMessage", "股票账户余额不足，无法购买");
                return "redirect:/finance";
            }
            AccountDatabase.updateBalance(stockAccount.getAccNo(), stockAccount.getBalance() - totalAmount);
            transactionType = "Trade-Buy";
        } else if ("SELL".equals(tradeType)) {
            AccountDatabase.updateBalance(stockAccount.getAccNo(), stockAccount.getBalance() + totalAmount);
            transactionType = "Trade-Sell";
        } else {
            model.addAttribute("errorMessage", "无效的交易类型");
            return "redirect:/finance";
        }

        Trade trade = new Trade(
                stockAccount,
                symbol,
                assetName,
                assetType,
                tradeType,
                quantity,
                price,
                totalAmount
        );
        TradeDatabase.insertTrade(trade);

        TransactionDatabase.insertTransaction(stockAccount.getAccNo(), totalAmount, transactionType);

        String action = "BUY".equals(tradeType) ? "买入" : "卖出";
        model.addAttribute("successMessage", 
            action + "成功！" + action + " " + quantity + " 股 " + assetName + 
            "，总金额: $" + String.format("%.2f", totalAmount));
        
        return "redirect:/finance";
    }

    @Scheduled(fixedRate = 3600000)
    public void scheduledRefresh() {
        financeService.refreshMarketData();
    }
}
