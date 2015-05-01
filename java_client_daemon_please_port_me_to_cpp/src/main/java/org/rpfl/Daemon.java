package org.rpfl;

import com.google.common.collect.ImmutableSet;
import org.abstractj.kalium.keys.VerifyKey;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import static java.lang.Thread.currentThread;
import static org.abstractj.kalium.encoders.Encoder.HEX;
import static org.freedesktop.dbus.DBusConnection.SESSION;
import static org.freedesktop.dbus.DBusConnection.getConnection;

public class Daemon {

    public static void main(String[] args) throws DBusException, NoSuchAlgorithmException, MalformedURLException {
        DBusConnection connection = getConnection(SESSION);

        connection.requestBusName("rpfl.rpfld");

        Set<TrustedEndpoint> trustedEndpoints = ImmutableSet.of(
                new TrustedEndpoint(
                        new URL("http://localhost:8080/rpfl-server/rpfl-server"),
                        new VerifyKey("42864c5ec14a431789b998fc4ecc7561b81eb4d10a8fdffe5af0c42a2151adf1", HEX)
                )
        );

        connection.exportObject("/", new RpflImpl(trustedEndpoints));

        currentThread().setDaemon(true);
    }
}
