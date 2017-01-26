package info.novatec.testit.livingdoc.confluence.demo.bank.scenario;

import info.novatec.testit.livingdoc.reflect.Fixture;
import info.novatec.testit.livingdoc.systemunderdevelopment.DefaultSystemUnderDevelopment;

public class BankScenarioSystemUnderDevelopment extends
		DefaultSystemUnderDevelopment {
	private Fixture bankScenarioFixture;

	public BankScenarioSystemUnderDevelopment() {
		super();
		addImport(BankScenarioSystemUnderDevelopment.class.getPackage()
				.getName());
	}

	@Override
	public Fixture getFixture(String name, String... params) throws Throwable {
		if (bankScenarioFixture == null) {
			bankScenarioFixture = super.getFixture(name, params);
		}
		return bankScenarioFixture;
	}
}
