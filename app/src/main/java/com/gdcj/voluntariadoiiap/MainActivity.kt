package com.gdcj.voluntariadoiiap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gdcj.voluntariadoiiap.data.local.SessionManager
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import com.gdcj.voluntariadoiiap.navigation.AppNavigation
import com.gdcj.voluntariadoiiap.ui.theme.VOLUNTARIADOIIAPTheme
import com.gdcj.voluntariadoiiap.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // 🔥 IMPORTANTE: inicializar Retrofit con SessionManager
        RetrofitClient.init(this)

        setContent {

            val sessionManager = remember { SessionManager(this) }

            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(sessionManager)
            )

            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(sessionManager)
            )

            val userViewModel: UserViewModel = viewModel()
            val roleViewModel: RoleViewModel = viewModel()
            val areaViewModel: AreaViewModel = viewModel()
            val projectViewModel: ProjectViewModel = viewModel()

            VOLUNTARIADOIIAPTheme(darkTheme = isDarkMode) {

                val backgroundColor by animateColorAsState(
                    targetValue = MaterialTheme.colorScheme.background,
                    animationSpec = tween(durationMillis = 500),
                    label = "backgroundColorAnimation"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                ) {

                    AppNavigation(
                        themeViewModel = themeViewModel,
                        authViewModel = authViewModel,
                        userViewModel = userViewModel,
                        roleViewModel = roleViewModel,
                        areaViewModel = areaViewModel,
                        projectViewModel = projectViewModel
                    )

                }
            }
        }
    }
}