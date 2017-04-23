package zeus.minhquan.lifemanager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.RecordActivity;

/**
 * Created by EDGY on 4/23/2017.
 */

public class RecordAdapter extends BaseAdapter{
    private static final String TAG = "RecordAdapter";
    private Context context;
    private List<RecordActivity.FileRecord> recordList;
    private ImageView ivRecord;
    private TextView tvRecordName;
    private TextView tvRecordTime;
    private LayoutInflater layoutInflater;

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
    public View getView(int position, View view, ViewGroup viewGroup) {
        Log.d(TAG,view + "");
        view = layoutInflater.inflate(R.layout.record_list_layout, null, false);
        ImageView ivRecord = (ImageView) view.findViewById(R.id.iv_record);
        TextView tvRecordName = (TextView) view.findViewById(R.id.tv_record_name);
        if(recordList == null){
            tvRecordName.setText("No record here");
        } else {
            tvRecordName.setText(recordList.get(position).getFileName());
        }
        return view;
    }
}
