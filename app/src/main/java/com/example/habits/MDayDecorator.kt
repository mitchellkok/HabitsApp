package com.example.habits

import android.app.Activity
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class MPassDecorator(context: Activity?, dates: ArrayList<CalendarDay>) : DayViewDecorator {
    private val drawable: Drawable?
    private val dateArray = dates

    init {
        drawable = ContextCompat.getDrawable(context!!, R.drawable.green_circle)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dateArray.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        if (drawable != null) {
            view.setBackgroundDrawable(drawable)
        }
    }
}

class MFailDecorator(context: Activity?, dates: ArrayList<CalendarDay>) : DayViewDecorator {
    private val drawable: Drawable?
    private val dateArray = dates

    init {
        drawable = ContextCompat.getDrawable(context!!, R.drawable.red_circle)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dateArray.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        if (drawable != null) {
            view.setBackgroundDrawable(drawable)
        }
    }
}

class MBlankDecorator(context: Activity?) : DayViewDecorator {
    private val drawable: Drawable?

    init {
        drawable = ContextCompat.getDrawable(context!!, R.drawable.blank_circle)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade) {
        if (drawable != null) {
            view.setBackgroundDrawable(drawable)
        }
    }
}