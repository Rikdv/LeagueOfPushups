package com.bletori.leagueofpushups;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static int amountPushups;
    public static String matchHistory;
    public static String mDiff;
    public static String name;
    public static String mServer;
    public static String mostrecent;
    public static int playerid;
    JsonArray jarray;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        matchHistory = bundle.getString("matchHistory");
        mDiff = bundle.getString("mDiff");
        name = bundle.getString("name");
        mServer = bundle.getString("server").toLowerCase();
        playerid = bundle.getInt("playerid");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(name);

        getMatchHistory mh = new getMatchHistory(mDiff, mServer);
        mh.execute((Void) null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class getMatchHistory extends AsyncTask<Void, Void, Boolean> {

        private String mDiff;
        private String mServer;

        getMatchHistory(String diff, String server) {
            mDiff = diff;
            mServer = server;
        }
        @Override
        protected void onPostExecute ( final Boolean success){
            if (success) {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.marker_progress);
                progressBar.setVisibility(View.GONE);
            } else {
                //showProgress(false);
                //summonername.setError(getString(R.string.error_summonername_not_found));
                //summonername.requestFocus();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Bitmap nullbmap = loadPng("nullbmap");
            if(nullbmap == null){
                try{
                    nullbmap = BitmapFactory.decodeStream((InputStream) new URL("https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcRfYzPc1OEM-0n4ztBKvLtb8mtctbwUc6IyBWoN-4OO7Dgk6t7cK2Pl").getContent());
                    savePngToInternalStorage(nullbmap, "nullbmap");
                }catch(Exception e){}
            }

            ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
            String total = "";
            try {
                URL oracle = new URL(MainActivity.matchHistory);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    total += inputLine;
                in.close();
            } catch (Exception e) {
                Log.i(LoginActivity.TAG, e.toString());
            }
            try {
                JsonElement jelement = new JsonParser().parse(total);
                JsonObject jobject = jelement.getAsJsonObject();
                jarray = jobject.getAsJsonArray("games");

                String mostRecentVersion = "https://global.api.pvp.net/api/lol/static-data/" + MainActivity.mServer + "/v1.2/versions?api_key=" + LoginActivity.apikey;

                String recentversionJson = "";
                try {
                    URL oracle = new URL(mostRecentVersion);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        recentversionJson += inputLine;
                    in.close();
                } catch (Exception e) {
                    Log.i(LoginActivity.TAG, e.toString());
                }
                JsonElement jelm = new JsonParser().parse(recentversionJson);
                JsonArray jobt = jelm.getAsJsonArray();
                mostrecent = jobt.get(0).toString().replace("\"", "");

                AssetManager am = getApplication().getAssets();
                for (int i = 0; i < 10; i++) {
                    JsonObject jobj = jarray.get(i).getAsJsonObject();
                    JsonObject stats = jobj.getAsJsonObject("stats");
                    boolean win = false;
                    try {
                        win = Boolean.parseBoolean(stats.get("win").toString());
                    } catch (Exception e) {
                    }
                    int kills;
                    try {
                        kills = Integer.parseInt(stats.get("championsKilled").toString());
                    } catch (Exception e) {
                        kills = 0;
                    }
                    int deaths;
                    try {
                        deaths = Integer.parseInt(stats.get("numDeaths").toString());
                    } catch (Exception e) {
                        deaths = 0;
                    }
                    int assists;
                    try {
                        assists = Integer.parseInt(stats.get("assists").toString());
                    } catch (Exception e) {
                        assists = 0;
                    }
                    int champId;
                    try {
                        champId = Integer.parseInt(jobj.get("championId").toString());
                    } catch (Exception e) {
                        champId = 0;
                    }
                    long gold = 0;
                    try{
                        gold = Long.parseLong(stats.get("goldEarned").toString());
                    }catch(Exception e){}
                    long cs;
                    try{
                        cs = Long.parseLong(stats.get("minionsKilled").toString()) + Long.parseLong(stats.get("neutralMinionsKilled").toString());
                    }catch(Exception e){
                        if(stats.get("minionsKilled") == null){
                            cs = Long.parseLong(stats.get("neutralMinionsKilled").toString());
                        }else if(stats.get("neutralMinionsKilled") == null){
                            cs = Long.parseLong(stats.get("minionsKilled").toString());
                        }else{
                            cs = 0;
                        }
                    }
                    long level = 1;
                    try{
                        level = Long.parseLong(stats.get("level").toString());
                    }catch(Exception e){}

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

                    //------------------------------
                    String champPngUrl = GameActivity.getPngUrl(champ);

                    Bitmap bitmap = loadPng(champ);
                    try {
                        if(bitmap == null){
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(champPngUrl).getContent());
                            savePngToInternalStorage(bitmap, champ);
                        }
                    } catch (Exception e) {
                        Log.i(LoginActivity.TAG, e.toString());
                    }

                    //------------------------------SUMMS & ITEMS-----------------------
                    int dSpell = Integer.parseInt(jobj.get("spell1").toString());
                    int fSpell = Integer.parseInt(jobj.get("spell2").toString());

                    //-- dSpell
                    String key = "";
                    try{
                        InputStream is = am.open("summsbyid.txt");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        try {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] prts = line.split(":");
                                if(Integer.parseInt(prts[1]) == dSpell){
                                    Log.i(LoginActivity.TAG, line);
                                    String[] parts = line.split(":");
                                    key = parts[0];
                                }
                            }
                        } finally {
                            reader.close();
                        }
                    }catch(Exception e){
                        Log.i(LoginActivity.TAG, e.toString());
                    }
                    if(key.equals("")){
                        String dSpellUrl = "https://global.api.pvp.net/api/lol/static-data/" + MainActivity.mServer + "/v1.2/summoner-spell/" + dSpell + "?api_key=" + LoginActivity.apikey;
                        String dspelltxt = "";
                        try {
                            URL oracle = new URL(dSpellUrl);
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(oracle.openStream()));

                            String inputLine;
                            while ((inputLine = in.readLine()) != null)
                                dspelltxt += inputLine;
                            in.close();
                        } catch (Exception e) {
                            Log.i(LoginActivity.TAG, e.toString());
                        }
                        JsonElement jelmnt = new JsonParser().parse(dspelltxt);
                        JsonObject jobjct = jelmnt.getAsJsonObject();
                        key = jobjct.get("key").toString();
                    }

                    String dSpellIconUrl = "http://ddragon.leagueoflegends.com/cdn/" + mostrecent + "/img/spell/" + key.replace("\"", "") + ".png";

                    Bitmap dBitmap = loadPng("summ" + key);
                    if(dBitmap == null){
                        dBitmap = BitmapFactory.decodeStream((InputStream) new URL(dSpellIconUrl).getContent());
                        savePngToInternalStorage(dBitmap, "summ" + key);
                    }

                    //-- fSpell
                    String key2 = "";
                    try{
                        InputStream is = am.open("summsbyid.txt");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        try {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] prts = line.split(":");
                                if(Integer.parseInt(prts[1]) == fSpell){
                                    Log.i(LoginActivity.TAG, line);
                                    String[] parts = line.split(":");
                                    key2 = parts[0];
                                }
                            }
                        } finally {
                            reader.close();
                        }
                    }catch(Exception e){
                        Log.i(LoginActivity.TAG, e.toString());
                    }

                    if(key2.equals("")){
                        String fSpellUrl = "https://global.api.pvp.net/api/lol/static-data/" + MainActivity.mServer + "/v1.2/summoner-spell/" + fSpell + "?api_key=" + LoginActivity.apikey;
                        String fspelltxt = "";
                        try {
                            URL oracle = new URL(fSpellUrl);
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(oracle.openStream()));
                            String inputLine;
                            while ((inputLine = in.readLine()) != null)
                                fspelltxt += inputLine;
                            in.close();
                        } catch (Exception e) {
                            Log.i(LoginActivity.TAG, e.toString());
                        }
                        JsonElement jelmnt2 = new JsonParser().parse(fspelltxt);
                        JsonObject jobjct2 = jelmnt2.getAsJsonObject();
                        key2 = jobjct2.get("key").toString();
                    }

                    String fSpellIconUrl = "http://ddragon.leagueoflegends.com/cdn/" + mostrecent + "/img/spell/" + key2.replace("\"", "") + ".png";

                    Bitmap fBitmap = loadPng("summ" + key2);
                    if(fBitmap == null){
                        fBitmap = BitmapFactory.decodeStream((InputStream) new URL(fSpellIconUrl).getContent());
                        savePngToInternalStorage(fBitmap, "summ" + key2);
                    }

                    Map<String, Bitmap> items = new HashMap<>();
                    for (int j = 0; j < 7; j++) {
                        String itemID = "";
                        try {
                            itemID = stats.get("item" + j).toString();
                            String itemLink = "http://ddragon.leagueoflegends.com/cdn/" + mostrecent + "/img/item/" + itemID + ".png";
                            Bitmap item = loadPng(itemID);
                            if(item == null){
                                item = BitmapFactory.decodeStream((InputStream) new URL(itemLink).getContent());
                                savePngToInternalStorage(item, itemID);
                            }
                            items.put("item" + j, item);
                        } catch (Exception e) {
                        }
                    }
                    //-------------------------------------------------------------------
                    try {
                        if (mDiff.equalsIgnoreCase("noobie")) {
                            amountPushups = deaths - ((kills + assists) / 4);
                            if(amountPushups > 10){
                                amountPushups = 10;
                            }
                        } else if (MainActivity.mDiff.equalsIgnoreCase("beginner")) {
                            amountPushups = (deaths * 2) - ((kills + assists) / 3);
                            if ((kills + assists) / deaths < 1) {
                                amountPushups += 5;
                            }
                            if(amountPushups > 15){
                                amountPushups = 15;
                            }
                        } else if (MainActivity.mDiff.equalsIgnoreCase("medium")) {
                            amountPushups = (deaths * 3) - ((kills + assists) / 2);
                            if ((kills + assists) / deaths < 1) {
                                amountPushups += 5;
                            }
                            if(amountPushups > 25){
                                amountPushups = 25;
                            }
                        } else if (MainActivity.mDiff.equalsIgnoreCase("hard")) {
                            amountPushups = (deaths * 4) - ((kills+assists)/2);
                            if ((kills + assists) / deaths < 1) {
                                amountPushups += 10;
                            }
                            if(amountPushups > 35){
                                amountPushups = 35;
                            }
                        } else if (MainActivity.mDiff.equalsIgnoreCase("extreme")) {
                            amountPushups = (deaths * 7) - ((kills+assists)*3);
                            if(amountPushups > 75){
                                amountPushups = 75;
                            }
                            if ((kills + assists) / deaths < 1) {
                                amountPushups += 20;
                            }
                        }
                    } catch (Exception e) {
                        Log.i(LoginActivity.TAG, e.toString());
                    }
                    String region;
                    if(mServer.equals("kr") || mServer.equals("ru")){
                        region = mServer;
                    }else if(mServer.contains("la")) {
                        if(mServer.equals("LAN"))
                            region = "LA1";
                        else
                            region = "LA2";
                    }else{
                        region = mServer + "1";
                    }
                    String champMastery = "https://euw.api.pvp.net/championmastery/location/" + region + "/player/" + playerid + "/champion/" + champId + "?api_key=" + LoginActivity.apikey;
                    String total2 = "";
                    try{
                        URL oracle = new URL(champMastery);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(oracle.openStream()));

                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                            total2 += inputLine;
                        in.close();

                        JsonElement jelement1 = new JsonParser().parse(total2);
                        JsonObject jobject1 = jelement1.getAsJsonObject();
                        int championlevel = Integer.parseInt(jobject1.get("championLevel").toString().replace("\"", ""));
                        int championpoints = Integer.parseInt(jobject1.get("championPoints").toString().replace("\"", ""));
                        Log.i(LoginActivity.TAG, "lvl " + championlevel + "/points " + championpoints);

                        if(championlevel == 1){
                            amountPushups = amountPushups / 2;
                        }else if(championlevel == 2){
                            amountPushups = amountPushups / 3 * 2;
                        }else if(championlevel == 3){
                            amountPushups = amountPushups / 4 * 3;
                        }else if(championlevel == 4){

                        }else if(championlevel == 5){
                            if(championpoints < 50000){
                                amountPushups = amountPushups / 10 * 11;
                            }else if(championpoints < 100000){
                                amountPushups = amountPushups / 10 * 13;
                            }else if(championpoints  < 250000){
                                amountPushups = amountPushups / 10 * 15;
                            }else{
                                amountPushups = amountPushups * 2;
                            }
                        }
                    }catch(Exception e){
                        Log.i(LoginActivity.TAG, e.toString());
                    }

                    if (MainActivity.amountPushups < 0) {
                        MainActivity.amountPushups = 0;
                    }
                    int secondsplayed = Integer.parseInt(stats.get("timePlayed").toString());

                    double value = (((double)kills + (double)assists) / (double)deaths);

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("bitmap", bitmap);
                    map.put("amountPushups", amountPushups);
                    map.put("kdaFull", kills + "/" + deaths + "/" + assists);
                    map.put("win", win);
                    map.put("champ", champ);
                    map.put("dBitmap", dBitmap);
                    map.put("fBitmap", fBitmap);
                    map.put("item0", items.get("item0"));
                    map.put("item1", items.get("item1"));
                    map.put("item2", items.get("item2"));
                    map.put("item3", items.get("item3"));
                    map.put("item4", items.get("item4"));
                    map.put("item5", items.get("item5"));
                    map.put("item6", items.get("item6"));
                    map.put("secondsplayed", secondsplayed);
                    map.put("gold", gold);
                    map.put("cs", cs);
                    map.put("level", level);
                    map.put("KDA", Math.round(value*10.0)/10.0);
                    data.add(map);
                }
            }catch(Exception e){

            }
            final ArrayList<HashMap<String, Object>> dal = data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(LoginActivity.TAG, "setting adapter");
                    ListView list = (ListView) findViewById(R.id.list);

                    list.setScrollingCacheEnabled(false);

                    LazyAdapter adapter = new LazyAdapter(getApplication(), dal);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            JsonObject jobj = jarray.get(position).getAsJsonObject();
                            String gameId = jobj.get("gameId").toString();
                            String gameURL = "https://" + mServer +".api.pvp.net/api/lol/" + mServer + "/v2.2/match/" + gameId + "?api_key=" + LoginActivity.apikey;
                            Intent intent = new Intent(getApplication(), GameActivity.class);
                            intent.putExtra("gameURL", gameURL);
                            intent.putExtra("inf", dal.get(position));
                            intent.putExtra("mServer", mServer);
                            startActivity(intent);
                        }
                    });
                }
            });
            return true;
        }
    }
}