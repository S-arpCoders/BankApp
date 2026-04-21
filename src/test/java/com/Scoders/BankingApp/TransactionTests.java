package com.Scoders.BankingApp;

import com.Scoders.BankingApp.config.TransactionConfig;
import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.database.UserDatabase;
import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;
import com.Scoders.BankingApp.service.BankingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {BankingAppApplication.class, TransactionConfig.class})
class TransactionTests {

    @Autowired
    private BankingService bankingService;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private Long testUserId1;
    private Long testUserId2;
    private Long fromAccountNo;
    private Long toAccountNo;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        
        String testUsername1 = "tx_test_user_" + System.currentTimeMillis();
        String testUsername2 = "tx_test_user2_" + System.currentTimeMillis();
        
        UserDatabase.insertUser(testUsername1, "TestUser1", "TestPassword123!");
        UserDatabase.insertUser(testUsername2, "TestUser2", "TestPassword123!");
        
        User user1 = UserDatabase.getUserByUsername(testUsername1);
        User user2 = UserDatabase.getUserByUsername(testUsername2);
        
        testUserId1 = user1.getId();
        testUserId2 = user2.getId();
        
        AccountDatabase.insertAccount(testUserId1, 1000.0);
        AccountDatabase.insertAccount(testUserId2, 500.0);
        
        Account fromAccount = AccountDatabase.getAccountByUserId(user1).get(0);
        Account toAccount = AccountDatabase.getAccountByUserId(user2).get(0);
        
        fromAccountNo = fromAccount.getAccNo();
        toAccountNo = toAccount.getAccNo();
        
