package zeus.minhquan.lifemanager.controllerRemind;

/**
 * Created by anh82 on 4/21/2017.
 */

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.database.models.Remind;

public class MyArrayAdapter extends ArrayAdapter<Remind>
{
    Activity context=null;
    ArrayList<Remind>myArray=null;
    LayoutInflater inflater;
    int layoutId;
    ViewHolder holder;
    SharedPreferences.Editor editor;
    /**
     * Constructor này dùng d? kh?i t?o các giá tr?
     * t? MainActivity truy?n vào
     * @param context : là Activity t? Main
     * @param layoutId : Là layout custom do ta t?o (my_item_layout.xml)
     * @param arr : Danh sách nhân viên truy?n t? Main
     */
    public MyArrayAdapter(Activity context, int layoutId, ArrayList<Remind>arr){
        super(context, layoutId, arr);
        this.context=context;
        this.layoutId=layoutId;
        this.myArray=arr;

        inflater=  context.getLayoutInflater();
    }
    public int getCount() {
        return myArray.size();

    }

    public Remind getItem(int position) {
        return myArray.get(position);
    }

    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        final Remind emp = myArray.get(position);
        if (convertView == null) {
            convertView=inflater.inflate(layoutId, null);

            holder = new ViewHolder();
            holder.txtContent = (TextView) convertView.findViewById(R.id.contentRemind1);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.descriptionRemind1);
            holder.txtTime = (TextView) convertView.findViewById(R.id.timeRemind1);
            holder.txtRecord = (TextView) convertView.findViewById(R.id.record1);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtContent.setText(emp.getTitle());
        holder.txtDescription.setText(emp.getDescription());
        holder.txtTime.setText(emp.getTime() + "    " + emp.getDate());
        holder.txtRecord.setText(emp.getRecord_name());
        Log.d("Content" , emp.getTitle());

        return convertView;

    }


    static class ViewHolder {
        TextView txtContent;
        TextView txtDescription;
        TextView txtTime;
        TextView txtRecord;

    }
}
