package com.cybex.lib.fingerprintidentify.impl;

import android.content.Context;
import android.os.Build;
import android.support.v4.os.CancellationSignal;

import com.cybex.lib.fingerprintidentify.aosp.FingerprintManagerCompat;
import com.cybex.lib.fingerprintidentify.base.BaseFingerprint;

/**
 * Android原生api，最低支持安卓6.0系统
 */
public class AndroidFingerprint extends BaseFingerprint {

    private CancellationSignal mCancellationSignal;
    private FingerprintManagerCompat mFingerprintManagerCompat;

    public AndroidFingerprint(Context context, FingerprintIdentifyExceptionListener exceptionListener) {
        super(context, exceptionListener);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        try {
            mFingerprintManagerCompat = FingerprintManagerCompat.from(mContext);
            setHardwareEnable(mFingerprintManagerCompat.isHardwareDetected());
            setRegisteredFingerprint(mFingerprintManagerCompat.hasEnrolledFingerprints());
        } catch (Throwable e) {
            onCatchException(e);
        }
    }

    @Override
    protected void doIdentify() {
        try {
            mCancellationSignal = new CancellationSignal();
            mFingerprintManagerCompat.authenticate(null, 0, mCancellationSignal,
                    new FingerprintManagerCompat.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            onSucceed();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            onNotMatch();
                        }

                        @Override
                        public void onAuthenticationError(int errMsgId, CharSequence errString) {
                            super.onAuthenticationError(errMsgId, errString);
                            // 7 - FingerprintManager.FINGERPRINT_ERROR_LOCKOUT
                            // 9 - FingerprintManager.FINGERPRINT_ERROR_LOCKOUT_PERMANENT (API-27)
                            onFailed(errMsgId == 7 || errMsgId == 9);
                        }
                    }, null);
        } catch (Throwable e) {
            onCatchException(e);
            onFailed(false);
        }
    }

    @Override
    protected void doCancelIdentify() {
        try {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        } catch (Throwable e) {
            onCatchException(e);
        }
    }

    @Override
    protected boolean needToCallDoIdentifyAgainAfterNotMatch() {
        return false;
    }
}