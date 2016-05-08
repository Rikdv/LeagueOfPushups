package com.bletori.leagueofpushups;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private UserLoginTask mAuthTask = null;

    public static String apikey = "af368afd-bce2-4b77-a138-f778134264ee";
    public static String TAG = "LeagueOfPushups";

    String name;
    int playerId;
    private EditText summonername;
    private View mProgressView;
    private View mLoginFormView;
    private Spinner server;
    private Spinner mode;
    private Button newacc;
    ArrayList<String> sKey = new ArrayList<String>();
    Dialog dialog;
    SimpleAdapter sAdapter;
    ListView lv;
    int modeNum;

    public boolean saveArray(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", sKey.size());
        for(int i=0;i<sKey.size();i++){
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, sKey.get(i));
        }
        return mEdit1.commit();
    }
    public void loadArray(Context mContext){
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        sKey.clear();
        int size = mSharedPreference1.getInt("Status_size", 0);

        for(int i=0;i<size;i++)        {
            sKey.add(mSharedPreference1.getString("Status_" + i, null));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences modes = getSharedPreferences("mode", 0);
        modeNum = modes.getInt("mode", 2);

        final Activity act = this;

        newacc = (Button)findViewById(R.id.newacc);
        newacc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(act);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.popup_new_signup);

                server = (Spinner) (dialog).findViewById(R.id.server);
                String[] spinnerArray = {"BR", "EUNE", "EUW", "KR", "LAN", "LAS", "NA", "OCE", "RU", "TR"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(act, android.R.layout.simple_spinner_item, spinnerArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                server.setAdapter(adapter);

                String locale = act.getResources().getConfiguration().locale.getCountry();
                AssetManager am = act.getAssets();
                try{
                    InputStream is = am.open("countrytocontinent.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    try {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if(line.contains(locale)){
                                Log.i(TAG, line);
                                String[] parts = line.split(",");
                                if(parts[1].equalsIgnoreCase("eu")){
                                    server.setSelection(2);
                                }else if(parts[1].equalsIgnoreCase("na")){
                                    server.setSelection(6);
                                }else if(parts[1].equalsIgnoreCase("EUN")){
                                    server.setSelection(2);
                                }else if(parts[0].equalsIgnoreCase("br")){
                                    server.setSelection(0);
                                }else if(parts[0].equalsIgnoreCase("kr")){
                                    server.setSelection(3);
                                }else if(parts[1].equalsIgnoreCase("LAN")){
                                    server.setSelection(4);
                                }else if(parts[1].equalsIgnoreCase("LAS")){
                                    server.setSelection(5);
                                }else if(parts[1].equalsIgnoreCase("oc")){
                                    server.setSelection(7);
                                }else if(parts[0].equalsIgnoreCase("ru")){
                                    server.setSelection(8);
                                }else if(parts[0].equalsIgnoreCase("tr")){
                                    server.setSelection(9);
                                }
                            }
                        }
                    } finally {
                        reader.close();
                    }
                }catch(Exception e){}


                mode = (Spinner) (dialog).findViewById(R.id.mode);
                String[] modeArray = {"Noobie", "Beginner", "Medium", "Hard", "Extreme"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(act, android.R.layout.simple_spinner_item, modeArray);
                modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mode.setAdapter(modeAdapter);
                mode.setSelection(modeNum);

                Button mEmailSignInButton = (Button)(dialog).findViewById(R.id.email_sign_in_button);
                mEmailSignInButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin();
                        dialog.cancel();
                    }
                });
                summonername = (EditText) (dialog).findViewById(R.id.summonername);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });

        String champs = "https://euw.api.pvp.net/api/lol/euw/v1.2/champion?api_key=af368afd-bce2-4b77-a138-f778134264ee";

        f2pchamps set = new f2pchamps(champs);
        set.execute((Void) null);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        loadArray(this);

        setArray();
    }

    public class f2pchamps extends AsyncTask<Void, Void, Boolean> {
        String url;
        HashMap f2ps = new HashMap();

        f2pchamps(String url) {
            this.url = url;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String total = "";
            try{
                URL oracle = new URL(url);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    total += inputLine;
                in.close();

                JsonElement jelement = new JsonParser().parse(total);
                JsonObject jobject = jelement.getAsJsonObject();
                JsonArray jarray = jobject.getAsJsonArray("champions");
                for(int i = 0; i < jarray.size(); i++) {
                    JsonElement jsonElementPlayer = jarray.get(i);
                    JsonObject jsonObject = jsonElementPlayer.getAsJsonObject();
                    boolean f2p = Boolean.parseBoolean(jsonObject.get("freeToPlay").toString());
                    if(f2p){
                        f2ps.put(i, getChamp(jsonObject.get("id").toString()));
                    }
                }
            }catch(Exception e){
                Log.i(TAG, "ERROR:" + e.toString());
            }
            Log.i(TAG, "f2p:" + f2ps.toString());
            return true;
        }

        public String getChamp(String champid){
            int champId = Integer.parseInt(champid);
            AssetManager am = getApplication().getAssets();
            String champ = "";
            try{
                InputStream is = am.open("championsbyid.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] prts = line.split(":");
                        if(Integer.parseInt(prts[1]) == champId){
                            Log.i(LoginActivity.TAG, line);
                            String[] parts = line.split(":");
                            champ = parts[0];
                        }
                    }
                } finally {
                    reader.close();
                }
            }catch(Exception e){
                Log.i(LoginActivity.TAG, e.toString());
            }
            if(champ.equals("")){
                String getChampFromId = "https://global.api.pvp.net/api/lol/static-data/" + MainActivity.mServer + "/v1.2/champion/" + champId + "?api_key=" + LoginActivity.apikey;
                String champjson = "";
                try {
                    URL oracle = new URL(getChampFromId);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        champjson += inputLine;
                    in.close();
                } catch (Exception e1) {
                    Log.i(LoginActivity.TAG, e1.toString());
                }
                JsonElement jel = new JsonParser().parse(champjson);
                JsonObject job = jel.getAsJsonObject();
                champ = job.get("name").toString().replace("\"", "");
            }
            return champ;
        }
        public void savePngToInternalStorage(Bitmap bitmap, String champname){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("images", Context.MODE_PRIVATE);
            File mypath = new File(directory, champname + ".png");

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try{
                    fos.close();
                }catch(Exception e1){}
            }
        }

        public Bitmap loadPng(String champname){
            try {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("images", Context.MODE_PRIVATE);
                File path = new File(directory, champname + ".png");
                return BitmapFactory.decodeStream(new FileInputStream(path));
            }
            catch (FileNotFoundException e){
                return null;
            }
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            LinearLayout llf2ps = (LinearLayout)findViewById(R.id.f2ps);
            for(int i = 0; i < f2ps.size(); i++){
                String champ = f2ps.keySet().toArray()[i].toString();
                Log.i(TAG, "Now loading f2p:" + i);
                ImageView imageView = new ImageView(getApplication());

                Bitmap bitmap = loadPng(champ);

                if(bitmap == null) {
                    String champPngUrl = GameActivity.getPngUrl(champ);
                    try {
                        bitmap = BitmapFactory.decodeStream((InputStream) new URL(champPngUrl).getContent());
                        savePngToInternalStorage(bitmap, champ);
                    } catch (Exception e) {
                        Log.i(LoginActivity.TAG, e.toString());
                    }
                }

                imageView.setImageBitmap(bitmap);
                //setting image position
                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                //adding view to layout
                llf2ps.addView(imageView);
                //make visible to program
            }
        }
    }

    public void setArray(){
        String[] from = { "name", "server" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        sAdapter = new SimpleAdapter(this, buildData(),
                android.R.layout.simple_list_item_2, from, to);

        lv = (ListView)findViewById(R.id.freq);
        lv.setAdapter(sAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> item = (HashMap)lv.getItemAtPosition(position);
                showProgress(true);
                try{
                    String diff = mode.getItemAtPosition(mode.getSelectedItemPosition()).toString();
                    mAuthTask = new UserLoginTask(item.get("name"), item.get("server"), diff, mode.getSelectedItemPosition());
                }catch(Exception e){
                    String diffs[]={"Noobie", "Beginner", "Medium", "Hard", "Extreme", "Impossible"};
                    String diff = diffs[modeNum];
                    mAuthTask = new UserLoginTask(item.get("name"), item.get("server"), diff, modeNum);
                }
                mAuthTask.execute((Void) null);
            }
        });
    }

    private ArrayList<Map<String, String>> buildData() {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for(int i=0;i < sKey.size(); i+=2){
            try{
                list.add(putData(sKey.get(i), sKey.get(i+1)));
            }catch(Exception e){
                break;
            }
        }
        Log.i(TAG, "returning" + list.toString());
        return list;
    }

    private HashMap<String, String> putData(String name, String purpose) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("name", name);
        item.put("server", purpose);
        return item;
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        summonername.setError(null);

        String name = summonername.getText().toString();
        String servername = server.getItemAtPosition(server.getSelectedItemPosition()).toString();
        String diff = mode.getItemAtPosition(mode.getSelectedItemPosition()).toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            summonername.setError(getString(R.string.error_field_required));
            focusView = summonername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(name, servername, diff, mode.getSelectedItemPosition());
            mAuthTask.execute((Void) null);
        }
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mUsername;
        private final String mServer;
        private String mDiff;
        private int diffn;

        UserLoginTask(String user, String server, String diff, int diffnum) {
            mUsername = user;
            mServer = server;
            mDiff = diff;
            diffn = diffnum;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mUsername = mUsername.replace(" ", "");
            String apisite = "https://" + mServer + ".api.pvp.net/api/lol/" + mServer + "/v1.4/summoner/by-name/" + mUsername + "?api_key=" + apikey;
            String total = "";
            try{
                URL oracle = new URL(apisite);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    total += inputLine;
                in.close();
            }catch(Exception e){
                Log.i(TAG, e.toString());
            }
            Log.i(TAG, apisite);
            Log.i(TAG, "Total:" + total);

            try {
                JsonElement jelement = new JsonParser().parse(total);
                JsonObject jobject = jelement.getAsJsonObject();
                jobject = jobject.getAsJsonObject(mUsername.toLowerCase());
                name = jobject.get("name").toString().replace("\"", "");
                playerId = Integer.parseInt(jobject.get("id").toString());
                return true;
            }catch(Exception e){
                Log.i(TAG, e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                SharedPreferences modes = getSharedPreferences("mode", 0);
                SharedPreferences.Editor editormode = modes.edit();
                editormode.putInt("mode", diffn);
                editormode.commit();

                if(sKey.contains(name)){
                    int index = sKey.indexOf(name);
                    sKey.remove(index + 1);
                    sKey.remove(index);
                }

                sKey.add(0, name);
                sKey.add(1, mServer);
                saveArray();
                loadArray(getApplication());

                setArray();

                String matchHistory = "https://" + mServer + ".api.pvp.net/api/lol/" + mServer + "/v1.3/game/by-summoner/" + playerId + "/recent?api_key=" + apikey;

                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.putExtra("playerid", playerId);
                intent.putExtra("matchHistory", matchHistory);
                intent.putExtra("mDiff", mDiff);
                intent.putExtra("name", name);
                intent.putExtra("server", mServer);
                startActivity(intent);

                showProgress(false);

            } else {
                showProgress(false);
                try{
                    summonername.setError(getString(R.string.error_summonername_not_found));
                    summonername.requestFocus();
                }catch(Exception e){
                    Log.i(TAG, "ERROR:" + e.toString());
                }
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

