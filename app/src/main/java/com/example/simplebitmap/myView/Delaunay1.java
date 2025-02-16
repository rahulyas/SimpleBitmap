package com.example.simplebitmap.myView;

import java.util.ArrayList;
import java.util.List;

import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;


public class Delaunay1 {
    public static List<Triangle2D> doDelaunayFromGit(List<Float> floatList){
        List<Float> result = new ArrayList<>();

        List<Vector2D> pointSet = new ArrayList<>();
        for (int i = 0; i < floatList.size(); i=i+3) {
            Vector2D vertex = new Vector2D(floatList.get(i),floatList.get(i+1));
            pointSet.add(vertex);
        }

        DelaunayTriangulator delaunayTriangulator = new DelaunayTriangulator(pointSet);
        try {
            delaunayTriangulator.triangulate();
        } catch (NotEnoughPointsException e) {
            e.printStackTrace();
        }
        return delaunayTriangulator.getTriangles();
    }
    //plus elevation
    public static List<Float> addHight(List<Triangle2D> triangleSoup,List<Float> sourcelist){

        List<Float> result = new ArrayList<>();
        for (int i = 0; i < triangleSoup.size(); i++) {
            Vector2D vectorA = triangleSoup.get(i).a;
            Vector2D vectorB = triangleSoup.get(i).b;
            Vector2D vectorC = triangleSoup.get(i).c;
            Float heightA = 0f;
            Float heightB = 0f;
            Float heightC = 0f;
            for (int j = 0; j < sourcelist.size()-2; j=j+3) {
                if (vectorA.x == (double) sourcelist.get(j) && vectorA.y == (double) sourcelist.get(j+1)){
                    heightA = sourcelist.get(j+2);
                    continue;
                }
                if (vectorB.x == (double) sourcelist.get(j) && vectorB.y == (double) sourcelist.get(j+1)){
                    heightB = sourcelist.get(j+2);
                    continue;
                }
                if (vectorC.x == (double) sourcelist.get(j) && vectorC.y == (double) sourcelist.get(j+1)){
                    heightC = sourcelist.get(j+2);
                }
            }

            result.add((float) vectorA.x);
            result.add((float) vectorA.y);
            result.add(heightA);

            result.add((float) vectorB.x);
            result.add((float) vectorB.y);
            result.add(heightB);

            result.add((float) vectorC.x);
            result.add((float) vectorC.y);
            result.add(heightC);
        }
        return result;
    }
    //Convert the triangle model to an edge model, plus elevation
    public static List<Float> doEdge(List<Triangle2D> triangleList,List<Float> sourcelist){
        List<Float> result = new ArrayList<>();
        for (int i = 0; i < triangleList.size(); i++) {
            Vector2D vectorA = triangleList.get(i).a;
            Vector2D vectorB = triangleList.get(i).b;
            Vector2D vectorC = triangleList.get(i).c;
            Float heightA = 0f;
            Float heightB = 0f;
            Float heightC = 0f;
            for (int j = 0; j < sourcelist.size()-2; j=j+3) {
                if (vectorA.x == (double) sourcelist.get(j) && vectorA.y == (double) sourcelist.get(j+1)){
                    heightA = sourcelist.get(j+2);
                    continue;
                }
                if (vectorB.x == (double) sourcelist.get(j) && vectorB.y == (double) sourcelist.get(j+1)){
                    heightB = sourcelist.get(j+2);
                    continue;
                }
                if (vectorC.x == (double) sourcelist.get(j) && vectorC.y == (double) sourcelist.get(j+1)){
                    heightC = sourcelist.get(j+2);
                }

            }

            // side AB
            result.add((float) vectorA.x);
            result.add((float) vectorA.y);
            result.add(heightA);

            result.add((float) vectorB.x);
            result.add((float) vectorB.y);
            result.add(heightB);

            //side BC
            result.add((float) vectorB.x);
            result.add((float) vectorB.y);
            result.add(heightB);

            result.add((float) vectorC.x);
            result.add((float) vectorC.y);
            result.add(heightC);

            //side CA
            result.add((float) vectorC.x);
            result.add((float) vectorC.y);
            result.add(heightC);

            result.add((float) vectorA.x);
            result.add((float) vectorA.y);
            result.add(heightA);


        }
        return result;
    }
}
