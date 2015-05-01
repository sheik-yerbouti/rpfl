package org.rpfl;


import org.freedesktop.dbus.DBusInterface;

import java.util.List;

public interface RpflInterface extends DBusInterface {
    void prepare(List<String> urls);
    boolean validate(List<String> urls, byte[] expectedHash);
}
