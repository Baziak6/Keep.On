package com.example.cookingmode

import android.content.Context

object ModeStore {
    private const val PREFS = "cooking_mode"
    private const val ACTIVE = "active"
    private const val END = "end"
    fun isActive(c: Context): Boolean {
        val p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val active = p.getBoolean(ACTIVE, false)
        val end = p.getLong(END, 0L)
        if (active && end > 0 && System.currentTimeMillis() >= end) { setInactive(c); return false }
        return active
    }
    fun setActive(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        .putBoolean(ACTIVE, true).putLong(END, System.currentTimeMillis() + 60*60*1000L).apply()
    fun setInactive(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    fun endAt(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(END, 0L)
}
