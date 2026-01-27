package com.chessmaster.game.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class Player(
    val name: String,
    val elo: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int
) {
    val totalGames: Int
        get() = wins + losses + draws
}

class PlayerManager {
    private val _whitePlayer: MutableState<Player> = mutableStateOf(Player("White", 1200, 0, 0, 0))
    private val _blackPlayer: MutableState<Player> = mutableStateOf(Player("Black", 1200, 0, 0, 0))
    
    val whitePlayer: Player
        get() = _whitePlayer.value
    
    val blackPlayer: Player
        get() = _blackPlayer.value
    
    init {
        loadPlayers()
    }
    
    fun loadPlayers() {
        val (white, black) = LocalDatabase.loadPlayerData()
        _whitePlayer.value = white
        _blackPlayer.value = black
    }
    
    fun setPlayerNames(whiteName: String, blackName: String) {
        _whitePlayer.value = _whitePlayer.value.copy(name = whiteName.ifBlank { "White" })
        _blackPlayer.value = _blackPlayer.value.copy(name = blackName.ifBlank { "Black" })
        savePlayers()
    }
    
    fun recordWin(isWhiteWinner: Boolean) {
        val eloGain = 8
        
        if (isWhiteWinner) {
            _whitePlayer.value = _whitePlayer.value.copy(
                elo = _whitePlayer.value.elo + eloGain,
                wins = _whitePlayer.value.wins + 1
            )
            _blackPlayer.value = _blackPlayer.value.copy(
                elo = maxOf(0, _blackPlayer.value.elo - eloGain),
                losses = _blackPlayer.value.losses + 1
            )
        } else {
            _blackPlayer.value = _blackPlayer.value.copy(
                elo = _blackPlayer.value.elo + eloGain,
                wins = _blackPlayer.value.wins + 1
            )
            _whitePlayer.value = _whitePlayer.value.copy(
                elo = maxOf(0, _whitePlayer.value.elo - eloGain),
                losses = _whitePlayer.value.losses + 1
            )
        }
        savePlayers()
    }
    
    fun recordDraw() {
        _whitePlayer.value = _whitePlayer.value.copy(
            draws = _whitePlayer.value.draws + 1
        )
        _blackPlayer.value = _blackPlayer.value.copy(
            draws = _blackPlayer.value.draws + 1
        )
        savePlayers()
    }
    
    fun resetStats() {
        _whitePlayer.value = _whitePlayer.value.copy(
            elo = 1200,
            wins = 0,
            losses = 0,
            draws = 0
        )
        _blackPlayer.value = _blackPlayer.value.copy(
            elo = 1200,
            wins = 0,
            losses = 0,
            draws = 0
        )
        savePlayers()
    }
    
    private fun savePlayers() {
        LocalDatabase.savePlayerData(_whitePlayer.value, _blackPlayer.value)
    }
}
