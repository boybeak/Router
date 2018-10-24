package com.github.boybeak.router;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public abstract class Interceptor {
    public abstract boolean onIntercept(@NonNull Context context, @NonNull Intent it);
}