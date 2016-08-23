package com.ifountain.opsgenie.client.model.team;

import com.ifountain.opsgenie.client.model.BaseRequest;

/**
 * Container for the parameters to make an remove team memberapi call.
 *
 * @see com.ifountain.opsgenie.client.ITeamOpsGenieClient#deleteTeamMember(DeleteTeamMemberRequest)
 */
public class DeleteTeamMemberRequest extends BaseRequest<DeleteTeamMemberResponse>{
    private String id;
    private String name;
    private String userId;
    private String username;
	
    @Override
    /**
     * @see com.ifountain.opsgenie.client.model.BaseRequest#createResponse()
     */
    public DeleteTeamMemberResponse createResponse() {
        return new DeleteTeamMemberResponse();
    }

	@Override
	public String getEndPoint() {
        return "/v1/json/team/member";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}