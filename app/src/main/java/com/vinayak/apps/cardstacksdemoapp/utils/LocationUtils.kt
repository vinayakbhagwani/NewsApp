package com.vinayak.apps.cardstacksdemoapp.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

object LocationUtils {

    fun areLocationPermissionsAlreadyGranted(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    fun openApplicationSettings(packageName: String, context: Context) {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)).also {
            context.startActivity(it)
        }
    }

    fun decideCurrentPermissionStatus(locationPermissionsGranted: Boolean,
                                              shouldShowPermissionRationale: Boolean): PermissionStatus {
        return if (locationPermissionsGranted) PermissionStatus.GRANTED
        else if (shouldShowPermissionRationale) PermissionStatus.REJECTED
        else PermissionStatus.DENIED
    }
}