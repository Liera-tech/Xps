package com.liera.xps.interfaces;

public interface XpsCallback {

    void permissionXSuccess();

    void permissionXCancel(String[] cancelPermissions);

    void permissionXDenied(String[] cancelPermissions, String[] deniedPermissions);
}
