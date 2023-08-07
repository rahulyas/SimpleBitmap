package com.apogee.calfilelibrary.Util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Util1 {

    // optimized5 dynamic
    public JSONObject findCoordinateUTM5(JSONObject jObj) {
        String type = "";
        JSONObject obj = new JSONObject();
        JSONObject finObj = new JSONObject();
        JSONArray json1 = new JSONArray();

        // START
        int coordLength = 0;
        double fixAvgX = 0.0, fixAvgY = 0.0, fixAvgZ = 0.0;
        double varAvgX = 0.0, varAvgY = 0.0, varAvgZ = 0.0;
        DecimalFormat df = new DecimalFormat("#.############");
        Map<Integer, JSONArray> fixMap = new HashMap<Integer, JSONArray>();
        Map<Integer, JSONArray> varMap = new HashMap<Integer, JSONArray>();
        Map<Integer, JSONArray> varMap2 = new HashMap<Integer, JSONArray>();
        Map<Integer, JSONArray> varMap3 = new HashMap<Integer, JSONArray>();
        Map<Integer, JSONArray> outputMap = new HashMap<Integer, JSONArray>();
        double avgAngle = 0.0, avgScale = 0.0;
        JSONArray avgTxTy = new JSONArray();
        ArrayList<Double> Tx = new ArrayList<>();
        ArrayList<Double> Ty = new ArrayList<>();

        try {
            type = jObj.getString("type");
            //System.err.println("type === " + type);
            String fixPointX, fixPointY, fixPointZ, varPointX, varPointY, varPointZ;
            int indexCount=0;
            for (int i = 1; i <= 250; i++) {
                JSONArray fixArr = new JSONArray();
                JSONArray varArr = new JSONArray();
                fixPointX = "FixX" + i;
                fixPointY = "FixY" + i;
                fixPointZ = "FixZ" + i;
                varPointX = "VarX" + i;
                varPointY = "VarY" + i;
                varPointZ = "VarZ" + i;
                if (jObj.has("FixX" + i)) {
                    // to handle if X-Y values are 0
                    if ((double) jObj.getDouble(fixPointX) != 0 && (double) jObj.getDouble(fixPointY) != 0 &&
                            (double) jObj.getDouble(varPointX) != 0 && (double) jObj.getDouble(varPointY) != 0 &&
                            (double) jObj.getDouble(fixPointX) != 0.0 && (double) jObj.getDouble(fixPointY) != 0.0 &&
                            (double) jObj.getDouble(varPointX) != 0.0 && (double) jObj.getDouble(varPointY) != 0.0) {

                        indexCount++;

                        fixArr.put(jObj.getDouble(fixPointX));
                        fixArr.put(jObj.getDouble(fixPointY));
                        fixArr.put(jObj.getDouble(fixPointZ));

                        if (type.equalsIgnoreCase("degree")) {
                            // convert all lat long to easting northing
                            double A2x = jObj.getDouble(varPointX);
                            double A2y = jObj.getDouble(varPointY);
                            List<Double> listAa = degreeToUTM(A2y, A2x);
                            A2x = listAa.get(0);
                            A2y = listAa.get(1);

                            List<Double> listA = degreeToUTM2(Double.valueOf(jObj.getDouble(varPointY)), Double.valueOf(jObj.getDouble(varPointX)));
                            // convert all lat long to easting northing

                            varArr.put(Double.valueOf(listA.get(0)));
                            varArr.put(Double.valueOf(listA.get(1)));
                            varArr.put(jObj.getDouble(varPointZ));

                        } else {

                            varArr.put(jObj.getDouble(varPointX));
                            varArr.put(jObj.getDouble(varPointY));
                            varArr.put(jObj.getDouble(varPointZ));
                        }

                        fixMap.put(indexCount, fixArr);
                        varMap.put(indexCount, varArr);

                    }

                }
            }

            coordLength = fixMap.size();

            Iterator entries = fixMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                JSONArray arr2 = (JSONArray) arr.get(0);
                fixAvgX += (double) arr2.get(0);
                fixAvgY += (double) arr2.get(1);
                //System.out.println((double) arr2.get(0));
                JSONArray fixArr = new JSONArray();
                //fixArrZ.add(arr2.get(2));
            }

            Iterator entries2 = varMap.entrySet().iterator();
            //System.err.println("------------------------------------");
            while (entries2.hasNext()) {
                Map.Entry entry = (Map.Entry) entries2.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                JSONArray arr2 = (JSONArray) arr.get(0);
                //System.err.println("array 2 - "+arr2.toString());
                varAvgX += (double) arr2.get(0);
                varAvgY += (double) arr2.get(1);
                //System.out.println((double) arr2.get(0));
                JSONArray varArr = new JSONArray();
                //varArrZ.add(arr2.get(2));
            }

            // Modified on 05-07-2023
            double avgTd = 0.0;
            double tD = 0.0;
            double heightDiffAvg = 0.0;
            double varHeightAvg = 0.0;
            ArrayList<Double> tDArray = new ArrayList<>();
            ArrayList<Double> heightArray = new ArrayList<>();
            // Modified on 05-07-2023

            Iterator entriesDiff = varMap.entrySet().iterator();
            //System.err.println("------------------------------------");
            while (entriesDiff.hasNext()) {
                Map.Entry entry = (Map.Entry) entriesDiff.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                double vdX, vdY;
                double fdX, fdY;

                JSONArray arr2 = (JSONArray) arr.get(0);
                vdX = (double) arr2.get(0);
                vdY = (double) arr2.get(1);

                JSONArray arr3 = new JSONArray();
                arr3.put(fixMap.get(key));
                JSONArray arr4 = (JSONArray) arr3.get(0);
                fdX = (double) arr4.get(0);
                fdY = (double) arr4.get(1);

                Tx.add(fdX - vdX);
                Ty.add(fdY - vdY);

                // Modified on 05-07-2023
                tD += getDistance(vdX, vdY, fdX, fdY);
                tDArray.add(getDistance(vdX, vdY, fdX, fdY));

                varHeightAvg += (double) arr2.get(2);
                heightArray.add((double) arr2.get(2));
//                System.err.println("distance: " + tD);
                // Modified on 05-07-2023
            }

            avgTd = tD / fixMap.size();
            System.err.println("avg distance: " + avgTd);
            varHeightAvg = varHeightAvg / varMap.size();
            System.err.println("avg height of Var: " + varHeightAvg);

            for (int i = 0; i < Tx.size(); i++) {
                avgTxTy.put(Math.sqrt(Math.pow(Tx.get(i), 2) + Math.pow(Ty.get(i), 2)));
            }

            double avgOfAvgTxTy = 0.0;
            ArrayList<Double> diffInAllTxTy = new ArrayList<>();
            int indexOfSecondPoint = 0;
            for (int i = 0; i < avgTxTy.length(); i++) {
                avgOfAvgTxTy += (double) avgTxTy.get(i);
            }
            avgOfAvgTxTy = avgOfAvgTxTy / avgTxTy.length();

            for (int i = 0; i < avgTxTy.length(); i++) {
                diffInAllTxTy.add(Math.abs(avgOfAvgTxTy - (double) avgTxTy.get(i)));
            }

            double minimum = diffInAllTxTy.get(0);
            int index = 0;

            double[] values = listToArray(tDArray);
            double target = avgTd;
            double closestValue = findClosestValue(values, target);

            for (int i = 0; i < tDArray.size(); i++) {
                if (closestValue == tDArray.get(i)) {
                    index = i;
                    break;
                }
            }

            System.out.println("Minimum element in ArrayList = " + minimum);
            System.err.println("---------------------- " + index);

            fixAvgX = fixAvgX / coordLength;
            fixAvgY = fixAvgY / coordLength;
            varAvgX = varAvgX / coordLength;
            varAvgY = varAvgY / coordLength;

            double originXEasting = 0.0, originYNorthing = 0.0, avgTX = 0.0, avgTY = 0.0;
            avgTX = (fixAvgX - varAvgX);
            avgTY = (fixAvgY - varAvgY);

            originXEasting = fixAvgX - avgTX;
            originYNorthing = fixAvgY - avgTY;

            Iterator entries3 = varMap.entrySet().iterator();
            while (entries3.hasNext()) {
                JSONArray varArrNew = new JSONArray();
                Map.Entry entry = (Map.Entry) entries3.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                JSONArray arr2 = (JSONArray) arr.get(0);
                varArrNew.put((double) arr2.get(0) + avgTX);
                varArrNew.put((double) arr2.get(1) + avgTY);
                varArrNew.put(arr2.get(2));
                varMap2.put(key, varArrNew);
            }

            // testing
            ArrayList<Double> fixDistArr = new ArrayList<>();
            ArrayList<Double> varDistArr = new ArrayList<>();

            ArrayList<Double> varAngArr = new ArrayList<>();
            ArrayList<Double> fixAngArr = new ArrayList<>();

            Iterator entries44 = varMap.entrySet().iterator();
            while (entries44.hasNext()) {
                JSONArray varArrNew = new JSONArray();
                Map.Entry entry = (Map.Entry) entries44.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                JSONArray arr2 = (JSONArray) arr.get(0);
                double angleVar = getAngleIn360(varAvgX, varAvgY, (double) arr2.get(0), (double) arr2.get(1));

                JSONArray arr3 = new JSONArray();
                arr3.put(fixMap.get(key));
                JSONArray arr4 = (JSONArray) arr3.get(0);
                double angleFix = getAngleIn360(fixAvgX, fixAvgY, (double) arr4.get(0), (double) arr4.get(1));

                varAngArr.add(angleVar);
                fixAngArr.add(angleFix);

                double distFix = Math.sqrt(Math.pow((double) arr4.get(0) - fixAvgX, 2) + Math.pow((double) arr4.get(1) - fixAvgY, 2));
                double distVar = Math.sqrt(Math.pow((double) arr2.get(0) - varAvgX, 2) + Math.pow((double) arr2.get(1) - varAvgY, 2));

                if (Double.isNaN(distFix)) {
                    distFix = 0.0;
                    distVar = 0.0;
                }

                fixDistArr.add(distFix);
                varDistArr.add(distVar);
                JSONArray varArrNew2 = new JSONArray();
                varArrNew2.put((double) arr2.get(0) - varAvgX);
                varArrNew2.put((double) arr2.get(1) - varAvgY);
                varArrNew2.put(arr2.get(2));
                varMap3.put(key, varArrNew2);

            }
            double fixDistArrDiff = 0.0;
            double varDistArrDiff = 0.0;

            double varAngArrDiff = 0.0;
            double fixAngArrDiff = 0.0;

            for (int i = 0; i < fixDistArr.size(); i++) {
                fixDistArrDiff += fixDistArr.get(i);
                varDistArrDiff += varDistArr.get(i);

                varAngArrDiff += varAngArr.get(i);
                fixAngArrDiff += fixAngArr.get(i);
            }

            fixDistArrDiff = fixDistArrDiff / fixDistArr.size();
            varDistArrDiff = varDistArrDiff / varDistArr.size();

            fixAngArrDiff = fixAngArrDiff / fixAngArr.size();
            varAngArrDiff = varAngArrDiff / varAngArr.size();

            avgAngle = (fixAngArrDiff - varAngArrDiff);
            avgScale = fixDistArrDiff / varDistArrDiff;

            Iterator entries5 = varMap.entrySet().iterator();
            while (entries5.hasNext()) {
                JSONArray varArrNew5 = new JSONArray();
                Map.Entry entry = (Map.Entry) entries5.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());
                JSONArray arr2 = (JSONArray) arr.get(0);
                double X = (avgScale * Math.cos(Math.toRadians(avgAngle)) * ((double) arr2.get(0) - varAvgX))
                        + (avgScale * Math.sin(Math.toRadians(avgAngle)) * ((double) arr2.get(1) - varAvgY)) + originXEasting + avgTX;
                double Y = -(avgScale * Math.sin(Math.toRadians(avgAngle)) * ((double) arr2.get(0) - varAvgX))
                        + (avgScale * Math.cos(Math.toRadians(avgAngle)) * ((double) arr2.get(1) - varAvgY)) + originYNorthing + avgTY;

                varArrNew5.put(X);
                varArrNew5.put(Y);
                varArrNew5.put((double) arr2.get(2));
                outputMap.put(key, varArrNew5);
            }

            JSONArray accuArray = new JSONArray();
            JSONArray accuArrayX = new JSONArray();
            JSONArray accuArrayY = new JSONArray();
            Iterator entries7 = outputMap.entrySet().iterator();
            while (entries7.hasNext()) {
                JSONArray varArrNew = new JSONArray();
                Map.Entry entry = (Map.Entry) entries7.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                JSONArray arr2 = (JSONArray) arr.get(0);

                JSONArray arr3 = new JSONArray();
                arr3.put(fixMap.get(key));
                JSONArray arr4 = (JSONArray) arr3.get(0);

                double diffx = 0.0, diffy = 0.0;
                diffx = (double) arr2.get(0) - (double) arr4.get(0);
                diffy = (double) arr2.get(1) - (double) arr4.get(1);

                accuArrayX.put(diffx);
                accuArrayY.put(diffy);

                double accu = Math.sqrt(Math.pow(diffx, 2) + Math.pow(diffy, 2));
                accuArray.put(accu);
            }
            double avgHeightFixMinusGPS = 0.00;
            Iterator entries11 = fixMap.entrySet().iterator();
            while (entries11.hasNext()) {
                Map.Entry entry = (Map.Entry) entries11.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                JSONArray arr2 = (JSONArray) arr.get(0);
                double heightFix = (double) arr2.get(2);

                JSONArray arr3 = new JSONArray();
                arr3.put(varMap.get(key));
                JSONArray arr4 = (JSONArray) arr3.get(0);
                double heightGPS = (double) arr4.get(2);
                if (key == 1) {
                    avgHeightFixMinusGPS = heightFix - heightGPS;
                }
            }

            Map<Integer, JSONArray> outPutMapAvg = new HashMap<Integer, JSONArray>();
            Map<Integer, JSONArray> outPutMapAvgPointFirstAndLast = new HashMap<Integer, JSONArray>();
            double outPutX = 0.00;
            double outPutY = 0.00;
            double outPutZ = 0.00;
            int outputMapCount = 0;
            boolean flag1 = false;
            boolean flag2 = true;
            Iterator entries9 = outputMap.entrySet().iterator();
            while (entries9.hasNext()) {
                JSONArray outPutArrAvgXYZ = new JSONArray();
                Map.Entry entry = (Map.Entry) entries9.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                JSONArray arr2 = (JSONArray) arr.get(0);

                JSONArray arr3 = new JSONArray();
                arr3.put(fixMap.get(key));
                JSONArray arr4 = (JSONArray) arr3.get(0);

                if ((double) arr2.get(2) != 0 || (double) arr2.get(2) != 0.0) {
                    flag1 = true;
                }

                if (flag1 == true && flag2 == true) {
                    outPutX = (double) arr2.get(0);
                    outPutY = (double) arr2.get(1);
                    outPutZ = (double) arr4.get(2) - (double) arr2.get(2);
                    flag2 = false;
                }

                if ((double) arr2.get(2) == 0 || (double) arr2.get(2) == 0.0) {

                } else {
                    outputMapCount++;
                    double diffx = 0.0, diffy = 0.0, diffz = 0.0;
                    diffx = (double) arr2.get(0) - outPutX;
                    diffy = (double) arr2.get(1) - outPutY;
                    diffz = ((double) arr4.get(2) - (double) arr2.get(2)) - outPutZ;
                    outPutArrAvgXYZ.put(diffx);
                    outPutArrAvgXYZ.put(diffy);
                    outPutArrAvgXYZ.put(diffz);
                    outPutMapAvg.put(outputMapCount, outPutArrAvgXYZ);
                }
                //}

            }

            double x1 = 0.0, y1 = 0.0, z1 = 0.0;
            double x2 = 0.0, y2 = 0.0, z2 = 0.0;
            double x3 = 0.0, y3 = 0.0, z3 = 0.0;
            double fixX = 0.0, fixY = 0.0, fixZ = 0.0;
            Iterator entries10 = outPutMapAvg.entrySet().iterator();
            double xzSlopeAvg = 0.00, yzSlopeAvg = 0.00;
            int k = 0;
            while (entries10.hasNext()) {
                JSONArray array = new JSONArray();
                Map.Entry entry = (Map.Entry) entries10.next();
                Integer key = (Integer) entry.getKey();

                JSONArray planeArr2 = new JSONArray();

                //if (key != 1 && key != outPutMapAvg.size()) {
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());
                JSONArray arr2 = (JSONArray) arr.get(0);

                if (key == 1) {
                    x1 = (double) arr2.get(0);
                    y1 = (double) arr2.get(1);
                    z1 = (double) arr2.get(2);

                    JSONArray arr3 = new JSONArray();
                    arr3.put(outputMap.get(key));
                    JSONArray arr4 = (JSONArray) arr3.get(0);
                    fixX = (double) arr4.get(0);
                    fixY = (double) arr4.get(1);
                    fixZ = (double) arr4.get(2);

                }

                if (key == index + 1) {
                    x2 = (double) arr2.get(0);
                    y2 = (double) arr2.get(1);
                    z2 = (double) arr2.get(2);
                    break;
                }
            }
            ArrayList<Double> distanceArrayforVar = new ArrayList<>();
            double maxDistanceIndex = 0.0;
            Iterator getDistance = outPutMapAvg.entrySet().iterator();
            while (getDistance.hasNext()) {
                Map.Entry entry = (Map.Entry) getDistance.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());
                double vdX, vdY;
                JSONArray arr2 = (JSONArray) arr.get(0);
                vdX = (double) arr2.get(0);
                vdY = (double) arr2.get(1);
                distanceArrayforVar.add(getDistance(x1, y1, vdX, vdY));
            }

            double tanThetaAvg = 0;
            Iterator entries1107 = outPutMapAvg.entrySet().iterator();
            while (entries1107.hasNext()) {
                JSONArray array = new JSONArray();
                Map.Entry entry = (Map.Entry) entries1107.next();
                Integer key = (Integer) entry.getKey();

                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());
                JSONArray arr2 = (JSONArray) arr.get(0);

                x2 = (double) arr2.get(0);
                y2 = (double) arr2.get(1);
                z2 = (double) arr2.get(2);

                if (key != 1) {
                    System.err.println("tan theta: " + z2 / distanceArrayforVar.get(key - 1));
//                    tanThetaAvg += (y2 - y1) / (x2 - x1);
                    tanThetaAvg += z2 / distanceArrayforVar.get(key - 1);
                }
            }
            tanThetaAvg = tanThetaAvg / (outPutMapAvg.size() - 1);
            System.err.println("tan theta avg: " + tanThetaAvg);
            double max = Double.MIN_VALUE; // Assuming the smallest possible value
            int maxIndex = -1; // Initializing the index to -1

            ArrayList<Double> XList = new ArrayList<>();
            ArrayList<Double> YList = new ArrayList<>();
            Iterator XYList = outPutMapAvg.entrySet().iterator();
            while (XYList.hasNext()) {
                JSONArray array = new JSONArray();
                Map.Entry entry = (Map.Entry) XYList.next();
                Integer key = (Integer) entry.getKey();

                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());
                JSONArray arr2 = (JSONArray) arr.get(0);

                XList.add((double) arr2.get(0));
                YList.add((double) arr2.get(1));
            }

            // Get X&Y max from XList
            double xMax = Double.MIN_VALUE;
            for (int i = 1; i < XList.size(); i++) {
                double number = XList.get(i);
                if (number > xMax) {
                    xMax = number; // Update the maximum value
                }
            }
            System.err.println("x max - " + xMax);

            double yMax = Double.MIN_VALUE;
            for (int i = 1; i < YList.size(); i++) {
                double number = YList.get(i);
                if (number > yMax) {
                    yMax = number; // Update the maximum value
                }
            }
            System.err.println("Y max - " + yMax);

            double xMin = Double.MAX_VALUE;
            for (int i = 1; i < XList.size(); i++) {
                double number = XList.get(i);
                if (number < xMin) {
                    xMin = number; // Update the maximum value
                }
            }
            System.err.println("X min - " + xMin);

            double yMin = Double.MAX_VALUE;
            for (int i = 1; i < YList.size(); i++) {
                double number = YList.get(i);
                if (number < yMin) {
                    yMin = number; // Update the maximum value
                }
            }
            System.err.println("Y min - " + yMin);

            for (int i = 0; i < distanceArrayforVar.size(); i++) {
                double number = distanceArrayforVar.get(i);
                if (number > max) {
                    max = number; // Update the maximum value
                    maxIndex = i; // Update the index
                }
            }
            System.err.println("max index of max dist: " + maxIndex);

            //get x,y on maxIndex
            double virtualX = 0.0, virtualY = 0.0, virtualH = 0.0, virtualD = 0.0, minX = 0.0, minY = 0.0;
            virtualX = (xMax - xMin) / 2;
            virtualY = (yMax - yMin) / 2;

            // get distance from D to virtual point
            virtualD = getDistance(x1, y1, virtualX, virtualY);
            // get Height from tantheta
            virtualH = tanThetaAvg * (virtualD);
            System.err.println("virtual Height: " + virtualH);
            System.err.println("virtual Distance: " + virtualD);

            // Log.d("TAG", "findCoordinateUTM5: "+outPutMapAvg.size());
            // Modified on 11-07-2023
            // Iterator entries101 = outPutMapAvg.entrySet().iterator();

            Iterator<Map.Entry<Integer, JSONArray>> entries101 = outPutMapAvg.entrySet().iterator();

            while (entries101.hasNext()) {
                Map.Entry<Integer, JSONArray> entry = entries101.next();
                Integer key = entry.getKey();
                JSONArray value = entry.getValue();
                x3 = (double) value.get(0);
                y3 = (double) value.get(1);
                z3 = (double) value.get(2);
                JSONArray planeEqArr = planeEquation(x1, y1, z1, virtualX, virtualY, virtualH, x3, y3, z3);
                System.out.println("Key: " +planeEqArr);
/*                if (planeEqArr.get(0).toString()!= "NaN"  && planeEqArr.get(0).toString() != "-Infinity" && planeEqArr.get(0).toString() != "Infinity") {
                    xzSlopeAvg += (double) planeEqArr.get(0);
                    yzSlopeAvg += (double) planeEqArr.get(1);
                    System.err.println("xzslope avg - " + xzSlopeAvg);
                    System.err.println("yzslope avg - " + yzSlopeAvg);
                }*/
                try {
                    if (planeEqArr.get(0).toString()!= "NaN"  && planeEqArr.get(0).toString() != "-Infinity" && planeEqArr.get(0).toString() != "Infinity") {
                        xzSlopeAvg += (double) planeEqArr.get(0);
                        yzSlopeAvg += (double) planeEqArr.get(1);
                        System.err.println("xzslope avg - " + xzSlopeAvg);
                        System.err.println("yzslope avg - " + yzSlopeAvg);
                    }
                } catch (Exception e) {
                    // Handle the exception here
                    e.printStackTrace();
                    Log.d("TAG", "findCoordinateUTM5: "+e.getMessage());
                }

/*                if (!Double.isInfinite(planeEqArr.getDouble(0)) && !Double.isInfinite(planeEqArr.getDouble(0))) {
                    xzSlopeAvg += planeEqArr.getDouble(0);
                    yzSlopeAvg += planeEqArr.getDouble(1);
                    System.err.println("xzslope avg - " + xzSlopeAvg);
                    System.err.println("yzslope avg - " + yzSlopeAvg);
                }*/
            }




            xzSlopeAvg = xzSlopeAvg / (outPutMapAvg.size() - 2);
            yzSlopeAvg = yzSlopeAvg / (outPutMapAvg.size() - 2);

            Map<Integer, JSONArray> fixMapFor3D = new HashMap<Integer, JSONArray>();
            Map<Integer, JSONArray> varMapFor3D = new HashMap<Integer, JSONArray>();
            int count3D = 0;
            JSONArray heightArr = new JSONArray();
            Iterator entries8 = varMap.entrySet().iterator();
            while (entries8.hasNext()) {
                Map.Entry entry = (Map.Entry) entries8.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());
                JSONArray arr2 = (JSONArray) arr.get(0);
                JSONArray arr3 = new JSONArray();
                arr3.put(fixMap.get(key));
                JSONArray arr4 = (JSONArray) arr3.get(0);

                if ((double) arr2.get(2) == 0.0 || (double) arr2.get(2) == 0) {

                } else {
                    count3D++;
                    double height = ((double) arr2.get(2) + avgHeightFixMinusGPS) + xzSlopeAvg * ((double) arr2.get(0) - fixX)
                            + yzSlopeAvg * ((double) arr2.get(1) - fixY);
//                double height = -((xzSlopeAvg * (double) arr2.get(0)) + ((yzSlopeAvg) * (double) arr2.get(1))
//                        + 0) / (1);
                    heightArr.put(height);

                    fixMapFor3D.put(count3D, arr4);
                    varMapFor3D.put(count3D, arr2);

                }
            }

            JSONArray heiArrayPlusavgHeightFixMinusGPS = new JSONArray();
            JSONArray heiArrayPlusavgHeightFixMinusGPSDiffArray = new JSONArray();
            Iterator entries12 = fixMapFor3D.entrySet().iterator();
            while (entries12.hasNext()) {
                Map.Entry entry = (Map.Entry) entries12.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                JSONArray arr2 = (JSONArray) arr.get(0);
                double heightFix = (double) arr2.get(2);

                JSONArray arr3 = new JSONArray();
                arr3.put(varMapFor3D.get(key));
                JSONArray arr4 = (JSONArray) arr3.get(0);
                double heightVar = (double) arr4.get(2);

                double hh = heightFix - (double) heightArr.get((key - 1));
//                hh = hh + heightVar;
//                heiArrayPlusavgHeightFixMinusGPS.add(hh);
                heiArrayPlusavgHeightFixMinusGPSDiffArray.put(hh);
            }

            double avgheightArrayDiff = 0.0;
            for (int i = 0; i < heightArr.length(); i++) {
                avgheightArrayDiff += (double) heiArrayPlusavgHeightFixMinusGPSDiffArray.get(i);
            }

            avgheightArrayDiff = avgheightArrayDiff / heightArr.length();
            System.err.println("height arr diff ---- " + avgheightArrayDiff);

            // Again Plane Equation
            // z'=z+Tz+Px(x-x0)+Py(y-y0)
            JSONArray heightArr2 = new JSONArray();
            Iterator entries81 = varMapFor3D.entrySet().iterator();
            while (entries81.hasNext()) {
                Map.Entry entry = (Map.Entry) entries81.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                JSONArray arr2 = (JSONArray) arr.get(0);

                double height = ((double) arr2.get(2) + (avgHeightFixMinusGPS + avgheightArrayDiff)) + xzSlopeAvg * ((double) arr2.get(0) - fixX)
                        + yzSlopeAvg * ((double) arr2.get(1) - fixY);

                heightArr2.put(height);
            }
            // Again Plane Equation

            // Height Diff
            JSONArray heiArrayPlusavgHeightFixMinusGPSDiffArray2 = new JSONArray();
            Iterator entries122 = fixMapFor3D.entrySet().iterator();
            while (entries122.hasNext()) {
                Map.Entry entry = (Map.Entry) entries122.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());

                JSONArray arr2 = (JSONArray) arr.get(0);
                double heightFix = (double) arr2.get(2);

                JSONArray arr3 = new JSONArray();
                arr3.put(varMapFor3D.get(key));
                JSONArray arr4 = (JSONArray) arr3.get(0);
                double heightVar = (double) arr4.get(2);

                double hh = heightFix - (double) heightArr2.get((key - 1));
