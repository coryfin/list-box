package com.coreo.listbox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.coreo.listbox.ui.theme.OPTION_COLOR_PALETTE
import com.coreo.listbox.ui.theme.OptionColorEntry
import com.coreo.listbox.ui.theme.optionColorEntryFor

private val SWATCH_SIZE = 28.dp
private val PICKER_COLUMNS = 4

@Composable
private fun OptionColorEntry.resolvedColor(): Color {
    return if (isSystemInDarkTheme()) darkTextColor else lightTextColor
}

@Composable
private fun OptionColorEntry.checkTint(): Color {
    return if (isSystemInDarkTheme()) Color(0xFF212121) else Color.White
}

/**
 * A small color-swatch circle that the user taps to open the color picker.
 * The swatch color matches what the option text will look like at runtime.
 *
 * @param tokenName        Currently selected color token name (e.g. "SKY").
 * @param expanded         Whether the picker popup is currently open.
 * @param onToggle         Called when the swatch is tapped to toggle the popup.
 * @param onColorSelected  Called with the new token name when the user picks a color.
 */
@Composable
fun OptionColorSwatch(
    tokenName: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    val currentEntry = optionColorEntryFor(tokenName)

    Box {
        Box(
            modifier = Modifier
                .size(SWATCH_SIZE)
                .clip(CircleShape)
                .background(currentEntry.resolvedColor())
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
                .clickable { onToggle() }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onToggle
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OPTION_COLOR_PALETTE.chunked(PICKER_COLUMNS).forEach { rowColors ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowColors.forEach { entry ->
                            val isSelected = entry.tokenName == tokenName
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(SWATCH_SIZE)
                                    .clip(CircleShape)
                                    .background(entry.resolvedColor())
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                                    .clickable { onColorSelected(entry.tokenName) }
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = entry.checkTint(),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A small non-interactive filled circle used as a leading color indicator in dropdowns
 * and text fields. Color matches the option text color for the current theme.
 *
 * @param tokenName  The color token name to look up (e.g. "SKY").
 * @param modifier   Optional layout modifier.
 */
@Composable
fun OptionColorDot(
    tokenName: String,
    modifier: Modifier = Modifier
) {
    val entry = optionColorEntryFor(tokenName)
    Box(
        modifier = modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(entry.resolvedColor())
    )
}
