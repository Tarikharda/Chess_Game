package com.chessmaster.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chessmaster.game.theme.AppThemeColors
import com.chessmaster.game.theme.BoardColorScheme
import com.chessmaster.game.theme.BoardThemes

@Composable
fun SettingsScreen(
    appTheme: AppThemeColors,
    isDarkMode: Boolean,
    currentBoardScheme: BoardColorScheme,
    onToggleTheme: () -> Unit,
    onBoardSchemeChange: (BoardColorScheme) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appTheme.backgroundColor)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = appTheme.textColor
            )
            
            TextButton(onClick = onBack) {
                Text("Done", color = appTheme.textColor)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // App Theme Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = appTheme.surfaceColor,
            elevation = 0.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "App Theme",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = appTheme.textColor
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Dark Mode",
                        fontSize = 16.sp,
                        color = appTheme.textColor
                    )
                    
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF4CAF50),
                            uncheckedThumbColor = Color.Gray
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Board Color Schemes
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = appTheme.surfaceColor,
            elevation = 0.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Board Colors",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = appTheme.textColor
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Board scheme options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BoardColorOption(
                        scheme = BoardColorScheme.CLASSIC_BROWN,
                        isSelected = currentBoardScheme == BoardColorScheme.CLASSIC_BROWN,
                        onClick = { onBoardSchemeChange(BoardColorScheme.CLASSIC_BROWN) }
                    )
                    
                    BoardColorOption(
                        scheme = BoardColorScheme.BLUE_MARBLE,
                        isSelected = currentBoardScheme == BoardColorScheme.BLUE_MARBLE,
                        onClick = { onBoardSchemeChange(BoardColorScheme.BLUE_MARBLE) }
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BoardColorOption(
                        scheme = BoardColorScheme.GREEN_FOREST,
                        isSelected = currentBoardScheme == BoardColorScheme.GREEN_FOREST,
                        onClick = { onBoardSchemeChange(BoardColorScheme.GREEN_FOREST) }
                    )
                    
                    BoardColorOption(
                        scheme = BoardColorScheme.GRAY_STONE,
                        isSelected = currentBoardScheme == BoardColorScheme.GRAY_STONE,
                        onClick = { onBoardSchemeChange(BoardColorScheme.GRAY_STONE) }
                    )
                }
            }
        }
    }
}

@Composable
fun BoardColorOption(
    scheme: BoardColorScheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = BoardThemes.getScheme(scheme)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        // Mini board preview
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) Color(0xFF4CAF50) else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Column {
                for (y in 0 until 4) {
                    Row {
                        for (x in 0 until 4) {
                            val isLight = (y + x) % 2 == 0
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(if (isLight) colors.lightSquare else colors.darkSquare)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            colors.name,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF4CAF50) else Color.Gray
        )
    }
}
