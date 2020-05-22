package com.thecodeyard.playground.jbox2d

/**
 * This is basic class that holds the values for a specific [BubbleView].
 * The 2D world updates these values after each simulation step and the view updates its values accordingly.
 */
data class Bubble(val viewId: Int, val viewSize: Int, val viewColor: Int, val viewText: String, var viewX: Float, var viewY: Float)