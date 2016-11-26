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

public class SavingsAccount extends BankAccount {

    public SavingsAccount(String number, Owner owner) {
        super(AccountType.SAVINGS, number, owner);
    }

    /**
     * No modifier for restricted access to this constructor.
     *
     * @param number the number of the bank account
     * @param owner the owner of the bank account
     * @param balance the account balance
     * @param frozen true for frozen account, false for active account
     */
    SavingsAccount(String number, Owner owner, Money balance, boolean frozen) {
        super(AccountType.SAVINGS, number, owner, balance, frozen);
    }

    @Override
    public void checkFunds(Money amount) throws Exception {
        if (getBalance().lowerThan(amount)) {
            throw new Exception("Not enougth money !");
        }
    }
}
