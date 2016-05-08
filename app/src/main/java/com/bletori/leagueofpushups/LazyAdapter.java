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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LazyAdapter extends BaseAdapter {
    private Application activity;
    private ArrayList<HashMap<String, Object>> data;
    private static LayoutInflater inflater=null;
    static String COLOR_WIN = "#04D604";
    static String COLOR_LOSE = "#FF0000";

    public LazyAdapter(Application a, ArrayList<HashMap<String, Object>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        if(vi==null){
            vi = inflater.inflate(R.layout.list_one_match, null);
            holder = new ViewHolder();

            holder.champView = (ImageView)vi.findViewById(R.id.champImg);
            holder.tvAmountPushups = (TextView) vi.findViewById(R.id.tvAmountPushups);
            holder.tvKdaFull = (TextView) vi.findViewById(R.id.tvKdaFull);
            holder.tvChampName = (TextView) vi.findViewById(R.id.tvChampName);
            holder.tvGameTime = (TextView) vi.findViewById(R.id.tvGameTime);
            holder.dView = (ImageView) vi.findViewById(R.id.dView);
            holder.fView = (ImageView) vi.findViewById(R.id.fView);
            holder.i1View = (ImageView) vi.findViewById(R.id.i1View);
            holder.i2View = (ImageView) vi.findViewById(R.id.i2View);
            holder.i3View = (ImageView) vi.findViewById(R.id.i3View);
            holder.i4View = (ImageView) vi.findViewById(R.id.i4View);
            holder.i5View = (ImageView) vi.findViewById(R.id.i5View);
            holder.i6View = (ImageView) vi.findViewById(R.id.i6View);
            holder.i7View = (ImageView) vi.findViewById(R.id.i7View);
            holder.champImg = (LinearLayout)vi.findViewById(R.id.llChampImg);
            holder.lld = (LinearLayout)vi.findViewById(R.id.lld);

            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
        }

        setImages set = new setImages(inf, holder);
        set.execute((Void) null);
        return vi;
    }
    static class ViewHolder {
        TextView tvAmountPushups;
        TextView tvKdaFull;
        ImageView champView;
        TextView tvChampName;
        TextView tvGameTime;
        ImageView dView;
        ImageView fView;
        ImageView i1View;
        ImageView i2View;
        ImageView i3View;
        ImageView i4View;
        ImageView i5View;
        ImageView i6View;
        ImageView i7View;
        LinearLayout champImg;
        LinearLayout lld;
    }

    public class setImages extends AsyncTask<Void, Void, Boolean> {

        private HashMap<String, Object> inf;
        private ViewHolder holder;

        setImages(HashMap<String, Object> m, ViewHolder h) {
            inf = m;
            holder = h;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            holder.champView.setImageBitmap((Bitmap)inf.get("bitmap"));
            holder.tvAmountPushups.setText("" + (int)inf.get("amountPushups"));
            holder.tvKdaFull.setText((String)inf.get("kdaFull"));
            if ((boolean)inf.get("win")) {
                holder.tvKdaFull.setTextColor(Color.parseColor(COLOR_WIN));
            } else {
                holder.tvKdaFull.setTextColor(Color.parseColor(COLOR_LOSE));
            }
            holder.tvChampName.setText((String)inf.get("champ"));

            int hr = (int)inf.get("secondsplayed") / 3600;
            int rem = (int)inf.get("secondsplayed") % 3600;
            int mn = rem / 60;
            int sec = rem % 60;
            String hrStr = "" + hr;
            String mnStr = (mn < 10 ? "0" : "") + mn;
            String secStr = (sec < 10 ? "0" : "") + sec;

            String hhmmss;

            if (!hrStr.equals("0")) {
                hhmmss = hrStr + ":" + mnStr + ":" + secStr;
            } else {
                hhmmss = mnStr + ":" + secStr;
            }
            holder.tvGameTime.setText(hhmmss);

            if ((Bitmap)inf.get("dBitmap") != null) {
                holder.dView.setImageBitmap((Bitmap)inf.get("dBitmap"));
            }else{
                holder.dView.setImageResource(R.drawable.emptyitem_icon);
            }

            if ((Bitmap)inf.get("fBitmap") != null) {
                holder.fView.setImageBitmap((Bitmap)inf.get("fBitmap"));
            }else{
                holder.fView.setImageResource(R.drawable.emptyitem_icon);
            }

            if ((Bitmap)inf.get("item0") != null) {
                holder.i1View.setImageBitmap((Bitmap)inf.get("item0"));
            }else{
                holder.i1View.setImageResource(R.drawable.emptyitem_icon);
            }
            if ((Bitmap)inf.get("item1") != null) {
                holder.i2View.setImageBitmap((Bitmap)inf.get("item1"));
            }else{
                holder.i2View.setImageResource(R.drawable.emptyitem_icon);
            }
            if ((Bitmap)inf.get("item2") != null) {
                holder.i3View.setImageBitmap((Bitmap)inf.get("item2"));
            }else{
                holder.i3View.setImageResource(R.drawable.emptyitem_icon);
            }

            if ((Bitmap)inf.get("item3") != null) {
                holder.i4View.setImageBitmap((Bitmap)inf.get("item3"));
            }else{
                holder.i4View.setImageResource(R.drawable.emptyitem_icon);
            }

            if ((Bitmap)inf.get("item4") != null) {
                holder.i5View.setImageBitmap((Bitmap)inf.get("item4"));
            }else{
                holder.i5View.setImageResource(R.drawable.emptyitem_icon);
            }

            if ((Bitmap)inf.get("item5") != null) {
                holder.i6View.setImageBitmap((Bitmap)inf.get("item5"));
            }else{
                holder.i6View.setImageResource(R.drawable.emptyitem_icon);
            }

            if ((Bitmap)inf.get("item6") != null) {
                holder.i7View.setImageBitmap((Bitmap)inf.get("item6"));
            }else{
                holder.i7View.setImageResource(R.drawable.emptyitem_icon);
            }

            final LinearLayout champImg = holder.champImg;
            holder.champImg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    champImg.setLayoutParams(new LinearLayout.LayoutParams(champImg.getHeight(), champImg.getHeight()));
                }
            });

            final LinearLayout lld = holder.lld;
            lld.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    lld.setLayoutParams(new LinearLayout.LayoutParams(lld.getWidth(), lld.getWidth()));
                }
            });
        }
    }
}