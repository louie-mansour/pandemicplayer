package dev.louiem.pandemicplayer.domain.valueobject

enum class Colour(var isCured: Boolean = false, var isErradicated: Boolean = false) {
    Blue,
    Yellow,
    Black,
    Red
}