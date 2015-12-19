package barqsoft.footballscores.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import barqsoft.footballscores.app.about.AboutActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.contract.BundleContract;

public final class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static int selectedMatchId;
    public static int currentFragment = 2;
    public static int matchPosition;
    private PagerFragment pagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            pagerFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, pagerFragment)
                    .commit();
        }

        Intent intent = getIntent();
        if (intent != null) {
            selectedMatchId = intent.getIntExtra(BundleContract.MATCH_ID, 0);
            matchPosition = intent.getIntExtra(BundleContract.MATCH_POSITION, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(BundleContract.PAGER_CURRENT, pagerFragment.pagerHandler.getCurrentItem());
        outState.putInt(BundleContract.SELECTED_MATCH_ID, selectedMatchId);
        getSupportFragmentManager().putFragment(outState,BundleContract.PAGER_FRAGMENT_KEY, pagerFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        currentFragment = savedInstanceState.getInt(BundleContract.PAGER_CURRENT);
        selectedMatchId = savedInstanceState.getInt(BundleContract.SELECTED_MATCH_ID);
        pagerFragment = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState, BundleContract.PAGER_FRAGMENT_KEY);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
