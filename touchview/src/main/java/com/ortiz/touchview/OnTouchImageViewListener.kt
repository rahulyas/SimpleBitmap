package com.ortiz.touchview

interface OnTouchImageViewListener {
    fun onMove(x : Float, y:Float, scaleFactor : Float)
    fun onScaleBegin(x : Float, y:Float, scaleFactor : Float)
    fun onScaleEnd(x : Float, y:Float,scaleFactor : Float)
    fun onDrag(deltax : Float, deltay : Float)
    fun onSpan(currX : Float, currY : Float, prevX : Float, prevY : Float)
    fun onSingleTap(x : Float, y:Float)
}