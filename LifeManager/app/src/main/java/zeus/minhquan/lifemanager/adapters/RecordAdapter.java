package zeus.minhquan.lifemanager.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.RecordActivity;

/**
 * Created by EDGY on 4/23/2017.
 */

public class RecordAdapter extends BaseAdapter{
    private static final String TAG = "RecordAdapter";
    private Context context;
    private ImageView ivRecord;
    private List<RecordActivity.FileRecord> recordList;
    private ImageView ivPlay;
    private LayoutInflater layoutInflater;
    private TextView tvRecordName;

    public RecordAdapter(Context context, List<RecordActivity.FileRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<RecordActivity.FileRecord> getRecordList() {
        return recordList;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int location) {
        return recordList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        Log.d(TAG,view + "");
        view = layoutInflater.inflate(R.layout.record_list_layout, null, false);
        ivRecord = (ImageView) view.findViewById(R.id.iv_record);
        tvRecordName = (TextView) view.findViewById(R.id.tv_record_name);
        if(recordList == null){
            tvRecordName.setText("No record here");
        } else {
            tvRecordName.setText(recordList.get(position).getFileName());
        }
        return view;
    }
}
