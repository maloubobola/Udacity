package thiebaudthomas.display;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        String joke = intent.getStringExtra(IntentContract.JOKE);

        TextView jokeDisplay = (TextView) findViewById(R.id.joke_display);
        jokeDisplay.setText(joke);
    }
}
