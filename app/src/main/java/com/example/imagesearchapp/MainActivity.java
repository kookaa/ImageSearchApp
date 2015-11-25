package com.example.imagesearchapp;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button button;
    private MyTask task;
    private String apikey = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApplicationInfo appliInfo = null;
        try {
            appliInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().getName(),e.getMessage());
        }
        apikey = appliInfo.metaData.getString("img_search_api_key");


        //main.xmlに設定したコンポーネントをid指定で取得します。
        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button);

        // タスクの生成
        task = new MyTask(imageView);

        //buttonがクリックされた時の処理を登録します。
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button)v).setEnabled(false);
                // 非同期処理を開始する
                String[] options = {apikey,"bird"};
                task.execute(options);
            }
        });
    }
}
