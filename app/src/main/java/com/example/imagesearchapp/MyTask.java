package com.example.imagesearchapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyTask extends AsyncTask<String, Integer, Bitmap> {

    private ImageView imageView;

    /**
     * コンストラクタ
     */
    public MyTask(ImageView imageView) {
        super();
        this.imageView = imageView;
    }

    /**
     * バックグランドで行う処理
     */
    @Override
    protected Bitmap doInBackground(String... value) {
        Bitmap bitmap = null;
        try {
            bitmap = getHttpImage(value[0],value[1]);
        } catch (Exception e) {
            Log.e(this.getClass().getName(),e.getMessage());
        }
        return bitmap;
    }

    /**
     * バックグランド処理が完了し、UIスレッドに反映する
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }


    /**
     * 画像取得
     * @return bitmap
     */
    public Bitmap getHttpImage(String apiKey, String searchWord) {
        // サーバーに保存
        String imgURL = null;
        Bitmap bitmap = null;
        try {

            // Flickr接続用。APIのkeyとか検索タグとか。下のAPI keyは適当です。
            String API_KEY = "api_key=" + apiKey;
            String API_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
            String API_TAG = "tags=" + searchWord;
            String PER_PAGE = "per_page=1";

            // APIに接続
            URL url = new URL(API_URL + "&" + API_KEY + "&" + API_TAG + "&" + PER_PAGE);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();

            // 結果を受信
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            // XMLをパース
            final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            final XmlPullParser parser = factory.newPullParser();
            parser.setInput(reader);
            for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser.next()) {
                if (type == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if (tagName.equals("photo")) {
                        String id = parser.getAttributeValue(0);
                        String secret = parser.getAttributeValue(2);
                        String server = parser.getAttributeValue(3);
                        String farm = parser.getAttributeValue(4);
                        imgURL = "http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg";
                        break;
                    }
                }
            }

            // close
            reader.close();
            in.close();
            conn.disconnect();

            // 画像の読み込み
            if (imgURL != null) {
                url = new URL(imgURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                in = new BufferedInputStream(conn.getInputStream());

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] w = new byte[1024];
                while (true) {
                    int ss = in.read(w);
                    if (ss <= 0) break;
                    bos.write(w, 0, ss);
                }
                ;
                bitmap = BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size());
                in.close();
                bos.close();
                conn.disconnect();

            }
        } catch (Exception e) {
            Log.d("Program Error", e.getClass().getName());
        }
        return bitmap;
    }

}