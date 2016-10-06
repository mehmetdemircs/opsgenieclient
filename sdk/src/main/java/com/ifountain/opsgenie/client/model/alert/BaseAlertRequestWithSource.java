package com.ifountain.opsgenie.client.model.alert;

import com.ifountain.opsgenie.client.model.BaseResponse;

/**
 * Abstract Container for the alert requests.
 *
 * @author Mehmet Mustafa Demir
 */
public abstract class BaseAlertRequestWithSource<T extends BaseResponse> extends BaseAlertRequestWithId<T> {
    private String source;

    /**
     * The source of action.
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source of action.
     */
    public void setSource(String source) {
        this.source = source;
    }
}
