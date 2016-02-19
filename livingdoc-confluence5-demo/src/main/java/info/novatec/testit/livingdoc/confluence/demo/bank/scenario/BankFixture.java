package info.novatec.testit.livingdoc.confluence.demo.bank.scenario;

import java.util.Collection;

import info.novatec.testit.livingdoc.TypeConversion;
import info.novatec.testit.livingdoc.confluence.demo.bank.Bank;
import info.novatec.testit.livingdoc.confluence.demo.bank.BankAccount;
import info.novatec.testit.livingdoc.confluence.demo.bank.Money;
import info.novatec.testit.livingdoc.confluence.demo.bank.NoSuchAccountException;
import info.novatec.testit.livingdoc.confluence.demo.bank.Owner;
import info.novatec.testit.livingdoc.confluence.demo.bank.OwnerTypeConverter;
import info.novatec.testit.livingdoc.confluence.demo.bank.WithdrawType;
import info.novatec.testit.livingdoc.interpreter.flow.scenario.Check;
import info.novatec.testit.livingdoc.interpreter.flow.scenario.Display;
import info.novatec.testit.livingdoc.interpreter.flow.scenario.Expectation;
import info.novatec.testit.livingdoc.interpreter.flow.scenario.Given;
import info.novatec.testit.livingdoc.interpreter.flow.scenario.Then;
import info.novatec.testit.livingdoc.interpreter.flow.scenario.When;


public class BankFixture {
    private final Bank bank;

    static {
        if ( ! TypeConversion.supports(Owner.class)) {
            TypeConversion.register(new OwnerTypeConverter());
        }
    }

    public BankFixture() {
        this.bank = new Bank();
    }

    @Given("I have a (\\w+) account (\\d{5}\\-\\d{5}) under the name of ([\\w|\\s]*)")
    public void openAccount(String type, String number, Owner owner) {
        if ("checking".equals(type)) {
            bank.openCheckingAccount(number, owner);
        } else if ("savings".equals(type)) {
            bank.openSavingsAccount(number, owner);
        }
    }

    @Given("I have a (\\w+) account (\\d{5}\\-\\d{5}) for ([\\w|\\s]*) with balance of (\\$\\d+\\.\\d\\d)")
    public void openAccountWithBalance(String type, String number, Owner owner, Money balance) {
        if ("checking".equals(type)) {
            bank.openCheckingAccount(number, owner).deposit(balance);
        } else if ("savings".equals(type)) {
            bank.openSavingsAccount(number, owner).deposit(balance);
        }
    }

    @Then("The balance of account (\\d{5}\\-\\d{5}) is (\\$\\d+\\.\\d\\d)")
    public void theBalanceOfAccount(String number, Expectation expectedBalance) throws NoSuchAccountException {
        Money actualBalance = bank.getAccount(number).getBalance();
        expectedBalance.setActual(actualBalance);
    }

    @When("I deposit (\\$\\d+\\.\\d\\d) in account (\\d{5}\\-\\d{5})")
    public void deposit(Money amount, String number) throws Exception {
        bank.deposit(amount, number);
    }

    @Check("I can deposit (\\$\\d+\\.\\d\\d) in account (\\d{5}\\-\\d{5})")
    public boolean canDeposit(Money amount, String number) throws Exception {
        bank.deposit(amount, number);
        return true;
    }

    @When("I withdraw (\\$\\d+\\.\\d\\d) from account (\\d{5}\\-\\d{5})")
    public void withdraw(Money amount, String number) throws Exception {
        bank.withdraw(amount, number, WithdrawType.ATM);
    }

    @Check("I can't withdraw (\\$\\d+\\.\\d\\d) from account (\\d{5}\\-\\d{5})")
    public boolean cannotWithdraw(Money amount, String number) {
        try {
            bank.withdraw(amount, number, WithdrawType.ATM);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Check("I can't witdraw (\\$\\d+\\.\\d\\d) from account (\\d{5}\\-\\d{5}) using (\\w+)")
    public boolean cannotWithdrawUsing(Money amount, String number, WithdrawType withdrawType) {
        try {
            bank.withdraw(amount, number, withdrawType);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Check("I can withdraw (\\$\\d+\\.\\d\\d) from account (\\d{5}\\-\\d{5})")
    public boolean canWithdraw(Money amount, String number) {
        try {
            bank.withdraw(amount, number, WithdrawType.ATM);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Display("Show the balance of account (\\d{5}\\-\\d{5})")
    public Money getBalanceOfAccount(String number) throws NoSuchAccountException {
        return bank.getAccount(number).getBalance();
    }

    @Then("Freeze account (\\d{5}\\-\\d{5})")
    public void freezeAccount(String number) {
        bank.freezeAccount(number);
    }

    public Collection<BankAccount> getOpenedAccounts() {
        return bank.getAccounts();
    }
}
