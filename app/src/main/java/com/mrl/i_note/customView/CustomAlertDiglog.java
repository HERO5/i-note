package com.mrl.i_note.customView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mrl.i_note.R;
import com.mrl.i_note.activity.MainActivity;
import com.mrl.i_note.constants.Constants;
import com.mrl.i_note.model.Knowledge;
import com.mrl.i_note.service.KnowledgeService;
import com.mrl.i_note.utils.ImgLoad;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 2018/5/29.
 */

public class CustomAlertDiglog {

    public static final int DETIAL = 0;
    public static final int DELETE = 1;
    public static final int DOWNLOAD = -1;
    public static final int THEME = 2;
    public static final int CREATEUSER = 3;
    public static final int UPLOAD = -3;

    public static void initDetialDialog(Context context, Knowledge knowledge, boolean isWebImg) {
        //dialog 不能用Application的Context
        AlertDialog menuDialog = new AlertDialog.Builder(context, R.style.menu_dialog).create();
        menuDialog.show();
        Window window = menuDialog.getWindow();
        window.setContentView(R.layout.detial_dialog);
        TextView title = window.findViewById(R.id.k_d_title);
        TextView content = window.findViewById(R.id.k_d_content);
        TextView important = window.findViewById(R.id.k_d_important);
        TextView difficult = window.findViewById(R.id.k_d_difficult);
        TextView viewTime = window.findViewById(R.id.k_d_view_time);
        TextView createDate = window.findViewById(R.id.k_d_create_date);
        ResizableImageView image = window.findViewById(R.id.k_d_img);
        title.setText(knowledge.getTitle());
        content.setText(knowledge.getContent());
        important.setText(String.valueOf(knowledge.getImportant()));
        difficult.setText(String.valueOf(knowledge.getDifficult()));
        viewTime.setText(String.valueOf(knowledge.getViewTime()));
        createDate.setText(knowledge.getCreateDate().toString());
        if(knowledge.getImgUrl()!=null&&!"".equals(knowledge.getImgUrl())){
            if(isWebImg||knowledge.getImgUrl().contains("/uploads/uploadImg/")){
                List<ImageView> imgs = new ArrayList<ImageView>();
                imgs.add(image);
                ImgLoad.loadImgArray(imgs, new String[]{Constants.SERVER_HOST+knowledge.getImgUrl()});
            }else {
                Uri uri = Uri.fromFile(new File(knowledge.getImgUrl()));
                image.setImageURI(uri);
            }
        }
//        WindowManager.LayoutParams params = window.getAttributes();
//        params = dialogParamsSetting(params);
//        window.setAttributes(params);//此句代码一定要放在show()后面，否则不起作用
        menuDialog.setCanceledOnTouchOutside(true);
    }

