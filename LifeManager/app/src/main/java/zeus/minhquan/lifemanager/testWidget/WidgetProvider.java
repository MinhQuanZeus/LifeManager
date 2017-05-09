/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
*/

   
package zeus.minhquan.lifemanager.testWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import zeus.minhquan.lifemanager.R;

public class WidgetProvider extends AppWidgetProvider {
    public static final String WIDGET_TITLE = "title_widget";
  public static final String WIDGET_TIME = "time_widget";
  public static final String WIDGET_DESCRIPTION = "description_widget";


  @Override
  public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
                       int[] appWidgetIds) {
    for (int i=0; i<appWidgetIds.length; i++) {
      Intent svcIntent=new Intent(ctxt, WidgetService.class);
      
      svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
      svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
      
      RemoteViews widget=new RemoteViews(ctxt.getPackageName(),
                                          R.layout.widget);
      
      widget.setRemoteAdapter(appWidgetIds[i], R.id.words,
                              svcIntent);

      Intent clickIntent=new Intent(ctxt, LoremActivity.class);
      PendingIntent clickPI= PendingIntent
                              .getActivity(ctxt, 0,
                                            clickIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
      
      widget.setPendingIntentTemplate(R.id.words, clickPI);
      ComponentName component = new ComponentName(ctxt, WidgetProvider.class);
      appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.words);
      appWidgetManager.updateAppWidget(component,widget);
      appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
    }

    super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
  }
}