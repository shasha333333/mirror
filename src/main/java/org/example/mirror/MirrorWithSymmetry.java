package org.example.mirror;//package com.example.demo.mirror;
//
//
//import org.bytedeco.javacv.*;
//import org.bytedeco.opencv.global.opencv_imgproc;
//import org.bytedeco.opencv.opencv_core.Mat;
//
//
//import javax.swing.*;
//
//import static com.example.demo.api.SymmetryDegree.calculateSymmetryDegree;
//
//public class MirrorWithSymmetry {
//    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {
//        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
//        grabber.start();
//
//        CanvasFrame canvas = new CanvasFrame("镜像与对称度");
//        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
//
//        while (canvas.isDisplayable()) {
//            Frame frame = grabber.grab();
//            if (frame == null) {
//                break;
//            }
//
//            Mat mat = converter.convert(frame);
//            Mat grayMat = new Mat();
//            opencv_imgproc.cvtColor(mat, grayMat, opencv_imgproc.COLOR_BGR2GRAY);
//
//            // 边缘检测
//            Mat edges = new Mat();
//            opencv_imgproc.Canny(grayMat, edges, 100, 200);
//
//            double symmetryScore = calculateSymmetryDegree(edges);
//
//            System.out.println("当前对称度: " + symmetryScore);
//
//            canvas.showImage(frame);
//        }
//
//        grabber.close();
//        canvas.dispose();
//    }
//
//
//}
