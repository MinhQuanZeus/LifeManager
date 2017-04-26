package zeus.minhquan.lifemanager.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by QuanT on 4/22/2017.
 */

public class Loggable {
    public String Name;
    JSONObject Properties;

    public void putProp(String property, Object value) {
        try {
            Properties.put(property, value);
        }
        catch (JSONException ex) {
            Logger.trackException(ex);
        }
    }

    public void putJSON(JSONObject json) {
        try {
            Iterator<?> keys = json.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
                Properties.put(key, json.get(key));
            }
        }
        catch (JSONException ex) {
            Logger.trackException(ex);
        }
    }

    public interface Key {
        String APP_ALARM_RINGING = "An alarm rang";
        String APP_EXCEPTION = "Exception caught";

        String ACTION_ALARM_SNOOZE = "Snoozed an alarm";
        String ACTION_ALARM_DISMISS = "Dismissed an alarm";
        String ACTION_ALARM_EDIT = "Editing an alarm";
        String ACTION_ALARM_CREATE = "Creating a new alarm";
        String ACTION_ALARM_SAVE = "Saving changes to an alarm";
        String ACTION_ALARM_SAVE_DISCARD = "Discarding changes to an alarm";
        String ACTION_ALARM_DELETE = "Deleting an alarm";


        String ACTION_GAME_NONETWORK = "Played an no network game";
        String ACTION_GAME_NONETWORK_TIMEOUT = "Timed out on an no network game";
        String ACTION_GAME_NONETWORK_SUCCESS = "Finished an no network game";

    }

    public static class UserAction extends Loggable {
        public UserAction (String name) {
            Name = name;
            Properties = new JSONObject();
            try {
                Properties.put("Type", "User Action");
            }
            catch (JSONException jsonEx) {
            }
        }
    }

    public static class AppAction extends Loggable {
        public AppAction (String name) {
            Name = name;
            Properties = new JSONObject();
            try {
                Properties.put("Type", "App Action");
            }
            catch (JSONException jsonEx) {
            }
        }
    }

    public static class AppException extends Loggable {
        public AppException (String name, Exception ex) {
            Name = name;
            Properties = new JSONObject();
            try {
                Properties.put("Type", "Exception");
                Properties.put("Message", ex);
                Properties.put("Detailed Message", ex.getMessage());
            }
            catch (JSONException jsonEx) {
            }
        }
    }

    public static class AppError extends Loggable {
        public AppError (String name, String error) {
            Name = name;
            Properties = new JSONObject();
            try {
                Properties.put("Type", "Error");
                Properties.put("Message", error);
            }
            catch (JSONException jsonEx) {
            }
        }
    }
}
