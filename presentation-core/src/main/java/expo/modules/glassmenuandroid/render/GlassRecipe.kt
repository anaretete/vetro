package expo.modules.glassmenuandroid.render

internal class GlassRecipe(
  val saturation: Float,
  val black: Float,
  val white: Float,
  val rollOff: Float,
  val fillAlpha: Float,
  val tint: Float = 0f,
  val tintAlpha: Float = 0f,
) {
  companion object {
    val ControlDark = GlassRecipe(1.3f, .14f, 1.125f, .35f, .06f)
    val MenuDark = GlassRecipe(1.3f, .18f, 1.125f, .38f, .10f, tint = .18f, tintAlpha = .24f)
    val ControlLight = GlassRecipe(1.2f, .45f, 1.05f, 0f, .22f)
    val MenuLight = GlassRecipe(1.2f, .48f, 1.05f, 0f, .24f, tint = .98f, tintAlpha = .28f)
  }
}
