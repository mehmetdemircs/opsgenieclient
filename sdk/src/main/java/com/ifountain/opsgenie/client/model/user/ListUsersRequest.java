package com.ifountain.opsgenie.client.model.user;

import com.ifountain.opsgenie.client.model.BaseRequest;

/**
 * Container for the parameters to make a list users api call.
 *
 * @author Mehmet Mustafa Demir
 * @see com.ifountain.opsgenie.client.IUserOpsGenieClient#listUsers(ListUsersRequest)
 */
public class ListUsersRequest extends BaseRequest<ListUsersResponse> {
    /**
     * Rest api uri of listing user operation.
     */
    @Override
    public String getEndPoint() {
        return "/v1/json/user";
    }

    /**
     * @see com.ifountain.opsgenie.client.model.BaseRequest#createResponse()
     */
    @Override
    public ListUsersResponse createResponse() {
        return new ListUsersResponse();
    }
}
