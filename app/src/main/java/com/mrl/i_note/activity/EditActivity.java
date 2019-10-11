package com.mrl.i_note.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mrl.i_note.R;
import com.mrl.i_note.constants.Constants;
import com.mrl.i_note.model.Knowledge;
import com.mrl.i_note.service.KnowledgeService;
import com.mrl.i_note.service.serviceImpl.KnowledgeServiceImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by apple on 2018/5/30.
 */

public class EditActivity extends AppCompatActivity {

    private Knowledge knowledge;
    private int dayNightTheme;
    private EditText title;
    private EditText content;
    private RadioGroup important;
    private RadioGroup difficult;
    private Button save;
    private Button cancel;
    private KnowledgeService knowledgeService;

    /* 图片选择组件 */
    private static final int REQUEST_IMAGE = 2;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;

    private Button selectImg;
    private TextView mResultText;
    private ArrayList<String> mSelectPath;

    private final boolean SHOW_CAMERA = true;
    private final int MAX_IMG_NUM = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent()!=null){
            Bundle bundle = getIntent().getExtras();
            knowledge = (Knowledge) bundle.getSerializable("knowledge");
            if(knowledge==null){
                knowledge = new Knowledge();
            }
        }
        dayNightTheme = MainActivity.getMainActivity().getDayNightTheme();
        getDelegate().setLocalNightMode(dayNightTheme);
        setContentView(R.layout.edit_dialog);
        knowledgeService = new KnowledgeServiceImpl(this);
        initView();
    }

    private void initView(){
        title = findViewById(R.id.k_e_title);
        content = findViewById(R.id.k_e_content);
        important = findViewById(R.id.k_e_important);
        difficult = findViewById(R.id.k_e_difficult);

        mResultText = findViewById(R.id.img_result);
        selectImg = findViewById(R.id.img_select);

        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        title.setText(knowledge.getTitle());
        content.setText(knowledge.getContent());
        if(dayNightTheme == Constants.THEME_DAY){
            title.setTextColor(Color.parseColor("#787878"));
            title.setBackgroundResource(R.drawable.edittext_selector_day);
            content.setTextColor(Color.parseColor("#787878"));
            content.setBackgroundResource(R.drawable.edittext_selector_day);

            save.setBackgroundResource(R.drawable.rounded_rectangle_day);
            cancel.setBackgroundResource(R.drawable.rounded_rectangle_day);
            selectImg.setBackgroundResource(R.drawable.rounded_rectangle_day);
            save.setTextColor(Color.parseColor("#787878"));
            cancel.setTextColor(Color.parseColor("#787878"));
            selectImg.setTextColor(Color.parseColor("#787878"));
        }else{
            title.setTextColor(Color.parseColor("#e9e9e9"));
            title.setBackgroundResource(R.drawable.edittext_selector_night);
            content.setTextColor(Color.parseColor("#e9e9e9"));
            content.setBackgroundResource(R.drawable.edittext_selector_night);

            save.setBackgroundResource(R.drawable.rounded_rectangle_night);
            cancel.setBackgroundResource(R.drawable.rounded_rectangle_night);
            selectImg.setBackgroundResource(R.drawable.rounded_rectangle_night);
            save.setTextColor(Color.parseColor("#e9e9e9"));
            cancel.setTextColor(Color.parseColor("#e9e9e9"));
            selectImg.setTextColor(Color.parseColor("#e9e9e9"));
        }

        View.OnClickListener mClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.save:
                        knowledge.setTitle(title.getText().toString());
                        for(int i=0;i<important.getChildCount();i++){
                            RadioButton radioButton = (RadioButton)important.getChildAt(i);
                            if(radioButton.isChecked()){
                                knowledge.setImportant(Integer.parseInt(radioButton.getText().toString()));
                                break;
                            }
                        }
                        for(int i=0;i<difficult.getChildCount();i++){
                            RadioButton radioButton = (RadioButton)difficult.getChildAt(i);
                            if(radioButton.isChecked()){
                                knowledge.setDifficult(Integer.parseInt(radioButton.getText().toString()));
                                break;
                            }
                        }
                        knowledge.setContent(content.getText().toString());
                        knowledge.setUpdateDate(new Date());
                        String message = "";
                        int result = knowledgeService.updateKnowledge(knowledge);
                        if(result>0){
                            message = "更新成功";
                        }else{
                            message = "更新失败";
                        }
                        Toast.makeText(EditActivity.this, message, Toast.LENGTH_SHORT).show();
                        break;
                    case  R.id.cancel:
                        break;
                    default:
                        break;
                }
                MainActivity.getMainActivity().setBefforAction(MainActivity.AFTER_EDIT);
                finish();
            }
        };
        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        save.setOnClickListener(mClickListener);
        cancel.setOnClickListener(mClickListener);
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        }else {
            boolean showCamera = SHOW_CAMERA;
            int maxNum = MAX_IMG_NUM;

            MultiImageSelector selector = MultiImageSelector.create(EditActivity.this);
            selector.showCamera(showCamera);
            selector.count(maxNum);
            //selector.multi();
            selector.single();
            selector.origin(mSelectPath);
            selector.start(EditActivity.this, REQUEST_IMAGE);
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(EditActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickImage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                for(String p: mSelectPath){
                    if(p!= null&&!"".equals(p)){
                        sb.append(p);
                        break;
                    }
                }
                mResultText.setText(sb.toString());

                if (mResultText == null) {
                    Toast.makeText(EditActivity.this, "请先选择图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                File file = new File(mSelectPath.get(0));
                if(file.length()>(1024*1024*10)){
                    Toast.makeText(EditActivity.this, "文件过大(max：10M)", Toast.LENGTH_SHORT).show();
                    return;
                }
                knowledge.setImgUrl(sb.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_img_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
