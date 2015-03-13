package org.rpfl.db.dao;

import com.google.inject.Singleton;
import org.rpfl.db.domain.ResourceFingerprint;

import java.util.function.Predicate;

import static org.rpfl.transport.protobuf.Messages.VerificationStrength.notfound;

@Singleton
public class WasFoundPredicate implements Predicate<ResourceFingerprint> {
    @Override
    public boolean test(ResourceFingerprint resourceFingerprint) {
        return !notfound.equals(resourceFingerprint.getVerificationStrength());
    }
}
