package com.chessmaster.game.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    LIGHT, DARK
}

data class ChessTheme(
    val lightSquare: Color,
    val darkSquare: Color,
    val hintMoveColor: Color,
    val moveTraceColor: Color,
    val backgroundColor: Color,
    val textColor: Color,
    val buttonColor: Color,
    val turnIndicatorWhite: Color,
    val turnIndicatorBlack: Color
)

object AppTheme {
    val lightTheme = ChessTheme(
        lightSquare = Color(0xFF7C4C3E),
        darkSquare = Color(0xFF512A2A),
        hintMoveColor = Color(0xFF3f8171),
        moveTraceColor = Color(0xFFb7c978),
        backgroundColor = Color(0xFFF5F5F5),
        textColor = Color(0xFF000000),
        buttonColor = Color(0xFF757575),
        turnIndicatorWhite = Color(0xFFc9c9ca),
        turnIndicatorBlack = Color.Black
    )
    
    val darkTheme = ChessTheme(
        lightSquare = Color(0xFF4A4A4A),
        darkSquare = Color(0xFF2B2B2B),
        hintMoveColor = Color(0xFF1e5d4f),
        moveTraceColor = Color(0xFF5d7a3a),
        backgroundColor = Color(0xFF1E1E1E),
        textColor = Color(0xFFE0E0E0),
        buttonColor = Color(0xFF424242),
        turnIndicatorWhite = Color(0xFF9E9E9E),
        turnIndicatorBlack = Color(0xFF212121)
    )
}

class ThemeManager {
    private val _currentTheme: MutableState<ThemeMode> = mutableStateOf(ThemeMode.LIGHT)
    
    val currentTheme: ThemeMode
        get() = _currentTheme.value
    
    fun toggleTheme() {
        _currentTheme.value = if (_currentTheme.value == ThemeMode.LIGHT) {
            ThemeMode.DARK
        } else {
            ThemeMode.LIGHT
        }
    }
    
    fun setTheme(mode: ThemeMode) {
        _currentTheme.value = mode
    }
    
    fun getTheme(): ChessTheme {
        return when (_currentTheme.value) {
            ThemeMode.LIGHT -> AppTheme.lightTheme
            ThemeMode.DARK -> AppTheme.darkTheme
        }
    }
}

@Composable
fun rememberThemeManager(): ThemeManager {
    return remember { ThemeManager() }
}
