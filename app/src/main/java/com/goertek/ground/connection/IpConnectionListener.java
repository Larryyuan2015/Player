package com.goertek.ground.connection;

import java.nio.ByteBuffer;

/**
 * Provides updates about the connection.
 */
public interface IpConnectionListener {

    void onIpConnected();

    void onIpDisconnected();

    void onPacketReceived(ByteBuffer packetBuffer);
}
