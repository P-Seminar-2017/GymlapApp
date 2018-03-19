package de.gymnasium_lappersdorf.gymlapapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by Leon on 03.01.2018.
 */

public class NewsItemView extends RelativeLayout {

    TextView title, txt, date;
    ImageView img;
    String href;

    public NewsItemView(Context context) {
        super(context);
        init();
    }

    public NewsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //initializes all the needed views
        inflate(getContext(), R.layout.newsitemview, this);
        title = findViewById(R.id.title_home);
        txt = findViewById(R.id.txt_home);
        date = findViewById(R.id.date_home);
        img = findViewById(R.id.img_home);
    }

    public void setParams(newsitem n) {
        title.setText(n.getTitle());
        txt.setText(n.getTxt());
        date.setText(n.getDate());
        href = n.getHref();
        Glide.with(getContext())
                .load(n.getImg())
                .apply(new RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.imagebroken))
                .into(img);
    }

    public String getHref() {
        return href;
    }

}