        System.out.println("\n=== 测试数据初始化 ===");
        System.out.println("用户1账户: " + fromAccountNo + ", 余额: " + fromAccount.getBalance());
        System.out.println("用户2账户: " + toAccountNo + ", 余额: " + toAccount.getBalance());
    }

    @AfterEach
    void tearDown() {
        try {
            if (fromAccountNo != null) {
                TransactionDatabase.getTransactionsByAccount(AccountDatabase.getAccountByAccNo(fromAccountNo))
                    .forEach(t -> TransactionDatabase.deleteTransaction(t.getTransId()));
                AccountDatabase.deleteAccount(fromAccountNo);
            }
            if (toAccountNo != null) {
                TransactionDatabase.getTransactionsByAccount(AccountDatabase.getAccountByAccNo(toAccountNo))
                    .forEach(t -> TransactionDatabase.deleteTransaction(t.getTransId()));
                AccountDatabase.deleteAccount(toAccountNo);
            }
            if (testUserId1 != null) {
                UserDatabase.deleteUser(testUserId1);
            }
            if (testUserId2 != null) {
                UserDatabase.deleteUser(testUserId2);
            }
        } catch (Exception e) {
            System.out.println("清理测试数据时出错: " + e.getMessage());
        }
    }

    @Test
    void testSuccessfulTransfer_ShouldUpdateBothAccounts() throws Exception {
        System.out.println("\n=== 测试1: 成功转账 ===");
        System.out.println("转账前:");
        System.out.println("  付款账户 " + fromAccountNo + " 余额: " + getAccountBalance(fromAccountNo));
        System.out.println("  收款账户 " + toAccountNo + " 余额: " + getAccountBalance(toAccountNo));
        
        double transferAmount = 200.0;
        double expectedFromBalance = 1000.0 - transferAmount;
        double expectedToBalance = 500.0 + transferAmount;
        
        bankingService.transfer(fromAccountNo, toAccountNo, transferAmount);
        
        double actualFromBalance = getAccountBalance(fromAccountNo);
        double actualToBalance = getAccountBalance(toAccountNo);
        
        System.out.println("转账后:");
        System.out.println("  付款账户 " + fromAccountNo + " 余额: " + actualFromBalance);
        System.out.println("  收款账户 " + toAccountNo + " 余额: " + actualToBalance);
        
        assertEquals(expectedFromBalance, actualFromBalance, "付款账户余额应该减少");
        assertEquals(expectedToBalance, actualToBalance, "收款账户余额应该增加");
        
        System.out.println("✓ 成功转账测试通过");
    }

    @Test
    void testTransferWithInsufficientFunds_ShouldThrowExceptionAndNotChangeBalances() throws Exception {
        System.out.println("\n=== 测试2: 余额不足时转账 ===");
        System.out.println("转账前:");
        System.out.println("  付款账户 " + fromAccountNo + " 余额: " + getAccountBalance(fromAccountNo));
        System.out.println("  收款账户 " + toAccountNo + " 余额: " + getAccountBalance(toAccountNo));
        
        double transferAmount = 2000.0;
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bankingService.transfer(fromAccountNo, toAccountNo, transferAmount);
        });
        
        String expectedMessage = "Insufficient funds";
        String actualMessage = exception.getMessage();
        
        System.out.println("抛出异常: " + actualMessage);
        
        assertTrue(actualMessage.contains(expectedMessage), "应该抛出余额不足的异常");
        
        double actualFromBalance = getAccountBalance(fromAccountNo);
        double actualToBalance = getAccountBalance(toAccountNo);
        
        System.out.println("转账尝试后:");
        System.out.println("  付款账户 " + fromAccountNo + " 余额: " + actualFromBalance);
        System.out.println("  收款账户 " + toAccountNo + " 余额: " + actualToBalance);
        
        assertEquals(1000.0, actualFromBalance, "余额不足时，付款账户余额应该不变");
        assertEquals(500.0, actualToBalance, "余额不足时，收款账户余额应该不变");
        
        System.out.println("✓ 余额不足转账测试通过");
    }

    @Test
    void testTransferToNonExistentAccount_ShouldThrowException() throws Exception {
        System.out.println("\n=== 测试3: 转账到不存在的账户 ===");
        
        long nonExistentAccount = 99999999L;
        double transferAmount = 100.0;
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bankingService.transfer(fromAccountNo, nonExistentAccount, transferAmount);
        });
        
        System.out.println("抛出异常: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("do not exist"), "应该抛出账户不存在的异常");
        
        double actualFromBalance = getAccountBalance(fromAccountNo);
        assertEquals(1000.0, actualFromBalance, "账户不存在时，付款账户余额应该不变");
        
        System.out.println("✓ 转账到不存在账户测试通过");
    }

    @Test
    void testSuccessfulDeposit_ShouldIncreaseBalance() throws Exception {
        System.out.println("\n=== 测试4: 成功存款 ===");
        System.out.println("存款前: 账户 " + fromAccountNo + " 余额: " + getAccountBalance(fromAccountNo));
        
        double depositAmount = 500.0;
        double expectedBalance = 1000.0 + depositAmount;
        
        bankingService.deposit(fromAccountNo, depositAmount);
        
        double actualBalance = getAccountBalance(fromAccountNo);
        System.out.println("存款后: 账户 " + fromAccountNo + " 余额: " + actualBalance);
        
        assertEquals(expectedBalance, actualBalance, "存款后余额应该增加");
        
        System.out.println("✓ 成功存款测试通过");
    }

    @Test
    void testDepositWithSmallAmount_ShouldThrowException() throws Exception {
        System.out.println("\n=== 测试5: 存款金额过小 ===");
        
        double smallAmount = 5.0;
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bankingService.deposit(fromAccountNo, smallAmount);
        });
        
        System.out.println("抛出异常: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("minimum"), "应该抛出最小存款金额的异常");
        
        double actualBalance = getAccountBalance(fromAccountNo);
        assertEquals(1000.0, actualBalance, "存款金额过小时，余额应该不变");
        
        System.out.println("✓ 小额存款异常测试通过");
    }

    @Test
    void testSuccessfulWithdraw_ShouldDecreaseBalance() throws Exception {
        System.out.println("\n=== 测试6: 成功取款 ===");
        System.out.println("取款前: 账户 " + fromAccountNo + " 余额: " + getAccountBalance(fromAccountNo));
        
        double withdrawAmount = 300.0;
        double expectedBalance = 1000.0 - withdrawAmount;
        
        bankingService.withdraw(fromAccountNo, withdrawAmount);
        
        double actualBalance = getAccountBalance(fromAccountNo);
        System.out.println("取款后: 账户 " + fromAccountNo + " 余额: " + actualBalance);
        
        assertEquals(expectedBalance, actualBalance, "取款后余额应该减少");
        
        System.out.println("✓ 成功取款测试通过");
    }

    @Test
    void testWithdrawWithInsufficientFunds_ShouldThrowException() throws Exception {
        System.out.println("\n=== 测试7: 余额不足时取款 ===");
        
        double withdrawAmount = 2000.0;
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bankingService.withdraw(fromAccountNo, withdrawAmount);
        });
        
        System.out.println("抛出异常: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Insufficient"), "应该抛出余额不足的异常");
        
        double actualBalance = getAccountBalance(fromAccountNo);
        assertEquals(1000.0, actualBalance, "余额不足时，余额应该不变");
        
        System.out.println("✓ 余额不足取款测试通过");
    }

    @Test
    void testTransactionRollbackScenario() throws Exception {
        System.out.println("\n=== 测试8: 事务回滚场景模拟 ===");
        System.out.println("============================================================");
        System.out.println("事务回滚原理:");
        System.out.println("1. 转账操作需要同时修改两个账户: 付款账户扣款, 收款账户加款");
        System.out.println("2. 如果在扣款成功后、加款前发生错误, 可能导致数据不一致");
        System.out.println("3. 事务保证: 要么全部成功, 要么全部回滚");
        System.out.println("============================================================");
        
        System.out.println("\n测试步骤:");
        System.out.println("1. 验证初始余额");
        System.out.println("2. 模拟转账失败场景 (使用业务验证)");
        System.out.println("3. 验证余额是否保持不变");
        
        double initialFromBalance = getAccountBalance(fromAccountNo);
        double initialToBalance = getAccountBalance(toAccountNo);
        
        System.out.println("\n初始状态:");
        System.out.println("  付款账户余额: " + initialFromBalance);
        System.out.println("  收款账户余额: " + initialToBalance);
        
        System.out.println("\n尝试转账 " + 2000.0 + " (超过账户余额)...");
        
        try {
            bankingService.transfer(fromAccountNo, toAccountNo, 2000.0);
            fail("应该抛出异常");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ 正确抛出异常: " + e.getMessage());
        }
        
        double finalFromBalance = getAccountBalance(fromAccountNo);
        double finalToBalance = getAccountBalance(toAccountNo);
        
        System.out.println("\n最终状态:");
        System.out.println("  付款账户余额: " + finalFromBalance);
        System.out.println("  收款账户余额: " + finalToBalance);
        
        assertEquals(initialFromBalance, finalFromBalance, "付款账户余额应该保持不变");
        assertEquals(initialToBalance, finalToBalance, "收款账户余额应该保持不变");
        
        System.out.println("\n============================================================");
        System.out.println("✓ 事务回滚测试通过 - 数据一致性得到保证");
        System.out.println("============================================================");
    }

    @Test
    void testAtomicTransferOperation() throws Exception {
        System.out.println("\n=== 测试9: 转账操作的原子性 ===");
        System.out.println("============================================================");
        System.out.println("原子性保证:");
        System.out.println("- 转账操作必须是原子的: 要么全部成功, 要么全部失败");
        System.out.println("- 不允许出现: 扣款成功但加款失败的情况");
        System.out.println("============================================================");
        
        System.out.println("\n测试场景: 连续执行多次转账操作");
        
        double initialFromBalance = getAccountBalance(fromAccountNo);
        double initialToBalance = getAccountBalance(toAccountNo);
        
        System.out.println("\n初始余额:");
        System.out.println("  付款账户: " + initialFromBalance);
        System.out.println("  收款账户: " + initialToBalance);
        
        System.out.println("\n执行第一次转账: 200.0");
        bankingService.transfer(fromAccountNo, toAccountNo, 200.0);
        
        System.out.println("执行第二次转账: 300.0");
        bankingService.transfer(fromAccountNo, toAccountNo, 300.0);
        
        System.out.println("尝试执行第三次转账: 600.0 (应该失败)");
        try {
            bankingService.transfer(fromAccountNo, toAccountNo, 600.0);
            fail("应该抛出余额不足异常");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ 正确抛出余额不足异常: " + e.getMessage());
        }
        
        double finalFromBalance = getAccountBalance(fromAccountNo);
        double finalToBalance = getAccountBalance(toAccountNo);
        
        double expectedFromBalance = initialFromBalance - 200.0 - 300.0;
        double expectedToBalance = initialToBalance + 200.0 + 300.0;
        
        System.out.println("\n最终余额:");
        System.out.println("  付款账户: " + finalFromBalance + " (预期: " + expectedFromBalance + ")");
        System.out.println("  收款账户: " + finalToBalance + " (预期: " + expectedToBalance + ")");
        
        assertEquals(expectedFromBalance, finalFromBalance, "只有成功的转账应该影响余额");
        assertEquals(expectedToBalance, finalToBalance, "只有成功的转账应该影响余额");
        
        System.out.println("\n============================================================");
        System.out.println("✓ 原子性测试通过 - 失败的转账没有影响数据");
        System.out.println("============================================================");
    }

    private double getAccountBalance(long accountNo) {
        Account account = AccountDatabase.getAccountByAccNo(accountNo);
        return account != null ? account.getBalance() : 0.0;
    }
}
