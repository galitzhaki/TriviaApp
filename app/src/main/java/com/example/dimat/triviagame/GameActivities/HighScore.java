package com.example.dimat.triviagame.GameActivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dimat.triviagame.classPackage.CustomAdapter;
import com.example.dimat.triviagame.classPackage.HttpUtils;
import com.example.dimat.triviagame.classPackage.Score;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HighScore extends AppCompatActivity {
    ProgressDialog loading = null;
    HighScore self = this;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        Intent intent = getIntent();
        int score = intent.getIntExtra("score", 0);
        username = intent.getStringExtra("username");
        TextView scoreview = findViewById(R.id.yourscore);
        String yourscore = getString(R.string.yourscore) + score;
        scoreview.setText(yourscore);

        RequestParams rp = new RequestParams();
        rp.add("score", "" + score);
        rp.add("username", username);
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.getting_problem));
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();

        HttpUtils.post("finishexam", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                loading.dismiss();

                try {
                    if (response.getBoolean("Response")) {
                        JSONArray scores = response.getJSONArray("scores");

                        // Get ListView Control
                        ListView scorelist = findViewById(R.id.scoreList);
                        // Arraylist of Score
                        List<Score> scoreData = new ArrayList<Score>();
                        for (int i = 0; i < scores.length() - 1; i++) {
                            JSONObject obj = scores.getJSONObject(i);
                            if (obj != null) {
                                // Insert id, username and score to scorelist
                                scoreData.add(new Score(i + 1, obj.getString("username"), obj.getInt("score")));
                            }
                        }
                        // Create Listview adapter with score data
                        CustomAdapter adapter = new CustomAdapter(self, scoreData);
                        // Set listview's adapter
                        scorelist.setAdapter(adapter);
                    } else {
                        int error_id = R.string.err_unknown;
                        String err_str = response.getString("Error");
                        if (err_str.equals("err_getscores"))
                            error_id = R.string.err_getscores;
                        else if (err_str.equals("err_noname"))
                            error_id = R.string.err_getscores;

                        new AlertDialog.Builder(self)
                                .setTitle(getString(R.string.alerttitle))
                                .setMessage(getString(error_id))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                } catch (Exception e) {
                    new AlertDialog.Builder(self)
                            .setTitle(getString(R.string.alerttitle))
                            .setMessage(getString(R.string.err_parseresponse))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loading.dismiss();
                new AlertDialog.Builder(self)
                        .setTitle(getString(R.string.alerttitle))
                        .setMessage(getString(R.string.err_parseresponse))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loading.dismiss();
                new AlertDialog.Builder(self)
                        .setTitle(getString(R.string.alerttitle))
                        .setMessage(getString(R.string.err_parseresponse))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loading.dismiss();
                new AlertDialog.Builder(self)
                        .setTitle(getString(R.string.alerttitle))
                        .setMessage(getString(R.string.err_parseresponse))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    public void onGoHome(View view) {
        startActivity(new Intent(self, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
    }
}
