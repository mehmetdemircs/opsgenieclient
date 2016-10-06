package com.ifountain.opsgenie.client.model.schedule;

import com.ifountain.opsgenie.client.model.BaseResponse;
import com.ifountain.opsgenie.client.model.beans.WhoIsOnCall;
import org.codehaus.jackson.annotate.JsonUnwrapped;

/**
 * Represents OpsGenie service response for who is on call request.
 *
 * @author Mehmet Mustafa Demir
 * @see com.ifountain.opsgenie.client.IScheduleOpsGenieClient#whoIsOnCall(WhoIsOnCallRequest)
 */
public class WhoIsOnCallResponse extends BaseResponse {
    @JsonUnwrapped
    private WhoIsOnCall whoIsOnCall;

    /**
     * Details of schedule on call
     *
     * @see com.ifountain.opsgenie.client.model.beans.WhoIsOnCall
     */
    public WhoIsOnCall getWhoIsOnCall() {
        return whoIsOnCall;
    }

    /**
     * Sets details of schedule on call
     *
     * @see com.ifountain.opsgenie.client.model.beans.WhoIsOnCall
     */
    public void setWhoIsOnCall(WhoIsOnCall whoIsOnCall) {
        this.whoIsOnCall = whoIsOnCall;
    }

}