    public static void initExecDialog(Context context, final List<Knowledge> currentKnowledge, final KnowledgeService knowledgeService, int dayNightTheme, final int type){
        final AlertDialog menuDialog = new AlertDialog.Builder(context, R.style.menu_dialog).create();
        menuDialog.show();
        Window window = menuDialog.getWindow();
        window.setContentView(R.layout.two_button_dialog);
        Button delete = window.findViewById(R.id.button1);
        Button cancel = window.findViewById(R.id.button2);
        delete.setText("执行");
        cancel.setText("取消");
        if(dayNightTheme == Constants.THEME_DAY){
            delete.setBackgroundResource(R.drawable.rounded_rectangle_day);
            cancel.setBackgroundResource(R.drawable.rounded_rectangle_day);
            delete.setTextColor(Color.parseColor("#787878"));
            cancel.setTextColor(Color.parseColor("#787878"));
        }else{
            delete.setBackgroundResource(R.drawable.rounded_rectangle_night);
            cancel.setBackgroundResource(R.drawable.rounded_rectangle_night);
            delete.setTextColor(Color.parseColor("#e9e9e9"));
            cancel.setTextColor(Color.parseColor("#e9e9e9"));
        }

        View.OnClickListener mClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.button1:
                        boolean result = false;
                        switch (type){
                            case DELETE:
                                result = (currentKnowledge!=null)&&(knowledgeService.deleteKnowledgeById(currentKnowledge.get(0).getId())>=0);
                                break;
                            case DOWNLOAD:
                                result = (currentKnowledge!=null)&&(knowledgeService.initBaseData(currentKnowledge)>=0);
                                break;
                            default:
                                break;
                        }
                        if(result){
                            Toast.makeText(MainActivity.getMainActivity(), "操作成功", Toast.LENGTH_SHORT).show();
                            MainActivity.getMainActivity().reflash(null, MainActivity.AFTER_OTHER);
                        }else{
                            Toast.makeText(MainActivity.getMainActivity(), "操作失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case  R.id.button2:
                        break;
                    default:
                        break;
                }
                menuDialog.dismiss();
            }
        };
        delete.setOnClickListener(mClickListener);
        cancel.setOnClickListener(mClickListener);
        menuDialog.setCanceledOnTouchOutside(true);
    }

    public static <T> void initChangeThemeDialog(final Activity activity, final Class<T> cls, int dayNightTheme){
        final AlertDialog menuDialog = new AlertDialog.Builder(activity, R.style.menu_dialog).create();
        menuDialog.show();
        Window window = menuDialog.getWindow();
        window.setContentView(R.layout.two_button_dialog);
        Button day = window.findViewById(R.id.button1);
        Button night = window.findViewById(R.id.button2);
        day.setText("日间模式");
        night.setText("夜间模式");
        if(dayNightTheme == Constants.THEME_DAY){
            day.setBackgroundResource(R.drawable.rounded_rectangle_day);
            night.setBackgroundResource(R.drawable.rounded_rectangle_day);
            day.setTextColor(Color.parseColor("#787878"));
            night.setTextColor(Color.parseColor("#787878"));
        }else{
            day.setBackgroundResource(R.drawable.rounded_rectangle_night);
            night.setBackgroundResource(R.drawable.rounded_rectangle_night);
            day.setTextColor(Color.parseColor("#e9e9e9"));
            night.setTextColor(Color.parseColor("#e9e9e9"));
        }

        View.OnClickListener mClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, cls);
                switch (view.getId()){
                    case R.id.button1:
                        intent.putExtra(Constants.THEMENAME, Constants.THEME_DAY);
                        break;
                    case  R.id.button2:
                        intent.putExtra(Constants.THEMENAME, Constants.THEME_NIGHT);
                        break;
                    default:
                        break;
                }
                menuDialog.dismiss();
                activity.finish();
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
        day.setOnClickListener(mClickListener);
        night.setOnClickListener(mClickListener);
        menuDialog.setCanceledOnTouchOutside(true);
    }

    public static void initUserInfoInputDialog(Context context, int dayNightTheme, final int type, String msg, final DataCallback dataCallback){
        final AlertDialog menuDialog = new AlertDialog.Builder(context, R.style.menu_dialog).create();
        menuDialog.show();
        Window window = menuDialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.user_info_input_dialog);
        final EditText userName = window.findViewById(R.id.user_name);
        final EditText userPassword = window.findViewById(R.id.user_password);
        TextView message = window.findViewById(R.id.message);
        Button upload = window.findViewById(R.id.exec);
        Button cancel = window.findViewById(R.id.cancel);
        message.setText(msg);
        if(dayNightTheme == Constants.THEME_DAY){
            userName.setTextColor(Color.parseColor("#787878"));
            userName.setBackgroundResource(R.drawable.edittext_selector_day);
            userPassword.setTextColor(Color.parseColor("#787878"));
            userPassword.setBackgroundResource(R.drawable.edittext_selector_day);

            upload.setBackgroundResource(R.drawable.rounded_rectangle_day);
            cancel.setBackgroundResource(R.drawable.rounded_rectangle_day);
            upload.setTextColor(Color.parseColor("#787878"));
            cancel.setTextColor(Color.parseColor("#787878"));
        }else{
            userName.setTextColor(Color.parseColor("#e9e9e9"));
            userName.setBackgroundResource(R.drawable.edittext_selector_night);
            userPassword.setTextColor(Color.parseColor("#e9e9e9"));
            userPassword.setBackgroundResource(R.drawable.edittext_selector_night);

            upload.setBackgroundResource(R.drawable.rounded_rectangle_night);
            cancel.setBackgroundResource(R.drawable.rounded_rectangle_night);
            upload.setTextColor(Color.parseColor("#e9e9e9"));
            cancel.setTextColor(Color.parseColor("#e9e9e9"));
        }

        View.OnClickListener mClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.exec:
                        String name = userName.getText().toString();
                        String password = userPassword.getText().toString();
                        Map<String,Object> datas = new HashMap<String,Object>();
                        datas.put("userName", name);
                        datas.put("userPassword",password);
                        switch (type){
                            case UPLOAD:
                                dataCallback.callback(datas);
                                menuDialog.dismiss();
                                break;
                            case CREATEUSER:
                                if(name!=null&&!"".equals(name)&&password!=null&&!"".equals(password)){
                                    dataCallback.callback(datas);
                                    menuDialog.dismiss();
                                }else {
                                    Toast.makeText(MainActivity.getMainActivity(), "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case  R.id.cancel:
                        menuDialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
        upload.setOnClickListener(mClickListener);
        cancel.setOnClickListener(mClickListener);
        menuDialog.setCanceledOnTouchOutside(true);
    }

    public static interface DataCallback{
        public void callback(Map<String,Object> datas);
    }

    protected static WindowManager.LayoutParams dialogParamsSetting(WindowManager.LayoutParams params){
        return params;
    }

}
