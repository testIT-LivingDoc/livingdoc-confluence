/**
 * Copyright (c) 2008 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package info.novatec.testit.livingdoc.confluence.demo.bank;

import java.util.Collection;

import info.novatec.testit.livingdoc.reflect.annotation.FixtureClass;


@FixtureClass("Banking")
public class BankFixture {

    private final Bank bank;

    public BankFixture() {
        this.bank = new Bank();
    }

    public Money theBalanceOfAccount(String number) throws NoSuchAccountException {
        return bank.getAccount(number).getBalance();
    }

    public boolean openSavingsAccountUnderTheNameOf(String number, String firstName, String lastName) {
        return bank.openSavingsAccount(number, new Owner(firstName, lastName)) != null;
    }

    public boolean openCheckingAccountUnderTheNameOf(String number, String firstName, String lastName) {
        return bank.openCheckingAccount(number, new Owner(firstName, lastName)) != null;
    }

    public boolean openAccountUnderTheNameOf(AccountType type, String number, String firstName, String lastName) {
        if (AccountType.SAVINGS == type) {
            return openSavingsAccountUnderTheNameOf(number, firstName, lastName);
        } else if (AccountType.CHECKING == type) {
            return openCheckingAccountUnderTheNameOf(number, firstName, lastName);
        }
        return false;
    }

    public Money thatBalanceOfAccountIs(String accountNumber) throws Exception {
        BankAccount account = bank.getAccount(accountNumber);
        return account.getBalance();
    }

    public boolean depositInAccount(Money amount, String accountNumber) throws Exception {
        try {
            bank.deposit(amount, accountNumber);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean withdrawFromAccount(Money amount, String accountNumber) throws Exception {
        return withdrawFromAccountUsing(amount, accountNumber, WithdrawType.ATM);
    }

    public boolean withdrawFromAccountUsing(Money amount, String accountNumber, WithdrawType withdrawType) throws Exception {
        try {
            bank.withdraw(amount, accountNumber, withdrawType);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Collection< ? > getOpenedAccounts() {
        return bank.getAccounts();
    }

    public void freezeAccount(String accountNumber) {
        bank.freezeAccount(accountNumber);
    }

    public boolean createAccountForWithBalanceOf(AccountType type, String number, String firstName, String lastName,
        Money balance) throws Exception {
        BankAccount account = ( type == AccountType.SAVINGS ) ? bank.openSavingsAccount(number, new Owner(firstName,
            lastName)) : bank.openCheckingAccount(number, new Owner(firstName, lastName));

        account.deposit(balance);
        return true;
    }

    public boolean transferFromAccountToAccount(Money amountToTransfer, String fromAccountNumber, String toAccountNumber)
        throws Exception {
        try {
            bank.transfer(fromAccountNumber, toAccountNumber, amountToTransfer);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
