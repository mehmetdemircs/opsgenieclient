package com.ifountain.opsgenie.client.model.notificationRule;

import com.ifountain.opsgenie.client.model.BaseRequest;

/**
 * Container for the parameters to make an enable notificationRule api
 * call.
 *
 * @see com.ifountain.opsgenie.client.INotificationRuleOpsGenieClient#enableNotificationRule(com.ifountain.opsgenie.client.model.notificationRule.EnableNotificationRuleRequest)
 */
public class EnableNotificationRuleRequest extends BaseRequest<EnableNotificationRuleResponse> {
	private String username;
	private String userId;
	private String id;

	/**
	 * Rest api uri of enable notificationRule operation.
	 */
	@Override
	public String getEndPoint() {
		return "/v1/json/user/notificationRule/enable";
	}


	@Override
	/**
	 * @see com.ifountain.opsgenie.client.model.BaseRequest#createResponse()
	 */
	public EnableNotificationRuleResponse createResponse() {
		return new EnableNotificationRuleResponse();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