//                hh = hh + heightVar;
//                heiArrayPlusavgHeightFixMinusGPS.add(hh);
                heiArrayPlusavgHeightFixMinusGPSDiffArray2.put(hh);
            }
            // Height Diff

            // avg of height array
            double avgheightArrayDiff2 = 0.0;
            for (int i = 0; i < heightArr2.length(); i++) {
                avgheightArrayDiff2 += (double) heiArrayPlusavgHeightFixMinusGPSDiffArray2.get(i);
            }

            avgheightArrayDiff2 = avgheightArrayDiff2 / heightArr2.length();
            System.err.println("height arr diff 2 ---- " + avgheightArrayDiff2);
            // avg of height array

            if (Double.isNaN(xzSlopeAvg)) {
                xzSlopeAvg = 0.0;
            }
            if (Double.isNaN(yzSlopeAvg)) {
                yzSlopeAvg = 0.0;
            }

            double elevAvg = 0.0;
            for (int m = 0; m < heiArrayPlusavgHeightFixMinusGPSDiffArray2.length(); m++) {
                elevAvg += (double) heiArrayPlusavgHeightFixMinusGPSDiffArray2.get(m);
            }

            //obj.put("avgAngle", Math.exp(avgAngle));
            obj.put("avgAngle", String.format("%.9f", avgAngle));
            obj.put("avgScale", String.format("%.9f", avgScale)); // new DecimalFormat("$#.00").format(shippingCost);
            //obj.put("sigmmaAvgH", avgHeightFixMinusGPS); // System.out.printf("\n$%10.2f",shippingCost);
            //obj.put("sigmaZ", "");
            obj.put("Origin_Easting", String.format("%.5f", originXEasting));
            obj.put("Origin_Northing", String.format("%.5f", originYNorthing));

            obj.put("Tx", String.format("%.5f", avgTX));
            obj.put("Ty", String.format("%.5f", avgTY));
            //obj.put("TZ", "");

            Iterator entries71 = outputMap.entrySet().iterator();
            JSONArray computeJSONArray=new JSONArray();
            while (entries71.hasNext()) {
                Map.Entry entry = (Map.Entry) entries71.next();
                Integer key = (Integer) entry.getKey();
                JSONArray arr = new JSONArray();
                arr.put(entry.getValue());
                JSONObject jsonObject=new JSONObject();
                JSONArray arr2 = (JSONArray) arr.get(0);
              /*  obj.put("Easting_" + (key), String.format("%.9f", arr2.get(0)));
                obj.put("Northing_" + (key), String.format("%.9f", arr2.get(1)));

                obj.put("Easting_accuracy_" + (key), String.format("%.5f", accuArrayX.get(key - 1)));
                obj.put("Northing_accuracy_" + (key), String.format("%.5f", accuArrayY.get(key - 1)));

                obj.put("accuracy_" + (key), String.format("%.9f", accuArray.get(key - 1)));*/

                jsonObject.put("Easting_", String.format("%.9f", arr2.get(0)));
                jsonObject.put("Northing_" , String.format("%.9f", arr2.get(1)));

                jsonObject.put("Easting_accuracy_", String.format("%.5f", accuArrayX.get(key - 1)));
                jsonObject.put("Northing_accuracy_", String.format("%.5f", accuArrayY.get(key - 1)));

                jsonObject.put("accuracy_", String.format("%.9f", accuArray.get(key - 1)));
                computeJSONArray.put(jsonObject);
            }
            Log.d("TAG", "computeJSONArray: "+computeJSONArray);

            obj.put("vertical_shift", String.format("%.5f", (avgHeightFixMinusGPS + avgheightArrayDiff)));

            obj.put("xzSlopeAvg", String.format("%.9f", xzSlopeAvg));
            obj.put("yzSlopeAvg", String.format("%.9f", yzSlopeAvg));

            obj.put("Origin_Easting_vertical", String.format("%.5f", fixX));
            obj.put("Origin_Northing_vertical", String.format("%.5f", fixY));

            JSONArray computeJSONArrayheight=new JSONArray();
            JSONArray computeJSONArraysigmaH=new JSONArray();

            for (int m = 0; m < heightArr2.length(); m++) {
                JSONObject jsonObject=new JSONObject();
//                obj.put("height_" + (m + 1), String.format("%.9f", heightArr2.get(m)));
                jsonObject.put("height_", String.format("%.9f", heightArr2.get(m)));
                computeJSONArrayheight.put(jsonObject);
            }

            for (int m = 0; m < heiArrayPlusavgHeightFixMinusGPSDiffArray2.length(); m++) {
                JSONObject jsonObject=new JSONObject();
//                obj.put("sigmaH_" + (m + 1), String.format("%.5f", heiArrayPlusavgHeightFixMinusGPSDiffArray2.get(m)));
                jsonObject.put("sigmaH_", String.format("%.5f", heiArrayPlusavgHeightFixMinusGPSDiffArray2.get(m)));
                computeJSONArraysigmaH.put(jsonObject);
            }

            obj.put("elevation avg", BigDecimal.valueOf(elevAvg / heiArrayPlusavgHeightFixMinusGPSDiffArray2.length()).toPlainString());

            json1.put(obj);
