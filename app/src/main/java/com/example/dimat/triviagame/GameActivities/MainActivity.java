package com.example.dimat.triviagame.GameActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.dimat.triviagame.classPackage.HttpUtils;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import android.app.ProgressDialog;

public class MainActivity extends AppCompatActivity {
    ProgressDialog loading = null;
    MainActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onTestStart(View view) {
        RequestParams rp = new RequestParams();
        final EditText username = findViewById(R.id.editUsername);
        // Set parameter of request
        rp.add("username", username.getText().toString());
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.login_prog));
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();

        // HttpUtils send html request to tomcat server
        // All activities has this method to call request
        HttpUtils.post("login", rp, new JsonHttpResponseHandler() {
            // Get Success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                loading.dismiss();

                try {
                    if (response.getBoolean("Response")) {
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        intent.putExtra("username", username.getText().toString());
                        startActivity(intent);
                    } else {
                        int error_id = R.string.err_unknown;
                        String err_str = response.getString("Error");
                        if (err_str.equals("err_noname"))
                            error_id = R.string.err_noname;
                        else if (err_str.equals("err_getname"))
                            error_id = R.string.err_getname;
                        else if (err_str.equals("err_newname"))
                            error_id = R.string.err_newname;
                        else if (err_str.equals("err_usename"))
                            error_id = R.string.err_usename;
                        else if (err_str.equals("err_dupname"))
                            error_id = R.string.err_dupname;

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

            // Get Failed
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

    @Override
    public void onBackPressed() {
    }

}
