package com.example.crimson30.cardczar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserStartActivity extends Activity {
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_start);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_start, menu);
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

    public void intentToWaitingRoom(View view) {

        EditText roomJoinEditText = (EditText) findViewById(R.id.roomJoinEditText);
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        TextView roomStatusTextView = (TextView) findViewById(R.id.roomStatusTextView);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_join.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("room",roomJoinEditText.getText().toString()));
            urlParameters.add(new BasicNameValuePair("user",usernameEditText.getText().toString()));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            Log.d("Result of join request", result);
        } catch (IOException e) { e.printStackTrace(); }

        if (Objects.equals(result, "OK")) {
            Intent roomIntent = new Intent(this, WaitingRoomActivity.class);
            Bundle extras = new Bundle();
            extras.putString("roomname", roomJoinEditText.getText().toString());
            extras.putString("username",usernameEditText.getText().toString());
            roomIntent.putExtras(extras);
            startActivity(roomIntent);
        } else {
            roomStatusTextView.setText(result);
        }


    }

}
