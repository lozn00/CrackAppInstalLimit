package com.coocaa.guard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseAppActivity extends Activity {

    private static final String TAG = "ChooseApp";
    private ListView listview;
    MySimpleAdapter myAdapter;
    HashMap<String,Boolean> packagesWrite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_app);
        listview = ((ListView) findViewById(R.id.listview));
        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        //用三个数组装载数据

        List<ApplicationInfo> packageinfo = this.getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的


        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFERENCE_NAME, Build.VERSION.SDK_INT>=24?Context.MODE_PRIVATE:Context.MODE_WORLD_READABLE);
        JSONArray array = JSON.parseArray(sharedPreferences.getString("installer_whitelist", "[]"));
       packagesWrite=new HashMap<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject=array.getJSONObject(i);
            String packageName = jsonObject.getString("packageName");
            packagesWrite.put(packageName,true);
        }

        for (ApplicationInfo info : packageinfo) {
            Map<String, Object> showitem = new HashMap<String, Object>();

            int labelRes = info.labelRes;

            String appName= this.getPackageManager().getApplicationLabel(info).toString();  //
            String packageName = info.packageName;
            Drawable drawable = info.loadIcon(getPackageManager());
            Boolean aBoolean = packagesWrite.get(packageName);
            if(aBoolean!=null&&aBoolean){
                showitem.put("app_name", appName+"[白名单]");

            }else{
                showitem.put("app_name", appName+"");
            }

            showitem.put("position", listitem.size());
            showitem.put("image", drawable);

            showitem.put("packagename", packageName);
            listitem.add(showitem);
        }

             myAdapter = new MySimpleAdapter(getApplicationContext(), listitem,
                    R.layout.list_item, new String[]{"position", "app_name", "packagename"},
                    new int[]{R.id.iv, R.id.app_name, R.id.packagename});
        listview.setAdapter(myAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap item = (HashMap) myAdapter.getItem(position);
                String str= (String) item.get("packagename");
                Log.w(TAG,"CHOOSE PACKAGENAME"+str+","+item);
                Intent i = new Intent();
                i.putExtra("packagename", str);
                setResult(RESULT_OK,i);
                finish();

            }
        });


    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        finish();

    }
}
