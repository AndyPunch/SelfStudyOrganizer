package ru.andypunch.ssorganizer.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ru.andypunch.ssorganizer.R;


public class About extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        TextView about = (TextView) findViewById(R.id.tv_about);
        String htmlAsString = getString(R.string.about_html);
        Spanned htmlAsSpanned = Html.fromHtml(htmlAsString);
        about.setText(htmlAsSpanned);
        about.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
