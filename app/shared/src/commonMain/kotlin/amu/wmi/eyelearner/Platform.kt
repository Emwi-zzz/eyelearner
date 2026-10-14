package amu.wmi.eyelearner

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform