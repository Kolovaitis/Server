package kolovaitis.by.server;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 01.11.2015.
 */
public class TherdActivity extends AppCompatActivity {
    public static LinearLayout root;
    public static int currentNumber = 0;
public static Context context;
    public static boolean bool;
    public String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therd);
        setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        root = (LinearLayout) findViewById(R.id.root);
        bool=true;
        context=this;

    }
    public void addField(View v) {
        final LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setId(currentNumber);
        root.addView(l);
        ImageView b=new ImageView(this);
        b.setBackgroundResource(R.drawable.delete);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                root.removeView(l);
                currentNumber-=1;

            }
        });
        EditText et1 = new EditText(this);
        et1.setHint("key");
        et1.setId(0);
        EditText et2 = new EditText(this);
        et2.setHint("value");

        l.addView(et1);
        l.addView(et2);
        l.addView(b);
        currentNumber++;
    }
    public void deleteField(int id) {
        LinearLayout l = (LinearLayout)findViewById(id);
        if(((EditText)l.getChildAt(0)).getText().equals("")){
            root.removeView(l);
        }

    }
    public void ok(View v){
        String url = "http://46.101.205.23:4444/test_db";
        /*Toast toast = Toast.makeText(getApplicationContext(),"nedodelal",Toast.LENGTH_LONG);
        toast.show();*/
        HashMap<String, String> params = new HashMap<String, String>();
        for(int i=0;i<currentNumber;i++){
            if((((EditText)((LinearLayout) root.getChildAt(i)).getChildAt(0)).getText()+"").equals("")==false){
            params.put(((EditText)((LinearLayout) root.getChildAt(i)).getChildAt(0)).getText()+"", ((EditText)((LinearLayout) root.getChildAt(i)).getChildAt(1)).getText()+"");
        }}

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            id=response.getString("id")+"";
                            Intent i=new Intent(TherdActivity.context,SecondActivity.class);
                            i.putExtra("id",response.getString("id")+"");
                            i.putExtra("ForError", response.toString());
                            startActivity(i);
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