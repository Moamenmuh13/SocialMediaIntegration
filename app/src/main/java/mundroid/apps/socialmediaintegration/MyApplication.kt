package mundroid.apps.socialmediaintegration

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(this)
    }
}