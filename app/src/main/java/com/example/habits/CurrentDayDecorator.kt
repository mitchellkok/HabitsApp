package com.example.habits

import android.R
import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade


class CurrentDayDecorator(context: Activity?, currentDay: CalendarDay) : DayViewDecorator {
    private val drawable: Drawable?
    var myDay = currentDay
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable!!)
    }

    init {
        // You can set background for Decorator via drawable here
        drawable = ContextCompat.getDrawable(context!!, R.drawable.checkbox_off_background)
    }
}

class MDayDecorator(context: Activity?, color: Int, dates: ArrayList<CalendarDay>) : DayViewDecorator {
    private val drawable: Drawable?
    private val colorSet = color
    private val dateArray = dates

    init {
        println("Setting $colorSet, $dateArray")
        drawable = ContextCompat.getDrawable(context!!, R.drawable.checkbox_off_background)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val retval = dateArray.contains(day)
        if (retval) {println("shouldDecorate $day in $dateArray: $retval")}
        return retval
    }

    override fun decorate(view: DayViewFacade) {
        if (drawable != null) {
            println("Decorating $view with $colorSet, $drawable")
            drawable.setTint(colorSet)
            view.setBackgroundDrawable(drawable)
        }
    }
}