package com.haraj.myapplicationwithunittesting

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.*


/**
 * Unit tests for the [SharedPreferencesHelper] that mocks [SharedPreferences].
 */
@RunWith(MockitoJUnitRunner::class)
class SharedPreferencesHelperTest() {
    companion object {
        private val TEST_NAME = "Test name"
        private val TEST_EMAIL = "test@email.com"
        private val TEST_DATE_OF_BIRTH = Calendar.getInstance()

        init {
            TEST_DATE_OF_BIRTH[1980, 1] = 1
        }
    }

    private var mSharedPreferenceEntry: SharedPreferenceEntry? = null
    private var mMockSharedPreferencesHelper: SharedPreferencesHelper? = null
    private var mMockBrokenSharedPreferencesHelper: SharedPreferencesHelper? = null

    @org.mockito.Mock
    var mMockSharedPreferences: SharedPreferences? = null

    @org.mockito.Mock
    var mMockBrokenSharedPreferences: SharedPreferences? = null

    @org.mockito.Mock
    var mMockEditor: Editor? = null

    @org.mockito.Mock
    var mMockBrokenEditor: Editor? = null
    @Before
    fun initMocks() {
        // Create SharedPreferenceEntry to persist.
        mSharedPreferenceEntry = SharedPreferenceEntry(
            TEST_NAME, TEST_DATE_OF_BIRTH,
            TEST_EMAIL
        ) // Create a mocked SharedPreferences.
        mMockSharedPreferencesHelper =
            createMockSharedPreference() // Create a mocked SharedPreferences that fails at saving data.
        mMockBrokenSharedPreferencesHelper = createBrokenMockSharedPreference()
    }

    @Test
    fun sharedPreferencesHelper_SaveAndReadPersonalInformation() {
        // Save the personal information to SharedPreferences
        val success = mMockSharedPreferencesHelper!!.savePersonalInfo(mSharedPreferenceEntry!!)
        MatcherAssert.assertThat(
            "Checking that SharedPreferenceEntry.save... returns true",
            success, CoreMatchers.`is`(true)
        ) // Read personal information from SharedPreferences
        val savedSharedPreferenceEntry =
            mMockSharedPreferencesHelper!!.personalInfo // Make sure both written and retrieved personal information are equal.
        MatcherAssert.assertThat(
            "Checking that SharedPreferenceEntry.name has been persisted and read correctly",
            mSharedPreferenceEntry!!.name,
            CoreMatchers.`is`(CoreMatchers.equalTo(savedSharedPreferenceEntry.name))
        )
        MatcherAssert.assertThat(
            "Checking that SharedPreferenceEntry.dateOfBirth has been persisted and read "
                    + "correctly",
            mSharedPreferenceEntry!!.dateOfBirth,
            CoreMatchers.`is`(CoreMatchers.equalTo(savedSharedPreferenceEntry.dateOfBirth))
        )
        MatcherAssert.assertThat(
            ("Checking that SharedPreferenceEntry.email has been persisted and read "
                    + "correctly"),
            mSharedPreferenceEntry!!.email,
            CoreMatchers.`is`(CoreMatchers.equalTo(savedSharedPreferenceEntry.email))
        )
    }

    @Test
    fun sharedPreferencesHelper_SavePersonalInformationFailed_ReturnsFalse() {
        // Read personal information from a broken SharedPreferencesHelper
        val success =
            mMockBrokenSharedPreferencesHelper!!.savePersonalInfo((mSharedPreferenceEntry)!!)
        MatcherAssert.assertThat(
            "Makes sure writing to a broken SharedPreferencesHelper returns false", success,
            CoreMatchers.`is`(false)
        )
    }

    /**
     * Creates a mocked SharedPreferences.
     */
    private fun createMockSharedPreference(): SharedPreferencesHelper {
        // Mocking reading the SharedPreferences as if mMockSharedPreferences was previously written
        // correctly.
        Mockito.`when`<String>(
            mMockSharedPreferences!!.getString(
                org.mockito.Matchers.eq<String>(
                    SharedPreferencesHelper.KEY_NAME
                ), org.mockito.Matchers.anyString()
            )
        )
            .thenReturn(mSharedPreferenceEntry!!.name)
        Mockito.`when`<String>(
            mMockSharedPreferences!!.getString(
                org.mockito.Matchers.eq<String>(
                    SharedPreferencesHelper.KEY_EMAIL
                ), org.mockito.Matchers.anyString()
            )
        )
            .thenReturn(mSharedPreferenceEntry!!.email)
        Mockito.`when`<Long>(
            mMockSharedPreferences!!.getLong(
                org.mockito.Matchers.eq<String>(
                    SharedPreferencesHelper.KEY_DOB
                ), org.mockito.Matchers.anyLong()
            )
        )
            .thenReturn(mSharedPreferenceEntry!!.dateOfBirth.timeInMillis) // Mocking a successful commit.
        Mockito.`when`<Boolean>(mMockEditor!!.commit())
            .thenReturn(true) // Return the MockEditor when requesting it.
        Mockito.`when`<Editor>(mMockSharedPreferences!!.edit()).thenReturn(mMockEditor)
        return SharedPreferencesHelper((mMockSharedPreferences)!!)
    }

    /**
     * Creates a mocked SharedPreferences that fails when writing.
     */
    private fun createBrokenMockSharedPreference(): SharedPreferencesHelper {
        // Mocking a commit that fails.
        Mockito.`when`<Boolean>(mMockBrokenEditor!!.commit())
            .thenReturn(false) // Return the broken MockEditor when requesting it.
        Mockito.`when`<Editor>(mMockBrokenSharedPreferences!!.edit()).thenReturn(mMockBrokenEditor)
        return SharedPreferencesHelper((mMockBrokenSharedPreferences)!!)
    }
}