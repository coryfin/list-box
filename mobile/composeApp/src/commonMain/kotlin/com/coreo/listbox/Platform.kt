package com.coreo.listbox

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform