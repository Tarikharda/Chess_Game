//
// Created by itsme on 3/20/25.
//

#ifndef CHESSGAME_CHESSPIECEMOVES_H
#define CHESSGAME_CHESSPIECEMOVES_H


#include <string>

enum class PieceType{ PAWN , KNIGHT, BISHOP, ROOK, QUEEN, KING };

enum class PieceColor {BLACK , WHITE};

class Piece {
private :
    std::string pieceName;
    PieceType pieceType;
    PieceColor pieceColor;
public :
    Piece(std::string p_name, PieceType p_type , PieceColor p_color);


    std::string getPieceName() const;
    PieceType getPieceType() const;
    PieceColor getPieceColor() const;


};
#endif //CHESSGAME_CHESSPIECEMOVES_H
