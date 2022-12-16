package com.lisbeth.findtext;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
        private static final String DEBUG_TAG = "HttpExample";
        private TextInputEditText urlText;
        private TextView textView;
        private TextInputEditText textFind;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            this.urlText = (TextInputEditText) findViewById(R.id.link_input);
            this.textView = (TextView) findViewById(R.id.text_view);
            this.textFind = (TextInputEditText) findViewById(R.id.text_input);
        }

        public void myClickHandler(View view) {
            String stringUrl = urlText.getText().toString();
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new DownloadWebpageText().execute(stringUrl);
            } else {
                textView.setText("No network connection available.");
            }
        }

    public void myButton(View view)  {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageText().execute(Objects.requireNonNull(urlText.getText()).toString());
        } else {
            Toast.makeText(this, "no se puede", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadWebpageText extends AsyncTask<Object, Object, String> {

        @Override
        protected String doInBackground(Object[] urls) {

            try {
                return downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            String words = result;
            String text= textFind.getText().toString();

            textView.setText(words);
        }


    }

    public String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        int len = 1900000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HttpExample", "The response is: " + response);
            is = conn.getInputStream();

            String contentAsString = readIt(is, len);
            return contentAsString;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    public  String getSentence(String text, String word) {
        String sentence = "";
        if (text.toLowerCase().contains(word)) {
            if (text.contains(".")) {  //Are there sentences terminating in a period?
                int loc = text.toLowerCase().indexOf(word);
                int a = loc;
                while (a >= 0) {
                    if (text.charAt(a) == '.' || a == 0) {
                        sentence = text.substring(a,loc);
                        a = 0;
                    }
                    a--;
                }
                a = loc + word.length();
                while (a <= text.length()) {
                    if (text.charAt(a) == '.' || a == text.length()) {
                        sentence += text.substring(loc,a+1);
                        a = text.length()+1;
                    }
                    a++;
                }
                return sentence;
            } else {
                return text;      //If no period, return full text
            }
        } else {
            return null;
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (stream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        String result =textBuilder.toString().replaceAll("\\<.*?\\>", "");
    return getSentence(result, textFind.getText().toString()).replaceAll("\n+", "");

    }

}
