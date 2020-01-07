package com.example.koininjectionsampleproject

import androidx.core.app.NotificationManagerCompat
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val INTERVAL_INJECTION = "injection.interval"
// connectivity
val appModule = module {
    single {
        AppConnectivityManager(get(), get(named(INTERVAL_INJECTION)))
    }

    factory { NotificationManagerCompat.from(get()) }
}

val otherModule = module {
    single<Long>(named(INTERVAL_INJECTION)){
        5_000L
    }
}