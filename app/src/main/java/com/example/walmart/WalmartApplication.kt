package com.example.walmart

import android.app.Application
import com.example.walmart.data.di.dataModule
import com.example.walmart.data.di.networkModule
import com.example.walmart.domain.di.ServiceProvider
import com.example.walmart.domain.di.add
import com.example.walmart.domain.di.module
import com.example.walmart.presentation.di.presentationModule

class WalmartApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceProvider.initialize(
            presentationModule,
            dataModule,
            networkModule,
            // add application context dependency
            module { add { applicationContext } }
        )
    }
}