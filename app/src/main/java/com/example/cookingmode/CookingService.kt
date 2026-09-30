package com.example.cookingmode

import android.app.*
import android.content.*
import android.graphics.Color
import android.os.*
import android.provider.Settings
import android.view.*
import android.widget.*

class CookingService : Service() {
    private val handler=Handler(Looper.getMainLooper())
    private var overlay: View?=null
    private lateinit var wm: WindowManager
    private val dim = Runnable { showBlackCover() }
    private val expire = Runnable { stopMode() }
    override fun onCreate(){ super.onCreate(); wm=getSystemService(WINDOW_SERVICE) as WindowManager; createChannel(); startForeground(7, notification()); schedule() }
    override fun onStartCommand(i:Intent?,f:Int,id:Int):Int { if(!ModeStore.isActive(this) || !Settings.canDrawOverlays(this)){ stopSelf(); return START_NOT_STICKY }; schedule(); return START_STICKY }
    private fun schedule(){ handler.removeCallbacks(dim); handler.removeCallbacks(expire); handler.postDelayed(dim,30_000); val left=(ModeStore.endAt(this)-System.currentTimeMillis()).coerceAtLeast(0); handler.postDelayed(expire,left) }
    private fun showBlackCover(){ if(overlay!=null || !ModeStore.isActive(this)) return
        val v=FrameLayout(this).apply { setBackgroundColor(Color.BLACK); isClickable=true; isFocusable=true
            setOnTouchListener { _,_ -> hideBlackCover(); true }
        }
        val hint=TextView(this).apply { text="Cooking Mode\nTouch or swipe to wake"; setTextColor(Color.DKGRAY); textSize=18f; gravity=Gravity.CENTER }
        v.addView(hint,FrameLayout.LayoutParams(-1,-1))
        val type=if(Build.VERSION.SDK_INT>=26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        val lp=WindowManager.LayoutParams(-1,-1,type,WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,android.graphics.PixelFormat.OPAQUE)
        try { wm.addView(v,lp); overlay=v } catch(_:Exception){}
    }
    private fun hideBlackCover(){ overlay?.let { try{wm.removeView(it)}catch(_:Exception){} }; overlay=null; handler.removeCallbacks(dim); handler.postDelayed(dim,30_000) }
    private fun stopMode(){ hideBlackCover(); ModeStore.setInactive(this); CookingTileService.refresh(this); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }
    override fun onDestroy(){ handler.removeCallbacksAndMessages(null); hideBlackCover(); super.onDestroy() }
    override fun onBind(i:Intent?)=null
    private fun createChannel(){ if(Build.VERSION.SDK_INT>=26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel("cooking","Cooking Mode",NotificationManager.IMPORTANCE_LOW)) }
    private fun notification():Notification { val pi=PendingIntent.getActivity(this,0,Intent(this,MainActivity::class.java),PendingIntent.FLAG_IMMUTABLE); return if(Build.VERSION.SDK_INT>=26) Notification.Builder(this,"cooking").setSmallIcon(R.drawable.ic_cooking).setContentTitle("Cooking Mode is on").setContentText("Recipe stays ready · automatically ends within 1 hour").setContentIntent(pi).setOngoing(true).build() else @Suppress("DEPRECATION") Notification.Builder(this).setSmallIcon(R.drawable.ic_cooking).setContentTitle("Cooking Mode is on").setContentText("Automatically ends within 1 hour").build() }
}
