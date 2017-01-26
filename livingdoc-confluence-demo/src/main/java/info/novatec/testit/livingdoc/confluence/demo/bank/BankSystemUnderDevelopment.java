package info.novatec.testit.livingdoc.confluence.demo.bank;

import info.novatec.testit.livingdoc.reflect.Fixture;
import info.novatec.testit.livingdoc.systemunderdevelopment.DefaultSystemUnderDevelopment;

public class BankSystemUnderDevelopment extends DefaultSystemUnderDevelopment {
	private Fixture bankFixture;

	public BankSystemUnderDevelopment() {
		super();
		addImport(BankSystemUnderDevelopment.class.getPackage().getName());
	}

	@Override
	public Fixture getFixture(String name, String... params) throws Throwable {
		if (bankFixture == null) {
			bankFixture = super.getFixture(name, params);
		}
		return bankFixture;
	}
}
