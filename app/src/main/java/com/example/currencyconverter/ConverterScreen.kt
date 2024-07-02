package com.example.currencyconverter

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CurrencyScreen() {
    var firstCurrency by rememberSaveable { mutableStateOf("Euro") }
    var firstCurrencyIcon by rememberSaveable { mutableIntStateOf(R.drawable.ic_euro) }
    var secondCurrency by rememberSaveable { mutableStateOf("Dollar") }
    var secondCurrencyIcon by rememberSaveable { mutableIntStateOf(R.drawable.ic_dollar) }
    var inputValue by rememberSaveable { mutableStateOf("") }
    var convertedValue by rememberSaveable { mutableStateOf("") }

    fun swapCurrencies() {
        val tempCurrency = firstCurrency
        val tempIcon = firstCurrencyIcon

        firstCurrency = secondCurrency
        firstCurrencyIcon = secondCurrencyIcon

        secondCurrency = tempCurrency
        secondCurrencyIcon = tempIcon
    }

    fun convertCurrency(value: String, fromCurrency: String, toCurrency: String) {
        val rate = when (fromCurrency) {
            "Euro" -> when (toCurrency) {
                "Dollar" -> 1.07
                "Pound" -> 0.85
                "Yen" -> 173.39
                else -> 1.0
            }

            "Dollar" -> when (toCurrency) {
                "Euro" -> 0.93
                "Pound" -> 0.79
                "Yen" -> 161.55
                else -> 1.0
            }

            "Pound" -> when (toCurrency) {
                "Euro" -> 1.18
                "Dollar" -> 1.27
                "Yen" -> 204.74
                else -> 1.0
            }

            "Yen" -> when (toCurrency) {
                "Euro" -> 0.0058
                "Dollar" -> 0.0062
                "Pound" -> 0.0049
                else -> 1.0
            }

            else -> 1.0

        }
        val result = value.toDoubleOrNull()?.times(rate) ?: 0.0
        convertedValue = String.format("%.2f", result)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 270.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CurrencyValue(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                value = inputValue,
                onValueChange = {
                    inputValue = it
                    convertCurrency(inputValue, firstCurrency, secondCurrency)
                })
            Spacer(modifier = Modifier.size(25.dp))
            CurrencyMenu(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                selectedCurrency = firstCurrency,
                selectedCurrencyIcon = firstCurrencyIcon,
                onCurrencySelected = { currency, icon ->
                    firstCurrency = currency
                    firstCurrencyIcon = icon
                    convertCurrency(inputValue, firstCurrency, secondCurrency)
                }
            )
            Spacer(modifier = Modifier.size(10.dp))
            SwapIcon(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    swapCurrencies()
                    convertCurrency(inputValue, firstCurrency, secondCurrency)
                }
            )
            Spacer(modifier = Modifier.size(10.dp))
            CurrencyMenu(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                selectedCurrency = secondCurrency,
                selectedCurrencyIcon = secondCurrencyIcon,
                onCurrencySelected = { currency, icon ->
                    secondCurrency = currency
                    secondCurrencyIcon = icon
                    convertCurrency(inputValue, firstCurrency, secondCurrency)
                }
            )
            Spacer(modifier = Modifier.size(25.dp))
            FinalResult(modifier = Modifier.align(Alignment.CenterHorizontally), convertedValue)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyMenu(
    modifier: Modifier,
    selectedCurrency: String,
    selectedCurrencyIcon: Int,
    onCurrencySelected: (String, Int) -> Unit
) {
    val currency = listOf("Euro", "Dollar", "Pound", "Yen")
    val currencyIcons =
        listOf(R.drawable.ic_euro, R.drawable.ic_dollar, R.drawable.ic_pound, R.drawable.ic_yen)
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }) {
            TextField(
                modifier = Modifier.menuAnchor(),
                value = selectedCurrency,
                onValueChange = {},
                readOnly = true,
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ), shape = RoundedCornerShape(15.dp), textStyle = TextStyle(fontSize = 20.sp),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = selectedCurrencyIcon),
                        contentDescription = "Icon selected"
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
            )
            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                currency.forEachIndexed { index, text ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = currencyIcons[index]),
                                    contentDescription = "Icons list"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = text)
                            }
                        },
                        onClick = {
                            onCurrencySelected(currency[index], currencyIcons[index])
                            isExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                    if (index < currency.size - 1) {
                        HorizontalDivider(color = Color(0xFF224B92))
                    }
                }
            }
        }
    }
}

@Composable
fun SwapIcon(modifier: Modifier, onClick: () -> Unit) {
    Column(modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_swap),
            contentDescription = "Swap icon",
            modifier = Modifier
                .size(40.dp)
                .clickable { onClick() },
            tint = Color(0xFF224B92)
        )
    }
}

@Composable
fun FinalResult(modifier: Modifier, result: String) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = result,
            onValueChange = {},
            readOnly = true,
            textStyle = TextStyle(fontSize = 20.sp)
        )
    }
}

@Composable
fun CurrencyValue(modifier: Modifier, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            maxLines = 1,
            singleLine = true,
            label = { Text(text = "Enter value to convert") },
            textStyle = TextStyle(fontSize = 20.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
    }
}
