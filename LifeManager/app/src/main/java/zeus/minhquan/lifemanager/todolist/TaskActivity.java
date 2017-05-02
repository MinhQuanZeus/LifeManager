package zeus.minhquan.lifemanager.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import zeus.minhquan.lifemanager.BuildConfig;
import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.LifeManagerApplication;
import zeus.minhquan.lifemanager.utils.ImageUtil;
import zeus.minhquan.lifemanager.utils.LiveQueryAdapter;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by QuanT on 5/2/2017.
 */

public class TaskActivity extends AppCompatActivity {
    public static final String INTENT_LIST_ID = "list_id";
    public static final String INTENT_LIST_TITLE = "list_TITLE";

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    private static final int THUMBNAIL_SIZE = 150;

    private static SimpleDateFormat mDateFormatter =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private String mListId;
    private String mListTitle;
    private Database mDatabase;
    private TaskAdapter mAdapter;
    private String mImagePathToBeAttached;
    private Document mCurrentTaskToAttachImage;
    private Bitmap mImageToBeAttached;

    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LifeManagerApplication application = (LifeManagerApplication) getApplication();
        mDatabase = application.getToDoCB().getDatabase();

        if (savedInstanceState != null) {
            mListId = savedInstanceState.getString(INTENT_LIST_ID);
            mListTitle = savedInstanceState.getString(INTENT_LIST_TITLE);
        }
        else {
            mListId = getIntent().getStringExtra(INTENT_LIST_ID);
            mListTitle = getIntent().getStringExtra(INTENT_LIST_TITLE);
        }
        getSupportActionBar().setTitle(mListTitle);

