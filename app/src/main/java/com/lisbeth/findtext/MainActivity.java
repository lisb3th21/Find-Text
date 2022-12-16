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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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


            textView.setText(words);
        }


    }

    public String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        int len = 50000;

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

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);

        return new String(buffer);
    }

}
