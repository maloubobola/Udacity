package com.example.android.sunshine.app.sync;

import com.example.android.sunshine.app.commons.contract.DataContract;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by thiebaudthomas on 26/01/16.
 */
public class SunshineWearService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                String path = dataEvent.getDataItem().getUri().getPath();
                if (path.equals(DataContract.WEATHER)) {
                    SunshineSyncAdapter.syncImmediately(this);
                }
            }
        }
    }
}
