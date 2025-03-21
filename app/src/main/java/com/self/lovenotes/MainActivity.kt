package com.self.lovenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.self.lovenotes.ui.navigation.AppNavGraph
import com.self.lovenotes.ui.theme.LoveNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /***
        TODO-LIST:
        1. 이벤트 수정 기능 (카드 터치 시 이벤트 변경화면 팝업)
        2. 구독중인 사람들 편집. 한명씩 삭제 가능하도록.
        3. Journal 입력 기능 (개인 일기 자료)
         ***/

        enableEdgeToEdge()
        setContent {
            LoveNotesTheme {
                AppNavGraph(navController = rememberNavController())
            }
        }
    }
}