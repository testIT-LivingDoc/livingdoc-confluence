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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class Bank {
    private final Map<String, BankAccount> accounts;

    public Bank() {
        accounts = new HashMap<String, BankAccount>();
    }

    /**
     * No modifier for restricted access to this constructor.
     *
     * @param accounts the bank accounts
     */
    Bank(Map<String, BankAccount> accounts) {
        this.accounts = accounts;
    }

    public boolean hasAccount(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    public BankAccount getAccount(String accountNumber) throws NoSuchAccountException {
        if ( ! hasAccount(accountNumber)) {
            throw new NoSuchAccountException(accountNumber);
        }
        return accounts.get(accountNumber);
    }

    public void addAccount(BankAccount account) {
        if (hasAccount(account.getNumber())) {
            return;
        }
        accounts.put(account.getNumber(), account);
    }

    public SavingsAccount openSavingsAccount(String number, Owner owner) {
        if (hasAccount(number)) {
            return null;
        }

        SavingsAccount account = new SavingsAccount(number, owner);
        accounts.put(number, account);
        return account;
    }

    public CheckingAccount openCheckingAccount(String number, Owner owner) {
        if (hasAccount(number)) {
            return null;
        }

        CheckingAccount account = new CheckingAccount(number, owner);
        accounts.put(number, account);
        return account;
    }

    public Money deposit(Money amount, String number) throws Exception {
        BankAccount account = accounts.get(number);
        return account.deposit(amount);
    }

    public Money withdraw(Money amount, String number, WithdrawType type) throws Exception {
        BankAccount account = accounts.get(number);
        return account.withdraw(amount, type);
    }

    public void freezeAccount(String number) {
        BankAccount account = accounts.get(number);
        account.freeze();
    }

    public Collection<BankAccount> getAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
    }

    public void transfer(String numberFrom, String numberTo, Money amountToTransfer) throws Exception {
        if ( ! hasAccount(numberFrom)) {
            throw new NoSuchAccountException(numberFrom);
        }
        if ( ! hasAccount(numberTo)) {
            throw new NoSuchAccountException(numberTo);
        }

        BankAccount accountFrom = accounts.get(numberFrom);
        BankAccount accountTo = accounts.get(numberTo);

        if (accountFrom.getOwner().getFirstName().equals(accountTo.getOwner().getFirstName()) && accountFrom.getOwnerName()
            .equals(accountTo.getOwnerName())) {
            accountFrom.withdraw(amountToTransfer);
            accountTo.deposit(amountToTransfer);
        } else {
            throw new Exception("Can't transfer from not owned account !");
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( accounts == null ) ? 0 : accounts.hashCode() );
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Bank otherBank = ( Bank ) other;
        if (accounts.size() != otherBank.accounts.size()) {
            return false;
        }

        for (Iterator<String> iterator = accounts.keySet().iterator(); iterator.hasNext();) {
            BankAccount thisAccount = accounts.get(iterator.next());
            if (thisAccount == null) {
                return false;
            }
            try {
                BankAccount otherBankAccount = otherBank.getAccount(thisAccount.getNumber());
                if ( ! thisAccount.equals(otherBankAccount)) {
                    return false;
                }
            } catch (NoSuchAccountException e) {
                return false;
            }
        }

        return true;

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
