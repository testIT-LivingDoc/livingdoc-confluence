package info.novatec.testit.livingdoc.confluence.demo.bank;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import info.novatec.testit.livingdoc.converter.TypeConverter;


public class BankConverter implements TypeConverter {

    @Override
    public boolean canConvertTo(Class< ? > type) {
        return Bank.class.isAssignableFrom(type);
    }

    @Override
    public Object parse(String value, Class< ? > type) {
        Bank converted = null;
        Map<String, BankAccount> accounts = new HashMap<String, BankAccount>();

        JSONObject jsonObject = new JSONObject(value);

        JSONArray jsonAccounts = jsonObject.getJSONArray("accounts");
        int numberOfAccounts = jsonAccounts.length();
        for (int i = 0; i < numberOfAccounts; i ++ ) {
            JSONObject jsonAccount = ( JSONObject ) jsonAccounts.get(i);
            String accountType = jsonAccount.getString("type");
            String number = jsonAccount.getString("number");
            if (accountType.equals("SavingsAccount")) {
                accounts.put(number, ( SavingsAccount ) new SavingsAccountConverter().parse(jsonAccount.toString(),
                    SavingsAccount.class));
            } else if (accountType.equals("CheckingAccount")) {
                accounts.put(number, ( CheckingAccount ) new CheckingAccountConverter().parse(jsonAccount.toString(),
                    SavingsAccount.class));
            }
        }

        converted = new Bank(accounts);
        return converted;
    }

    @Override
    public String toString(Object value) {
        String bankAsJson = null;
        if (value instanceof Bank) {
            Bank bank = ( Bank ) value;
            JSONArray jsonAccounts = new JSONArray();
            for (BankAccount account : bank.getAccounts()) {
                if (account instanceof CheckingAccount) {
                    jsonAccounts.put(new JSONObject(new CheckingAccountConverter().toString(account)));
                } else if (account instanceof SavingsAccount) {
                    jsonAccounts.put(new JSONObject(new SavingsAccountConverter().toString(account)));
                }
            }

            JSONObject jsonBank = new JSONObject();
            jsonBank.put("type", "Bank");
            jsonBank.put("accounts", jsonAccounts);

            bankAsJson = jsonBank.toString();
        }
        return bankAsJson;
    }
}
