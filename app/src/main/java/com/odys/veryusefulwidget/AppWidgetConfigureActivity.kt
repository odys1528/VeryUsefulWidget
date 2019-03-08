package com.odys.veryusefulwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText

/**
 * The configuration screen for the [AppWidget] AppWidget.
 */
class AppWidgetConfigureActivity : Activity() {
    private var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var mAppWidgetNameText: EditText
    private lateinit var mAppWidgetSurnameText: EditText
    private var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@AppWidgetConfigureActivity

        // When the button is clicked, store the string locally
        val widgetText = "${mAppWidgetNameText.text};${mAppWidgetSurnameText.text}"
        saveTitlePref(context, mAppWidgetId, widgetText)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        AppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.app_widget_configure)
        mAppWidgetNameText = findViewById<View>(R.id.appwidget_nameText) as EditText
        mAppWidgetSurnameText = findViewById<View>(R.id.appwidget_surnameText) as EditText
        findViewById<View>(R.id.save_button).setOnClickListener(mOnClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        val data = loadTitlePref(this@AppWidgetConfigureActivity, mAppWidgetId)
        mAppWidgetNameText.setText(data.split(";")[0])
        mAppWidgetSurnameText.setText(data.split(";")[1])
    }

    companion object {

        private const val PREFS_NAME = "com.odys.veryusefulwidget.AppWidget"
        private const val PREF_PREFIX_KEY = "appwidget_"

        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
            prefs.apply()
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadTitlePref(context: Context, appWidgetId: Int): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
            return titleValue
                    ?: "${context.getString(R.string.sample_name)};${context.getString(R.string.sample_surname)}"
        }

        internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId)
            prefs.apply()
        }
    }
}

