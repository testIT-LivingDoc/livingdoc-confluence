package info.novatec.testit.livingdoc.confluence.demo.bank;

import info.novatec.testit.livingdoc.converter.TypeConverter;

import org.json.JSONObject;

public class JSONOwnerConverter implements TypeConverter {

    @Override
    public boolean canConvertTo(Class<?> type) {
        return JSONOwner.class.isAssignableFrom(type);
    }

    @Override
    public Object parse(String value, Class<?> type) {
        Owner converted = null;
        
        JSONObject jsonObject = new JSONObject(value);
        String firstName = jsonObject.has("first name") ? jsonObject.getString("first name") : null;
        String lastName = jsonObject.has("last name") ? jsonObject.getString("last name") : null;
        converted = new Owner(firstName, lastName);
        JSONOwner jsonOwner = new JSONOwner(converted);
        
        return jsonOwner;
    }

    @Override
    public String toString(Object value) {
        String ownerAsJson = null;
        if (value instanceof JSONOwner) {
        	JSONOwner jsonValue = (JSONOwner) value;
            Owner owner = (Owner) jsonValue.getOwner();
            JSONObject jsonOwner = new JSONObject();
            jsonOwner.put("type", "Owner");
            jsonOwner.put("first name", owner.getFirstName());
            jsonOwner.put("last name", owner.getLastName());
            ownerAsJson = jsonOwner.toString();
        }
        return ownerAsJson;
    }
}