        Query query = getQuery();
        mAdapter = new TaskAdapter(this, query.toLiveQuery());

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);
        setListHeader(listView);
        setListItemLongClick(listView);

        mDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(INTENT_LIST_ID, mListId);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setListHeader(ListView listView) {
        ViewGroup header = (ViewGroup) getLayoutInflater().inflate(
                R.layout.view_task_create, listView, false);

        final ImageView imageView = (ImageView) header.findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAttachImageDialog(null);
            }
        });

        final EditText text = (EditText) header.findViewById(R.id.text);
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String inputText = text.getText().toString();
                    if (inputText.length() > 0)
                        createTask(inputText, mImageToBeAttached, mListId);

                    text.setText("");
                    deleteCurrentPhoto();

                    return true;
                }
                return false;
            }
        });

        listView.addHeaderView(header);
    }

    private void setListItemLongClick(ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int pos, long id) {
                PopupMenu popup = new PopupMenu(TaskActivity.this, view);
                popup.inflate(R.menu.task_item);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Document task = (Document) mAdapter.getItem(pos - 1);
                        handleTaskPopupAction(item, task);
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });
    }

    private void handleTaskPopupAction(MenuItem item, Document task) {
        switch (item.getItemId()) {
            case R.id.update:
                updateTask(task);
                return;
            case R.id.delete:
                deleteTask(task);
                return;
        }
    }

    private Query getQuery() {
        LifeManagerApplication application = (LifeManagerApplication) getApplication();
        Query query = application.getToDoCB().getTasksView().createQuery();
        query.setDescending(true);

        List<Object> startKeys = new ArrayList<Object>();
        startKeys.add(mListId);
        startKeys.add(new HashMap<String, Object>());

        List<Object> endKeys = new ArrayList<Object>();
        endKeys.add(mListId);

        query.setStartKey(startKeys);
        query.setEndKey(endKeys);

        return query;
    }

    private void createTask(String title, Bitmap image, String listId) {
        String currentTimeString = mDateFormatter.format(new Date());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "task");
        properties.put("title", title);
        properties.put("checked", Boolean.FALSE);
        properties.put("created_at", currentTimeString);
        properties.put("list_id", listId);

        Document document = mDatabase.createDocument();
        UnsavedRevision revision = document.createRevision();
        revision.setUserProperties(properties);

        if (image != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            revision.setAttachment("image", "image/jpg", in);
        }

        try {
            revision.save();
        } catch (CouchbaseLiteException e) {
            Log.e(LifeManagerApplication.TAG, "Cannot create new task", e);
        }
    }

    private void updateTask(final Document task) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.title_dialog_update));

        final EditText input = new EditText(this);
        input.setMaxLines(1);
        input.setSingleLine(true);
        String text = (String) task.getProperty("title");
        input.setText(text);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String currentTimeString = mDateFormatter.format(new Date());

                Map<String, Object> updatedProperties = new HashMap<String, Object>();
                updatedProperties.putAll(task.getProperties());
                updatedProperties.put("title", input.getText().toString());
                updatedProperties.put("updated_at", currentTimeString);

                try {
                    task.putProperties(updatedProperties);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.show();
    }

    private void deleteTask(final Document task) {
        try {
            task.delete();
        } catch (CouchbaseLiteException e) {
            Log.e(LifeManagerApplication.TAG, "Cannot delete a task", e);
        }
    }

    private void attachImage(Document task, Bitmap image) {
        if (task == null || image == null) return;

        UnsavedRevision revision = task.createRevision();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        revision.setAttachment("image", "image/jpg", in);

        try {
            revision.save();
        } catch (CouchbaseLiteException e) {
            Log.e(LifeManagerApplication.TAG, "Cannot attach image", e);
        }
    }

    private void updateCheckedStatus(Document task, boolean checked) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.putAll(task.getProperties());
        properties.put("checked", checked);

        try {
            task.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(LifeManagerApplication.TAG, "Cannot update checked status", e);
        }
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(LifeManagerApplication.TAG, "Cannot create a temp image file", e);
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(TaskActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider", photoFile));
                if(checkPermission()) {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }else{
                    requestPermission();
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "TODO_LITE-" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        mImagePathToBeAttached = image.getAbsolutePath();
        return image;
    }

    private void dispatchChoosePhotoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CHOOSE_PHOTO);
    }

    private void deleteCurrentPhoto() {
        if (mImageToBeAttached != null) {
            mImageToBeAttached.recycle();
            mImageToBeAttached = null;
        }

        ViewGroup view = (ViewGroup) findViewById(R.id.create_task);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera));
    }

    private void displayAttachImageDialog(final Document task) {
        CharSequence[] items;
        if (mImageToBeAttached != null)
            items = new CharSequence[] { "Take photo", "Choose photo", "Delete photo" };
        else
            items = new CharSequence[] { "Take photo", "Choose photo" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    mCurrentTaskToAttachImage = task;
                    dispatchTakePhotoIntent();
                } else if (item == 1) {
                    mCurrentTaskToAttachImage = task;
                    dispatchChoosePhotoIntent();
                } else {
                    deleteCurrentPhoto();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            if (mCurrentTaskToAttachImage != null)
                mCurrentTaskToAttachImage = null;
            return;
        }

        final int size = THUMBNAIL_SIZE;
        Bitmap thumbnail = null;
        if (requestCode == REQUEST_TAKE_PHOTO) {
            File file = new File(mImagePathToBeAttached);
            if (file.exists()) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                options.inJustDecodeBounds = false;
                mImageToBeAttached = BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                if (mCurrentTaskToAttachImage == null) {
                    thumbnail = ThumbnailUtils.extractThumbnail(mImageToBeAttached, size, size);
                }

                // Delete the temporary image file
                file.delete();
            }
            mImagePathToBeAttached = null;
        } else if (requestCode == REQUEST_CHOOSE_PHOTO) {
            try {
                Uri uri = data.getData();
                ContentResolver resolver = getContentResolver();
                mImageToBeAttached = MediaStore.Images.Media.getBitmap(resolver, uri);
                if (mCurrentTaskToAttachImage == null) {
                    AssetFileDescriptor asset = resolver.openAssetFileDescriptor(uri, "r");
                    thumbnail = ImageUtil.thumbnailFromDescriptor(asset.getFileDescriptor(), size, size);
                }
            } catch (IOException e) {
                Log.e(LifeManagerApplication.TAG, "Cannot get a selected photo from the gallery.", e);
            }
        }

        if (mImageToBeAttached != null) {
            if (mCurrentTaskToAttachImage != null) {
                attachImage(mCurrentTaskToAttachImage, mImageToBeAttached);
                mImageToBeAttached = null;
            }
        }

        if (thumbnail != null) {
            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(thumbnail);
        }

        // Ensure resetting the task to attach an image
        if (mCurrentTaskToAttachImage != null)
            mCurrentTaskToAttachImage = null;
    }


    private class TaskAdapter extends LiveQueryAdapter {
        public TaskAdapter(Context context, LiveQuery query) {
            super(context, query);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_task, null);
            }

            final Document task = (Document) getItem(position);
            if (task == null || task.getCurrentRevision() == null) {
                return convertView;
            }

            Bitmap thumbnail = getTaskThumbnail(task);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            if (thumbnail != null)
                imageView.setImageBitmap(thumbnail);
            else
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_light));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (task.getCurrentRevision().getAttachment("image") != null) {
                        Intent intent = new Intent(TaskActivity.this, ImageActivity.class);
                        intent.putExtra(ImageActivity.INTENT_TASK_DOC_ID, task.getId());
                        startActivity(intent);
                    } else
                        displayAttachImageDialog(task);
                }
            });

            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText((String) task.getProperty("title"));

            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checked);
            Boolean checkedProperty = (Boolean) task.getProperty("checked");
            boolean checked = checkedProperty != null ? checkedProperty.booleanValue() : false;
            checkBox.setChecked(checked);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCheckedStatus(task, checkBox.isChecked());
                }
            });

            return convertView;
        }

        private Bitmap getTaskThumbnail(Document task) {
            List<Attachment> attachments = task.getCurrentRevision().getAttachments();
            if (attachments.size() == 0)
                return null;

            Bitmap bitmap = null;
            InputStream is = null;
            final int size = THUMBNAIL_SIZE;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                is = attachments.get(0).getContent();
                BitmapFactory.decodeStream(is, null, options);
                options.inSampleSize = ImageUtil.calculateInSampleSize(options, size, size);
                is.close();

                options.inJustDecodeBounds = false;
                is = task.getCurrentRevision().getAttachments().get(0).getContent();
                bitmap = BitmapFactory.decodeStream(is, null, options);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, size, size);
            } catch (Exception e) {
                Log.e(LifeManagerApplication.TAG, "Cannot decode the attached image", e);
            } finally {
                try { if (is != null) is.close(); } catch (IOException e) { }
            }
            return bitmap;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(TaskActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(TaskActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(TaskActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, RequestPermissionCode);
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                CAMERA);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}
