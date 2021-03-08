package com.haraj.myapplicationwithunittesting

import java.util.*

/**
 * Model class containing personal information that will be saved to SharedPreferences.
 */
class SharedPreferenceEntry(// Name of the user.
    val name // Date of Birth of the user.
    : String, // Email address of the user.
    var dateOfBirth: Calendar, email: String
) {
    val email: String

    init {
        dateOfBirth = dateOfBirth
        this.email = email
    }
}