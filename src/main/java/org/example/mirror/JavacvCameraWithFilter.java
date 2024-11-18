package org.example.mirror;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
/**
 * @Author: ZhangTong
 * @Date: 2024/11/16/17/04
 */

public class JavacvCameraWithFilter {
    public static void main(String[] args) throws FrameGrabber.Exception {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0); // 使用默认摄像头
        grabber.start();

        CanvasFrame canvas = new CanvasFrame("摄像头 - 灰度滤镜");
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        while (canvas.isVisible()) {
            // 获取摄像头图像帧
            Frame frame = grabber.grab();
            if (frame == null) {
                continue;
            }

            // 将帧转换为 Mat 对象
            Mat colorMat = converter.convert(frame);

            // 创建一个空白 Mat，用于存储灰度图
            Mat grayMat = new Mat();

            // 转换为灰度图像
            opencv_imgproc.cvtColor(colorMat, grayMat, opencv_imgproc.COLOR_BGR2GRAY);

            // 显示处理后的图像
            Frame grayFrame = converter.convert(grayMat);
            canvas.showImage(grayFrame);
        }

        grabber.close();
        canvas.dispose();
    }
}
