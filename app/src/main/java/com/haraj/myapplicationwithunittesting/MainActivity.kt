package com.haraj.myapplicationwithunittesting

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {
    // Logger for this class.
    private val TAG = "MainActivity" // The helper that manages writing to SharedPreferences.

    private var mSharedPreferencesHelper // The input field where the user enters his name.
            : SharedPreferencesHelper? = null
    private var mNameText // The date picker where the user enters his date of birth.
            : EditText? = null
    private var mDobPicker // The input field where the user enters his email.
            : DatePicker? = null
    private var mEmailText // The validator for the email input field.
            : EditText? = null
    private var mEmailValidator: EmailValidator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Shortcuts to input fields.
        mNameText = findViewById<View>(R.id.userNameInput) as EditText
        mDobPicker = findViewById<View>(R.id.dateOfBirthInput) as DatePicker
        mEmailText = findViewById<View>(R.id.emailInput) as EditText // Setup field validators.
        mEmailValidator = EmailValidator()
        mEmailText!!.addTextChangedListener(mEmailValidator) // Instantiate a SharedPreferencesHelper.
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mSharedPreferencesHelper =
            SharedPreferencesHelper(sharedPreferences) // Fill input fields from data retrieved from the SharedPreferences.
        populateUi()
    }

    /**
     * Initialize all fields from the personal info saved in the SharedPreferences.
     */
    private fun populateUi() {
        val sharedPreferenceEntry: SharedPreferenceEntry = mSharedPreferencesHelper!!.personalInfo
        mNameText!!.setText(sharedPreferenceEntry.name)
        val dateOfBirth: Calendar = sharedPreferenceEntry.dateOfBirth
        mDobPicker!!.init(
            dateOfBirth.get(Calendar.YEAR), dateOfBirth.get(Calendar.MONTH),
            dateOfBirth.get(Calendar.DAY_OF_MONTH), null
        )
        mEmailText!!.setText(sharedPreferenceEntry.email)
    }

    /**
     * Called when the "Save" button is clicked.
     */
    fun onSaveClick(view: View?) {
        // Don't save if the fields do not validate.
        if (!mEmailValidator!!.isValid) {
            mEmailText!!.error = "Invalid email"
            Log.w(TAG, "Not saving personal information: Invalid email")
            return
        } // Get the text from the input fields.
        val name = mNameText!!.text.toString()
        val dateOfBirth: Calendar = Calendar.getInstance()
        dateOfBirth.set(mDobPicker!!.year, mDobPicker!!.month, mDobPicker!!.dayOfMonth)
        val email = mEmailText!!.text.toString() // Create a Setting model class to persist.
        val sharedPreferenceEntry =
            SharedPreferenceEntry(name, dateOfBirth, email) // Persist the personal information.
        val isSuccess = mSharedPreferencesHelper!!.savePersonalInfo(sharedPreferenceEntry)
        if (isSuccess) {
            Toast.makeText(this, "Personal information saved", Toast.LENGTH_LONG).show()
            Log.i(TAG, "Personal information saved")
        } else {
            Log.e(TAG, "Failed to write personal information to SharedPreferences")
        }
    }

    /**
     * Called when the "Revert" button is clicked.
     */
    fun onRevertClick(view: View?) {
        populateUi()
        Toast.makeText(this, "Personal information reverted", Toast.LENGTH_LONG).show()
        Log.i(TAG, "Personal information reverted")
    }
}