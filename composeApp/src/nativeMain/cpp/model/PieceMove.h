//
// Created by itsme on 3/21/25.
//

#ifndef CHESSGAME_PIECEMOVE_H
#define CHESSGAME_PIECEMOVE_H



#include <string>


enum class PieceType{ PAWN , KNIGHT, BISHOP, ROOK, QUEEN, KING };

enum class PieceColor {BLACK , WHITE};

struct PieceMove{
   std::string p_id;
   PieceType p_type;
   PieceColor p_color;
};

#endif //CHESSGAME_PIECEMOVE_H
