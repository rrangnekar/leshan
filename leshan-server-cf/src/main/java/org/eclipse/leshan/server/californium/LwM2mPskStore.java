/*******************************************************************************
 * Copyright (c) 2013-2015 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan.server.californium;

import java.net.InetSocketAddress;

import javax.crypto.SecretKey;

import org.eclipse.californium.scandium.dtls.PskPublicInformation;
import org.eclipse.californium.scandium.dtls.pskstore.PskStore;
import org.eclipse.californium.scandium.util.SecretUtil;
import org.eclipse.californium.scandium.util.ServerNames;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationStore;
import org.eclipse.leshan.server.security.SecurityInfo;
import org.eclipse.leshan.server.security.SecurityStore;

public class LwM2mPskStore implements PskStore {

    private SecurityStore securityStore;
    private RegistrationStore registrationStore;

    public LwM2mPskStore(SecurityStore securityStore) {
        this(securityStore, null);
    }

    public LwM2mPskStore(SecurityStore securityStore, RegistrationStore registrationStore) {
        this.securityStore = securityStore;
        this.registrationStore = registrationStore;
    }

    @Override
    public SecretKey getKey(PskPublicInformation identity) {
        if (securityStore == null)
            return null;

        SecurityInfo info = securityStore.getByIdentity(identity.getPublicInfoAsString());
        if (info == null || info.getPreSharedKey() == null) {
            return null;
        } else {
            // defensive copy
            return SecretUtil.create(info.getPreSharedKey(), "PSK");
        }
    }

    @Override
    public SecretKey getKey(ServerNames serverNames, PskPublicInformation identity) {
        // serverNames is not supported
        return getKey(identity);
    }

    @Override
    public PskPublicInformation getIdentity(InetSocketAddress inetAddress) {
        if (registrationStore == null)
            return null;

        Registration registration = registrationStore.getRegistrationByAdress(inetAddress);
        if (registration != null) {
            SecurityInfo securityInfo = securityStore.getByEndpoint(registration.getEndpoint());
            if (securityInfo != null) {
                return new PskPublicInformation(securityInfo.getIdentity());
            }
            return null;
        }
        return null;
    }

    @Override
    public PskPublicInformation getIdentity(InetSocketAddress peerAddress, ServerNames virtualHost) {
        // TODO should we support SNI ?
        throw new UnsupportedOperationException();
    }
}
