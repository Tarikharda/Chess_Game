package com.chessmaster.game.data

import androidx.compose.runtime.mutableStateMapOf
import com.chessmaster.game.theme.BoardColorScheme

// Simple in-memory storage that can be extended to platform-specific persistent storage
object LocalDatabase {
    private val preferences = mutableMapOf<String, String>()
    private val savedGamesMap = mutableStateMapOf<String, SavedGame>()
    
    // Preferences keys
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_BOARD_SCHEME = "board_scheme"
    private const val KEY_GAME_MODE = "game_mode"
    private const val KEY_WHITE_PLAYER_NAME = "white_player_name"
    private const val KEY_BLACK_PLAYER_NAME = "black_player_name"
    private const val KEY_WHITE_PLAYER_ELO = "white_player_elo"
    private const val KEY_BLACK_PLAYER_ELO = "black_player_elo"
    private const val KEY_WHITE_PLAYER_WINS = "white_player_wins"
    private const val KEY_BLACK_PLAYER_WINS = "black_player_wins"
    private const val KEY_WHITE_PLAYER_LOSSES = "white_player_losses"
    private const val KEY_BLACK_PLAYER_LOSSES = "black_player_losses"
    private const val KEY_WHITE_PLAYER_DRAWS = "white_player_draws"
    private const val KEY_BLACK_PLAYER_DRAWS = "black_player_draws"
    
    // Save preference
    fun saveString(key: String, value: String) {
        preferences[key] = value
    }
    
    fun saveInt(key: String, value: Int) {
        preferences[key] = value.toString()
    }
    
    fun saveBoolean(key: String, value: Boolean) {
        preferences[key] = value.toString()
    }
    
    // Get preference
    fun getString(key: String, defaultValue: String = ""): String {
        return preferences[key] ?: defaultValue
    }
    
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return preferences[key]?.toIntOrNull() ?: defaultValue
    }
    
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return preferences[key]?.toBooleanStrictOrNull() ?: defaultValue
    }
    
    // Theme preferences
    fun saveDarkMode(isDark: Boolean) = saveBoolean(KEY_DARK_MODE, isDark)
    fun getDarkMode(): Boolean = getBoolean(KEY_DARK_MODE, false)
    
    fun saveBoardScheme(scheme: BoardColorScheme) = saveString(KEY_BOARD_SCHEME, scheme.name)
    fun getBoardScheme(): BoardColorScheme {
        val schemeName = getString(KEY_BOARD_SCHEME, BoardColorScheme.CLASSIC_BROWN.name)
        return try {
            BoardColorScheme.valueOf(schemeName)
        } catch (e: Exception) {
            BoardColorScheme.CLASSIC_BROWN
        }
    }
    
    fun saveGameMode(mode: GameMode) = saveString(KEY_GAME_MODE, mode.name)
    fun getGameMode(): GameMode {
        val modeName = getString(KEY_GAME_MODE, GameMode.CASUAL.name)
        return try {
            GameMode.valueOf(modeName)
        } catch (e: Exception) {
            GameMode.CASUAL
        }
    }
    
    // Player data
    fun savePlayerData(
        whitePlayer: Player,
        blackPlayer: Player
    ) {
        saveString(KEY_WHITE_PLAYER_NAME, whitePlayer.name)
        saveString(KEY_BLACK_PLAYER_NAME, blackPlayer.name)
        saveInt(KEY_WHITE_PLAYER_ELO, whitePlayer.elo)
        saveInt(KEY_BLACK_PLAYER_ELO, blackPlayer.elo)
        saveInt(KEY_WHITE_PLAYER_WINS, whitePlayer.wins)
        saveInt(KEY_BLACK_PLAYER_WINS, blackPlayer.wins)
        saveInt(KEY_WHITE_PLAYER_LOSSES, whitePlayer.losses)
        saveInt(KEY_BLACK_PLAYER_LOSSES, blackPlayer.losses)
        saveInt(KEY_WHITE_PLAYER_DRAWS, whitePlayer.draws)
        saveInt(KEY_BLACK_PLAYER_DRAWS, blackPlayer.draws)
    }
    
    fun loadPlayerData(): Pair<Player, Player> {
        val whitePlayer = Player(
            name = getString(KEY_WHITE_PLAYER_NAME, "White"),
            elo = getInt(KEY_WHITE_PLAYER_ELO, 1200),
            wins = getInt(KEY_WHITE_PLAYER_WINS, 0),
            losses = getInt(KEY_WHITE_PLAYER_LOSSES, 0),
            draws = getInt(KEY_WHITE_PLAYER_DRAWS, 0)
        )
        
        val blackPlayer = Player(
            name = getString(KEY_BLACK_PLAYER_NAME, "Black"),
            elo = getInt(KEY_BLACK_PLAYER_ELO, 1200),
            wins = getInt(KEY_BLACK_PLAYER_WINS, 0),
            losses = getInt(KEY_BLACK_PLAYER_LOSSES, 0),
            draws = getInt(KEY_BLACK_PLAYER_DRAWS, 0)
        )
        
        return Pair(whitePlayer, blackPlayer)
    }
    
    // Saved games
    fun saveGame(game: SavedGame) {
        savedGamesMap[game.id] = game
    }
    
    fun deleteGame(gameId: String) {
        savedGamesMap.remove(gameId)
    }
    
    fun getAllGames(): List<SavedGame> {
        return savedGamesMap.values.toList()
    }
    
    fun getGame(gameId: String): SavedGame? {
        return savedGamesMap[gameId]
    }
    
    fun clearAllGames() {
        savedGamesMap.clear()
    }
}
