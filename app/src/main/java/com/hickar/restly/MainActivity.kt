package com.hickar.restly

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hickar.restly.databinding.ActivityMainBinding
import com.hickar.restly.utils.KeyboardUtil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navController = navHostFragment!!.findNavController()

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_requests, R.id.navigation_history, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

//    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
//        if (event != null && event.action == MotionEvent.ACTION_DOWN) {
//            val view = currentFocus
//
//            if (view is EditText) {
//                val outRect = Rect()
//                view.getGlobalVisibleRect(outRect)
//
//                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
//                    KeyboardUtil.hideKeyboard(this, view)
//                }
//            }
//        }
//
//        return super.dispatchTouchEvent(event)
//    }
}