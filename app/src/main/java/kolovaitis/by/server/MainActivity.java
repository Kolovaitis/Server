package kolovaitis.by.server;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.UnsavedRevision;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static TextView textView;
    public static Context context;
    public static String id;
    private static final int REQUEST = 1;
    private ImageView image;
    public static String atId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        atId = "";

        setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View plus = (View) findViewById(R.id.button4);
        plus.setBackgroundResource(R.drawable.plus);

        context = this;
        textView = (TextView) findViewById(R.id.textView2);
        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isConnected()) {
                        findViewById(R.id.textView).setBackgroundColor(Color.GREEN);
                    } else {
                        findViewById(R.id.textView).setBackgroundColor(Color.RED);
                        Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_LONG).show();


                    }
                } catch (Exception e) {
                }
                textView.setTextColor(Color.BLACK);
                id = ((EditText) findViewById(R.id.editText)).getText() + "";
                final RequestQueue queue = Volley.newRequestQueue(context);
                String url = "http://46.101.205.23:4444/test_db/" + MainActivity.id;

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Auto-generated method stub
                        id = ((EditText) findViewById(R.id.editText)).getText() + "";
                        if (response.toString().contains("_attachments")) {
                            String atVal = "";
                            try {
                                atVal = response.getString("_attachments");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                            String newId = atVal.substring(2, atVal.indexOf(".") + 4);
                            String url = "http://46.101.205.23:4444/test_db/" + id + "/" + newId;
                            atId = id ;
                            ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {


                                @Override

                                public void onResponse(Bitmap response) {


                                    ImageView view = new ImageView(MainActivity.this);
                                    ScrollView sv = new ScrollView(MainActivity.this);
                                    view.setImageBitmap(response);
                                    sv.addView(view);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle(atId)
                                            .setView(sv)
                                            .setPositiveButton("change img", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    addImg();
                                                    dialogInterface.cancel();
                                                }
                                            })
                                            .setIcon(R.mipmap.ic_launcher)
                                            .setCancelable(true)
                                            .setNeutralButton("ok",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                            dialog.cancel();
                                                        }
                                                    });

                                    AlertDialog alert = builder.create();
                                    alert.show();


                                }

                            }, 0, 0, null, null);


                            Volley.newRequestQueue(context).add(ir);
// Retrieves an image specified by the URL, displays it in the UI.


                        } else {
                            Intent i = new Intent(context, SecondActivity.class);
                            i.putExtra("id", id);

                            startActivity(i);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                        String url = "http://46.101.205.23:4444/test_db/" + MainActivity.id;
// Retrieves an image specified by the URL, displays it in the UI.
                        ImageRequest request = new ImageRequest(url,
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap bitmap) {
                                        ImageView view = new ImageView(MainActivity.this);
                                        view.setImageBitmap(bitmap);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle(id)
                                                .setView(view)
                                                .setIcon(R.mipmap.ic_launcher)
                                                .setCancelable(true)
                                                .setNeutralButton("ok",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {

                                                                dialog.cancel();
                                                            }
                                                        });

                                        AlertDialog alert = builder.create();
                                        alert.show();

                                    }
                                }, 0, 0, null,
                                new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                        ImageView view = new ImageView(MainActivity.this);


                                        view.setBackgroundColor((Color.RED));
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle("Error")
                                                .setView(view)
                                                .setIcon(R.mipmap.ic_launcher)
                                                .setCancelable(true)
                                                .setNeutralButton("ok",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {

                                                                dialog.cancel();
                                                            }
                                                        });

                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });
// Access the RequestQueue through your singleton class.
                        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);


                    }
                });

                queue.add(jsObjRequest);

            }
        });
        try {
            if (isConnected()) {
                findViewById(R.id.textView).setBackgroundColor(Color.GREEN);
            } else {
                findViewById(R.id.textView).setBackgroundColor(Color.RED);
                Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();


            }
        } catch (Exception e) {
        }
    }


    public void addImg() {
        image = new ImageView(this);
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.error);

        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setImageBitmap(img);
        }
        super.onActivityResult(requestCode, resultCode, data);
        LinearLayout l = new LinearLayout(MainActivity.this);
        l.setOrientation(LinearLayout.VERTICAL);
        ScrollView v = new ScrollView(MainActivity.this);
        final EditText et = new EditText(MainActivity.this);
        if (!atId.equals("")) {

            et.setText(atId);
            et.setTextSize(32);

            atId = "";
            l.addView(et);
        } else {

            et.setHint("id");
            et.setTextSize(32);

            l.addView(et);
        }

        l.addView(v);

        v.addView(image);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final Bitmap finalImg = img;
        builder.setTitle("Sending image to server")
                .setView(l)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(true)
                .setNeutralButton("ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    send(finalImg,et.getText().toString());
                                } catch (CouchbaseLiteException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(MainActivity.this, "Don't work!", Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void forPlus(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Type of new document")

                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setNeutralButton("Text",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(context, TherdActivity.class);


                                startActivity(i);
                                dialog.cancel();
                            }
                        });
        builder.setPositiveButton("Image", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addImg();
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    public void send(Bitmap bmp ,String id) throws CouchbaseLiteException {
      Document document=new Document(new Database("https://example.com/mydatabase/",null),id);



attachImage(document,bmp);
    }
    public static void attachImage(com.couchbase.lite.Document task, Bitmap image) throws CouchbaseLiteException {
        if (task == null || image == null) return;
        UnsavedRevision revision = task.createRevision();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        revision.setAttachment("image", "image/jpg", in);
        revision.save();
    }


}



class MyTask extends AsyncTask<Void, Void, Void> {

    String title;//Тут храним значение заголовка сайта
    @Override
    protected Void doInBackground(Void... params) {

        org.jsoup.nodes.Document doc = null;//Здесь хранится будет разобранный html документ
        try {
            //Считываем заглавную страницу http://harrix.org
            doc = Jsoup.connect("http://46.101.205.23:4444/test_db/"+MainActivity.id).get();
        } catch (IOException e) {
            //Если не получилось считать
            e.printStackTrace();
        }

        //Если всё считалось, что вытаскиваем из считанного html документа заголовок
        if (doc!=null)

            title = doc.body().text();
        else

            title = "Error";

        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if(title.equals("Error")){
            MainActivity.textView.setTextColor(Color.RED);

        }

        MainActivity.textView.setText(title);

    }
}

