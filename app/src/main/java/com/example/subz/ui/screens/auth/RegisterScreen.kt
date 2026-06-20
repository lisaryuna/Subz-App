package com.example.subz.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subz.ui.components.AuthButton
import com.example.subz.ui.components.AuthFooter
import com.example.subz.ui.components.AuthHeader
import com.example.subz.ui.components.SubzPasswordField
import com.example.subz.ui.components.SubzTextField
import com.example.subz.ui.theme.PrimaryBlue
import com.example.subz.ui.viewmodel.AuthState
import com.example.subz.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            viewModel.resetState()
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthHeader(
            title = "Create Account",
            subtitle = "Join Subz to start managing your\nsubscriptions."
        )

        SubzTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            placeholder = "John Doe",
            leadingIcon = Icons.Default.Person,
        )
        Spacer(modifier = Modifier.height(16.dp))

        SubzTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "john@example.com",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(16.dp))

        SubzPasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            leadingIcon = Icons.Default.Lock
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        AuthButton(
            text = "Register",
            isLoading = authState is AuthState.Loading,
            onClick = { viewModel.register(fullName, email, password) }
        )
        Spacer(modifier = Modifier.height(24.dp))

        AuthFooter(
            questionText = "Already have an account? ",
            actionText = "Log In",
            onActionClick = onNavigateToLogin
        )
    }
}