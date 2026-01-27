package com.chessmaster.game.theme

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

enum class BoardColorScheme {
    CLASSIC_BROWN,
    BLUE_MARBLE,
    GREEN_FOREST,
    GRAY_STONE
}

data class BoardColors(
    val lightSquare: Color,
    val darkSquare: Color,
    val hintMoveColor: Color,
    val moveTraceColor: Color,
    val name: String
)

object BoardThemes {
    val classicBrown = BoardColors(
        lightSquare = Color(0xFFF0D9B5),
        darkSquare = Color(0xFFB58863),
        hintMoveColor = Color(0xFF829769),
        moveTraceColor = Color(0xFFBBCA2B),
        name = "Classic"
    )
    
    val blueMarble = BoardColors(
        lightSquare = Color(0xFFDEE3E6),
        darkSquare = Color(0xFF8CA2AD),
        hintMoveColor = Color(0xFF5B8FA3),
        moveTraceColor = Color(0xFF6FA8DC),
        name = "Blue"
    )
    
    val greenForest = BoardColors(
        lightSquare = Color(0xFFEBECD0),
        darkSquare = Color(0xFF779556),
        hintMoveColor = Color(0xFF5B7444),
        moveTraceColor = Color(0xFF9BC070),
        name = "Green"
    )
    
    val grayStone = BoardColors(
        lightSquare = Color(0xFFC8C8C8),
        darkSquare = Color(0xFF6E6E6E),
        hintMoveColor = Color(0xFF4A4A4A),
        moveTraceColor = Color(0xFF8A8A8A),
        name = "Gray"
    )
    
    fun getScheme(scheme: BoardColorScheme): BoardColors {
        return when (scheme) {
            BoardColorScheme.CLASSIC_BROWN -> classicBrown
            BoardColorScheme.BLUE_MARBLE -> blueMarble
            BoardColorScheme.GREEN_FOREST -> greenForest
            BoardColorScheme.GRAY_STONE -> grayStone
        }
    }
}

data class AppThemeColors(
    val backgroundColor: Color,
    val surfaceColor: Color,
    val textColor: Color,
    val textSecondaryColor: Color,
    val buttonColor: Color,
    val turnIndicatorWhite: Color,
    val turnIndicatorBlack: Color
)

object AppThemes {
    val light = AppThemeColors(
        backgroundColor = Color(0xFFF5F5F5),
        surfaceColor = Color(0xFFFFFFFF),
        textColor = Color(0xFF2C2C2C),
        textSecondaryColor = Color(0xFF757575),
        buttonColor = Color(0xFF6B6B6B),
        turnIndicatorWhite = Color(0xFFEEEEEE),
        turnIndicatorBlack = Color(0xFF333333)
    )
    
    val dark = AppThemeColors(
        backgroundColor = Color(0xFF1E1E1E),
        surfaceColor = Color(0xFF2D2D2D),
        textColor = Color(0xFFE0E0E0),
        textSecondaryColor = Color(0xFF9E9E9E),
        buttonColor = Color(0xFF424242),
        turnIndicatorWhite = Color(0xFF9E9E9E),
        turnIndicatorBlack = Color(0xFF212121)
    )
}

class EnhancedThemeManager {
    private val _isDarkMode: MutableState<Boolean> = mutableStateOf(false)
    private val _boardScheme: MutableState<BoardColorScheme> = mutableStateOf(BoardColorScheme.CLASSIC_BROWN)
    
    val isDarkMode: Boolean
        get() = _isDarkMode.value
    
    val boardScheme: BoardColorScheme
        get() = _boardScheme.value
    
    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }
    
    fun setDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
    }
    
    fun setBoardScheme(scheme: BoardColorScheme) {
        _boardScheme.value = scheme
    }
    
    fun getAppTheme(): AppThemeColors {
        return if (_isDarkMode.value) AppThemes.dark else AppThemes.light
    }
    
    fun getBoardColors(): BoardColors {
        return BoardThemes.getScheme(_boardScheme.value)
    }
}
