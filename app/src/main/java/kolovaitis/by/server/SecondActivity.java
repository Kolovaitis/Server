package kolovaitis.by.server;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by user on 30.10.2015.
 */
public class SecondActivity extends AppCompatActivity {

    JsonObjectRequest request;
    public String id;
    public Context context;
    public int currentNumber=0;
public LinearLayout root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
root=(LinearLayout)findViewById(R.id.LL);



        id =getIntent().getStringExtra("id") ;
        context=this;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://46.101.205.23:4444/test_db/" +getIntent().getStringExtra("id");
final String sb=getIntent().getStringExtra("ForError");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

Iterator<String> i=response.keys();
while(i.hasNext()){
    String s=i.next();
    try {
        addField(s,response.getString(s));
    } catch (JSONException e) {
        e.printStackTrace();
    }
}

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsObjRequest);






    }

    public void addField(View v) {
        final EditText et=new EditText(this);
        et.setHint("Key");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Field")
                .setView(et)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        if(!et.getText().toString().equals("")){
                        final LinearLayout l = new LinearLayout(SecondActivity.this);
                        l.setOrientation(LinearLayout.HORIZONTAL);
                        l.setId(currentNumber);
                        root.addView(l);
                        ImageView b = new ImageView(SecondActivity.this);
                        b.setBackgroundResource(R.drawable.delete);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                root.removeView(l);


                            }
                        });
                        TextView et1 = new TextView(SecondActivity.this);
                        et1.setText(et.getText());
                        et1.setId(0);
                        EditText et2 = new EditText(SecondActivity.this);
                        et2.setHint("value");

                        l.addView(et1);
                        l.addView(et2);
                        l.addView(b);
                        currentNumber++;
                    }else {dialog.cancel();}
                }});
        AlertDialog alert = builder.create();
        alert.show();

    }
    public void addField(String key,String value) {
        final LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setId(currentNumber);
        root.addView(l);
        ImageView b=new ImageView(this);
        if(!key.equals("_id")&&!key.equals("_rev")){
        b.setBackgroundResource(R.drawable.delete);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                root.removeView(l);
                currentNumber -= 1;

            }
        });}
        TextView et1 = new TextView(this);
        et1.setText(key);
        et1.setId(0);
        EditText et2 = new EditText(this);
        et2.setHint("value");
et2.setText(value);
        l.addView(et1);
        l.addView(et2);
        l.addView(b);
        currentNumber++;
    }
public void back(View v){
    this.finish();
}

public HashMap<String,String>getParams(){
    HashMap<String, String> params = new HashMap<String, String>();

    boolean b=true;

        for(int i=0;i<currentNumber;i++){
            try{ {
                params.put(((TextView)((LinearLayout) root.getChildAt(i)).getChildAt(0)).getText()+"", ((EditText)((LinearLayout) root.getChildAt(i)).getChildAt(1)).getText()+"");
            }}catch(Exception e){}}


    return params;
}
    public void save(View v){
        HashMap<String, String> params = getParams();
        String url = "http://46.101.205.23:4444/test_db/" ;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            id=response.getString("id")+"";
Intent intent=new Intent (SecondActivity.this,SecondActivity.class);
                            intent.putExtra("id",id);

                            Toast.makeText(SecondActivity.this,"Saving...",Toast.LENGTH_LONG).show();
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            Toast toast = Toast.makeText(getApplicationContext(),e +"",Toast.LENGTH_LONG);
                            toast.show();                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());


            }
        });
        queue.add(req);
    }
   }
