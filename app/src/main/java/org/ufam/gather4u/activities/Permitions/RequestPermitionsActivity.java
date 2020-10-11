package org.ufam.gather4u.activities.Permitions;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import org.ufam.gather4u.activities.BaseActivity;
import org.ufam.gather4u.activities.Permitions.listeners.CameraPermitionListener;
import org.ufam.gather4u.activities.Permitions.listeners.GpsPermitionListener;
import org.ufam.gather4u.activities.Permitions.listeners.SmsReceiverListener;
import org.ufam.gather4u.activities.Permitions.listeners.WriteExternalStoragePermitionListener;

public class RequestPermitionsActivity extends BaseActivity {

    private WriteExternalStoragePermitionListener mWriteExternalStoragePermitionListener;
    private CameraPermitionListener mCameraPermitionListener;
    private GpsPermitionListener mGpsPermitionListener;
    private SmsReceiverListener mSmsReceiverListener;

    public static final int WRITE_EXTERNAL_STORAGE_PERMITION = 1;
    public static final int CAMERA_PERMITION = 2;
    public static final int ACCESS_FINE_LOCATION_PERMITION = 3;
    public static final int RECEIVE_SMS_PERMITION = 4;

    public void checkWriteExternalStoragePermition(WriteExternalStoragePermitionListener writeExternalStoragePermitionListener){

        this.mWriteExternalStoragePermitionListener = writeExternalStoragePermitionListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMITION);
            }else{
                mWriteExternalStoragePermitionListener.onWriteExternalStoregeGranted();
            }
        }else{
            mWriteExternalStoragePermitionListener.onWriteExternalStoregeGranted();
        }
    }

    public void checkCameraPermition(CameraPermitionListener cameraPermitionListener){

        this.mCameraPermitionListener = cameraPermitionListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMITION);
            } else {
                mCameraPermitionListener.onCameraGranted();
            }
        } else {
            mCameraPermitionListener.onCameraGranted();
        }
    }

    protected void checkGpsPermition(GpsPermitionListener gpsPermitionListener){

        this.mGpsPermitionListener = gpsPermitionListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMITION);
            } else {
                mGpsPermitionListener.onGpsGranted();
            }
        } else {
            mGpsPermitionListener.onGpsGranted();
        }
    }

    protected void checkSmsReceivePermition(SmsReceiverListener smsReceiverListener){

        this.mSmsReceiverListener = smsReceiverListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_PERMITION);
            }else{
                mSmsReceiverListener.onSmsReceiverGranted();
            }
        }else{
            mSmsReceiverListener.onSmsReceiverGranted();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_PERMITION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mWriteExternalStoragePermitionListener.onWriteExternalStoregeGranted();
                }
                else{
                    mWriteExternalStoragePermitionListener.onWriteExternalStoregeDenied();
                }
                break;

            case CAMERA_PERMITION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraPermitionListener.onCameraGranted();
                } else {
                    mCameraPermitionListener.onCameraDenied();
                }
                break;

            case ACCESS_FINE_LOCATION_PERMITION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGpsPermitionListener.onGpsGranted();
                }
                else{
                    mGpsPermitionListener.onGpsDenied();
                }
                break;
            }

            case RECEIVE_SMS_PERMITION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mSmsReceiverListener.onSmsReceiverGranted();
                }
                else{
                    mSmsReceiverListener.onSmsReceiverDenied();
                }
                break;

        }
    }
}