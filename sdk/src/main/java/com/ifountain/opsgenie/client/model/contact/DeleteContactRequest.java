package com.ifountain.opsgenie.client.model.contact;


import com.ifountain.opsgenie.client.model.BaseUserRequest;

/**
 * Container for the parameters to make a delete contact api call.
 *
 * @author Mehmet Mustafa Demir
 * @see com.ifountain.opsgenie.client.IContactOpsGenieClient#deleteContact(DeleteContactRequest)
 */
public class DeleteContactRequest extends BaseUserRequest<DeleteContactResponse> {
    private String id;

    /**
     * Rest api uri of deleting contact operation.
     */
    @Override
    public String getEndPoint() {
        return "/v1/json/user/contact";
    }

    /**
     * @see com.ifountain.opsgenie.client.model.BaseRequest#createResponse()
     */
    @Override
    public DeleteContactResponse createResponse() {
        return new DeleteContactResponse();
    }

    /**
     * Id of contact to be deleted.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets Id of contact to be deleted.
     */
    public void setId(String id) {
        this.id = id;
    }


}
