package com.chess.game.logic

import chessgame.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

data class Piece(
    var p_id: String,
    var p_type: PieceType,
    var p_color: PieceColor,
) {


    companion object {
        fun p_typeFromId(p_id: String?): Piece? {
            if (p_id == null) return null
            else {
                val piece = p_fromId(p_id)
                return Piece(p_id, piece.first, piece.second)
            }
        }

        fun p_image(p_type: PieceType?, p_color: PieceColor): DrawableResource {
            return when (p_type) {
                PieceType.PAWN -> if (p_color is PieceColor.WHITE) Res.drawable.w_pawn else Res.drawable.b_pawn
                PieceType.KNIGHT -> if (p_color is PieceColor.WHITE) Res.drawable.w_knight else Res.drawable.b_knight
                PieceType.BISHOP -> if (p_color is PieceColor.WHITE) Res.drawable.w_bishop else Res.drawable.b_bishop
                PieceType.ROOK -> if (p_color is PieceColor.WHITE) Res.drawable.w_rook else Res.drawable.b_rook
                PieceType.QUEEN -> if (p_color is PieceColor.WHITE) Res.drawable.w_queen else Res.drawable.b_queen
                PieceType.KING -> if (p_color is PieceColor.WHITE) Res.drawable.w_king else Res.drawable.b_king
                null -> throw IllegalStateException("The Piece Type not valid")
            }
        }
    }

}

sealed class PieceType(val value: Int) {
    object PAWN : PieceType(1)
    object KNIGHT : PieceType(3)
    object BISHOP : PieceType(3)
    object ROOK : PieceType(5)
    object QUEEN : PieceType(9)
    object KING : PieceType(0)
}

sealed class PieceColor {
    object WHITE : PieceColor()
    object BLACK : PieceColor()
}

private fun p_fromId(p_id: String): Pair<PieceType, PieceColor> {
    val p_id = p_id.toCharArray()

    if (p_id.size != 3) throw IllegalStateException("ID Should have 3 character")

    val p_color = if (p_id[0] == 'W') PieceColor.WHITE else PieceColor.BLACK
    val p_type = when (p_id[1]) {
        'P' -> PieceType.PAWN
        'N' -> PieceType.KNIGHT
        'B' -> PieceType.BISHOP
        'R' -> PieceType.ROOK
        'Q' -> PieceType.QUEEN
        'K' -> PieceType.KING
        else -> {
            throw IllegalStateException("This Type not Found")
        }
    }

    return p_type to p_color
}

var WHITE_INITIAL_POSITION = mutableListOf(
    mutableListOf("BR1", "BN2", "BB3", "BQ4", "BK5", "BB6", "BN7", "BR8").map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf("BP1", "BP2", "BP3", "BP4", "BP5", "BP6", "BP7", "BP8").map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf("WP1", "WP2", "WP3", "WP4", "WP5", "WP6", "WP7", "WP8").map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf("WR1", "WN2", "WB3", "WQ4", "WK5", "WB6", "WN7", "WR8").map { Piece.p_typeFromId(it) }.toMutableList()
)

data class Position(val y: Int, val x: Int)

fun p_movement(current_p: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {
    var listOfPositions: ArrayList<Pair<Position , Boolean>> = ArrayList()
    val pChar = current_p.p_id.toCharArray()

    listOfPositions =
        when (pChar[1]) {
        'P' -> pawnMove(current_p, currentPosition, board)
        'K'-> kingMove(current_p, currentPosition, board)
//        'Q'-> queenMove(current_p, currentPosition, board)
//        'R'-> rookMove(current_p, currentPosition, board)
//        'N'-> knightMove(current_p, currentPosition, board)
//        'B'-> bishopMove(current_p, currentPosition, board)
        null -> throw IllegalStateException("The Piece Type not valid")

        else -> {
            return ArrayList()
        }
    }
    return listOfPositions
}

private fun pawnMove(pawn: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {
    var listOfPositions: ArrayList<Pair<Position , Boolean>> = ArrayList()
    return listOfPositions
}

private fun kingMove(king: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {

    var listOfPositions: ArrayList<Pair<Position, Boolean>> = ArrayList()

    return listOfPositions
}

data class Move(
    val piece: Piece,
    val from: Position,
    val to: Position,
    var isTreated: Boolean = false
)

fun getMoveName(piece : Piece , y : Int , x : Char, preX : Char , isThreat : Boolean , cnt : Int) : String{
    var isWhite = piece.p_color == PieceColor.WHITE
    var moveCnt = if(isWhite)  " $cnt - " else ""
    return when (piece.p_type) {
        PieceType.PAWN -> moveCnt + if (!isThreat) "$x$y" else "${preX}x$x$y"
        PieceType.KNIGHT -> moveCnt + "N$x$y"
        PieceType.BISHOP -> moveCnt + "B$x$y"
        PieceType.ROOK -> moveCnt + "R$x$y"
        PieceType.QUEEN -> moveCnt + "Q$x$y"
        PieceType.KING -> moveCnt + "K$x$y"
        null -> throw IllegalStateException("The Piece Type not valid")
    }
}

