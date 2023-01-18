package com.ndup.berealtechnicaltest

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ndup.berealtechnicaltest.domain.User
import com.ndup.berealtechnicaltest.presentation.IMainListener
import com.ndup.berealtechnicaltest.presentation.MainViewModel
import com.ndup.berealtechnicaltest.repository.Repository
import com.ndup.berealtechnicaltest.repository.ServiceFactory

class MainActivity : AppCompatActivity() {

    private val repo by lazy { Repository(ServiceFactory.service) }
    private val mainListener: IMainListener by lazy { MainViewModel(repository = repo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            with(mainListener.getModelAsState().value) {
                println(currentUser)
                ActivityMainLayout(mainUser = currentUser)
            }
        }
    }

    @Composable
    private fun ActivityMainLayout(
        mainUser: User?,
    ) {
        Text(
            text = mainUser?.json ?: "No user identified yet",
            color = Color.White,
        )
    }

}