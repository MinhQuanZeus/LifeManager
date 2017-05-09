package zeus.minhquan.lifemanager.testWidget;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import zeus.minhquan.lifemanager.R;

public class LoremActivity extends Activity {
  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    
    String time=getIntent().getStringExtra(WidgetProvider.WIDGET_TIME);
    String description = getIntent().getStringExtra(WidgetProvider.WIDGET_DESCRIPTION);
    if(description.equals("") || description == null){
      Toast.makeText(this, R.string.no_widget_title +  " will start in "+ time, Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(this, R.string.widget_title + "'"+description+"'will start in " + time, Toast.LENGTH_LONG).show();
    }
    finish();
  }

}