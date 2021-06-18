/*
 * Copyright (c) 2018 Samsung Electronics Co., Ltd. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation and/or
 *       other materials provided with the distribution.
 *     * Neither the name of Samsung Electronics Co., Ltd. nor the names of its contributors may be used to endorse
 *       or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.bigdipper.android.polaris;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.bigdipper.android.polaris.R;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.*;

import javax.security.cert.X509Certificate;

public class AccessoryProvider extends SAAgentV2 {
    private static final String TAG = "AccessoryProvider(P)";
    private static final Class<ServiceConnection> SASOCKET_CLASS = ServiceConnection.class;
    private ServiceConnection mConnectionHandler = null;
    private Context mContext;
    Handler mHandler = new Handler();

    public AccessoryProvider(Context context) {
        super(TAG, context, SASOCKET_CLASS);
        mContext = context;
        SA mAccessory = new SA();
        try {
            mAccessory.initialize(mContext);
        } catch (SsdkUnsupportedException e) {
            // try to handle SsdkUnsupportedException
            if (processUnsupportedException(e) == true) {
                return;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            /*
             * Your application can not use Samsung Accessory SDK. Your application should work smoothly
             * without using this SDK, or you may want to notify user and close your application gracefully
             * (release resources, stop Service threads, close UI thread, etc.)
             */
            releaseAgent();
        }
    }

    @Override
    protected void onFindPeerAgentsResponse(SAPeerAgent[] peerAgents, int result) {
        Log.d(TAG, "onFindPeerAgentResponse : result =" + result);
    }

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        if (peerAgent != null) {
            addMessage("Received: ", mContext.getResources().getString(R.string.ConnectionAcceptedMsg1));
            acceptServiceConnectionRequest(peerAgent);

            /**
             * [Authentication]
             * For sending data more securely, you can use authentication before establishing Service Connection.
             * When service connection is requested by a peer agent, you can call SAAgentV2.authenticatePeerAgent() instead of acceptServiceConnectionRequest(peerAgent).
             * Then SAAgentV2.onAuthenticationResponse() is called with a peer agent's authentication token.
             * After comparing both of signed keys, if they are matched, you can accept the request of Service Connection.
             * You can release a comment below to call SAAgentV2.authenticatePeerAgent().
             **/

            //authenticatePeerAgent(peerAgent);
        }
    }

    @Override
    protected void onServiceConnectionResponse(SAPeerAgent peerAgent, SASocket socket, int result) {
        if (result == SAAgentV2.CONNECTION_SUCCESS) {
            if (socket != null) {
                mConnectionHandler = (ServiceConnection) socket;
            }
        } else if (result == SAAgentV2.CONNECTION_ALREADY_EXIST) {
            Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
        }
    }

    @Override
    protected void onError(SAPeerAgent peerAgent, String errorMessage, int errorCode) {
        super.onError(peerAgent, errorMessage, errorCode);
    }

    /**
     * [Authentication]
     **/
    @Override
    protected void onAuthenticationResponse(SAPeerAgent peerAgent, SAAuthenticationToken authToken, int error) {
        if (authToken.getAuthenticationType() == SAAuthenticationToken.AUTHENTICATION_TYPE_CERTIFICATE_X509) {
            mContext = getApplicationContext();
            byte[] myAppKey = getApplicationCertificate(mContext);
            if (authToken.getKey() != null) {
                boolean matched = true;
                if (authToken.getKey().length != myAppKey.length) {
                    matched = false;
                } else {
                    for (int i = 0; i < authToken.getKey().length; i++) {
                        if (authToken.getKey()[i] != myAppKey[i]) {
                            matched = false;
                        }
                    }
                }
                if (matched) {
                    Log.d(TAG, "onAuthenticationResponse : authentication is matched");
                    acceptServiceConnectionRequest(peerAgent);
                    addMessage("Received: ", mContext.getResources().getString(R.string.Aunthenticated));
                }
            }
        } else if (authToken.getAuthenticationType() == SAAuthenticationToken.AUTHENTICATION_TYPE_NONE)
            Log.e(TAG, "onAuthenticationResponse : CERT_TYPE(NONE)");
    }

    public void sendData(final String data) {
        if (mConnectionHandler != null) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        mConnectionHandler.send(getServiceChannelId(0), data.getBytes());
                        addMessage("Sent: ", data);
                    } catch (IOException e) {
                        e.printStackTrace();
                        addMessage("Exception: ", e.getMessage());
                    }
                }
            }).start();
        }
    }

    /**
     * [Sending Data Securely]
     * You can also send data more securely through SASocket.secureSend().
     * Data will be encrypted with a signed key if you use SASocket.secureSend() instead of SASocket.send().
     * You can implement it as following codes.
     *
     * <code>
     public boolean secureSendData(final String data) {
     boolean retvalue = false;
     if (mConnectionHandler != null) {
     new Thread(new Runnable() {
     public void run() {
     try {
     mConnectionHandler.secureSend(getServiceChannelId(0), data.getBytes());
     } catch (IOException e) {
     e.printStackTrace();
     }
     }
     }).start();
     }
     return retvalue;
     }
     * </code>
     */

    /**
     * [Authentication]
     **/
    private static byte[] getApplicationCertificate(Context context) {
        if (context == null) {
            return null;
        }
        byte[] cert = null;
        String packageName = context.getPackageName();
        if (context != null) {
            try {
                PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                if (pkgInfo == null) {
                    return null;
                }
                Signature[] sigs = pkgInfo.signatures;
                if (sigs == null) {

                } else {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    ByteArrayInputStream stream = new ByteArrayInputStream(sigs[0].toByteArray());
                    X509Certificate x509cert = X509Certificate.getInstance(stream);
                    cert = x509cert.getPublicKey().getEncoded();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (javax.security.cert.CertificateException e) {
                e.printStackTrace();
            }
        }
        return cert;
    }

    private boolean processUnsupportedException(SsdkUnsupportedException e) {
        e.printStackTrace();
        int errType = e.getType();
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
                || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            /*
             * Your application can not use Samsung Accessory SDK. You application should work smoothly
             * without using this SDK, or you may want to notify user and close your app gracefully (release
             * resources, stop Service threads, close UI thread, etc.)
             */
            releaseAgent();
        } else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            Log.e(TAG, "You need to install Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            Log.e(TAG, "You need to update Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            Log.e(TAG, "We recommend that you update your Samsung Accessory SDK before using this application.");
            return false;
        }
        return true;
    }

    private void addMessage(final String prefix, final String data) {
        final String strToUI = prefix.concat(data);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                com.bigdipper.android.polaris.AccessoryActivity.addMessage(strToUI);
            }
        });
    }

    public class ServiceConnection extends SASocket {
        public ServiceConnection() {
            super(ServiceConnection.class.getName());
        }

        @Override
        public void onError(int channelId, String errorMessage, int errorCode) {
        }

        @Override
        public void onReceive(int channelId, byte[] data) {
            if (mConnectionHandler == null) {
                return;
            }
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd aa hh:mm:ss.SSS");
            String timeStr = " " + dateFormat.format(calendar.getTime());
            String strToUpdateUI = new String(data);
            addMessage("Received: ", strToUpdateUI);
            final String message = strToUpdateUI.concat(timeStr);
            sendData(message);
        }

        @Override
        protected void onServiceConnectionLost(int reason) {
            mConnectionHandler = null;
            addMessage("Received: ", mContext.getResources().getString(R.string.ConnectionTerminateddMsg));
        }
    }
}
