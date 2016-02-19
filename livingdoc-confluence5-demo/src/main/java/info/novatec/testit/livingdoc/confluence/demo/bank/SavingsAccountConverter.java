package info.novatec.testit.livingdoc.confluence.demo.bank;

import org.json.JSONObject;

import info.novatec.testit.livingdoc.converter.TypeConverter;


public class SavingsAccountConverter implements TypeConverter {

    @Override
    public boolean canConvertTo(Class< ? > type) {
        return SavingsAccount.class.isAssignableFrom(type);
    }

    @Override
    public Object parse(String value, Class< ? > type) {
        SavingsAccount converted = null;
        String number = null;
        Owner owner = null;
        Money balance = Money.ZERO;
        boolean frozen = false;

        JSONObject jsonAccount = new JSONObject(value);
        if (jsonAccount.has("number")) {
            number = jsonAccount.getString("number");
        }
        if (jsonAccount.has("owner")) {
            owner = ( ( JSONOwner ) new JSONOwnerConverter().parse(jsonAccount.get("owner").toString(), JSONOwner.class) )
                .getOwner();
        }
        if (jsonAccount.has("balance")) {
            balance = Money.parse(jsonAccount.getString("balance"));
        }
        if (jsonAccount.has("frozen")) {
            frozen = jsonAccount.getBoolean("frozen");
        }

        converted = new SavingsAccount(number, owner, balance, frozen);
        return converted;
    }

    @Override
    public String toString(Object value) {
        String savingsAccountAsJson = null;
        if (value instanceof SavingsAccount) {
            SavingsAccount savingsAccount = ( SavingsAccount ) value;
            JSONObject jsonAccount = new JSONObject();
            jsonAccount.put("type", "SavingsAccount");
            jsonAccount.put("number", savingsAccount.getNumber());
            jsonAccount.put("owner", new JSONObject(new JSONOwnerConverter().toString(new JSONOwner(savingsAccount
                .getOwner()))));
            jsonAccount.put("balance", savingsAccount.getBalance().toString());
            jsonAccount.put("frozen", savingsAccount.isFrozen());
            savingsAccountAsJson = jsonAccount.toString();
        }
        return savingsAccountAsJson;
    }
}
