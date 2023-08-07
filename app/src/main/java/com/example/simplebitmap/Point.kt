package com.example.simplebitmap


class Point internal constructor(var x: Double, var y: Double, var z: Double, var id: Int) :
    Comparable<Point?> {
    override fun compareTo(other: Point?): Int {
        return if (id > other!!.id) {
            1
        } else {
            -1
        }
    }

    override fun equals(obj: Any?): Boolean {
        val p = obj as Point?
        return p!!.x == x && p.y == y && p.z == z
    }

    override fun hashCode(): Int {

        return id
    }

    override fun toString(): String {
        return "P$id"
    }

}
