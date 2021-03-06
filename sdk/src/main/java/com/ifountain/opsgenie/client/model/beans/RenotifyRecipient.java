package com.ifountain.opsgenie.client.model.beans;

import com.ifountain.opsgenie.client.OpsGenieClientConstants;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * RenotifyRecipient bean
 */
public class RenotifyRecipient implements IBean{
    public enum Type{
        user,
        group
    }
    private String recipient;
    private Type type;

    /**
     * Name of recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets name of recipient
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Type of recipient
     * Could be one of user, group, escalation
     * @see Type
     */
    public Type getType() {
        return type;
    }


    @Override
    public Map toMap() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put(OpsGenieClientConstants.API.RECIPIENT, recipient);
        if(type != null){
            json.put(OpsGenieClientConstants.API.TYPE, type.name());
        }
        return json;
    }

    @Override
    public void fromMap(Map map) throws ParseException {
        recipient = (String) map.get(OpsGenieClientConstants.API.RECIPIENT);
        if(map.containsKey(OpsGenieClientConstants.API.TYPE)){
            type = Type.valueOf(((String) map.get(OpsGenieClientConstants.API.TYPE)).toLowerCase());
        }
    }
}
