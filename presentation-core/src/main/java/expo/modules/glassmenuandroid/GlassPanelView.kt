package expo.modules.glassmenuandroid

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.RenderNode
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import expo.modules.glassmenuandroid.overlay.GlassMenuOverlay
import expo.modules.glassmenuandroid.records.GlassMenuConfigRecord
import expo.modules.glassmenuandroid.records.GlassTriggerConfigRecord
import expo.modules.glassmenuandroid.render.GlassRenderer
import expo.modules.glassmenuandroid.render.GlassSurfaceState
import kotlin.math.max
import kotlin.math.min

internal fun Context.findHostActivity(): Activity? {
  var current: Context? = this
  while (current is ContextWrapper) {
    if (current is Activity) return current
    current = current.baseContext
  }
  return null
}

/**
 * Native liquid glass surface powered by the exact same [GlassRenderer] (`GlassMaterialShader` +
 * `GlassShadowShader` + `GlassRecipe.MenuDark` / `GlassRecipe.MenuLight`) as [GlassMenuOverlay].
 *
 * Captures the host Activity's `android.R.id.content` hierarchy into a hardware [RenderNode] and
 * renders real SDF squircle lens refraction, 26dp Gaussian blur, 1.3x saturation, and CASDF
 * specular rim highlights—even when hosted inside a separate Dialog or Popup window.
 */
open class GlassPanelView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr), ViewTreeObserver.OnPreDrawListener {

  private val density = resources.displayMetrics.density
  private val supportsGlass = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
  private var glassRenderer: Any? = null
  private var backdropNode: Any? = null
  private val surfaceState = GlassSurfaceState()
  private val limits = Rect()
  private val viewLoc = IntArray(2)
  private val rootLoc = IntArray(2)
  private var lastScreenX = Int.MIN_VALUE
  private var lastScreenY = Int.MIN_VALUE
  private var lastW = 0
  private var lastH = 0
  private var selfCapturing = false
  private var observedActivityDecor: View? = null

  private val fallbackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val fallbackRect = RectF()

  var config = GlassMenuConfigRecord()
    set(value) {
      field = value
      invalidate()
    }

  var triggerConfig = GlassTriggerConfigRecord()
    set(value) {
      field = value
      invalidate()
    }

  var cornerRadiusDp: Float = 24f
    set(value) {
      if (field != value) {
        field = value
        invalidate()
      }
    }

