package barqsoft.footballscores.app.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.commons.Day;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public final class PagerFragment extends Fragment {
    private static final String TAG = PagerFragment.class.getSimpleName();
    private static final int NUM_PAGES = 5;
    public ViewPager pagerHandler;
    private PagerAdapter pagerAdapter;
    private MainFragment[] viewFragments = new MainFragment[NUM_PAGES];

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        pagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getChildFragmentManager());

        for (int i = 0; i<NUM_PAGES; i++) {
            Calendar fragmentCalendar = Calendar.getInstance();
            fragmentCalendar.setTimeInMillis(System.currentTimeMillis());

            fragmentCalendar.add(Calendar.DATE, i - 2);

            long current = fragmentCalendar.getTimeInMillis();
            long start = Day.getStart(current);
            long end = Day.getEnd(current);

            viewFragments[i] = new MainFragment();
            viewFragments[i].setStartOfDay(start);
            viewFragments[i].setEndOfDay(end);
        }

        pagerHandler.setAdapter(pagerAdapter);
        pagerHandler.setCurrentItem(MainActivity.currentFragment);
        return rootView;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        @Override
        public Fragment getItem(int i) {
            return viewFragments[i];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return getDayName(getActivity(),System.currentTimeMillis() + ((position-2) * 86400000));
        }

        private String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if (julianDay == currentJulianDay + 1) {
                return context.getString(R.string.tomorrow);
            }
            else if (julianDay == currentJulianDay - 1) {
                return context.getString(R.string.yesterday);
            }
            else {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                String day = dayFormat.format(dateInMillis);
                return day.substring(0, 1).toUpperCase() + day.substring(1);
            }
        }
    }
}
