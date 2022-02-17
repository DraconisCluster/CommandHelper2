package adams.im;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GridBackup {
    private String gridName;
    private long entityId;
    private Date date;

    public GridBackup(String gridName, long entityId, Date date) {
        this.gridName = gridName;
        this. entityId = entityId;
        this.date = date;
    }

    public String getgridName(){
        return this.gridName;
    }

    public String getEntityId(){
        return Long.toString(this.entityId);
    }

    public String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        format.applyPattern("yyyy-MM-dd kk:mm:ss");
        return format.format(date);
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String toString(){
        return this.gridName + " " + this.entityId + " " + this.date;
    }
}
