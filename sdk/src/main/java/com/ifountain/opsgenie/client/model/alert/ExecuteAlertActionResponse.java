package com.ifountain.opsgenie.client.model.alert;

/**
 * Represents the OpsGenie service response for an execute alert action request.
 *
 * @author Mehmet Mustafa Demir
 * @see com.ifountain.opsgenie.client.IAlertOpsGenieClient#executeAlertAction(ExecuteAlertActionRequest)
 */
public class ExecuteAlertActionResponse extends AddNoteResponse {
    private String result;

    /**
     * Action execution result.
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the action execution result.
     */
    public void setResult(String result) {
        this.result = result;
    }

}
