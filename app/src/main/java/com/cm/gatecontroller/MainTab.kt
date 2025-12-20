package com.cm.gatecontroller

sealed class MainTab(val route: String) {
    object Monitoring : MainTab("monitoring")
    object Configuration : MainTab("configuration")
    object BoardTest : MainTab("boardtest")
}