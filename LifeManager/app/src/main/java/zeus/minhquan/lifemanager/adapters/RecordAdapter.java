package zeus.minhquan.lifemanager.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import zeus.minhquan.lifemanager.MyListener;
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
    private ImageView ivDele;
    private MyListener myListener;


    public RecordAdapter(Context context, List<RecordActivity.FileRecord> recordList, MyListener myListener) {
        super();
        this.context = context;
        this.recordList = recordList;
        this.myListener = myListener;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<RecordActivity.FileRecord> getRecordList() {
        return recordList;
    }

    public void rotateDisk(TextView textView){
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(4000);
        rotate.setRepeatCount(Animation.INFINITE);
        textView.setAnimation(rotate);
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

    public boolean deleFileRecord(String absolutePath){
        File file = new File(absolutePath);
        return file.delete();
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        Log.d(TAG,view + "");
        view = layoutInflater.inflate(R.layout.record_list_layout, null, false);
        ivRecord = (ImageView) view.findViewById(R.id.iv_record);
        tvRecordName = (TextView) view.findViewById(R.id.tv_record_name);
        ivDele = (ImageView) view.findViewById(R.id.iv_dele);
        if(recordList == null){
            tvRecordName.setText("No record here");
        } else {
            tvRecordName.setText(recordList.get(position).getFileName());
        }
        ivDele.setTag(position);
        ivDele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleFileRecord(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +tvRecordName.getText().toString());
                Integer index = (Integer) ivDele.getTag();
                recordList.remove(index.intValue());
                if(recordList.size() == 0){
                    myListener.emptyClick();
                    Log.d(TAG,"fking jump : "+ recordList.size());
                }
                notifyDataSetChanged();
            }
        });
        return view;
    }
}
