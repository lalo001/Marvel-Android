package android.lalo.com.marvel;

import android.graphics.Bitmap;
import android.lalo.com.marvel.adapters.MarvelAdapter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private String currentId;
    private RequestQueue mQueue;
    private TextView nameTextView;
    private TextView dateTextView;
    private TextView descriptionTextView;
    private NetworkImageView networkImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("Character Detail");

        currentId = (String) getIntent().getSerializableExtra("id");
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        networkImageView = (NetworkImageView) findViewById(R.id.networkImageView);

        mQueue = VolleySingleton.getInstance(this).getRequestQueue();
        jsonMarvel(getMarvelString(currentId));
    }


    private final String LOG_TAG = "MARVEL";

    private static char[] HEXCodes = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    private void jsonMarvel(String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray jsonArray = data.getJSONArray("results");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONObject thumbnail = jsonObject.getJSONObject("thumbnail");
                    String imageUrl = thumbnail.getString("path") + "/standard_amazing" + "." + thumbnail.getString("extension");
                    String name = jsonObject.getString("name");
                    String numberOfComics = jsonObject.getJSONObject("comics").getInt("available") + "";
                    String numberOfSeries = jsonObject.getJSONObject("series").getInt("available") + "";
                    String numberOfStories = jsonObject.getJSONObject("stories").getInt("available") + "";
                    String description = jsonObject.getString("description");
                    RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
                    nameTextView.setText(name);
                    dateTextView.setText("Appears to date in " + numberOfComics + " comics, " + numberOfSeries + " series and " + numberOfStories + " stories.");
                    if (description.isEmpty()) {
                        descriptionTextView.setText("No Description Available");
                    } else {
                        descriptionTextView.setText(description);
                    }
                    ImageLoader imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
                        private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);
                        @Override
                        public Bitmap getBitmap(String url) {
                            return cache.get(url);
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            cache.put(url, bitmap);
                        }
                    });
                    networkImageView.setImageUrl(imageUrl, imageLoader);
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

    public String getMarvelString(String id) {
        String ts = Long.toString(System.currentTimeMillis() / 1000);
        String apikey = "1681a9eefcf8fbf43de66c59727718da";
        String hash = md5(ts + "ede49375699321e3736436b53011574333433f40" + "1681a9eefcf8fbf43de66c59727718da");
        ArrayList<String> arrayList = new ArrayList<>();


            /*
                Conexión con el getway de marvel
            */
        final String CHARACTER_BASE_URL =
                "http://gateway.marvel.com/v1/public/characters/" + id;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

            /*
                Configuración de la petición
            */
        String characterJsonStr = null;
        final String TIMESTAMP = "ts";
        final String API_KEY = "apikey";
        final String HASH = "hash";

        Uri builtUri;
        builtUri = Uri.parse(CHARACTER_BASE_URL+"?").buildUpon()
                .appendQueryParameter(TIMESTAMP, ts)
                .appendQueryParameter(API_KEY, apikey)
                .appendQueryParameter(HASH, hash)
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
        return new String(result);
    }
}
