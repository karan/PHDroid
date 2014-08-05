package im.goel.producthunt;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class PHActivity extends Activity {

    ListView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ph);
        Log.i(PHActivity.class.getName(), "Calling stuff");
        t = (ListView) findViewById(R.id.posts);

        URL url = null;
        try {
            url = new URL("http://hook-api.herokuapp.com/today");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new ApiCall().execute(url);
    }

    public void paintPosts(final ArrayList<Post> posts) {
        ArrayList<String> list = new ArrayList<String>();
        for (Post p : posts) {
            list.add(p.getTitle());
        }
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        t.setAdapter(adapter);

        t.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition = position;

                String itemValue = (String) t.getItemAtPosition(position);

                String url = posts.get(position).getUrl();
                Intent i = new Intent(PHActivity.this, WebActivity.class);
                i.putExtra("URL", url);
                startActivity(i);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Loading " + itemValue, Toast.LENGTH_SHORT)
                        .show();

            }

        });

    }

    public class ApiCall extends AsyncTask<URL, Void, Long> {
        private String result;

        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalSize = 0;
            StringBuilder resultBuilder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                try {
                    // Read all the text returned by the server
                    InputStreamReader reader = new InputStreamReader(urls[i].openStream());
                    BufferedReader in = new BufferedReader(reader);
                    String resultPiece;
                    while ((resultPiece = in.readLine()) != null) {
                        resultBuilder.append(resultPiece);
                    }
                    in.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // if cancel() is called, leave the loop early
                if (isCancelled()) {
                    break;
                }
            }
            // save the result
            this.result = resultBuilder.toString();
            return totalSize;
        }

        // called after doInBackground finishes
        protected void onPostExecute(Long result) {
            try {
                JSONObject jsonObj = new JSONObject(this.result);
                JSONArray postsLists = jsonObj.getJSONArray("hunts");

                ArrayList<Post> list = new ArrayList<Post>();
                for (int i = 0; i < postsLists.length(); ++i) {
                    list.add(new Post(
                            postsLists.getJSONObject(i).getString("title"),
                            postsLists.getJSONObject(i).getString("url"),
                            postsLists.getJSONObject(i).getInt("votes")));
                }
                paintPosts(list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