  var topCornersOnly: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        invalidate()
      }
    }

  var colorScheme: String = "auto"
    set(value) {
      if (field != value) {
        field = value
        invalidate()
      }
    }

  var surfaceOpacity: Float = 1f
    set(value) {
      if (field != value) {
        field = value
        invalidate()
      }
    }

  var materialProgress: Float = 1f
    set(value) {
      if (field != value) {
        field = value
        invalidate()
      }
    }

  private val customFrameRect = RectF()
  private var hasCustomFrame = false

  var customCornerRadiusPx: Float? = null
    set(value) {
      if (field != value) {
        field = value
        invalidate()
      }
    }

  fun setCustomFrame(left: Float, top: Float, right: Float, bottom: Float) {
    if (!hasCustomFrame ||
      customFrameRect.left != left ||
      customFrameRect.top != top ||
      customFrameRect.right != right ||
      customFrameRect.bottom != bottom
    ) {
      customFrameRect.set(left, top, right, bottom)
      hasCustomFrame = true
      invalidate()
    }
  }

  fun clearCustomFrame() {
    if (hasCustomFrame) {
      hasCustomFrame = false
      invalidate()
    }
  }

  private val activityPreDrawListener = ViewTreeObserver.OnPreDrawListener {
    val actRoot = resolveActivityRoot()
    if (actRoot != null && isHierarchyDirty(actRoot)) {
      invalidate()
    }
    true
  }

  init {
    setWillNotDraw(false)
    clipChildren = false
    clipToPadding = false
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    disableParentClipping()
    configureHostWindow()
    viewTreeObserver.addOnPreDrawListener(this)
    val actDecor = context.findHostActivity()?.window?.decorView
    if (actDecor != null && actDecor !== rootView) {
      observedActivityDecor = actDecor
      actDecor.viewTreeObserver.addOnPreDrawListener(activityPreDrawListener)
    }
    invalidate()
  }

  override fun onDetachedFromWindow() {
    viewTreeObserver.removeOnPreDrawListener(this)
    observedActivityDecor?.let { decor ->
      if (decor.viewTreeObserver.isAlive) {
        decor.viewTreeObserver.removeOnPreDrawListener(activityPreDrawListener)
      }
    }
    observedActivityDecor = null
    if (supportsGlass) releaseGraphics()
    super.onDetachedFromWindow()
  }

  private fun disableParentClipping() {
    var current = parent
    var depth = 0
    while (current is ViewGroup && depth < 4) {
      if (current.id == android.R.id.content) break
      current.clipChildren = false
      current.clipToPadding = false
      current = current.parent
      depth++
    }
  }

  private fun configureHostWindow() {
    val actDecor = context.findHostActivity()?.window?.decorView ?: return
    val hostRoot = rootView ?: return
    if (hostRoot === actDecor) return
    val params = hostRoot.layoutParams as? WindowManager.LayoutParams ?: return
    var changed = false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      if ((params.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND) != 0) {
        params.flags = params.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        params.blurBehindRadius = 0
        changed = true
      }
    }
    if ((params.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND) != 0 && params.dimAmount > 0.08f) {
      params.dimAmount = 0.08f
      changed = true
    }
    if (changed) {
      val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
      try {
        wm?.updateViewLayout(hostRoot, params)
      } catch (_: Throwable) {
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  private fun releaseGraphics() {
    (glassRenderer as? GlassRenderer)?.release()
    glassRenderer = null
    (backdropNode as? RenderNode)?.discardDisplayList()
    backdropNode = null
  }

  override fun onPreDraw(): Boolean {
    if (!isShown || width <= 0 || height <= 0) return true
    getLocationOnScreen(viewLoc)
    if (viewLoc[0] != lastScreenX || viewLoc[1] != lastScreenY || width != lastW || height != lastH) {
      lastScreenX = viewLoc[0]
      lastScreenY = viewLoc[1]
      lastW = width
      lastH = height
      invalidate()
    } else {
      val actRoot = resolveActivityRoot()
      if (actRoot != null && isHierarchyDirty(actRoot)) {
        invalidate()
      }
    }
    return true
  }

  private fun isHierarchyDirty(group: ViewGroup): Boolean {
    if (group.isDirty) return true
    for (i in 0 until group.childCount) {
      val child = group.getChildAt(i)
      if (child !is GlassMenuOverlay && child.isDirty) return true
    }
    return false
  }

  internal fun resolveActivityRoot(): ViewGroup? {
    val activity = context.findHostActivity()
    val actContent = activity?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)
    if (actContent != null) return actContent
    return (rootView.findViewById<ViewGroup>(android.R.id.content)) ?: (rootView as? ViewGroup)
  }

  internal fun isDark(): Boolean = when (colorScheme) {
    "dark" -> true
    "light" -> false
    else -> resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  private fun renderer(pixelDensity: Float): GlassRenderer =
    glassRenderer as? GlassRenderer ?: GlassRenderer(pixelDensity, pixelDensity * FieldStep).also { glassRenderer = it }

  override fun onDraw(canvas: Canvas) {
    if (selfCapturing || width <= 0 || height <= 0) return
    if (!supportsGlass || !canvas.isHardwareAccelerated) {
      drawFallback(canvas)
      return
    }
    drawGlass(canvas)
  }

  private fun drawFallback(canvas: Canvas) {
    val left = if (hasCustomFrame) customFrameRect.left else 0f
    val top = if (hasCustomFrame) customFrameRect.top else 0f
    val right = if (hasCustomFrame) customFrameRect.right else width.toFloat()
    val bottom = if (hasCustomFrame) customFrameRect.bottom else height.toFloat()
    val radius = customCornerRadiusPx ?: (cornerRadiusDp * density)
    fallbackRect.set(left, top, right, bottom)
    fallbackPaint.color = if (isDark()) 0xE61C1C22.toInt() else 0xF0F4F5F8.toInt()
    canvas.drawRoundRect(fallbackRect, radius, radius, fallbackPaint)
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  private fun drawGlass(canvas: Canvas) {
    val actRoot = resolveActivityRoot() ?: return
    val rootW = actRoot.width
    val rootH = actRoot.height
    if (rootW <= 0 || rootH <= 0) return

    getLocationOnScreen(viewLoc)
    actRoot.getLocationOnScreen(rootLoc)
    val offsetX = (viewLoc[0] - rootLoc[0]).toFloat()
    val offsetY = (viewLoc[1] - rootLoc[1]).toFloat()

    if (capturingBackdropDepth > 0) {
      val cachedRenderer = glassRenderer as? GlassRenderer ?: return
      val save = canvas.save()
      canvas.translate(-offsetX, -offsetY)
      cachedRenderer.drawCachedGlass(canvas)
      canvas.restoreToCount(save)
      return
    }

    val node = backdropNode as? RenderNode ?: RenderNode("GlassPanel.backdrop").also { backdropNode = it }
    limits.set(0, 0, rootW, rootH)
    node.setPosition(limits)
    selfCapturing = true
    capturingBackdropDepth++
    try {
      val recording = node.beginRecording(rootW, rootH)
      val dark = isDark()
      recording.drawColor(if (dark) Color.BLACK else Color.WHITE)
      actRoot.background?.draw(recording)
      for (i in 0 until actRoot.childCount) {
        val child = actRoot.getChildAt(i)
        if (child !is GlassMenuOverlay && child.visibility == VISIBLE &&
          child.id != android.R.id.statusBarBackground &&
          child.id != android.R.id.navigationBarBackground
        ) {
          child.draw(recording)
        }
      }
      node.endRecording()
    } finally {
      capturingBackdropDepth--
      selfCapturing = false
    }

    val left = offsetX + (if (hasCustomFrame) customFrameRect.left else 0f)
    val top = offsetY + (if (hasCustomFrame) customFrameRect.top else 0f)
    val right = offsetX + (if (hasCustomFrame) customFrameRect.right else width.toFloat())
    val bottom = offsetY + (if (hasCustomFrame) customFrameRect.bottom else height.toFloat())
    if (right <= left || bottom <= top) return

    val boxW = right - left
    val boxH = bottom - top
    val maxRadius = min(boxW, boxH) / 2f
    val radiusPx = min(customCornerRadiusPx ?: (cornerRadiusDp * density), maxRadius)
    val bottomExtension = if (topCornersOnly) max(radiusPx * 2f, 40f * density) else 0f

    val state = surfaceState
    state.incoming.set(left, top, right, bottom + bottomExtension)
    state.captureBounds.set(left, top, right, bottom)
    state.captureBounds.inset(-CaptureMargin * density, -CaptureMargin * density)
    state.lockedFieldStep = 0f
    state.radiusX = radiusPx
    state.radiusY = radiusPx
    state.hasSource = false
    state.smoothing = 0f
    state.lensHeight = FullLensHeight * density
    state.lensAmount = FullLensAmount * density
    state.materialProgress = materialProgress.coerceIn(0f, 1f)
    state.surfaceOpacity = surfaceOpacity.coerceIn(0f, 1f)
    state.dark = isDark()
    state.config = config
    state.trigger = triggerConfig
    state.pressX = 0f
    state.pressY = 0f
    state.pressRadius = LightRadius * density
    state.poolGain = 0f
    state.pressLift = 0f
    state.rimLift = 0f

    val renderer = renderer(density)
    renderer.prepare(state)
    val save = canvas.save()
    if (topCornersOnly) {
      val shadowGuard = 64f * density
      canvas.clipRect(-shadowGuard, -shadowGuard, width.toFloat() + shadowGuard, height.toFloat())
    }
    canvas.translate(-offsetX, -offsetY)
    renderer.drawGlass(canvas, node, state, limits)
    if (renderer.warmPipelines(canvas)) post { invalidate() }
    canvas.restoreToCount(save)
  }

  companion object {
    internal var capturingBackdropDepth = 0
    private const val FieldStep = 4f
    private const val CaptureMargin = 20f
    private const val FullLensHeight = 16f
    private const val FullLensAmount = -27f
    private const val LightRadius = 38f
  }
}
