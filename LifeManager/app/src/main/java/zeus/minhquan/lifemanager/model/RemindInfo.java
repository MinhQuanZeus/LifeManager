package zeus.minhquan.lifemanager.model;

/**
 * Created by EDGY on 4/21/2017.
 */

public class RemindInfo {
    private String title;
    private String description;
    private String date;
    private String time;

    public RemindInfo(String title, String description, String date, String time) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
