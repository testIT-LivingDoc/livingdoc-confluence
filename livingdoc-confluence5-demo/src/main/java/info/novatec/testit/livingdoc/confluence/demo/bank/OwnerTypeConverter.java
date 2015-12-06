package info.novatec.testit.livingdoc.confluence.demo.bank;

import info.novatec.testit.livingdoc.converter.AbstractTypeConverter;


public class OwnerTypeConverter extends AbstractTypeConverter {

    @Override
    public boolean canConvertTo(Class< ? > type) {
        return Owner.class.isAssignableFrom(type);
    }

    @Override
    protected Object doConvert(String value) {
        String[] names = value.split("\\s");
        return new Owner(names[0], names.length > 1 ? names[1] : null);
    }
}
