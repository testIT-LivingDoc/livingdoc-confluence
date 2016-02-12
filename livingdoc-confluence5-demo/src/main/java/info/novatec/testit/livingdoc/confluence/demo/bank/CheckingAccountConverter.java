package info.novatec.testit.livingdoc.confluence.demo.bank;

import info.novatec.testit.livingdoc.converter.TypeConverter;

import org.json.JSONObject;

public class CheckingAccountConverter implements TypeConverter {

    @Override
    public boolean canConvertTo(Class<?> type) {
        return CheckingAccount.class.isAssignableFrom(type);
    }

    @Override
    public Object parse(String value, Class<?> type) {
        CheckingAccount converted = null;
        String number = null;
        Owner owner = null;
        Money maxCredit = Money.ZERO;
        Money balance = Money.ZERO;
        boolean frozen = false;
        
        JSONObject jsonAccount = new JSONObject(value);
        if (jsonAccount.has("number")) {
            number = jsonAccount.getString("number");
        }
        if (jsonAccount.has("owner")) {
            owner = ((JSONOwner) new JSONOwnerConverter().parse(jsonAccount.get("owner").toString(), JSONOwner.class)).getOwner();
        }
        if (jsonAccount.has("credit line")) {
            maxCredit = Money.parse(jsonAccount.getString("credit line"));
        }
        if (jsonAccount.has("balance")) {
            balance = Money.parse(jsonAccount.getString("balance"));
        }
        if (jsonAccount.has("frozen")) {
            frozen = jsonAccount.getBoolean("frozen");
        }
        
        converted = new CheckingAccount(number, owner, maxCredit, balance, frozen);
        return converted;
    }

    @Override
    public String toString(Object value) {
        String checkingAccountAsJson = null;
        if (value instanceof CheckingAccount) {
            CheckingAccount checkingAccount = (CheckingAccount) value;
            JSONObject jsonAccount = new JSONObject();
            jsonAccount.put("type", "CheckingAccount");
            jsonAccount.put("number", checkingAccount.getNumber());
            jsonAccount.put("owner", new JSONObject(new JSONOwnerConverter().toString(new JSONOwner(checkingAccount.getOwner()))));
            jsonAccount.put("credit line", checkingAccount.getCreditLine().toString());
            jsonAccount.put("balance", checkingAccount.getBalance().toString());
            jsonAccount.put("frozen", checkingAccount.isFrozen());
            checkingAccountAsJson = jsonAccount.toString();
        }
        return checkingAccountAsJson;
    }
}
