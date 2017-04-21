package zeus.minhquan.lifemanager.databases.models;

/**
 * Created by anh82 on 4/18/2017.
 */

public class Remind {
    private String title;
    private String description;
    private String time;

    public Remind(String title, String description, String time) {
        this.title = title;
        this.description = description;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }
}
