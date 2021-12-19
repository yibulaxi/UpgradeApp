package ru.get.better.vm

import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import ru.get.better.repo.UserAchievementsRepository
import ru.get.better.repo.UserDiaryRepository
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
) : BaseViewModel() {

    fun getUserData() = Firebase.auth.currentUser

    fun updateUserData(
        name: String? = null
    ) {
        val profileUpdates = userProfileChangeRequest {

        }

        getUserData()?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
            }
    }

//    fun deleteUserData() {
//        getUserData().delete()
//            .addOnCompleteListener {
//
//            }
//    }
//
//    fun logout() {
//
//    }
}