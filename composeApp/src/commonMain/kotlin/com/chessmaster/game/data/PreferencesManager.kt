package com.chessmaster.game.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.chessmaster.game.theme.BoardColorScheme

enum class GameMode {
    CASUAL, DEATH_MATCH
}

class PreferencesManager {
    // Theme preferences
    private val _isDarkMode: MutableState<Boolean> = mutableStateOf(false)
    private val _boardScheme: MutableState<BoardColorScheme> = mutableStateOf(BoardColorScheme.CLASSIC_BROWN)
    
    // Game preferences
    private val _gameMode: MutableState<GameMode> = mutableStateOf(GameMode.CASUAL)
    private val _showCoordinates: MutableState<Boolean> = mutableStateOf(true)
    private val _animationEnabled: MutableState<Boolean> = mutableStateOf(true)
    
    val isDarkMode: Boolean get() = _isDarkMode.value
    val boardScheme: BoardColorScheme get() = _boardScheme.value
    val gameMode: GameMode get() = _gameMode.value
    val showCoordinates: Boolean get() = _showCoordinates.value
    val animationEnabled: Boolean get() = _animationEnabled.value
    
    fun setDarkMode(value: Boolean) {
        _isDarkMode.value = value
        LocalDatabase.saveDarkMode(value)
    }
    
    fun setBoardScheme(scheme: BoardColorScheme) {
        _boardScheme.value = scheme
        LocalDatabase.saveBoardScheme(scheme)
    }
    
    fun setGameMode(mode: GameMode) {
        _gameMode.value = mode
        LocalDatabase.saveGameMode(mode)
    }
    
    fun setShowCoordinates(value: Boolean) {
        _showCoordinates.value = value
    }
    
    fun setAnimationEnabled(value: Boolean) {
        _animationEnabled.value = value
    }
    
    fun loadPreferences() {
        _isDarkMode.value = LocalDatabase.getDarkMode()
        _boardScheme.value = LocalDatabase.getBoardScheme()
        _gameMode.value = LocalDatabase.getGameMode()
    }
}
