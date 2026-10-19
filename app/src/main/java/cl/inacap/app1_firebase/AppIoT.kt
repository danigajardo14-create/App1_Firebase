package cl.inacap.app1_firebase

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class AppIoT : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
