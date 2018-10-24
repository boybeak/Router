package com.github.boybeak.router;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsRouterEngine {
    private Map<Class<? extends Interceptor>, Interceptor> mInterceptors = new HashMap<>();
    public void putInterceptor(Interceptor interceptor) {
        mInterceptors.put(interceptor.getClass(), interceptor);
    }
    public <T extends Interceptor> T findInterceptor(Class<T> clz) {
        return (T) mInterceptors.get(clz);
    }
}
