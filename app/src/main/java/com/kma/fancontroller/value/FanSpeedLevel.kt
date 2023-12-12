package com.kma.fancontroller.value

enum class FanSpeedLevel(val value: Int, val recognitionValues: List<String>) {
    OFF(0, listOf("tắt", "off", "0")),
    LEVEL_1(1, listOf("một", "1", "one")),
    LEVEL_2(2, listOf("hai", "2", "two")),
    LEVEL_3(3, listOf("ba", "3", "three")),
    LEVEL_4(4, listOf("bốn", "4", "four")),
    LEVEL_5(5, listOf("năm", "5", "five")),
}