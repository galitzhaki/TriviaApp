package com.example.dimat.triviagame.GameActivities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.dimat.triviagame.classPackage.HttpUtils;
import com.example.dimat.triviagame.classPackage.Question;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import cz.msebera.android.httpclient.Header;

public class GameActivity extends AppCompatActivity {
    ProgressDialog loading = null;
    GameActivity self = this;
    String username = "";
    int score = 0;
    int count = 0;
    int time = 30;
    Question newq = null;
    Runnable updater;
    Handler timerHandler = null;
    RadioButton radioanswer1 = null;
    RadioButton radioanswer2 = null;
    RadioButton radioanswer3 = null;
    RadioButton radioanswer4 = null;
    ImageView imageView = null;
    ObjectAnimator colorAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        radioanswer1 = findViewById(R.id.radioanswer1);
        radioanswer2 = findViewById(R.id.radioanswer2);
        radioanswer3 = findViewById(R.id.radioanswer3);
        radioanswer4 = findViewById(R.id.radioanswer4);

        imageView = findViewById(R.id.imageView);

        TextView textView = findViewById(R.id.txtquestion);
        colorAnim = ObjectAnimator.ofInt(textView, "textColor",
                Color.RED, Color.GREEN, Color.BLUE, Color.BLACK, Color.WHITE, Color.YELLOW);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();
        colorAnim.setDuration(10000);

        onNext(null);
    }

    public void onGoHome(View view) {
        RequestParams rp = new RequestParams();
        rp.add("username", username);
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.gotohome));
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();


        HttpUtils.post("unuseuser", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                loading.dismiss();

                try {
                    if (response.getBoolean("Response")) {
                        startActivity(new Intent(self, MainActivity.class));
                    } else {
                        int error_id = R.string.err_unknown;
                        String err_str = response.getString("Error");

                        if (err_str.equals("err_noname"))
                            error_id = R.string.err_noname;
                        else if (err_str.equals("err_getname"))
                            error_id = R.string.err_getname;
                        else if (err_str.equals("err_unusename"))
                            error_id = R.string.err_unusename;

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

    public void onNext(View view) {
        if (newq != null) {
            new AlertDialog.Builder(self)
                    .setTitle("Correct Answer")
                    .setMessage("Correct answer : " + newq.correct)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doNext();
                        }
                    }).show();
        } else
            doNext();
    }

    private void doNext() {

        if (timerHandler != null) {
            timerHandler.removeCallbacks(updater);
            timerHandler = null;
        }

        if (newq != null) {
            boolean answer1 = radioanswer1.isChecked();
            boolean answer2 = radioanswer2.isChecked();
            boolean answer3 = radioanswer3.isChecked();
            boolean answer4 = radioanswer4.isChecked();

            if (answer1 && newq.correct == 1)
                score += 1;
            else if (answer2 && newq.correct == 2)
                score += 1;
            else if (answer3 && newq.correct == 3)
                score += 1;
            else if (answer4 && newq.correct == 4)
                score += 1;

            newq = null;
            count++;
        }

        if (count == 20) {
            Intent intent = new Intent(self, HighScore.class);
            intent.putExtra("score", score);
            intent.putExtra("username", username);
            startActivity(intent);
        } else {
            RequestParams rp = new RequestParams();
            rp.add("count", "" + count);
            loading = new ProgressDialog(this);
            loading.setCancelable(false);
            loading.setMessage(getString(R.string.getting_problem));
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.show();

            HttpUtils.post("getproblem", rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    loading.dismiss();

                    try {
                        if (response.getBoolean("Response")) {
                            time = 30;
                            updateTime();

                            JSONObject jsonq = response.getJSONObject("problem");
                            newq = new Question(jsonq.getInt("id"),
                                    jsonq.getString("question"),
                                    jsonq.getString("answer1"),
                                    jsonq.getString("answer2"),
                                    jsonq.getString("answer3"),
                                    jsonq.getString("answer4"),
                                    jsonq.getString("imgurl"),
                                    jsonq.getInt("correct"));

                            ((TextView)findViewById(R.id.txtquestion)).setText((count + 1) + ". " + newq.problem);
                            radioanswer1.setText(newq.answer1);
                            radioanswer2.setText(newq.answer2);
                            radioanswer3.setText(newq.answer3);
                            radioanswer4.setText(newq.answer4);

                            GetXMLTask task = new GetXMLTask();
                            task.execute(new String[] { newq.imgurl });

                            colorAnim.start();
                        } else {
                            int error_id = R.string.err_unknown;
                            String err_str = response.getString("Error");
                            if (err_str.equals("err_getproblem"))
                                error_id = R.string.err_getproblem;

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
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStop() {
        RequestParams rp = new RequestParams();
        rp.add("username", username);
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.gotohome));
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();


        HttpUtils.post("unuseuser", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                loading.dismiss();
            }
        });

        finish();
        super.onStop();
    }

    void updateTime() {
        final TextView txttimeleft = findViewById(R.id.txttimeleft);
        timerHandler = new Handler();

        updater = new Runnable() {
            @Override
            public void run() {
                txttimeleft.setText(time + (" " + getString(R.string.seconds) + " "));
                if (time == 0) {
                    timerHandler.removeCallbacks(updater);
                    timerHandler = null;
                    onNext(null);
                } else {
                    time--;
                    timerHandler.postDelayed(updater, 1000);
                }
            }
        };
        timerHandler.post(updater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerHandler != null)
            timerHandler.removeCallbacks(updater);
    }

    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }
}
