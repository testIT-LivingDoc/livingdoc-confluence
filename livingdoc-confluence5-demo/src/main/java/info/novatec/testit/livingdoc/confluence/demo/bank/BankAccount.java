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

import org.apache.commons.lang3.builder.ToStringBuilder;


public abstract class BankAccount {

    private final AccountType type;
    private Money balance = Money.ZERO;
    private final String number;
    private final Owner owner;

    public boolean frozen;

    public BankAccount(AccountType accountType, String number, Owner owner) {
        this.number = number;
        this.type = accountType;
        this.owner = owner;
    }

    /**
     * No modifier for restricted access to this constructor.
     *
     * @param accountType the type of the account
     * @param number the number of the bank account
     * @param owner the owner of the bank account
     * @param balance the account balance
     * @param frozen true for frozen account, false for active account
     */
    BankAccount(AccountType accountType, String number, Owner owner, Money balance, boolean frozen) {
        this.type = accountType;
        this.number = number;
        this.owner = owner;
        this.balance = balance;
        this.frozen = frozen;
    }

    public abstract void checkFunds(Money amount) throws Exception;

    public Money withdraw(Money amount, WithdrawType withdrawType) throws Exception {

        Money limit = type.limitFor(withdrawType);
        if ( ! AccountType.isNoLimit(limit) && amount.strictlyGreaterThan(limit)) {
            throw new Exception("Limit overpassed");
        }
        Money fees = type.feesFor(withdrawType);
        return withdraw(amount.plus(fees));
    }

    public Money withdraw(Money amount) throws Exception {
        checkNotFrozen();
        checkFunds(amount);
        balance = balance.minus(amount);
        return balance;
    }

    private void checkNotFrozen() throws Exception {
        if (frozen) {
            throw new Exception("Account frozen!");
        }
    }

    public String getNumber() {
        return number;
    }

    public Money deposit(Money amount) {
        balance = balance.plus(amount);
        return balance;
    }

    public Money getBalance() {
        return balance;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void freeze() {
        frozen = true;
    }

    public AccountType getType() {
        return type;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return owner.getFullName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( balance == null ) ? 0 : balance.hashCode() );
        result = prime * result + ( frozen ? 1231 : 1237 );
        result = prime * result + ( ( number == null ) ? 0 : number.hashCode() );
        result = prime * result + ( ( owner == null ) ? 0 : owner.hashCode() );
        result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
        return result;
    }

    @Override
    public boolean equals(Object value) {
        if (value instanceof BankAccount) {
            BankAccount other = ( BankAccount ) value;
            if (type != other.type) {
                return false;
            }
            if ( ! number.equals(other.number)) {
                return false;
            }
            if ( ! owner.equals(other.owner)) {
                return false;
            }
            if ( ! balance.equals(other.balance)) {
                return false;
            }
            if (frozen != other.frozen) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
