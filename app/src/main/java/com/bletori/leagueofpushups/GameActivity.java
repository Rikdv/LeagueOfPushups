package com.bletori.leagueofpushups;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {
    String mServer;
    ViewPagerAdapter adapter;
    int amount;
    int amountPushups;

    public static String getPngUrl(String champ){
        String champPngUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/champion/" + champ.replaceAll(" ", "") + ".png";
        if (champ.equals("Cho'Gath")) {
            champPngUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/champion/" + "Chogath" + ".png";
        } else if (champ.equals("Kog'Maw")) {
            champPngUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/champion/" + "KogMaw" + ".png";
        } else if (champ.equals("Rek'Sai")) {
            champPngUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/champion/" + "RekSai" + ".png";
        } else if (champ.equals("Vel'Koz")) {
            champPngUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/champion/" + "Velkoz" + ".png";
        } else if (champ.equals("Kha'Zix")) {
            champPngUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/champion/" + "Khazix" + ".png";
        } else if (champ.equals("Dr. Mundo")) {
            champPngUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/champion/" + "DrMundo" + ".png";
        } else if (champ.equals("LeBlanc")) {
            champPngUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/champion/" + "Leblanc" + ".png";
        } else if (champ.equals("Wukong")) {
            champPngUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/champion/" + "MonkeyKing" + ".png";
        } else if (champ.equals("Fiddlesticks")) {
            champPngUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/champion/" + "FiddleSticks" + ".png";
        }
        return champPngUrl;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String gameURL = bundle.getString("gameURL");
        mServer = bundle.getString("mServer");
        Log.i(LoginActivity.TAG, gameURL);
        setTitle("Game");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        HashMap<String, Object> info = (HashMap<String, Object>)bundle.get("inf");

        Game game = new Game(gameURL, info);
        game.execute((Void) null);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
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
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StatsFragment(), "STATS");
        adapter.addFragment(new GraphFragment(), "GRAPH");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        ArrayList<HashMap<String, Object>> obj;
        ArrayList<HashMap<String, Object>> obj2;
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        //call this method to update fragments in ViewPager dynamically
        public void update(ArrayList<HashMap<String, Object>> xyzData, ArrayList<HashMap<String, Object>> xyzData2) {
            this.obj = xyzData;
            this.obj2 = xyzData2;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof UpdateableFragment) {
                ((UpdateableFragment) object).update(obj, obj2);
            }
            //don't return POSITION_NONE, avoid fragment recreation.
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public interface UpdateableFragment {
        public void update(ArrayList<HashMap<String, Object>> xyzData, ArrayList<HashMap<String, Object>> xyzData2);
    }
    public class Game extends AsyncTask<Void, Void, Boolean> {

        private String gameURL;
        private HashMap<String, Object> inf;
        private HashMap<String, String> subdata = new HashMap<>();

        Game(String game, HashMap<String, Object> i) {
            gameURL = game;
            inf = i;

            RelativeLayout grl1 = (RelativeLayout)findViewById(R.id.gameRelativeLayout1);

            grl1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), PushupsActivity.class);
                    intent.putExtra("amount", amountPushups);
                    startActivity(intent);
                }
            });


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

        @Override
        protected Boolean doInBackground(Void... params) {
            ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> data2 = new ArrayList<HashMap<String, Object>>();
            String json = "";
            try {
                URL oracle = new URL(gameURL);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    json += inputLine;
                in.close();
            } catch (Exception e) {
                Log.i(LoginActivity.TAG, e.toString());
            }
            try{
                JsonElement jelement = new JsonParser().parse(json);
                JsonObject jobject = jelement.getAsJsonObject();
                JsonArray jarray = jobject.getAsJsonArray("participants");

                String matchDuration = jobject.get("matchDuration").toString();
                String matchCreation = jobject.get("matchCreation").toString();
                String queueType = jobject.get("queueType").toString();
                String mapId = jobject.get("mapId").toString();
                subdata.put("matchDuration", matchDuration);
                subdata.put("matchCreation", matchCreation);
                subdata.put("queueType", queueType);
                subdata.put("mapId", mapId);

                AssetManager am = getApplication().getAssets();
                //----------------------------------TEAM ONE ----------------------------
                for(int i = 0; i < 5; i++){
                    JsonElement jsonElementPlayer = jarray.get(i);
                    JsonObject jsonObjectPlayer = jsonElementPlayer.getAsJsonObject();
                    String spell1 = jsonObjectPlayer.get("spell1Id").toString();
                    String spell2 = jsonObjectPlayer.get("spell2Id").toString();
                    String championid = jsonObjectPlayer.get("championId").toString();

                    JsonObject stats = jsonObjectPlayer.getAsJsonObject("stats");

                    Map<String, Bitmap> items = new HashMap<>();
                    Bitmap nullbmap = loadPng("nullbmap");
                    for (int j = 0; j < 7; j++) {
                        String itemID = "";
                        try {
                            itemID = stats.get("item" + j).toString();
                            Bitmap itembmap = loadPng(itemID);
                            if(itembmap == null){
                                String itemLink = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/item/" + itemID + ".png";
                                itembmap = BitmapFactory.decodeStream((InputStream) new URL(itemLink).getContent());
                                savePngToInternalStorage(itembmap, itemID);
                            }else{
                                amount += 1;
                            }
                            items.put("item" + j, itembmap);
                        } catch (Exception e) {
                            try{
                                amount += 1;
                                items.put("item" + j, nullbmap);
                            }catch(Exception e1){}
                        }
                    }

                    String champ = getChamp(championid);
                    Bitmap bitmap = loadPng(champ);

                    if(bitmap == null){
                        String champPngUrl = getPngUrl(champ);
                        try {
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(champPngUrl).getContent());
                            savePngToInternalStorage(bitmap, champ);
                        } catch (Exception e) {
                            Log.i(LoginActivity.TAG, e.toString());
                        }
                    }else{
                        amount += 1;
                    }

                    boolean win = Boolean.parseBoolean(stats.get("winner").toString());
                    String Kills;
                    try{
                        Kills = stats.get("kills").toString();
                    }catch(Exception e){
                        Kills = "0";
                    }
                    String Deaths;
                    try{
                        Deaths = stats.get("deaths").toString();
                    }catch(Exception e){
                        Deaths = "0";
                    }
                    String Assists;
                    try{
                        Assists = stats.get("assists").toString();
                    }catch(Exception e){
                        Assists = "0";
                    }
                    String dealt = stats.get("totalDamageDealtToChampions").toString();
                    String taken = stats.get("totalDamageTaken").toString();
                    JsonArray participantIdentities = jobject.getAsJsonArray("participantIdentities");
                    JsonObject jobj = participantIdentities.get(i).getAsJsonObject();
                    String summName;
                    try{
                        JsonObject player = jobj.getAsJsonObject("player");
                        summName = player.get("summonerName").toString().replaceAll("\"", "");
                    }catch(Exception e){
                        summName = champ;
                    }

                    int dSpell = Integer.parseInt(spell1);
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

                    String dSpellIconUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/spell/" + key.replace("\"", "") + ".png";

                    Bitmap dBitmap = loadPng("summ" + key.replace("\"", ""));
                    if(dBitmap == null){
                        dBitmap = BitmapFactory.decodeStream((InputStream) new URL(dSpellIconUrl).getContent());
                        savePngToInternalStorage(dBitmap, "summ" + key.replace("\"", ""));
                    }else{
                        amount += 1;
                    }


                    //-- fSpell
                    int fSpell = Integer.parseInt(spell2);
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

                    String fSpellIconUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/spell/" + key2.replace("\"", "") + ".png";

                    Bitmap fBitmap = loadPng("summ" + key2.replace("\"", ""));
                    if(fBitmap == null){
                        fBitmap = BitmapFactory.decodeStream((InputStream) new URL(fSpellIconUrl).getContent());
                        savePngToInternalStorage(fBitmap, "summ" + key2.replace("\"", ""));
                    }else{
                        amount += 1;
                    }

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("bitmap", bitmap);
                    map.put("kdaFull", Kills + "/" + Deaths + "/" + Assists);
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
                    map.put("win", win);
                    map.put("summName", summName);
                    map.put("emptyicon",BitmapFactory.decodeResource(getResources(), R.drawable.emptyitem_icon));
                    map.put("dealt", dealt);
                    map.put("taken", taken);
                    data.add(map);
                }
                //-----------------------------TEAM TWO --------------------------
                for(int i = 5; i < 10; i++){
                    JsonElement jsonElementPlayer = jarray.get(i);
                    JsonObject jsonObjectPlayer = jsonElementPlayer.getAsJsonObject();
                    String spell1 = jsonObjectPlayer.get("spell1Id").toString();
                    String spell2 = jsonObjectPlayer.get("spell2Id").toString();
                    String championid = jsonObjectPlayer.get("championId").toString();

                    JsonObject stats = jsonObjectPlayer.getAsJsonObject("stats");

                    Map<String, Bitmap> items = new HashMap<>();
                    Bitmap nullbmap = loadPng("nullbmap");
                    for (int j = 0; j < 7; j++) {
                        String itemID = "";
                        try {
                            itemID = stats.get("item" + j).toString();
                            Bitmap itembmap = loadPng(itemID);
                            if(itembmap == null){
                                String itemLink = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/item/" + itemID + ".png";
                                itembmap = BitmapFactory.decodeStream((InputStream) new URL(itemLink).getContent());
                                savePngToInternalStorage(itembmap, itemID);
                            }else{
                                amount += 1;
                            }
                            items.put("item" + j, itembmap);
                        } catch (Exception e) {
                            try{
                                amount += 1;
                                items.put("item" + j, nullbmap);
                            }catch(Exception e1){}
                        }
                    }
                    String champ = getChamp(championid);
                    Bitmap bitmap = loadPng(champ);

                    if(bitmap == null){
                        String champPngUrl = getPngUrl(champ);
                        try {
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(champPngUrl).getContent());
                            savePngToInternalStorage(bitmap, champ);
                        } catch (Exception e) {
                            Log.i(LoginActivity.TAG, e.toString());
                        }
                    }else{
                        amount += 1;
                    }

                    boolean win = Boolean.parseBoolean(stats.get("winner").toString());
                    String Kills;
                    try{
                        Kills = stats.get("kills").toString();
                    }catch(Exception e){
                        Kills = "0";
                    }
                    String Deaths;
                    try{
                        Deaths = stats.get("deaths").toString();
                    }catch(Exception e){
                        Deaths = "0";
                    }
                    String Assists;
                    try{
                        Assists = stats.get("assists").toString();
                    }catch(Exception e){
                        Assists = "0";
                    }
                    String dealt = stats.get("totalDamageDealtToChampions").toString();
                    String taken = stats.get("totalDamageTaken").toString();
                    String rank = jsonObjectPlayer.get("highestAchievedSeasonTier").toString();

                    JsonArray participantIdentities = jobject.getAsJsonArray("participantIdentities");
                    JsonObject jobj = participantIdentities.get(i).getAsJsonObject();
                    String summName;
                    try{
                        JsonObject player = jobj.getAsJsonObject("player");
                        summName = player.get("summonerName").toString().replaceAll("\"", "");
                    }catch(Exception e){
                        summName = champ;
                    }


                    //-- dSpell
                    int dSpell = Integer.parseInt(spell1);
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
                    String dSpellIconUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/spell/" + key.replace("\"", "") + ".png";

                    Bitmap dBitmap = loadPng("summ" + key.replace("\"", ""));
                    if(dBitmap == null){
                        dBitmap = BitmapFactory.decodeStream((InputStream) new URL(dSpellIconUrl).getContent());
                        savePngToInternalStorage(dBitmap, "summ" + key.replace("\"", ""));
                    }else{
                        amount += 1;
                    }


                    //-- fSpell
                    int fSpell = Integer.parseInt(spell2);
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

                    String fSpellIconUrl = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.mostrecent + "/img/spell/" + key2.replace("\"", "") + ".png";

                    Bitmap fBitmap = loadPng("summ" + key2.replace("\"", ""));
                    if(fBitmap == null){
                        fBitmap = BitmapFactory.decodeStream((InputStream) new URL(fSpellIconUrl).getContent());
                        savePngToInternalStorage(fBitmap, "summ" + key2.replace("\"", ""));
                    }else{
                        amount += 1;
                    }

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("bitmap", bitmap);
                    map.put("kdaFull", Kills + "/" + Deaths + "/" + Assists);
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
                    map.put("win", win);
                    map.put("summName", summName);
                    map.put("rank", rank);
                    map.put("emptyicon",BitmapFactory.decodeResource(getResources(), R.drawable.emptyitem_icon));
                    map.put("dealt", dealt);
                    map.put("taken", taken);
                    data2.add(map);
                }
                Log.i(LoginActivity.TAG, "Amount of images loaded from internal storage:" + amount);
                final ArrayList<HashMap<String, Object>> dal = data;
                final ArrayList<HashMap<String, Object>> dal2 = data2;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(LoginActivity.TAG, "setting player adapter");
                        ListView list = (ListView) findViewById(R.id.ListTeam1);

                        list.setScrollingCacheEnabled(false);

                        PlayersAdapter adapter1 = new PlayersAdapter(getApplication(), dal, dal2);
                        list.setAdapter(adapter1);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                TextView tv = (TextView) view.findViewById(R.id.gameSummName);
                                Log.i(LoginActivity.TAG, tv.getText().toString());
                            }
                        });
                        listViewAdjust(list);

                        adapter.update(dal, dal2);

                    }
                });
            }catch(Exception e){
                Log.i(LoginActivity.TAG, e.toString());
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            LinearLayout ll = (LinearLayout)findViewById(R.id.gameLLChampImg);
            final LinearLayout champll = ll;
            champll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    champll.setLayoutParams(new LinearLayout.LayoutParams(champll.getHeight(), champll.getHeight()));
                }
            });

            ImageView champImg = (ImageView)findViewById(R.id.gameChampImg);
            champImg.setImageBitmap((Bitmap) inf.get("bitmap"));

            TextView tvAmountPushups = (TextView)findViewById(R.id.gameTvAmountPushups);
            tvAmountPushups.setText("" + (int) inf.get("amountPushups"));
            amountPushups = (int) inf.get("amountPushups");

            int aGold = Integer.parseInt(inf.get("gold").toString());
            double value = (double)aGold / 1000;
            double result = Math.round(value * 10.0) / 10.0;

            TextView gold = (TextView)findViewById(R.id.gameGold);
            gold.setText(result + "k");

            TextView cs = (TextView)findViewById(R.id.gameCS);
            cs.setText("" + inf.get("cs"));

            TextView level = (TextView)findViewById(R.id.gameLevel);
            level.setText("" + inf.get("level"));

            TextView KDA = (TextView)findViewById(R.id.gameKDA);
            KDA.setText("" + inf.get("KDA"));

            TextView tvKdaFull = (TextView)findViewById(R.id.gameTvKdaFull);
            tvKdaFull.setText((String)inf.get("kdaFull"));
            if ((boolean)inf.get("win")) {
                tvKdaFull.setTextColor(Color.parseColor(LazyAdapter.COLOR_WIN));
            } else {
                tvKdaFull.setTextColor(Color.parseColor(LazyAdapter.COLOR_LOSE));
            }

            TextView tvChampName = (TextView)findViewById(R.id.gameTvChampName);
            tvChampName.setText((String) inf.get("champ"));

            TextView gameGametime = (TextView)findViewById(R.id.gameGametime);
            TextView gameCreationtime = (TextView)findViewById(R.id.gameCreationTime);
            TextView gameGamemode = (TextView)findViewById(R.id.gameGamemode);
            String hhmmss = "";
            try{
                int hr = Integer.parseInt(subdata.get("matchDuration")) / 3600;
                int rem = Integer.parseInt(subdata.get("matchDuration")) % 3600;
                int mn = rem / 60;
                int sec = rem % 60;
                String hrStr = "" + hr;
                String mnStr = (mn < 10 ? "0" : "") + mn;
                String secStr = (sec < 10 ? "0" : "") + sec;

                if (!hrStr.equals("0")) {
                    hhmmss = hrStr + ":" + mnStr + ":" + secStr;
                } else {
                    hhmmss = mnStr + ":" + secStr;
                }
            }catch(Exception e){

            }
            gameGametime.setText(hhmmss);

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            try{
                Date resultdate = new Date((Long.parseLong(subdata.get("matchCreation")) + Long.parseLong(subdata.get("matchDuration"))));
                gameCreationtime.setText(df.format(resultdate));

                String input = subdata.get("queueType").replaceAll("_", " ").replaceAll("\"", "").toLowerCase();
                gameGamemode.setText(input.substring(0, 1).toUpperCase() + input.substring(1));
            }catch(Exception e){
                Log.i(LoginActivity.TAG, e.toString());
            }


            /*String map = "";
            String mapId = subdata.get("mapId");
            if(mapId.equals("1") || mapId.equals("2") || mapId.equals("11")){
                map = "Summoner's Rift";
            }else if(mapId.equals("3")){
                map = "The Proving Grounds";
            }else if(mapId.equals("4") || mapId.equals("10")){
                map = "Twisted Treeline";
            }else if(mapId.equals("8")){
                map = "The Crystal Scar";
            }else if(mapId.equals("12")){
                map = "Howling Abyss";
            }else if(mapId.equals("14")){
                map = "Butcher's Bridge";
            }*/

            ProgressBar game_marker = (ProgressBar)findViewById(R.id.game_marker);
            game_marker.setVisibility(View.GONE);

            RelativeLayout container = (RelativeLayout)findViewById(R.id.container);
            container.setVisibility(View.VISIBLE);
        }
    }
    public void listViewAdjust (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }
}
