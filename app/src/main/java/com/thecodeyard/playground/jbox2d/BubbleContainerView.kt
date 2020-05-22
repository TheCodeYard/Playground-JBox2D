package com.thecodeyard.playground.jbox2d

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.PolygonShape
import java.util.*

/**
 * The container view of the bubbles. This will add the [BubbleView]s, start the simulation, and update the values of the views at each simulation step.
 */
class BubbleContainerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), BubbleWorld.Listener {
    companion object {
        /**
         * If set to true, it will draw on the canvas the bodies of the 2D world.
         */
        private const val DEBUG_WORLD = false

        /**
         * The size (width and height) of the bubble view, in pixels.
         */
        private const val BUBBLE_SIZE = 320

        /**
         * The amount of bubbles to create.
         */
        private const val BUBBLE_COUNT = 6
    }

    private val world = BubbleWorld(this)
    private val paint = Paint()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Make sure that we have the width and height of the container view before creating the 2D world.
        post {
            // Create the world if it's not already created.
            if (world.state == BubbleWorld.State.IDLE) {
                world.create(measuredWidth, measuredHeight)

                val random = Random()
                for (i in 0 until BUBBLE_COUNT) {
                    val bubble = Bubble(viewId = View.generateViewId(),
                            viewSize = BUBBLE_SIZE,
                            viewColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)),
                            viewText = "Bubble $i",
                            viewX = 0f,
                            viewY = 0f)

                    addBubbleView(bubble)
                }
            }

            // Start simulation.
            world.startSimulation()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        world.stopSimulation()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (DEBUG_WORLD) {
            debugWorld(canvas)
        }

        super.dispatchDraw(canvas)
    }

    override fun onSimulationUpdate(bubble: Bubble) {
        Log.d(BubbleContainerView::class.java.simpleName, "OnUpdate. id: ${bubble.viewId} x: ${bubble.viewX} y: ${bubble.viewY}")
        findViewById<BubbleView>(bubble.viewId)?.let {
            it.x = bubble.viewX
            it.y = bubble.viewY
        }

        if (DEBUG_WORLD) {
            // This will trigger dispatchDraw().
            invalidate()
        }
    }

    /**
     * Adds a new [BubbleView] and tells the 2D world to add a bubble object that corresponds to this view.
     */
    private fun addBubbleView(bubble: Bubble) {
        val bubbleView = BubbleView(context)
        bubbleView.layoutParams = LayoutParams(bubble.viewSize, bubble.viewSize)
        bubbleView.id = bubble.viewId
        bubbleView.background.setColorFilter(bubble.viewColor, PorterDuff.Mode.SRC_ATOP)
        bubbleView.text = bubble.viewText

        world.createBubble(bubble)

        addView(bubbleView)
    }

    /**
     * This will draw all the of the world's bodies.
     * This is useful in order to make sure that the conversions of the 2D world values (sizes, positions of the bodies etc) is correct.
     */
    private fun debugWorld(canvas: Canvas) {
        var body = world.getBodyList()
        paint.color = Color.parseColor("#ff0000")
        paint.strokeWidth = 8f

        while (body != null) {
            var fixture = body.fixtureList
            while (fixture != null) {
                when (val shape = fixture.shape) {
                    is CircleShape -> {
                        // Draw the circle.
                        canvas.drawCircle(Metrics.metersToPixels(body.position.x), Metrics.metersToPixels(body.position.y), Metrics.metersToPixels(shape.m_radius), paint)
                    }
                    is PolygonShape -> {
                        // Currently the only polygon shapes that we have in the 2D world are the 4 lines that form its boundaries.
                        val fromX = Metrics.metersToPixels(shape.vertices[0].x)
                        val fromY = Metrics.metersToPixels(shape.vertices[0].y)
                        val toX = Metrics.metersToPixels(shape.vertices[1].x)
                        val toY = Metrics.metersToPixels(shape.vertices[1].y)

                        canvas.drawLine(fromX, fromY, toX, toY, paint)
                    }
                    else -> {
                        Log.d(BubbleContainerView::class.java.simpleName, "Unknown shape")
                    }
                }

                fixture = fixture.next
            }

            body = body.next
        }
    }
}