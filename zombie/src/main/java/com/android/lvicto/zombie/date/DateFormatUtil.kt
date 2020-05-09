package com.android.lvicto.zombie.date

import java.text.SimpleDateFormat
import java.util.*

class DateFormatUtil(locale: Locale = Locale.US) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", locale)
    private val dayFormat = SimpleDateFormat("EEE", locale)
    private val shortMonthDayFormat = SimpleDateFormat("MMM dd", locale)
    private val monthDayFormat = SimpleDateFormat("MMMM dd", locale)
    private val hourMinute24Format = SimpleDateFormat("HH:mm", locale)
    private val hourMinute12Format = SimpleDateFormat("h:mm a", locale)

    /**
     * This will return a formatted date
     *
     * @param date - the [Date] object we want to format
     * @param tZone - the [TimeZone] we want to use to format the date
     *
     * @return the date in the format of "yyyy-MM-dd"
     *  > examples:
     *      - 2020-03-26
     *      - 2020-12-26
     */
    fun formatDate(date: Date, tZone: TimeZone): String {
        return dateFormat
                .apply {
                    timeZone = tZone
                }.format(date)
    }

    /**
     * This will return a formatted day
     *
     * @param date - the [Date] object we want to format
     * @param tZone - the [TimeZone] we want to use to format the date
     *
     * @return the date in the format of "EEE"
     *  > examples:
     *      - Mon
     *      - Wed
     */
    fun formatDay(date: Date, tZone: TimeZone): String {
        return dayFormat
                .apply {
                    timeZone = tZone
                }.format(date)
    }

    /**
     * This will return a formatted month and day
     *
     * @param date - the [Date] object we want to format
     * @param tZone - the [TimeZone] we want to use to format the date
     *
     * @return the date in the format of "MMM dd"
     *  > examples:
     *      - Mar 26
     *      - Jun 20
     */
    fun formatShortMonthDay(date: Date, tZone: TimeZone): String {
        return shortMonthDayFormat
                .apply {
                    timeZone = tZone
                }.format(date)
    }

    /**
     * This will return a formatted month and day
     *
     * @param date - the [Date] object we want to format
     * @param tZone - the [TimeZone] we want to use to format the date
     *
     * @return the date in the format of "MMMMM dd"
     *  > examples:
     *      - Mar 26
     *      - Jun 20
     */
    fun formatMonthDay(date: Date, tZone: TimeZone): String {
        return monthDayFormat
                .apply {
                    timeZone = tZone
                }.format(date)
    }

    /**
     * This will return a formatted time
     *
     * @param date - the [Date] object we want to format
     * @param tZone - the [TimeZone] we want to use to format the date
     *
     * @return the date in the format of "HH:mm" (24 hours)
     *  > examples:
     *      - 11:00
     *      - 14:00
     */
    fun formatTime24Hour(date: Date, tZone: TimeZone?): String {
        return hourMinute24Format.apply {
            tZone?.let { timeZone = it }
        }.format(date)
    }

    /**
     * This will return a formatted time
     *
     * @param date - the [Date] object we want to format
     * @param tZone - the [TimeZone] we want to use to format the date
     *
     * @return the date in the format of "HH:mm a" (12 hours)
     *  > examples:
     *      - 11:00 AM
     *      - 2:00 PM
     */
    fun formatTime12Hour(date: Date, tZone: TimeZone?): String {
        return hourMinute12Format.apply {
            tZone?.let { timeZone = it }
        }.format(date)
    }
}