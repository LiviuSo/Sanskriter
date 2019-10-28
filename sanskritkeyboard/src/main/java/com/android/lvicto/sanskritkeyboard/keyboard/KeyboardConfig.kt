package com.android.lvicto.sanskritkeyboard.keyboard


data class KeyboardConfig(val isTablet: Boolean = false,
                          val orientation: Int = -1,
                          val type: KeyboardType = KeyboardType.SA)