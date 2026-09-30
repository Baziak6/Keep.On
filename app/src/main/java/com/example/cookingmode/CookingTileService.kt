package com.example.cookingmode

import android.content.*
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.*

class CookingTileService : TileService() {
    override fun onStartListening(){ super.onStartListening(); update() }
    override fun onClick(){ super.onClick()
        if(ModeStore.isActive(this)){ stopService(Intent(this,CookingService::class.java)); ModeStore.setInactive(this); update() }
        else if(Settings.canDrawOverlays(this)){ ModeStore.setActive(this); if(Build.VERSION.SDK_INT>=26) startForegroundService(Intent(this,CookingService::class.java)) else startService(Intent(this,CookingService::class.java)); update() }
        else { unlockAndRun { val i=Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i) } }
    }
    private fun update(){ qsTile?.let { val on=ModeStore.isActive(this); it.state=if(on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE; it.label="Cooking Mode"; if(Build.VERSION.SDK_INT>=29) it.subtitle=if(on) "ON · max 1 hour" else "OFF"; it.updateTile() } }
    companion object { fun refresh(c:Context){ requestListeningState(c, android.content.ComponentName(c,CookingTileService::class.java)) } }
}
