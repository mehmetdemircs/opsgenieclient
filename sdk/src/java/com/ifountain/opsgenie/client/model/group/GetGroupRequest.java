package com.ifountain.opsgenie.client.model.group;

import com.ifountain.opsgenie.client.OpsGenieClientConstants;
import com.ifountain.opsgenie.client.model.BaseRequest;
import com.ifountain.opsgenie.client.model.escalation.UpdateEscalationResponse;

import java.util.Map;

/**
 * Container for the parameters to make a get group api call.
 *
 * @see com.ifountain.opsgenie.client.IGroupOpsGenieClient#getGroup(GetGroupRequest)
 */
public class GetGroupRequest extends BaseRequest<GetGroupResponse> {
    private String id;
    private String name;
    /**
     * Rest api uri of getting group operation.
     */
    @Override
    public String getEndPoint() {
        return "/v1/json/group";
    }

    /**
     * Id of group to be queried.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id of group to be queried.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Name of group to be queried.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of group to be queried.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    /**
     * @see com.ifountain.opsgenie.client.model.BaseRequest#serialize()
     */
    public Map serialize() {
        Map json = super.serialize();
        if(getId() != null){
            json.put(OpsGenieClientConstants.API.ID, getId());
        }
        if(getName() != null){
            json.put(OpsGenieClientConstants.API.NAME, getName());
        }
        return json;
    }

    @Override
    /**
     * @see com.ifountain.opsgenie.client.model.BaseRequest#createResponse()
     */
    public GetGroupResponse createResponse() {
        return new GetGroupResponse();
    }
}