package de.uni_potsdam.hpi.bpt.bp2014.database;

/**
 *
 */
public class DbBoundaryEvent extends DbObject {
    /**
     *
     * @param eventNodeId The control node Id of the boundary event
     * @param fragmentInstanceId The id of the fragment instance where the bounday event is
     *                           initialized in
     * @return databaseId of the activity the bounday event is attached to
     */
    public int getControlNodeAttachedToEvent(int eventNodeId, int fragmentInstanceId) {
        String editorIdQuery = "SELECT * FROM boundaryeventref where controlnode_id = "
                + eventNodeId + ";";
        String editorId = this.executeStatementReturnsString(editorIdQuery, "attachedtoref");
        String getControlNodeForEditorId = "SELECT * FROM controlnode WHERE modelid = "
                + editorId + ";";
        int activityControlNodeId = this.executeStatementReturnsInt(getControlNodeForEditorId,
                "controlnode_id");
        DbControlNodeInstance controlNodeInstance = new DbControlNodeInstance();
        return controlNodeInstance.getControlNodeInstanceID(activityControlNodeId,
                fragmentInstanceId);
    }
}
