package com.example.cookingmode

import android.app.*
import android.content.*
import android.graphics.Color
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.Gravity
import android.widget.*
import androidx.annotation.RequiresApi

class MainActivity : Activity() {
    private lateinit var status: TextView
    override fun onCreate(b: Bundle?) { super.onCreate(b); buildUi() }
    override fun onResume() { super.onResume(); updateStatus() }
    private fun buildUi() {
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; setPadding(48,64,48,64); setBackgroundColor(Color.rgb(250,250,250)) }
        val title=TextView(this).apply { text="Cooking Mode"; textSize=30f; setTextColor(Color.BLACK); gravity=Gravity.CENTER }
        val info=TextView(this).apply { text="Keep a recipe ready without repeatedly unlocking your phone.\n\nWhen active, the phone stays awake. After 30 seconds a black cover appears; touch or swipe anywhere to reveal the recipe instantly. Cooking Mode automatically ends after 1 hour."; textSize=17f; setTextColor(Color.DKGRAY); gravity=Gravity.CENTER; setPadding(0,30,0,30) }
        status=TextView(this).apply { textSize=18f; gravity=Gravity.CENTER; setPadding(0,12,0,24) }
        val permission=Button(this).apply { text="1. Allow display over other apps"; setOnClickListener { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) } }
        val tile=Button(this).apply { text="2. Add Quick Settings tile"; setOnClickListener { if(Build.VERSION.SDK_INT>=33) requestAddTile() else Toast.makeText(this@MainActivity,"Pull down Quick Settings, tap Edit, then add Cooking Mode.",Toast.LENGTH_LONG).show() } }
        val toggle=Button(this).apply { text="Toggle Cooking Mode"; setOnClickListener { toggleMode() } }
        val note=TextView(this).apply { text="Privacy/security: this app does not remove your PIN, password, fingerprint or normal lock-screen settings. When Cooking Mode ends, normal Android locking is unchanged."; textSize=14f; setTextColor(Color.GRAY); gravity=Gravity.CENTER; setPadding(0,30,0,0) }
        listOf(title,info,status,permission,tile,toggle,note).forEach { root.addView(it, LinearLayout.LayoutParams(-1,-2).apply { setMargins(0,8,0,8) }) }
        setContentView(root)
    }
    private fun updateStatus(){ status.text = if(ModeStore.isActive(this)) "● Cooking Mode ON" else "○ Cooking Mode OFF" }
    private fun toggleMode(){
        if(ModeStore.isActive(this)){ stopService(Intent(this,CookingService::class.java)); ModeStore.setInactive(this) }
        else if(!Settings.canDrawOverlays(this)){ Toast.makeText(this,"First allow display over other apps.",Toast.LENGTH_LONG).show() }
        else { ModeStore.setActive(this); startForegroundService(Intent(this,CookingService::class.java)) }
        updateStatus(); CookingTileService.refresh(this)
    }
    @RequiresApi(33)
private fun requestAddTile() {
val statusBarManager = getSystemService(StatusBarManager::class.java)

statusBarManager.requestAddTileService(
ComponentName(this, CookingTileService::class.java),
"Cooking Mode",
Icon.createWithResource(this, android.R.drawable.ic_menu_view),
mainExecutor
) { result ->
Toast.makeText(
this,
if (result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED)
"Cooking Mode added to Quick Settings"
else
"You can add Cooking Mode from Quick Settings → Edit",
Toast.LENGTH_LONG
).show()
}
}
}
