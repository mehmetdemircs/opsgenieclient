package com.ifountain.opsgenie.client.model.beans;

/**
 * Alert bean
 */
public class AlertNote extends Bean {
    private String owner;
    private String note;
    private long createdAt;

    /**
     * @return Owner of the comment
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the comment
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return The note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return The creation time of the note in nanoseconds
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation time of the note in nanoseconds
     */
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

}
