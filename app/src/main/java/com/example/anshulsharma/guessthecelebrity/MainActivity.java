package com.example.anshulsharma.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    ImageView actors;
    Bitmap myImage;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    DownloadImage task;
    DownloadTask html;
    ArrayList<String> celebsUrls=new ArrayList<>();
    ArrayList<String> celebsNames=new ArrayList<>();
    int chosenCeleb=0;
    int loactionOfCorrectAnswer;
    String[] answers=new String[4];

    public class DownloadImage extends AsyncTask<String,Void ,Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {


            URL url;
            HttpURLConnection urlConnection;
            try{

                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream in=urlConnection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(in);
                return  myBitmap;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void ,String> {


        @Override
        protected String doInBackground(String... urls) {


            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{

                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while (data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void checkAnswer(View view){

    if(view.getTag().toString().equals(Integer.toString(loactionOfCorrectAnswer))){
        Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();

    }else{
        Toast.makeText(this, "Wrong! It was"+celebsNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
    }

     genrateQuestion();

    }

    public void genrateQuestion(){

        Random random=new Random();
        chosenCeleb=random.nextInt(celebsUrls.size());
        try {
            myImage = task.execute(celebsUrls.get(chosenCeleb)).get();
            actors.setImageBitmap(myImage);
            loactionOfCorrectAnswer=random.nextInt(4);
            int n1;
            for(int i=0;i<4;i++){
                if(i==loactionOfCorrectAnswer)
                    answers[i]=celebsNames.get(chosenCeleb);
                else {
                    n1=random.nextInt(celebsUrls.size());
                    answers[i]=celebsNames.get(n1);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actors=findViewById(R.id.celebrityImage);
        button0=findViewById(R.id.button0);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);

        task=new DownloadImage();
        html=new DownloadTask();
        String result=null;
        try {
            result=html.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult=result.split("<div class=\"sidebarContainer\">");
            Pattern p=Pattern.compile("<img src\"(.*?)\"");
            Matcher m=p.matcher(splitResult[0]);
            while (m.find()){
                celebsUrls.add(m.group(1));
            }
            p=Pattern.compile("alt=\"(.*?)\"");
            m=p.matcher(splitResult[0]);
            while (m.find()){
                celebsNames.add(m.group(1));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        genrateQuestion();
    }
}
