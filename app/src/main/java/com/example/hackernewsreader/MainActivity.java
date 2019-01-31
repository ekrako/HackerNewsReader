package com.example.hackernewsreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase newsDatabase;
    ArrayList<String> articleTitles;
    ArrayList<String> articleUrls;

    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsDatabase=this.openOrCreateDatabase("Articles",MODE_PRIVATE,null);
        newsDatabase.execSQL("CREATE TABLE IF NOT EXISTS articles (id VARCHAR, title VARCHAR,url VARCHAR)");
        newsDatabase.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_articles_id ON articles (id);");
        articleTitles= new ArrayList<>();
        articleUrls = new ArrayList<>();
        ListView articleLV=findViewById(R.id.articlesLV);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,articleTitles);
        articleLV.setAdapter(arrayAdapter);
        articleLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),showArticleActivity.class);
                intent.putExtra("url",articleUrls.get(position));
                //based on item add info to intent
                startActivity(intent);
            }
        });
        getNews();
    }
    public void getNews() {
        try {
            DownloadNewsTask task = new DownloadNewsTask();
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json");


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not find new :(",Toast.LENGTH_SHORT).show();
        }
    }
    public class DownloadNewsTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),"Could not find news :(",Toast.LENGTH_SHORT).show();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                Log.i("newsList", s);


                JSONArray arr = new JSONArray(s);

                for (int i=0;i<arr.length()&&i<50;i++){
                    String id = arr.getString(i);
                    getArticle(id);

                }
            } catch (Exception e) {

                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }

        }

    }
    public class DownloadArticleTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),"Could not find news :(",Toast.LENGTH_SHORT).show();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                Log.i("Article", s);


                JSONObject article = new JSONObject(s);
                String title = DatabaseUtils.sqlEscapeString(article.getString("title"));
                String url = DatabaseUtils.sqlEscapeString(article.getString("url"));
                String id = DatabaseUtils.sqlEscapeString(article.getString("id"));
                articleUrls.add(article.getString("url"));
                articleTitles.add(article.getString("title"));
                arrayAdapter.notifyDataSetChanged();
                Log.i("article Title",title);
                Log.i("article URL",url);
                Log.i("article id",id);
                newsDatabase.execSQL(String.format("INSERT INTO articles (id,title,url) VALUES (%s,%s,%s)",id,title,url));
//                for (int i=0;i<arr.length();i++){
//                    String id = arr.getString(i);
//                    getArticle(id);


            } catch (Exception e) {

                Toast.makeText(getApplicationContext(),"Could not find article :(",Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }

        }

    }
    public void getArticle(String id) {
        Cursor c= newsDatabase.rawQuery("SELECT * FROM articles where id = "+id,null);
        if (c.moveToFirst()) {
            int titleIndex = c.getColumnIndex("title");
            int urlIndex = c.getColumnIndex("url");
            articleUrls.add(c.getString(urlIndex));
            articleTitles.add(c.getString(titleIndex));
            arrayAdapter.notifyDataSetChanged();
            Log.i("article Title from DB", c.getString(titleIndex));
            Log.i("article URL from DB", c.getString(urlIndex));
        }else {
            try {
                String url = String.format("https://hacker-news.firebaseio.com/v0/item/%s.json", id);
                Log.i("article URL", url);
                DownloadArticleTask task = new DownloadArticleTask();
                task.execute(url);


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find new :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

