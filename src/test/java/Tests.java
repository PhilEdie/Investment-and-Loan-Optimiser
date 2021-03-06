import Controller.AccountController;
import Controller.AccountForm;
import Controller.Utilities;
import Model.Account;
import Model.AccountsModel;
import Model.Investment;
import Model.Loan;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Tests {

    @Test
    void payOffLoanWithPayment() {
        Loan loan = new Loan("Loan 1", 1.05, -500, 100);
        double leftoverIncome = loan.makePayment(1000);
        assertEquals(500.0, leftoverIncome);
        assertEquals(loan.getPaymentForPeriod(), 500);
        assertTrue(loan.isPaidOff());

    }

    @Test
    void payOffLoanWithMinimumPayment() {
        Loan loan = new Loan("Loan 1", 1.05, -100, 200);
        double leftoverIncome = loan.makeMinimumPayment();
        assertEquals(100.0, leftoverIncome);
        assertEquals(loan.getPaymentForPeriod(), 100);
        assertTrue(loan.isPaidOff());
    }

    @Test
    void sortingAccounts1() {
        List<Account> toSort = new ArrayList<>();
        toSort.add(new Loan("Loan 1", 1.25, -300, 50));
        toSort.add(new Loan("Loan 2", 1.1, -1500, 100));
        toSort.add(new Investment("Investment 1", 1.08, 5000));
        toSort.add(new Investment("Investment 2", 1.04, 20000));

        List<Account> expected = new ArrayList<>(toSort);
        Collections.sort(toSort);
        assertEquals(expected, toSort);
    }

    @Test
    void sortingAccounts2() {
        List<Account> toSort = new ArrayList<>();
        toSort.add(new Loan("Loan 1", 1.25, -300, 50));
        toSort.add(new Investment("Investment 1", 1.25, 5000));

        List<Account> expected = new ArrayList<>(toSort);
        Collections.sort(toSort);
        assertEquals(expected, toSort);
    }

    @Test
    void sortingAccounts3() {
        List<Account> toSort = new ArrayList<>();
        toSort.add(new Loan("Loan 1", 1.26, -300, 50));
        toSort.add(new Loan("Loan 2", 1.25, -300, 50));
        toSort.add(new Investment("Investment 1", 1.25, 5000));

        List<Account> expected = new ArrayList<>(toSort);
        Collections.sort(toSort);
        assertEquals(expected, toSort);
    }

    @Test
    void sortingEqualInterestRateAccounts() {
        List<Account> toSort = new ArrayList<>();
        toSort.add(new Investment("Investment 2", 1.08, 5000));
        toSort.add(new Investment("Investment 1", 1.1, 5000));
        toSort.add(new Loan("Loan 1", 1.1, -10000, 100));
        toSort.add(new Loan("Loan 2", 1.08, -10000, 100));
        Collections.sort(toSort);

        List<Account> expected = new ArrayList<>();

        expected.add(new Loan("Loan 1", 1.1, -10000, 100));
        expected.add(new Investment("Investment 1", 1.1, 5000));
        expected.add(new Loan("Loan 2", 1.08, -10000, 100));
        expected.add(new Investment("Investment 2", 1.08, 5000));

        assertEquals(toSort.toString(), expected.toString());

    }

    @Test
    void accumulatingInterest() {
        AccountController p = new AccountController();
        AccountsModel m = p.getAccountsModel();
        p.getAccountsModel().addStartingAccount(new Loan("Loan 1", 1.05, -1000, 100));
        p.run(1, 100);
        assertEquals(-945, m.getHistory().peek().get(0).getBalance());
        assertEquals(-45, m.getHistory().peek().get(0).getInterestForPeriod());
    }

    @Test
    void distributeAcrossLoans1() {

        AccountController p = new AccountController();
        AccountsModel m = p.getAccountsModel();
        m.addStartingAccount(new Loan("Loan 1", 1.05, -200, 100));
        m.addStartingAccount(new Loan("Loan 2", 1.05, -200, 100));
        m.addStartingAccount(new Investment("Investment 1", 1.05, 0));
        p.run(1, 500);

        List<Account> topOfStack = m.getHistory().peek();

        assertEquals(
                "[Loan 1[interestRate=1.05, balance=$0.00, minimumPayment=$100.00], "
                        + "Loan 2[interestRate=1.05, balance=$0.00, minimumPayment=$100.00], "
                        + "Investment 1[interestRate=1.05, balance=$105.00]]",
                topOfStack.toString()
        );
        assertEquals("Loan 1, Loan 2", Utilities.getPaidOffLoanNames(topOfStack));
    }

    @Test
    void defaultNameRegex() {
        assertTrue(Utilities.isDefaultName("Loan1"));
        assertTrue(Utilities.isDefaultName("Investment1"));
        assertTrue(Utilities.isDefaultName("Loan10"));
        assertTrue(Utilities.isDefaultName("Investment10"));
        assertFalse(Utilities.isDefaultName("Loan"));
        assertFalse(Utilities.isDefaultName("Investment"));
        assertFalse(Utilities.isDefaultName("loan1"));
        assertFalse(Utilities.isDefaultName("investment1"));
        assertFalse(Utilities.isDefaultName("loan10"));
        assertFalse(Utilities.isDefaultName("investment10"));
    }

    @Test
    void testInvalidInputs1() {
        AccountForm f = new AccountForm();
        f.setBalance("1000000000000");
        f.setMinimumPayment("1000000000000");
        f.setInterestRate("1000");
        f.setIncome("1000000000000");
        f.setTotalPeriods("2000");
        assertEquals(0.0, f.getBalanceValue());
        assertEquals(0.0, f.getMinimumPaymentValue());
        assertEquals(1.0, f.getInterestRateValue());
        assertEquals(0.0, f.getIncomeValue());
        assertEquals(1, f.getTotalPeriods());
    }

    @Test
    void testInvalidInputs2Investment() {
        AccountForm f = new AccountForm();
        f.setBalance("-1");
        f.setMinimumPayment("-1");
        f.setInterestRate("-1");
        f.setIncome("-1");
        f.setTotalPeriods("-1");
        assertEquals(0.0, f.getBalanceValue());
        assertEquals(0.0, f.getMinimumPaymentValue());
        assertEquals(1.0, f.getInterestRateValue());
        assertEquals(0.0, f.getIncomeValue());
        assertEquals(1, f.getTotalPeriods());
    }

    @Test
    void testInvalidInputs2Loan() {
        AccountForm f = new AccountForm();
        f.setType(Loan.class);
        f.setBalance("-1");
        f.setMinimumPayment("-1");
        f.setInterestRate("-1");
        f.setIncome("-1");
        f.setTotalPeriods("-1");
        assertEquals(-1.0, f.getBalanceValue());
        assertEquals(0.0, f.getMinimumPaymentValue());
        assertEquals(1.0, f.getInterestRateValue());
        assertEquals(0.0, f.getIncomeValue());
        assertEquals(1, f.getTotalPeriods());
    }

    @Test
    void testInvalidInputs3() {
        AccountForm f = new AccountForm();
        f.setBalance("0");
        f.setMinimumPayment("0");
        f.setInterestRate("0");
        f.setIncome("0");
        f.setTotalPeriods("0");
        assertEquals(0.0, f.getBalanceValue());
        assertEquals(0.0, f.getMinimumPaymentValue());
        assertEquals(1.0, f.getInterestRateValue());
        assertEquals(0.0, f.getIncomeValue());
        assertEquals(1, f.getTotalPeriods());
    }

    @Test
    void testInvalidInputs4() {
        AccountForm f = new AccountForm();
        f.setBalance("number");
        f.setMinimumPayment("number");
        f.setInterestRate("number");
        f.setIncome("number");
        f.setTotalPeriods("number");
        assertEquals(0.0, f.getBalanceValue());
        assertEquals(0.0, f.getMinimumPaymentValue());
        assertEquals(1.0, f.getInterestRateValue());
        assertEquals(0.0, f.getIncomeValue());
        assertEquals(1, f.getTotalPeriods());
    }

    @Test
    void testInvalidInputs5() {
        AccountForm f = new AccountForm();
        f.setBalance("1.5");
        f.setMinimumPayment("1.5");
        f.setInterestRate("1.5");
        f.setIncome("1.5");
        f.setTotalPeriods("1.5");
        assertEquals(1.5, f.getBalanceValue());
        assertEquals(1.5, f.getMinimumPaymentValue());
        assertEquals(1.015, f.getInterestRateValue());
        assertEquals(1.5, f.getIncomeValue());
        assertEquals(1, f.getTotalPeriods());
    }

    @Test
    void testInvalidInputs6() {
        AccountForm f = new AccountForm();
        f.setType(Loan.class);  // Should be made a negative number.
        f.setBalance("1.5");
        f.setMinimumPayment("1.5");
        f.setInterestRate("1.5");
        f.setIncome("1.5");
        f.setTotalPeriods("1.5");
        assertEquals(-1.5, f.getBalanceValue());
        assertEquals(1.5, f.getMinimumPaymentValue());
        assertEquals(1.015, f.getInterestRateValue());
        assertEquals(1.5, f.getIncomeValue());
        assertEquals(1, f.getTotalPeriods());
    }

    @Test
    void allLoansPaidOffEarly() {
        AccountController p = new AccountController();
        AccountsModel m = p.getAccountsModel();
        m.addStartingAccount(new Loan("Loan 1", 1.05, -500, 100));
        m.addStartingAccount(new Loan("Loan 2", 1.05, -500, 100));

        // Testing to ensure it doesn't crash when there are no accounts left to pay into.
        p.run(10, 500);

        /* History should only contain two lists, the starting loans,
        and the loans after being paid off.*/

        assertEquals(4, m.getHistory().size());
    }

    @Test
    void validatingNumbers() {
        assertTrue(Utilities.validateNumber("0", 1));
        assertFalse(Utilities.validateNumber("-100", 10));
        assertFalse(Utilities.validateNumber("100000000000000000000", 10));
        assertFalse(Utilities.validateNumber("ten", 10));
    }

    @Test
    void stringFormatting() {
        assertEquals("$1,234.57", Utilities.convertToDollarFormat(1234.5678));
        assertEquals("12.35%", Utilities.convertToPercentageFormat(12.345678));
    }

    @Test
    void ensureAccountBalanceInterestRateAlwaysPositive() {
        try {
            new Investment("i", -1, 1);
            fail();
        } catch (IllegalArgumentException ignored) {

        }
    }

    @Test
    void ensureInvestmentBalanceAlwaysPositive1() {
        try {
            new Investment("i", 1, -1);
            fail();
        } catch (IllegalArgumentException ignored) {

        }
    }

    @Test
    void ensureInvestmentBalanceAlwaysPositive2() {
        try {
            Investment inv = new Investment("i", 1, 1);
            inv.setBalance(-100);
            fail();
        } catch (IllegalArgumentException ignored) {

        }
    }

    @Test
    void ensureLoanBalanceAlwaysNegative1() {
        try {
            new Loan("i", 1, 1, 1);
            fail();
        } catch (IllegalArgumentException ignored) {

        }
    }

    @Test
    void ensureLoanBalanceAlwaysNegative2() {
        try {
            Loan loan = new Loan("i", 1, -1, 1);
            loan.setBalance(100);
            fail();
        } catch (IllegalArgumentException ignored) {

        }
    }
}
