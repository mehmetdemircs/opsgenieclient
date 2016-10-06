package com.ifountain.opsgenie.client.model.alert;

/**
 * Container for the parameters to make a delete alert api call.
 *
 * @author Mehmet Mustafa Demir
 * @see com.ifountain.opsgenie.client.IAlertOpsGenieClient#deleteAlert(DeleteAlertRequest)
 */
public class DeleteAlertRequest extends BaseAlertRequestWithSource<DeleteAlertResponse> {
    private String user;

    /**
     * Rest api uri of delete alert operation.
     */
    @Override
    public String getEndPoint() {
        return "/v1/json/alert";
    }

    /**
     * The user who is performing the delete alert operation.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user who is performing the delete alert operation.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @see com.ifountain.opsgenie.client.model.BaseRequest#createResponse()
     */
    @Override
    public DeleteAlertResponse createResponse() {
        return new DeleteAlertResponse();
    }
}