//            json2.add(obj2);
            finObj.put("values", json1);
            finObj.put("values1", computeJSONArray);
            finObj.put("values2", computeJSONArrayheight);
            finObj.put("values3", computeJSONArraysigmaH);
//            finObj.put("formula", json2);
        } catch (Exception e) {
            System.out.println("findCoordinateUTM5()- " + e);
        }
        return finObj;
    }
// optimized5 dynamic


    public JSONArray planeEquation(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        try {
            double a1 = x2 - x1;
            double b1 = y2 - y1;
            double c1 = z2 - z1;
            double a2 = x3 - x1;
            double b2 = y3 - y1;
            double c2 = z3 - z1;
            double a = b1 * c2 - b2 * c1;
            double b = a2 * c1 - a1 * c2;
            double c = a1 * b2 - b1 * a2;
            double d = (-a * x1 - b * y1 - c * z1);
//            double slopexz = (-d - a) / c;
//            double slopeyz = (-d - b) / c;
//            arr.put(slopexz);
//            arr.put(slopeyz);
            JSONArray arr = new JSONArray();
/*            if (!Double.isNaN(slopexz) && !Double.isInfinite(slopexz) && !Double.isNaN(slopeyz) && !Double.isInfinite(slopeyz)) {
                arr.put(slopexz);
                arr.put(slopeyz);
            }*/
            double slopexz = (-d - a) / c;
            if (!Double.isNaN(slopexz) && !Double.isInfinite(slopexz)) {
                arr.put(slopexz);
            }else{
                arr.put(0.0);
            }
            double slopeyz = (-d - b) / c;
            if (!Double.isNaN(slopeyz) && !Double.isInfinite(slopeyz)) {
                arr.put(slopeyz);
            }else{
                arr.put(0.0);
            }
            return arr;
        } catch (Exception e) {
            Log.d("TAG", "planeEquation: Exception"+e.getMessage());
            return null;
        }
    }

    public List degreeToUTM(double Lat, double Lon) {
        List<Double> northEastList = new ArrayList<Double>();
        try {
            double Easting;
            double Northing;
            int Zone;
            char Letter;

            Zone = (int) Math.floor(Lon / 6 + 31);
            if (Lat < -72) {
                Letter = 'C';
            } else if (Lat < -64) {
                Letter = 'D';
            } else if (Lat < -56) {
                Letter = 'E';
            } else if (Lat < -48) {
                Letter = 'F';
            } else if (Lat < -40) {
                Letter = 'G';
            } else if (Lat < -32) {
                Letter = 'H';
            } else if (Lat < -24) {
                Letter = 'J';
            } else if (Lat < -16) {
                Letter = 'K';
            } else if (Lat < -8) {
                Letter = 'L';
            } else if (Lat < 0) {
                Letter = 'M';
            } else if (Lat < 8) {
                Letter = 'N';
            } else if (Lat < 16) {
                Letter = 'P';
            } else if (Lat < 24) {
                Letter = 'Q';
            } else if (Lat < 32) {
                Letter = 'R';
            } else if (Lat < 40) {
                Letter = 'S';
            } else if (Lat < 48) {
                Letter = 'T';
            } else if (Lat < 56) {
                Letter = 'U';
            } else if (Lat < 64) {
                Letter = 'V';
            } else if (Lat < 72) {
                Letter = 'W';
            } else {
                Letter = 'X';
            }
            Easting = 0.5 * Math.log((1 + Math.cos(Lat * Math.PI / 180) * Math.sin(Lon * Math.PI / 180
                    - (6 * Zone - 183) * Math.PI / 180)) / (1 - Math.cos(Lat * Math.PI / 180) * Math.sin(Lon * Math.PI / 180
                    - (6 * Zone - 183) * Math.PI / 180))) * 0.9996 * 6399593.62 / Math.pow((1 + Math.pow(0.0820944379, 2)
                    * Math.pow(Math.cos(Lat * Math.PI / 180), 2)), 0.5) * (1 + Math.pow(0.0820944379, 2) / 2
                    * Math.pow((0.5 * Math.log((1 + Math.cos(Lat * Math.PI / 180) * Math.sin(Lon * Math.PI / 180 - (6 * Zone - 183)
                    * Math.PI / 180)) / (1 - Math.cos(Lat * Math.PI / 180) * Math.sin(Lon * Math.PI / 180 - (6 * Zone - 183)
                    * Math.PI / 180)))), 2) * Math.pow(Math.cos(Lat * Math.PI / 180), 2) / 3) + 500000;
            //Easting = 0.5 * Math.log((1 + Math.cos(Lat * Math.PI / 180) * Math.sin(Lon * Math.PI / 180 - (6 * Zone - 183) * Math.PI / 180)) / (1 - Math.cos(Lat * Math.PI / 180) * Math.sin(Lon * Math.PI / 180 - (6 * Zone - 183) * Math.PI / 180))) * 1.000004910 * 6399593.62 / Math.pow((1 + Math.pow(0.0820944379, 2) * Math.pow(Math.cos(Lat * Math.PI / 180), 2)), 0.5) * (1 + Math.pow(0.0820944379, 2) / 2 * Math.pow((0.5 * Math.log((1 + Math.cos(Lat * Math.PI / 180) * Math.sin(Lon * Math.PI / 180 - (6 * Zone - 183) * Math.PI / 180)) / (1 - Math.cos(Lat * Math.PI / 180) * Math.sin(Lon * Math.PI / 180 - (6 * Zone - 183) * Math.PI / 180)))), 2) * Math.pow(Math.cos(Lat * Math.PI / 180), 2) / 3) + 500000;
            //Easting = Math.round(Easting * 100) * 0.01;

            Northing = (Math.atan(Math.tan(Lat * Math.PI / 180) / Math.cos((Lon * Math.PI / 180 - (6 * Zone - 183) * Math.PI / 180)))
                    - Lat * Math.PI / 180) * 0.9996 * 6399593.625 / Math.sqrt(1 + 0.006739496742
                    * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) * (1 + 0.006739496742 / 2
                    * Math.pow(0.5 * Math.log((1 + Math.cos(Lat * Math.PI / 180) * Math.sin((Lon * Math.PI / 180 - (6 * Zone - 183)
                    * Math.PI / 180))) / (1 - Math.cos(Lat * Math.PI / 180) * Math.sin((Lon * Math.PI / 180 - (6 * Zone - 183)
                    * Math.PI / 180)))), 2) * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) + 0.9996 * 6399593.625
                    * (Lat * Math.PI / 180 - 0.005054622556 * (Lat * Math.PI / 180 + Math.sin(2 * Lat * Math.PI / 180) / 2)
                    + 4.258201531e-05 * (3 * (Lat * Math.PI / 180 + Math.sin(2 * Lat * Math.PI / 180) / 2)
                    //+ Math.sin(2 * Lat * Math.PI / 180) * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) / 4 - 1.674057895e-07
                    + Math.sin(2 * Lat * Math.PI / 180) * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) / 4 - 0.0000001674057895
                    * (5 * (3 * (Lat * Math.PI / 180 + Math.sin(2 * Lat * Math.PI / 180) / 2) + Math.sin(2 * Lat * Math.PI / 180)
                    * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) / 4 + Math.sin(2 * Lat * Math.PI / 180)
                    * Math.pow(Math.cos(Lat * Math.PI / 180), 2) * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) / 3);
            //Northing = (Math.atan(Math.tan(Lat * Math.PI / 180) / Math.cos((Lon * Math.PI / 180 - (6 * Zone - 183) * Math.PI / 180))) - Lat * Math.PI / 180) * 1.000004910 * 6399593.625 / Math.sqrt(1 + 0.006739496742 * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) * (1 + 0.006739496742 / 2 * Math.pow(0.5 * Math.log((1 + Math.cos(Lat * Math.PI / 180) * Math.sin((Lon * Math.PI / 180 - (6 * Zone - 183) * Math.PI / 180))) / (1 - Math.cos(Lat * Math.PI / 180) * Math.sin((Lon * Math.PI / 180 - (6 * Zone - 183) * Math.PI / 180)))), 2) * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) + 0.9996 * 6399593.625 * (Lat * Math.PI / 180 - 0.005054622556 * (Lat * Math.PI / 180 + Math.sin(2 * Lat * Math.PI / 180) / 2) + 4.258201531e-05 * (3 * (Lat * Math.PI / 180 + Math.sin(2 * Lat * Math.PI / 180) / 2) + Math.sin(2 * Lat * Math.PI / 180) * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) / 4 - 1.674057895e-07 * (5 * (3 * (Lat * Math.PI / 180 + Math.sin(2 * Lat * Math.PI / 180) / 2) + Math.sin(2 * Lat * Math.PI / 180) * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) / 4 + Math.sin(2 * Lat * Math.PI / 180) * Math.pow(Math.cos(Lat * Math.PI / 180), 2) * Math.pow(Math.cos(Lat * Math.PI / 180), 2)) / 3);
            if (Letter < 'M') {
                Northing = Northing + 10000000;
            }
            //Northing = Math.round(Northing * 100) * 0.01;

            northEastList.add(Easting);
            northEastList.add(Northing);
        } catch (Exception e) {
            System.out.println("degreeToUTM()- " + e);
        }
        return northEastList;
    }

    public List degreeToUTM2(double latitude, double longitude) {
        List<Double> northEastList = new ArrayList<Double>();
        try {

            int force_zone_number = 0;
            String force_zone_letter = "";

            double Zone;
            char Letter;

            Zone = (double) Math.floor(longitude / 6 + 31);

            double K0 = 0.9996;

            double E = 0.00669438;
            double E2 = E * E;
            double E3 = E2 * E;
            double E_P2 = E / (1 - E);

            double SQRT_E = Math.sqrt(1 - E);
            double _E = (1 - SQRT_E) / (1 + SQRT_E);
            double _E2 = _E * _E;
            double _E3 = _E2 * _E;
            double _E4 = _E3 * _E;
            double _E5 = _E4 * _E;

            double M1 = (1 - E / 4 - 3 * E2 / 64 - 5 * E3 / 256);
            double M2 = (3 * E / 8 + 3 * E2 / 32 + 45 * E3 / 1024);
            double M3 = (15 * E2 / 256 + 45 * E3 / 1024);
            double M4 = (35 * E3 / 3072);

            double P2 = (3 / 2 * _E - 27 / 32 * _E3 + 269 / 512 * _E5);
            double P3 = (21 / 16 * _E2 - 55 / 32 * _E4);
            double P4 = (151 / 96 * _E3 - 417 / 128 * _E5);
            double P5 = (1097 / 512 * _E4);

            double R = 6378137;

            String ZONE_LETTERS = "CDEFGHJKLMNPQRSTUVWXX";
            double lat_rad = Math.toRadians(latitude);
            double lat_sin = Math.sin(lat_rad);
            double lat_cos = Math.cos(lat_rad);

            double lat_tan = lat_sin / lat_cos;
            double lat_tan2 = lat_tan * lat_tan;
            double lat_tan4 = lat_tan2 * lat_tan2;
            double lon_rad = Math.toRadians(longitude);
            double central_lon = zone_number_to_central_longitude(Zone);
            double central_lon_rad = Math.toRadians(central_lon);

            double n = R / Math.sqrt(1 - E * Math.pow(lat_sin, 2));
            double c = E_P2 * Math.pow(lat_cos, 2);

            double a = lat_cos * mod_angle(lon_rad - central_lon_rad);
            double a2 = a * a;
            double a3 = a2 * a;
            double a4 = a3 * a;
            double a5 = a4 * a;
            double a6 = a5 * a;

            double m = R * (M1 * lat_rad
                    - M2 * Math.sin(2 * lat_rad)
                    + M3 * Math.sin(4 * lat_rad)
                    - M4 * Math.sin(6 * lat_rad));

            double easting = K0 * n * (a
                    + a3 / 6 * (1 - lat_tan2 + c)
                    + a5 / 120 * (5 - 18 * lat_tan2 + lat_tan4 + 72 * c - 58 * E_P2)) + 500000;

            double northing = K0 * (m + n * lat_tan * (a2 / 2 + a4 / 24 * (5 - lat_tan2 + 9 * c + 4 * Math.pow(c, 2))
                    + a6 / 720 * (61 - 58 * lat_tan2 + lat_tan4 + 600 * c - 330 * E_P2)));

            northEastList.add(easting);
            northEastList.add(northing);

        } catch (Exception e) {
            System.out.println("degreeToUTM2()- " + e);
        }
        return northEastList;
    }

    public Double zone_number_to_central_longitude(double zone_number) {
        double result = 0.0;
        try {
            result = (zone_number - 1) * 6 - 180 + 3;
        } catch (Exception e) {
            System.out.println("zone_number_to_central_longitude()- " + e);
        }
        return result;
    }

    public Double mod_angle(double value) {
        double result = 0.0;
        try {
            result = (value + Math.PI) % (2 * Math.PI) - Math.PI;
        } catch (Exception e) {
            System.out.println("mod_angle()- " + e);
        }
        return result;
    }


    public double getDistance(double vX, double vY, double fX, double fY) {
        double distance = 0.0;
        try {
            distance = Math.sqrt(Math.pow((fX - vX), 2) + Math.pow((fY - vY), 2));
        } catch (Exception e) {
            System.out.println("getDistance(): " + e);
        }
        return distance;
    }

    public static double findClosestValue(double[] arr, double target) {
        double closest = Double.MAX_VALUE; // Initialize closest with a large value
        double minDiff = Double.MAX_VALUE; // Initialize the minimum difference

        for (int i = 0; i < arr.length; i++) {
            double diff = Math.abs(arr[i] - target);
            if (diff < minDiff && arr[i] != target) {
                minDiff = diff;
                closest = arr[i];
            }
        }
        return closest;
    }

    public static double[] listToArray(List<Double> list) {
        double[] array = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    // get angle for 360
    public double getAngleIn360(double x1, double y1, double x2, double y2) {
        System.err.println("var fix - " + x1 + " - " + y1 + " - " + x2 + " - " + y2);
        double angle = 0.00;
        try {
            angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
            // Keep angle between 0 and 360
            angle = angle + Math.ceil(-angle / 360) * 360;

        } catch (Exception e) {
            System.out.println("getAngleIn360()- " + e);
        }
        return angle;
    }
}
