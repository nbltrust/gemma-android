package jdenticon

class Config (
        saturation: Float,
        colorLightness: (Float) -> Float,
        grayscaleLightness: (Float) -> Float,
        backColor: String
) {
    var saturation = saturation
    var colorLightness = colorLightness
    var grayscaleLightness = grayscaleLightness
    var backColor = backColor
}