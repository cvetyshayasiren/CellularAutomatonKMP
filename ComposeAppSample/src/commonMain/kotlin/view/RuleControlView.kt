package view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cellularAutomaton.CellularAutomatonState
import theme.defaultPadding

@Composable
fun RuleControlView(caState: CellularAutomatonState) {
    val rule = caState.rule.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = "B", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            repeat(8) {
                val element = it + 1
                val selected = element in rule.value.born
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(element.toString())
                    RadioButton(
                        modifier = Modifier.size(defaultPadding * 3),
                        selected = selected, onClick = {
                            caState.setRule(
                                rule.value.copy(
                                    born = if(selected) rule.value.born.minus(element) else
                                        rule.value.born.plus(element)
                                )
                            )
                        }
                    )
                }
            }
        }
        Spacer(Modifier.size(defaultPadding * 2))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("S", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            repeat(8) {
                val element = it + 1
                val selected = element in rule.value.survive
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(element.toString())
                    RadioButton(
                        modifier = Modifier.size(defaultPadding * 3),
                        selected = selected, onClick = {
                            caState.setRule(
                                rule.value.copy(
                                    survive = if(selected) rule.value.survive.minus(element) else
                                        rule.value.survive.plus(element)
                                )
                            )
                        }
                    )
                }
            }
        }
        Spacer(Modifier.size(defaultPadding * 2))
        ControlSlider(
            text = "aging ${rule.value.aging}",
            value = rule.value.aging.toFloat(),
            onValueChange = { caState.setRule(rule.value.copy(aging = it.toInt())) },
            valueRange = (0f..100f)
        )
    }
}