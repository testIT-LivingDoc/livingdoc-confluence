package info.novatec.testit.livingdoc.confluence.demo.bank;

import info.novatec.testit.livingdoc.LivingDoc;
import info.novatec.testit.livingdoc.reflect.BeforeFirstExpectation;
import info.novatec.testit.livingdoc.reflect.BeforeTable;
import info.novatec.testit.livingdoc.reflect.annotation.Alias;
import info.novatec.testit.livingdoc.reflect.annotation.FixtureClass;

@FixtureClass({ "Bank Verwaltung", "AccountManagement" })
public class BankManagementFixture {

	private Bank expectedData;
	private Bank initialData;

	@BeforeTable
	public void init() {
		LivingDoc.register(new BankConverter());
		LivingDoc.register(new SavingsAccountConverter());
		LivingDoc.register(new CheckingAccountConverter());
		LivingDoc.register(new JSONOwnerConverter());
	}

	@BeforeFirstExpectation
	public void execute() {
		this.expectedData = initialData;
	}

	public Bank getExpectedData() {
		return expectedData;
	}

	public void setInitialData(Bank initialData) {
		this.initialData = initialData;
	}

	@Alias("createCheckingAccount")
	public void addCheckingAccount(CheckingAccount checkingAccount) {
		initialData.addAccount(checkingAccount);
	}

	@Alias("createSavingsAccount")
	public void addSavingsAccount(SavingsAccount savingsAccount) {
		initialData.addAccount(savingsAccount);
	}

	@Alias("stopAccessToAccountNumber")
	public void freezeAccount(String number) {
		initialData.freezeAccount(number);
	}
}
