package info.novatec.testit.livingdoc.confluence.demo.calculator;

import info.novatec.testit.livingdoc.reflect.Fixture;
import info.novatec.testit.livingdoc.systemunderdevelopment.DefaultSystemUnderDevelopment;

public class CalculatorSystemUnderDevelopment extends
		DefaultSystemUnderDevelopment {
	private Fixture calculatorFixture;

	public CalculatorSystemUnderDevelopment() {
		super();
		addImport(CalculatorSystemUnderDevelopment.class.getPackage().getName());
	}

	@Override
	public Fixture getFixture(String name, String... params) throws Throwable {
		if (calculatorFixture == null) {
			calculatorFixture = super.getFixture(name, params);
		}
		return calculatorFixture;
	}
}
