package com.coreo.listbox.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Each [OptionColorEntry] represents one named color token in the option palette.
 *
 * @param tokenName       String identifier stored in the database (e.g. "SKY").
 * @param lightTextColor  Medium-vibrant shade for text/indicators in light theme.
 * @param darkTextColor   Lighter shade for text/indicators in dark theme.
 */
data class OptionColorEntry(
    val tokenName: String,
    val lightTextColor: Color,
    val darkTextColor: Color
)

/**
 * A curated palette of 8 visually distinct colors harmonized with the app's warm brown/blue
 * Material 3 theme. Light values are medium-vibrant (~M3 tone 40) for readability on light
 * backgrounds; dark values are light/pastel (~M3 tone 80) for readability on dark backgrounds.
 */
val OPTION_COLOR_PALETTE: List<OptionColorEntry> = listOf(
    OptionColorEntry("SKY",      Color(0xFF1565C0), Color(0xFF90CAF9)), // blue
    OptionColorEntry("MINT",     Color(0xFF2E7D32), Color(0xFFA5D6A7)), // green
    OptionColorEntry("ROSE",     Color(0xFF880E4F), Color(0xFFF48FB1)), // deep pink
    OptionColorEntry("PEACH",    Color(0xFFE65100), Color(0xFFFFAB91)), // deep orange
    OptionColorEntry("LAVENDER", Color(0xFF6A1B9A), Color(0xFFCE93D8)), // purple
    OptionColorEntry("LEMON",    Color(0xFFF9A825), Color(0xFFFFF176)), // amber/yellow
    OptionColorEntry("CORAL",    Color(0xFFB71C1C), Color(0xFFEF9A9A)), // red
    OptionColorEntry("TEAL",     Color(0xFF006064), Color(0xFF80DEEA)), // cyan/teal
)

/**
 * Picks the first palette token name not already used in [existingColors].
 * Wraps back to the first entry if all tokens are taken.
 */
fun pickColorForNewOption(existingColors: List<String>): String {
    return OPTION_COLOR_PALETTE.firstOrNull { it.tokenName !in existingColors }?.tokenName
        ?: OPTION_COLOR_PALETTE.first().tokenName
}

/**
 * Returns the [OptionColorEntry] for [tokenName], or the first palette entry as a fallback.
 * Also handles legacy hex strings gracefully by returning the fallback.
 */
fun optionColorEntryFor(tokenName: String): OptionColorEntry {
    return OPTION_COLOR_PALETTE.firstOrNull { it.tokenName == tokenName }
        ?: OPTION_COLOR_PALETTE.first()
}
