package com.goertek.ground.connection;

import android.os.Bundle;


import com.o3dr.services.android.lib.model.ICommandListener;

public class DataLink {

    public interface DataLinkProvider<T> {

        void sendMessage(T message, ICommandListener listener);

        boolean isConnected();

        void openConnection();

        void closeConnection();

        Bundle getConnectionExtras();

    }

    public interface DataLinkListener<T> {

        void notifyReceivedData(T packet);

        void onConnectionStatus(LinkConnectionStatus connectionStatus);
    }
}
