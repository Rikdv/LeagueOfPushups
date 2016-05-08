package com.bletori.leagueofpushups;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayersAdapter  extends BaseAdapter {
    private Application activity;
    private ArrayList<HashMap<String, Object>> data;
    private ArrayList<HashMap<String, Object>> data2;
    private static LayoutInflater inflater=null;


    public PlayersAdapter(Application a, ArrayList<HashMap<String, Object>> d, ArrayList<HashMap<String, Object>> d2) {
        activity = a;
        data=d;
        data2=d2;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View vi, ViewGroup parent) {
        ViewHolder holder;
        HashMap<String, Object> inf = data.get(position);
        HashMap<String, Object> inf2 = data2.get(position);

        if(vi==null){
            vi = inflater.inflate(R.layout.list_one_player, null);
            holder = new ViewHolder();

            holder.champView = (ImageView)vi.findViewById(R.id.onep_champ);
            holder.tvKdaFull = (TextView) vi.findViewById(R.id.onep_kda);
            holder.dView = (ImageView) vi.findViewById(R.id.gameD);
            holder.fView = (ImageView) vi.findViewById(R.id.gameF);
            holder.i1View = (ImageView) vi.findViewById(R.id.onep_item0);
            holder.i2View = (ImageView) vi.findViewById(R.id.onep_item1);
            holder.i3View = (ImageView) vi.findViewById(R.id.onep_item2);
            holder.i4View = (ImageView) vi.findViewById(R.id.onep_item3);
            holder.i5View = (ImageView) vi.findViewById(R.id.onep_item4);
            holder.i6View = (ImageView) vi.findViewById(R.id.onep_item5);
            holder.i7View = (ImageView) vi.findViewById(R.id.onep_item6);
            holder.summName = (TextView) vi.findViewById(R.id.gameSummName);
            holder.llgameF = (LinearLayout)vi.findViewById(R.id.llgameF);
            holder.llgameD = (LinearLayout)vi.findViewById(R.id.llgameD);

            holder.champView2 = (ImageView)vi.findViewById(R.id.onep_champ2);
            holder.tvKdaFull2 = (TextView) vi.findViewById(R.id.onep_kda2);
            holder.dView2 = (ImageView) vi.findViewById(R.id.gameD2);
            holder.fView2 = (ImageView) vi.findViewById(R.id.gameF2);
            holder.i1View2 = (ImageView) vi.findViewById(R.id.onep_item02);
            holder.i2View2 = (ImageView) vi.findViewById(R.id.onep_item12);
            holder.i3View2 = (ImageView) vi.findViewById(R.id.onep_item22);
            holder.i4View2 = (ImageView) vi.findViewById(R.id.onep_item32);
            holder.i5View2 = (ImageView) vi.findViewById(R.id.onep_item42);
            holder.i6View2 = (ImageView) vi.findViewById(R.id.onep_item52);
            holder.i7View2 = (ImageView) vi.findViewById(R.id.onep_item62);
            holder.summName2 = (TextView) vi.findViewById(R.id.gameSummName2);
            holder.llgameF2 = (LinearLayout)vi.findViewById(R.id.llgameF2);
            holder.llgameD2 = (LinearLayout)vi.findViewById(R.id.llgameD2);

            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
        }

        setImages set = new setImages(inf, inf2, holder);
        set.execute((Void) null);
        return vi;
    }
    static class ViewHolder {
        TextView tvKdaFull;
        ImageView champView;
        ImageView dView;
        ImageView fView;
        ImageView i1View;
        ImageView i2View;
        ImageView i3View;
        ImageView i4View;
        ImageView i5View;
        ImageView i6View;
        ImageView i7View;
        TextView summName;
        LinearLayout llgameF;
        LinearLayout llgameD;

        TextView tvKdaFull2;
        ImageView champView2;
        ImageView dView2;
        ImageView fView2;
        ImageView i1View2;
        ImageView i2View2;
        ImageView i3View2;
        ImageView i4View2;
        ImageView i5View2;
        ImageView i6View2;
        ImageView i7View2;
        TextView summName2;
        LinearLayout llgameF2;
        LinearLayout llgameD2;
    }

    public class setImages extends AsyncTask<Void, Void, Boolean> {

        private HashMap<String, Object> inf;
        private HashMap<String, Object> inf2;
        private ViewHolder holder;

        setImages(HashMap<String, Object> m, HashMap<String, Object> m2,  ViewHolder h) {
            inf = m;
            inf2 = m2;
            holder = h;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            holder.champView.setImageBitmap((Bitmap) inf.get("bitmap"));
            final ImageView champView = holder.champView;
            champView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int hw = champView.getHeight();
                    champView.setLayoutParams(new RelativeLayout.LayoutParams(hw, hw));
                }
            });
            holder.summName.setText((String) inf.get("summName"));
            holder.tvKdaFull.setText((String)inf.get("kdaFull"));

            if ((boolean)inf.get("win")) {
                holder.tvKdaFull.setTextColor(Color.parseColor(LazyAdapter.COLOR_WIN));
            } else {
                holder.tvKdaFull.setTextColor(Color.parseColor(LazyAdapter.COLOR_LOSE));
            }

            if ((Bitmap)inf.get("dBitmap") != null) {
                holder.dView.setImageBitmap((Bitmap)inf.get("dBitmap"));
            }else{
                holder.dView.setImageBitmap((Bitmap)inf.get("dBitmap"));
                holder.dView.setImageBitmap((Bitmap)inf.get("emptyicon"));
            }

            if ((Bitmap)inf.get("fBitmap") != null) {
                holder.fView.setImageBitmap((Bitmap)inf.get("fBitmap"));
            }else{
                holder.dView.setImageBitmap((Bitmap)inf.get("dBitmap"));
                holder.fView.setImageBitmap((Bitmap)inf.get("emptyicon"));
            }

            if ((Bitmap)inf.get("item0") != null) {
                holder.i1View.setImageBitmap((Bitmap)inf.get("item0"));
            }else{
                holder.i1View.setImageBitmap((Bitmap)inf.get("dBitmap"));
                holder.i1View.setImageBitmap((Bitmap)inf.get("emptyicon"));
            }

            if ((Bitmap)inf.get("item1") != null) {
                holder.i2View.setImageBitmap((Bitmap)inf.get("item1"));
            }else{
                holder.i2View.setImageBitmap((Bitmap)inf.get("dBitmap"));
                holder.i2View.setImageBitmap((Bitmap)inf.get("emptyicon"));
            }

            if ((Bitmap)inf.get("item2") != null) {
                holder.i3View.setImageBitmap((Bitmap)inf.get("item2"));
            }else{
                holder.i3View.setImageBitmap((Bitmap)inf.get("dBitmap"));
                holder.i3View.setImageBitmap((Bitmap)inf.get("emptyicon"));
            }

            if ((Bitmap)inf.get("item3") != null) {
                holder.i4View.setImageBitmap((Bitmap)inf.get("item3"));
            }else{
                holder.i4View.setImageBitmap((Bitmap)inf.get("dBitmap"));
                holder.i4View.setImageBitmap((Bitmap)inf.get("emptyicon"));
            }

            if ((Bitmap)inf.get("item4") != null) {
                holder.i5View.setImageBitmap((Bitmap)inf.get("item4"));
            }else{
                holder.i5View.setImageBitmap((Bitmap)inf.get("dBitmap"));
                holder.i5View.setImageBitmap((Bitmap)inf.get("emptyicon"));
            }

            if ((Bitmap)inf.get("item5") != null) {
                holder.i6View.setImageBitmap((Bitmap)inf.get("item5"));
            }else{
                holder.i6View.setImageBitmap((Bitmap)inf.get("dBitmap"));
                holder.i6View.setImageBitmap((Bitmap)inf.get("emptyicon"));
            }

            if ((Bitmap)inf.get("item6") != null) {
                holder.i7View.setImageBitmap((Bitmap)inf.get("item6"));
            }else{
                holder.i7View.setImageBitmap((Bitmap)inf.get("dBitmap"));
                holder.i7View.setImageBitmap((Bitmap)inf.get("emptyicon"));
            }

            final ImageView gameD = holder.dView;
            gameD.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    gameD.setLayoutParams(new LinearLayout.LayoutParams(gameD.getWidth(), gameD.getWidth()));
                }
            });

            final ImageView gameF = holder.fView;
            gameF.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    gameF.setLayoutParams(new LinearLayout.LayoutParams(gameF.getWidth(), gameF.getWidth()));
                }
            });

            final LinearLayout llGameF = holder.llgameF;
            llGameF.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    llGameF.setLayoutParams(new LinearLayout.LayoutParams(llGameF.getWidth(), llGameF.getWidth()));
                }
            });

            final LinearLayout llGameD = holder.llgameD;
            llGameD.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    llGameD.setLayoutParams(new LinearLayout.LayoutParams(llGameD.getWidth(), llGameD.getWidth()));
                }
            });

            //------------------ SECOND TEAM ---------------------------

            holder.champView2.setImageBitmap((Bitmap) inf2.get("bitmap"));
            final ImageView champView2 = holder.champView2;
            champView2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int hw = champView2.getHeight();
                    champView2.setLayoutParams(new RelativeLayout.LayoutParams(hw, hw));
                }
            });
            holder.summName2.setText((String) inf2.get("summName"));
            holder.tvKdaFull2.setText((String)inf2.get("kdaFull"));

            if ((boolean)inf2.get("win")) {
                holder.tvKdaFull2.setTextColor(Color.parseColor(LazyAdapter.COLOR_WIN));
            } else {
                holder.tvKdaFull2.setTextColor(Color.parseColor(LazyAdapter.COLOR_LOSE));
            }

            if ((Bitmap)inf2.get("dBitmap") != null) {
                holder.dView2.setImageBitmap((Bitmap)inf2.get("dBitmap"));
            }else{
                holder.dView2.setImageBitmap((Bitmap)inf2.get("dBitmap"));
                holder.dView2.setImageBitmap((Bitmap)inf2.get("emptyicon"));
            }

            if ((Bitmap)inf2.get("fBitmap") != null) {
                holder.fView2.setImageBitmap((Bitmap)inf2.get("fBitmap"));
            }else{
                holder.dView2.setImageBitmap((Bitmap)inf2.get("dBitmap"));
                holder.fView2.setImageBitmap((Bitmap)inf2.get("emptyicon"));
            }

            if ((Bitmap)inf2.get("item0") != null) {
                holder.i1View2.setImageBitmap((Bitmap)inf2.get("item0"));
            }else{
                holder.i1View2.setImageBitmap((Bitmap)inf2.get("dBitmap"));
                holder.i1View2.setImageBitmap((Bitmap)inf2.get("emptyicon"));
            }

            if ((Bitmap)inf2.get("item1") != null) {
                holder.i2View2.setImageBitmap((Bitmap)inf2.get("item1"));
            }else{
                holder.i2View2.setImageBitmap((Bitmap)inf2.get("dBitmap"));
                holder.i2View2.setImageBitmap((Bitmap)inf2.get("emptyicon"));
            }

            if ((Bitmap)inf2.get("item2") != null) {
                holder.i3View2.setImageBitmap((Bitmap)inf2.get("item2"));
            }else{
                holder.i3View2.setImageBitmap((Bitmap)inf2.get("dBitmap"));
                holder.i3View2.setImageBitmap((Bitmap)inf2.get("emptyicon"));
            }

            if ((Bitmap)inf2.get("item3") != null) {
                holder.i4View2.setImageBitmap((Bitmap)inf2.get("item3"));
            }else{
                holder.i4View2.setImageBitmap((Bitmap)inf2.get("dBitmap"));
                holder.i4View2.setImageBitmap((Bitmap)inf2.get("emptyicon"));
            }

            if ((Bitmap)inf2.get("item4") != null) {
                holder.i5View2.setImageBitmap((Bitmap)inf2.get("item4"));
            }else{
                holder.i5View2.setImageBitmap((Bitmap)inf2.get("dBitmap"));
                holder.i5View2.setImageBitmap((Bitmap)inf2.get("emptyicon"));
            }

            if ((Bitmap)inf2.get("item5") != null) {
                holder.i6View2.setImageBitmap((Bitmap)inf2.get("item5"));
            }else{
                holder.i6View2.setImageBitmap((Bitmap)inf2.get("dBitmap"));
                holder.i6View2.setImageBitmap((Bitmap)inf2.get("emptyicon"));
            }

            if ((Bitmap)inf2.get("item6") != null) {
                holder.i7View2.setImageBitmap((Bitmap)inf2.get("item6"));
            }else{
                holder.i7View2.setImageBitmap((Bitmap)inf2.get("dBitmap"));
                holder.i7View2.setImageBitmap((Bitmap)inf2.get("emptyicon"));
            }

            final ImageView gameD2 = holder.dView;
            gameD2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    gameD2.setLayoutParams(new LinearLayout.LayoutParams(gameD2.getWidth(), gameD2.getWidth()));
                }
            });

            final ImageView gameF2 = holder.fView;
            gameF2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    gameF2.setLayoutParams(new LinearLayout.LayoutParams(gameF2.getWidth(), gameF2.getWidth()));
                }
            });

            final LinearLayout llGameF2 = holder.llgameF;
            llGameF2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    llGameF2.setLayoutParams(new LinearLayout.LayoutParams(llGameF2.getWidth(), llGameF2.getWidth()));
                }
            });

            final LinearLayout llGameD2 = holder.llgameD;
            llGameD2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    llGameD2.setLayoutParams(new LinearLayout.LayoutParams(llGameD2.getWidth(), llGameD2.getWidth()));
                }
            });
        }
    }
}