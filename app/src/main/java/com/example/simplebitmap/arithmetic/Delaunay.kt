package com.example.a3dmodel.arithmetic

import io.github.jdiemke.triangulation.DelaunayTriangulator
import io.github.jdiemke.triangulation.NotEnoughPointsException
import io.github.jdiemke.triangulation.Triangle2D
import io.github.jdiemke.triangulation.Vector2D

object Delaunay {
    fun doDelaunayFromGit(floatList: List<Double>): List<Triangle2D> {
        val result: List<Double> = ArrayList()
        val pointSet: MutableList<Vector2D> = ArrayList()
        var i = 0
        while (i < floatList.size) {
            val vertex = Vector2D(floatList[i].toDouble(), floatList[i + 1].toDouble())
            pointSet.add(vertex)
            i = i + 3
        }
        val delaunayTriangulator = DelaunayTriangulator(pointSet)
        try {
            delaunayTriangulator.triangulate()
        } catch (e: NotEnoughPointsException) {
            e.printStackTrace()
        }
        return delaunayTriangulator.triangles
    }

    //plus elevation
    fun addHight(triangleSoup: List<Triangle2D>, sourcelist: List<Double>): List<Double> {
        val result: MutableList<Double> = ArrayList()
        for (i in triangleSoup.indices) {
            val vectorA = triangleSoup[i].a
            val vectorB = triangleSoup[i].b
            val vectorC = triangleSoup[i].c
            var heightA = 0.0
            var heightB = 0.0
            var heightC = 0.0
            var j = 0
            while (j < sourcelist.size - 2) {
                if (vectorA.x == sourcelist[j].toDouble() && vectorA.y == sourcelist[j + 1].toDouble()) {
                    heightA = sourcelist[j + 2]
                    j = j + 3
                    continue
                }
                if (vectorB.x == sourcelist[j].toDouble() && vectorB.y == sourcelist[j + 1].toDouble()) {
                    heightB = sourcelist[j + 2]
                    j = j + 3
                    continue
                }
                if (vectorC.x == sourcelist[j].toDouble() && vectorC.y == sourcelist[j + 1].toDouble()) {
                    heightC = sourcelist[j + 2]
                }
                j = j + 3
            }
            result.add(vectorA.x)
            result.add(vectorA.y)
            result.add(heightA)
            result.add(vectorB.x)
            result.add(vectorB.y)
            result.add(heightB)
            result.add(vectorC.x)
            result.add(vectorC.y)
            result.add(heightC)
        }
        return result
    }

    //Convert the triangle model to an edge model, plus elevation
    fun doEdge(triangleList: List<Triangle2D>, sourcelist: List<Double>): List<Double> {
        val result: MutableList<Double> = ArrayList()
        for (i in triangleList.indices) {
            val vectorA = triangleList[i].a
            val vectorB = triangleList[i].b
            val vectorC = triangleList[i].c
            var heightA = 0.0
            var heightB = 0.0
            var heightC = 0.0
            var j = 0
            while (j < sourcelist.size - 2) {
                if (vectorA.x == sourcelist[j].toDouble() && vectorA.y == sourcelist[j + 1].toDouble()) {
                    heightA = sourcelist[j + 2]
                    j = j + 3
                    continue
                }
                if (vectorB.x == sourcelist[j].toDouble() && vectorB.y == sourcelist[j + 1].toDouble()) {
                    heightB = sourcelist[j + 2]
                    j = j + 3
                    continue
                }
                if (vectorC.x == sourcelist[j].toDouble() && vectorC.y == sourcelist[j + 1].toDouble()) {
                    heightC = sourcelist[j + 2]
                }
                j = j + 3
            }

            // side AB
            result.add(vectorA.x)
            result.add(vectorA.y)
            result.add(heightA)
            result.add(vectorB.x)
            result.add(vectorB.y)
            result.add(heightB)

            //side BC
            result.add(vectorB.x)
            result.add(vectorB.y)
            result.add(heightB)
            result.add(vectorC.x)
            result.add(vectorC.y)
            result.add(heightC)

            //side CA
            result.add(vectorC.x)
            result.add(vectorC.y)
            result.add(heightC)
            result.add(vectorA.x)
            result.add(vectorA.y)
            result.add(heightA)
        }
        return result
    }
}