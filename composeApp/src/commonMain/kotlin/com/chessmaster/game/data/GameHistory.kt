package com.chessmaster.game.data

import com.chessmaster.game.logic.Move1
import com.chessmaster.game.logic.Piece
import com.chessmaster.game.logic.PieceColor
import com.chessmaster.game.logic.Position
import androidx.compose.runtime.mutableStateListOf

data class SavedGame(
    val id: String,
    val name: String,
    val date: String,
    val board: List<List<String?>>, // Store piece IDs as strings
    val moves: List<SavedMove>,
    val currentMoveIndex: Int,
    val currentTurn: String // "WHITE" or "BLACK"
)

data class SavedMove(
    val pieceId: String,
    val fromY: Int,
    val fromX: Int,
    val toY: Int,
    val toX: Int,
    val isCapture: Boolean,
    val captureSquareY: Int?,
    val captureSquareX: Int?
)

class GameHistoryManager {
    private val _savedGames = mutableStateListOf<SavedGame>()
    private var gameIdCounter = 0
    
    val savedGames: List<SavedGame>
        get() = _savedGames
    
    init {
        loadGames()
    }
    
    fun loadGames() {
        _savedGames.clear()
        _savedGames.addAll(LocalDatabase.getAllGames())
        // Update counter based on existing games
        gameIdCounter = _savedGames.size
    }
    
    fun saveGame(
        name: String,
        board: MutableList<MutableList<Piece?>>,
        moves: List<Move1>,
        currentMoveIndex: Int,
        currentTurn: PieceColor
    ): String {
        val gameId = generateGameId()
        val currentDate = getCurrentDateTimeString()
        
        // Convert board to serializable format
        val serializedBoard = board.map { row ->
            row.map { piece -> piece?.p_id }
        }
        
        // Convert moves to serializable format
        val serializedMoves = moves.map { move ->
            SavedMove(
                pieceId = move.piece.p_id,
                fromY = move.from.y,
                fromX = move.from.x,
                toY = move.to.y,
                toX = move.to.x,
                isCapture = move.isCapture,
                captureSquareY = move.captureSquare?.y,
                captureSquareX = move.captureSquare?.x
            )
        }
        
        val savedGame = SavedGame(
            id = gameId,
            name = name,
            date = currentDate,
            board = serializedBoard,
            moves = serializedMoves,
            currentMoveIndex = currentMoveIndex,
            currentTurn = if (currentTurn == PieceColor.WHITE) "WHITE" else "BLACK"
        )
        
        LocalDatabase.saveGame(savedGame)
        _savedGames.add(0, savedGame) // Add to beginning
        return gameId
    }
    
    fun deleteGame(gameId: String) {
        LocalDatabase.deleteGame(gameId)
        _savedGames.removeAll { it.id == gameId }
    }
    
    fun getGame(gameId: String): SavedGame? {
        return _savedGames.find { it.id == gameId }
    }
    
    private fun generateGameId(): String {
        gameIdCounter++
        return "game_$gameIdCounter"
    }
    
    private fun getCurrentDateTimeString(): String {
        return "Game #$gameIdCounter"
    }
}

// Extension functions to convert between saved and runtime formats
fun SavedGame.toRuntimeBoard(): MutableList<MutableList<Piece?>> {
    return board.map { row ->
        row.map { pieceId -> 
            if (pieceId != null) Piece.p_typeFromId(pieceId) else null
        }.toMutableList()
    }.toMutableList()
}

fun SavedGame.toRuntimeMoves(): ArrayList<Move1> {
    val result = ArrayList<Move1>()
    moves.forEach { savedMove ->
        val piece = Piece.p_typeFromId(savedMove.pieceId)
        if (piece != null) {
            val captureSquare = if (savedMove.captureSquareY != null && savedMove.captureSquareX != null) {
                Position(savedMove.captureSquareY, savedMove.captureSquareX)
            } else null
            
            result.add(Move1(
                piece = piece,
                from = Position(savedMove.fromY, savedMove.fromX),
                to = Position(savedMove.toY, savedMove.toX),
                isCapture = savedMove.isCapture,
                captureSquare = captureSquare
            ))
        }
    }
    return result
}
