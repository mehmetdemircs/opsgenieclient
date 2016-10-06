package com.ifountain.opsgenie.client.model.contact;

import com.ifountain.opsgenie.client.OpsGenieClientConstants;
import com.ifountain.opsgenie.client.OpsGenieClientValidationException;
import com.ifountain.opsgenie.client.model.BaseUserRequest;

/**
 * Container for the parameters to make an update contact api call.
 *
 * @author Mehmet Mustafa Demir
 * @see com.ifountain.opsgenie.client.IContactOpsGenieClient#updateContact(UpdateContactRequest)
 */
public class UpdateContactRequest extends BaseUserRequest<UpdateContactResponse> {
    private String id;
    private String to;

    /**
     * Rest api uri of updating contact operation.
     */
    @Override
    public String getEndPoint() {
        return "/v1/json/user/contact";
    }

    /**
     * check the parameters for validation.
     *
     * @throws OpsGenieClientValidationException when id is null!
     */
    @Override
    public void validate() throws OpsGenieClientValidationException {
        super.validate();
        if (id == null)
            throw OpsGenieClientValidationException.missingMandatoryProperty(OpsGenieClientConstants.API.ID);
    }

    /**
     * @see com.ifountain.opsgenie.client.model.BaseRequest#createResponse()
     */
    @Override
    public UpdateContactResponse createResponse() {
        return new UpdateContactResponse();
    }

    /**
     * Id of contact to be updated
     */
    public String getId() {
        return id;
    }

    /**
     * Sets Id of contact to be updated
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * to of contact to be updated
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets to of contact to be updated
     */
    public void setTo(String to) {
        this.to = to;
    }


}
