package com.github.boybeak.friend;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.github.boybeak.router.Interceptor;

public class RoleInterceptor extends Interceptor {
    @Override
    public boolean onIntercept(@NonNull Context context, @NonNull Intent it) {
        return false;
    }
}
