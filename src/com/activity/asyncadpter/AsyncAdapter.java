package com.activity.asyncadpter;


import com.fedorvlasov.lazylist.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AsyncAdapter extends BaseAdapter {
    
    private Activity activity;
    private String[] data,usernames,tweets;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    private int[] colors = new int[] { 0x003399, 0x003399 };
    public AsyncAdapter(Activity a, String[] d,String[] u,String[] t) {
        activity = a;
        data=d;
        usernames=u;
        tweets=t;        
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.item, null);

        int colorPos = position % colors.length;
        vi.setBackgroundColor(colors[colorPos]);
        
        TextView text=(TextView)vi.findViewById(R.id.text);
        TextView tweet=(TextView)vi.findViewById(R.id.tweet);
        ImageView image=(ImageView)vi.findViewById(R.id.image);
        text.setText(usernames[position]);
        tweet.setText(tweets[position]);
        imageLoader.DisplayImage(data[position], image);
        return vi;
    }
    





}