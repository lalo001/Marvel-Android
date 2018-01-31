package android.lalo.com.marvel;

import android.app.ListActivity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private EditText editText;
    private Button button;
    private iTunesArrayAdapter itunesArrayAdapter;

    private RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Json json = new Json();
//        String jsonString = json.serviceCall("https://itunes.apple.com/search?term=jack+johnson");
//        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
//
//        arrayAdapter.add(jsonString);
//        setListAdapter(arrayAdapter);
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        editText.setText("");
        editText.setHint("Enter offset");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                updateOffset(editText);

                return true;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(arrayAdapter);

        mQueue = VolleySingleton.getInstance(this).getRequestQueue();

        jsonMarvel(getMarvelString("0"), arrayAdapter);
        //new MarvelJson(arrayAdapter).execute();
//        try {
//            URL url = new URL("https://itunes.apple.com/search?term=jack+johnson");
//            new ProcesaJson(arrayAdapter).execute(url);
//        } catch (MalformedURLException e) {
//            Log.e(MainActivity.class.getSimpleName(), "MalformedURLExecption" + e.getMessage());
//        }
    }

    public void updateOffset(View view) {
        if (editText.getText() != null) {
            try {
                if (Integer.parseInt(editText.getText().toString()) > 900) {
                    jsonMarvel(getMarvelString("900"), arrayAdapter);
                } else {
                    jsonMarvel(getMarvelString(editText.getText().toString()), arrayAdapter);
                }
            }catch (Exception e) {
                e.printStackTrace();
                editText.setError("Not a number.");
            }
        }
    }

//    public class ProcesaJson extends AsyncTask<URL, Integer, ArrayList<iTunes>> {
//        private iTunesArrayAdapter adapter;
//
//        public ProcesaJson(iTunesArrayAdapter adapter) {
//            this.adapter = adapter;
//        }
//
//        @Override
//        protected ArrayList<iTunes> doInBackground(URL... urls) {
//            Json json = new Json();
//            String jsonString = json.serviceCall(urls[0].toString());
//            ArrayList<iTunes> arrayList = new ArrayList<>();
//
//            try {
//                JSONObject jsonObject = new JSONObject(jsonString);
//                JSONArray jsonArray = jsonObject.getJSONArray("results");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject data = jsonArray.getJSONObject(i);
//                    iTunes itunes = new iTunes();
//                    itunes.collectionName = data.getString("collectionName");
//                    itunes.collectionName = data.getString("trackPrice");
//                    itunes.collectionName = data.getString("trackPrice");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return arrayList;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<iTunes> strings) {
//            adapter.clear();
//            adapter.addAll(strings);
//            adapter.notifyDataSetChanged();
//        }
//    }

    private final String LOG_TAG = "MARVEL";

    private static char[] HEXCodes = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    private void jsonMarvel(String url, final ArrayAdapter<String> arrayAdapter) {
        arrayAdapter.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray jsonArray = data.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        arrayAdapter.add(jsonObject.getString("name"));
                    }
                    arrayAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);
    }

    public String getMarvelString(String offset) {
        String ts = Long.toString(System.currentTimeMillis() / 1000);
        String apikey = "1681a9eefcf8fbf43de66c59727718da";
        String hash = md5(ts + "ede49375699321e3736436b53011574333433f40" + "1681a9eefcf8fbf43de66c59727718da");
        ArrayList<String> arrayList = new ArrayList<>();


            /*
                Conexión con el getway de marvel
            */
        final String CHARACTER_BASE_URL =
                "http://gateway.marvel.com/v1/public/characters";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

            /*
                Configuración de la petición
            */
        String characterJsonStr = null;
        final String TIMESTAMP = "ts";
        final String API_KEY = "apikey";
        final String HASH = "hash";
        final String ORDER = "orderBy";

        Uri builtUri;
        builtUri = Uri.parse(CHARACTER_BASE_URL+"?").buildUpon()
                .appendQueryParameter(TIMESTAMP, ts)
                .appendQueryParameter(API_KEY, apikey)
                .appendQueryParameter(HASH, hash)
                .appendQueryParameter(ORDER, "name")
                .appendQueryParameter("limit", "100")
                .appendQueryParameter("offset", offset)
                .build();
        return builtUri.toString();
    }


//    public class MarvelJson extends AsyncTask< String, Integer,ArrayList<String>>{
//        private ArrayAdapter<String> adapter;

//        public MarvelJson(ArrayAdapter<String> adapter){
//            this.adapter = adapter;
//        }
//        @Override
//        protected ArrayList<String> doInBackground(String... urls) {
//
//            /*
//            Investiga y reporta qué es md5?
//
//
//
//            */
//            String ts = Long.toString(System.currentTimeMillis() / 1000);
//            String apikey = "1681a9eefcf8fbf43de66c59727718da";
//            String hash = md5(ts + "ede49375699321e3736436b53011574333433f40" + "1681a9eefcf8fbf43de66c59727718da");
//            ArrayList<String> arrayList = new ArrayList<>();
//
//
//            /*
//                Conexión con el getway de marvel
//            */
//            final String CHARACTER_BASE_URL =
//                    "http://gateway.marvel.com/v1/public/characters";
//
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            /*
//                Configuración de la petición
//            */
//            String characterJsonStr = null;
//            final String TIMESTAMP = "ts";
//            final String API_KEY = "apikey";
//            final String HASH = "hash";
//            final String ORDER = "orderBy";
//
//            Uri builtUri;
//            builtUri = Uri.parse(CHARACTER_BASE_URL+"?").buildUpon()
//                    .appendQueryParameter(TIMESTAMP, ts)
//                    .appendQueryParameter(API_KEY, apikey)
//                    .appendQueryParameter(HASH, hash)
//                    .appendQueryParameter(ORDER, "name")
//                    .build();
//
//            try {
//
//            /*
//                Ejecución de la conexión
//            */
//                URL url = new URL(builtUri.toString());
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                Log.d(LOG_TAG, "Response: " + urlConnection.getResponseCode() + " - " + urlConnection.getResponseMessage());
//
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line + "\n");
//                }
//
//
//                /*
//                    JSON Obtenido
//                */
//                characterJsonStr = buffer.toString();
//
//
//                /*
//
//                    Procesa el JSON y muestra el nombre de cada Marvel Character obtenido
//                */
//                arrayList.add(characterJsonStr);
//
//
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            Log.v(LOG_TAG,arrayList.get(0));
//
//            return arrayList;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<String> strings) {
//            adapter.clear();
//            adapter.addAll(strings);
//            adapter.notifyDataSetChanged();
//        }
//    }


    /*
        Investiga y reporta qué es md5:

    */
    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            String hash = new String(hexEncode(digest.digest()));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    /*
        Investiga y reporta qué hace esta aplicación
    */
    public static String hexEncode(byte[] bytes) {
        char[] result = new char[bytes.length*2];
        int b;
        for (int i = 0, j = 0; i < bytes.length; i++) {
            b = bytes[i] & 0xff;
            result[j++] = HEXCodes[b >> 4];
            result[j++] = HEXCodes[b & 0xf];
        }
        return new String(result);}
}



