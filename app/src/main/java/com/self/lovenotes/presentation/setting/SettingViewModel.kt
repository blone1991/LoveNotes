package com.self.lovenotes.presentation.setting

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.androidParameters
import com.google.firebase.dynamiclinks.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.dynamiclinks.shortLinkAsync
import com.self.lovenotes.data.remote.model.User
import com.self.lovenotes.data.remote.repository.UserRepository
import com.self.lovenotes.domain.usecase.CalendarUsecase
import com.self.lovenotes.domain.usecase.SettingUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingUsecase: SettingUsecase,
    private val dynamicLinks: FirebaseDynamicLinks
) : ViewModel() {
    val users = settingUsecase.users
    val shareInviteCode = MutableSharedFlow<String>()


    fun subscribe(code: String) {
        viewModelScope.launch {
            settingUsecase.subscribe(code)
        }
    }

    fun deleteSubscribe(user: User) {
        viewModelScope.launch {
            settingUsecase.deleteSubscribe(user)
        }
    }

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            settingUsecase.updateNickname(nickname)
        }
    }

    fun shareInvitelink() {
        viewModelScope.launch {
            dynamicLinks.shortLinkAsync {
                link =
                    Uri.parse("https://lovenotes8083d.page.link/invite?invite_code=${users.value[0].invitationCode}")
                domainUriPrefix = "https://lovenotes8083d.page.link"

                androidParameters("com.self.lovenotes") {
                    fallbackUrl =
                        Uri.parse("https://drive.google.com/file/d/1XH0Ush6ecNMLfuSRLvkiT6hI3WsAeUJp/view?usp=drive_link")
                }

                socialMetaTagParameters {
                    title = "친구 초대"
                    description = "링크를 통해 앱을 다운로드 받거나 초대한 친구를 등록할 수 있습니다."
                    imageUrl =
                        Uri.parse("https://github.com/blone1991/LoveNotes/blob/master/app/src/main/res/mipmap/love_notes_icon.jpg")
                }
            }.addOnSuccessListener {
                viewModelScope.launch {
                    it.shortLink?.let {
                        shareInviteCode.emit(value = it.toString())
                    }
                }
            }
        }
    }
}