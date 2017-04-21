package zeus.minhquan.lifemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import zeus.minhquan.lifemanager.databases.RemindDatabase;
import zeus.minhquan.lifemanager.databases.models.Remind;

public class RemindActivity extends AppCompatActivity {

    private ImageView ivAdd;

    ArrayList<Remind> arrRemind;
    //Sử dụng MyArrayAdapter thay thì ArrayAdapter
    MyArrayAdapter adapter=null;
    ListView lvRemind=null;


    ImageButton btnRemoveAll;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        ivAdd = (ImageView) findViewById(R.id.iv_add);
        lvRemind=(ListView) findViewById(R.id.lvRemaind);
        btnRemoveAll=(ImageButton) findViewById(R.id.btndelete);
        RemindDatabase remindDatabase = RemindApplication.getInstance().getStoryDatabase();
         arrRemind = new ArrayList<>();
        arrRemind = (ArrayList<Remind>) remindDatabase.loadAllReminds();

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RemindActivity.this, AddRemindActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        //Khởi tạo đối tượng adapter và gán Data source
        adapter=new MyArrayAdapter(this,
                R.layout.my_item_layout
                , arrRemind/*thiết lập data source*/);
        lvRemind.setAdapter(adapter);

        btnRemoveAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                xulyXoa();
            }
        });
    }
    public void xulyXoa()
    {
        //ta nên đi ngược danh sách, kiểm tra phần tử nào checked
        //thì xóa đúng vị trí đó ra khỏi arrEmployee
        for(int i=lvRemind.getChildCount()-1;i>=0;i--)
        {
            //lấy ra dòng thứ i trong ListView
            //Dòng thứ i sẽ có 3 phần tử: ImageView, TextView, Checkbox
            View v=lvRemind.getChildAt(i);
            //Ta chỉ lấy CheckBox ra kiểm tra
            CheckBox chk=(CheckBox) v.findViewById(R.id.chkitem);
            //Nếu nó Checked thì xóa ra khỏi arrEmployee
            if(chk.isChecked())
            {
                //xóa phần tử thứ i ra khỏi danh sách
                arrRemind.remove(i);
            }
        }
        //Sau khi xóa xong thì gọi update giao diện
        adapter.notifyDataSetChanged();
    }


}
