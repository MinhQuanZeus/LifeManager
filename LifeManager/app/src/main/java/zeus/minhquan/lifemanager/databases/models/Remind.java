package zeus.minhquan.lifemanager.databases.models;

/**
 * Created by anh82 on 4/18/2017.
 */

public class Remind {
    private int id;
    private String title;
    private String description;
    private String date;
    private String time;
    private String record_name;

    public Remind(int id, String title, String description, String date, String time, String record_name) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.record_name = record_name;
    }

    public Remind(int id, String title, String description, String date, String time) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public Remind(String title, String description, String date, String time) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public Remind(String title, String description, String date, String time, String record_name) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.record_name = record_name;
    }

    public int getId() {
        return id;
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

    public String getRecord_name() {
        return record_name;
    }
}
