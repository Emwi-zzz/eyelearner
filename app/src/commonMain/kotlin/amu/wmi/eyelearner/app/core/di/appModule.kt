package amu.wmi.eyelearner.app.core.di

import amu.wmi.eyelearner.app.auth.di.authModule
import org.koin.dsl.module

val appModule = module {
    includes(authModule)
}