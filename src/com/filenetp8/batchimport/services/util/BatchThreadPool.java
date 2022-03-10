package com.filenetp8.batchimport.services.util;

public class BatchThreadPool {
    
    public static final ThreadLocal loggerThreadLocal = new ThreadLocal();

    public static void set (ContextThread ctx){
        loggerThreadLocal.set(ctx);
    }

    public static void unset(){
        loggerThreadLocal.remove();
    }

    public static String get(){
        if(loggerThreadLocal.get() != null){
            return ((ContextThread)loggerThreadLocal.get()).getContext();
        }else {
            return null;
        }
        
    }
}
