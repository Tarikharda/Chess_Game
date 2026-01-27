package com.chessmaster.game.logic

// Check if the king of the given color is in check
fun isKingInCheck(board: List<List<Piece?>>, kingColor: PieceColor): Boolean {
    // Find the king position
    var kingPos: Position? = null
    for (y in 0..7) {
        for (x in 0..7) {
            val piece = board[y][x]
            if (piece != null && piece.p_type == PieceType.KING && piece.p_color == kingColor) {
                kingPos = Position(y, x)
                break
            }
        }
        if (kingPos != null) break
    }
    
    if (kingPos == null) return false
    
    // Check if any opponent piece can attack the king
    for (y in 0..7) {
        for (x in 0..7) {
            val piece = board[y][x]
            if (piece != null && piece.p_color != kingColor) {
                val moves = p_movement(piece, Position(y, x), board.toMutableBoard(), null)
                if (moves.any { it.to == kingPos }) {
                    return true
                }
            }
        }
    }
    
    return false
}

// Check if the player has any valid moves (for checkmate/stalemate detection)
fun hasValidMoves(board: List<List<Piece?>>, playerColor: PieceColor): Boolean {
    for (y in 0..7) {
        for (x in 0..7) {
            val piece = board[y][x]
            if (piece != null && piece.p_color == playerColor) {
                val moves = p_movement(piece, Position(y, x), board.toMutableBoard(), null)
                // Check if any move would not leave the king in check
                for (move in moves) {
                    val testBoard = board.toMutableBoard()
                    testBoard[y][x] = null
                    if (move.captureSquare != null) {
                        testBoard[move.captureSquare.y][move.captureSquare.x] = null
                    }
                    testBoard[move.to.y][move.to.x] = piece
                    
                    if (!isKingInCheck(testBoard, playerColor)) {
                        return true
                    }
                }
            }
        }
    }
    return false
}

// Check if it's checkmate
fun isCheckmate(board: List<List<Piece?>>, playerColor: PieceColor): Boolean {
    return isKingInCheck(board, playerColor) && !hasValidMoves(board, playerColor)
}

// Check if it's stalemate
fun isStalemate(board: List<List<Piece?>>, playerColor: PieceColor): Boolean {
    return !isKingInCheck(board, playerColor) && !hasValidMoves(board, playerColor)
}

// Helper function to convert immutable list to mutable
fun List<List<Piece?>>.toMutableBoard(): MutableList<MutableList<Piece?>> {
    return this.map { it.toMutableList() }.toMutableList()
}

// Get all valid moves for a piece (moves that don't leave king in check)
fun getValidMoves(piece: Piece, position: Position, board: List<List<Piece?>>, lastMove: Move1?): List<Move1> {
    val allMoves = p_movement(piece, position, board.toMutableBoard(), lastMove)
    val validMoves = mutableListOf<Move1>()
    
    for (move in allMoves) {
        val testBoard = board.toMutableBoard()
        testBoard[position.y][position.x] = null
        if (move.captureSquare != null) {
            testBoard[move.captureSquare.y][move.captureSquare.x] = null
        }
        testBoard[move.to.y][move.to.x] = piece
        
        if (!isKingInCheck(testBoard, piece.p_color)) {
            validMoves.add(move)
        }
    }
    
    return validMoves
}
