package com.example.koininjectionsampleproject

import com.example.koininjectionsampleproject.AppConnectivityManager
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val INTERVAL_INJECTION = "injection.interval"
// connectivity
val appModule = module {
    single {
        AppConnectivityManager(get(), get(named(INTERVAL_INJECTION)))
    }
}

val otherModule = module {
    single<Long>(named(INTERVAL_INJECTION)){
        5_000L
    }
}