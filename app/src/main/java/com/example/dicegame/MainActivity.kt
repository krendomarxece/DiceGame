package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceGameScreen()
        }
    }
}

@Composable
fun DiceGameScreen() {
    var diceValues by remember { mutableStateOf(List(6) { Random.nextInt(1, 7) }) }
    var resultText by remember { mutableStateOf("Result") }
    val scope = rememberCoroutineScope()
    var isRolling by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            DiceImage(value = diceValues[0])
            Spacer(modifier = Modifier.width(8.dp))
            DiceImage(value = diceValues[1])
            Spacer(modifier = Modifier.width(8.dp))
            DiceImage(value = diceValues[2])
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            DiceImage(value = diceValues[3])
            Spacer(modifier = Modifier.width(8.dp))
            DiceImage(value = diceValues[4])
            Spacer(modifier = Modifier.width(8.dp))
            DiceImage(value = diceValues[5])
        }

        Button(
            onClick = {
                if (!isRolling) {
                    isRolling = true
                    resultText = "Rolling..."
                    scope.launch {
                        repeat(10) {
                            diceValues = List(6) { Random.nextInt(1, 7) }
                            delay(100)
                        }
                        isRolling = false
                        resultText = evaluateResult(diceValues)
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Roll Dice")
        }

        Text(
            text = resultText,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Composable
fun DiceImage(value: Int) {
    Image(
        painter = painterResource(id = getDiceImage(value)),
        contentDescription = null,
        modifier = Modifier.size(80.dp)
    )
}

fun getDiceImage(value: Int): Int {
    return when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }
}

fun evaluateResult(diceValues: List<Int>): String {
    diceValues.sorted()
    val counts = diceValues.groupingBy { it }.eachCount()
    return when {
        counts.containsValue(6) -> "Six of a Kind - ${diceValues.sorted().joinToString(" ")}"
        counts.containsValue(5) -> "Five of a Kind - ${diceValues.sorted().joinToString(" ")}"
        counts.containsValue(4) -> "Four of a Kind - ${diceValues.sorted().joinToString(" ")}"
        counts.containsValue(3) && counts.containsValue(2) -> "Full House - ${diceValues.sorted().joinToString(" ")}"
        counts.containsValue(3) -> "Three of a Kind - ${diceValues.sorted().joinToString(" ")}"
        counts.filterValues { it == 2 }.size == 2 -> "Two Pairs - ${diceValues.sorted().joinToString(" ")}"
        counts.containsValue(2) -> "One Pair - ${diceValues.sorted().joinToString(" ")}"
        diceValues.sorted() == listOf(1, 2, 3, 4, 5, 6) -> "Straight - ${diceValues.sorted().joinToString(" ")}"
        else -> "No matches - ${diceValues.sorted().joinToString(" ")}"
    }
}