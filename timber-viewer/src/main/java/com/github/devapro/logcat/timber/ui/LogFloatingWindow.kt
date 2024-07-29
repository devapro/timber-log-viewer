package com.github.devapro.logcat.timber.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.github.devapro.logcat.timber.LogActivity
import com.github.devapro.logcat.timber.R
import com.github.devapro.logcat.timber.data.LogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


internal class LogFloatingWindow(
    private val context: Context
) {

    private lateinit var floatView: ViewGroup

    private lateinit var dragAndDropBtn: View
    private lateinit var changeSizeBtn: View
    private lateinit var maximizeBtn: View
    private lateinit var closeBtn: View

    private lateinit var logList: RecyclerView

    private lateinit var windowManager: WindowManager

    private lateinit var floatWindowLayoutParam: WindowManager.LayoutParams

    private val logAdapter = LogAdapter()

    private var logCollectJob: Job? = null

    fun createWindow() {


        // The screen height and width are calculated, cause
        // the height and width of the floating window is set depending on this
        val metrics: DisplayMetrics = context.applicationContext.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels


        // To obtain a WindowManager of a different Display,
        // we need a Context for that display, so WINDOW_SERVICE is used
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager


        // A LayoutInflater instance is created to retrieve the
        // LayoutInflater for the floating_layout xml


        // inflate a new view hierarchy from the floating_layout xml
        floatView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.floating_layout, null) as ViewGroup


        // The Buttons and the EditText are connected with
        // the corresponding component id used in floating_layout xml file
        maximizeBtn = floatView.findViewById(R.id.maximize_btn)
        dragAndDropBtn = floatView.findViewById(R.id.drag_and_drop_btn)
        changeSizeBtn = floatView.findViewById(R.id.change_size_btn)
        closeBtn = floatView.findViewById(R.id.close_btn)

        logList = floatView.findViewById(R.id.log_list)



        // WindowManager.LayoutParams takes a lot of parameters to set the
        // the parameters of the layout. One of them is Layout_type.
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // If API Level is more than 26, we need TYPE_APPLICATION_OVERLAY
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            // If API Level is lesser than 26, then we can
            // use TYPE_SYSTEM_ERROR,
            // TYPE_SYSTEM_OVERLAY, TYPE_PHONE, TYPE_PRIORITY_PHONE.
            // But these are all
            // deprecated in API 26 and later. Here TYPE_TOAST works best.
            WindowManager.LayoutParams.TYPE_TOAST
        }


        // Now the Parameter of the floating-window layout is set.
        // 1) The Width of the window will be 95% of the phone width.
        // 2) The Height of the window will be 58% of the phone height.
        // 3) Layout_Type is already set.
        // 4) Next Parameter is Window_Flag. Here FLAG_NOT_FOCUSABLE is used. But
        // problem with this flag is key inputs can't be given to the EditText.
        // This problem is solved later.
        // 5) Next parameter is Layout_Format. System chooses a format that supports
        // translucency by PixelFormat.TRANSLUCENT
        floatWindowLayoutParam = WindowManager.LayoutParams(
            (width * (0.95f)).toInt(),
            (height * (0.58f)).toInt(),
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )


        // The Gravity of the Floating Window is set.
        // The Window will appear in the center of the screen
        floatWindowLayoutParam.gravity = Gravity.CENTER


        // X and Y value of the window is set
        floatWindowLayoutParam.x = 0
        floatWindowLayoutParam.y = 0


        // The ViewGroup that inflates the floating_layout.xml is
        // added to the WindowManager with all the parameters
        windowManager.addView(floatView, floatWindowLayoutParam)


        initMaximizeButton()
        initMoveButton()
        initCloseButton()
        initSizeButton()
        initList()
    }

    fun removeWindow() {
        logCollectJob?.cancel()
        windowManager.removeView(floatView)
    }

    private fun initList() {
        logList.adapter = logAdapter
        logCollectJob = GlobalScope.launch {
            LogRepository.updates.collect{
                launch(Dispatchers.Main) {
                    logAdapter.setItems(LogRepository.logsList)
                    logList.scrollToPosition(logAdapter.itemCount - 1)
                }
            }
        }
    }

    private fun initMaximizeButton() {
        // The button that helps to maximize the app
        maximizeBtn.setOnClickListener { // stopSelf() method is used to stop the service if
            // it was previously started
            //   stopSelf()


            // The window is removed from the screen
            windowManager.removeView(floatView)


            // The app will maximize again. So the MainActivity
            // class will be called again.
            val backToHome = Intent(
                context,
                LogActivity::class.java
            )
            backToHome.addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_MULTIPLE_TASK)


            // 1) FLAG_ACTIVITY_NEW_TASK flag helps activity to start a new task on the history stack.
            // If a task is already running like the floating window service, a new activity will not be started.
            // Instead the task will be brought back to the front just like the MainActivity here
            // 2) FLAG_ACTIVITY_CLEAR_TASK can be used in the conjunction with FLAG_ACTIVITY_NEW_TASK. This flag will
            // kill the existing task first and then new activity is started.
            backToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(backToHome)
        }
    }

    private fun initCloseButton() {
        // The button that helps to maximize the app
        closeBtn.setOnClickListener { // stopSelf() method is used to stop the service if
            // The window is removed from the screen
            windowManager.removeView(floatView)
        }
    }

    private fun initMoveButton() {
        // Another feature of the floating window is, the window is movable.
        // The window can be moved at any position on the screen.
        dragAndDropBtn.setOnTouchListener(object : View.OnTouchListener {
            val floatWindowLayoutUpdateParam: WindowManager.LayoutParams = floatWindowLayoutParam
            var x: Double = 0.0
            var y: Double = 0.0
            var px: Double = 0.0
            var py: Double = 0.0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = floatWindowLayoutUpdateParam.x.toDouble()
                        y = floatWindowLayoutUpdateParam.y.toDouble()


                        // returns the original raw X
                        // coordinate of this event
                        px = event.rawX.toDouble()


                        // returns the original raw Y
                        // coordinate of this event
                        py = event.rawY.toDouble()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        floatWindowLayoutUpdateParam.x = ((x + event.rawX) - px).toInt()
                        floatWindowLayoutUpdateParam.y = ((y + event.rawY) - py).toInt()

                        // updated parameter is applied to the WindowManager
                        windowManager.updateViewLayout(floatView, floatWindowLayoutUpdateParam)
                    }
                }
                return false
            }
        })
    }

    private fun initSizeButton() {
        // Another feature of the floating window is, the window is movable.
        // The window can be moved at any position on the screen.
        changeSizeBtn.setOnTouchListener(object : View.OnTouchListener {
            val floatWindowLayoutUpdateParam: WindowManager.LayoutParams = floatWindowLayoutParam
            var with: Double = 0.0
            var height: Double = 0.0
            var y: Double = 0.0
            var py: Double = 0.0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        height = floatWindowLayoutUpdateParam.height.toDouble()

                        y = floatWindowLayoutUpdateParam.y.toDouble()

                        // returns the original raw Y
                        // coordinate of this event
                        py = event.rawY.toDouble()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        floatWindowLayoutUpdateParam.height = (py - event.rawY + height).toInt()
                        // updated parameter is applied to the WindowManager
                        windowManager.updateViewLayout(floatView, floatWindowLayoutUpdateParam)
                    }
                }
                return false
            }
        })
    }
}