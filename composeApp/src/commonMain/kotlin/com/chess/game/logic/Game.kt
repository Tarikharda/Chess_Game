package com.chess.game.logic

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.chess.game.ui.BoardView
import com.chess.game.ui.MovesView

@Composable
fun Game() {
    var moves by remember { mutableStateOf("") }

    Column {
        BoardView(
            moves,
        ) { newMove -> moves = newMove }
        Divider()
        MovesView(moves)
    }
}
