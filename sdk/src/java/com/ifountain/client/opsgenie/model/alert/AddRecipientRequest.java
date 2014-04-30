package com.ifountain.client.opsgenie.model.alert;

import com.ifountain.client.ClientConstants;
import com.ifountain.client.ClientValidationException;

import java.util.Map;

/**
 * Container for the parameters to make an add recipient call.
 *
 * @author Sezgin Kucukkaraaslan
 * @version 1/9/13 4:03 PM
 * @see com.ifountain.client.opsgenie.IAlertOpsGenieClient#addRecipient(AddRecipientRequest)
 */
public class AddRecipientRequest extends BaseAlertRequestWithSource<AddRecipientResponse> {
    private String user;
    private String recipient;
    private String note;

    /**
     * Rest api uri of assign ownership operation.
     */
    @Override
    public String getEndPoint() {
        return "/v1/json/alert/recipient";
    }

    /**
     * The user who is performing the add recipient operation.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user who is performing the add recipient operation.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * The recipient that will be added.
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient that will be added.
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Additional alert note.
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets additional alert note.
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @see com.ifountain.client.model.BaseRequest#serialize()
     */
    public Map serialize() throws ClientValidationException {
        Map json = super.serialize();
        json.put(ClientConstants.API.RECIPIENT, getRecipient());
        if (getUser() != null)
            json.put(ClientConstants.API.USER, getUser());
        if (getNote() != null)
            json.put(ClientConstants.API.NOTE, getNote());
        return json;
    }

    @Override
    /**
     * @see com.ifountain.client.model.BaseRequest#createResponse()
     */
    public AddRecipientResponse createResponse() {
        return new AddRecipientResponse();
    }
}
