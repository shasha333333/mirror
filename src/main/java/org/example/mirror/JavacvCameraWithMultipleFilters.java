package org.example.mirror;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import javax.swing.*;

public class JavacvCameraWithMultipleFilters {
    public static void main(String[] args) throws FrameGrabber.Exception {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0); // 打开默认摄像头
        grabber.start();

        CanvasFrame canvas = new CanvasFrame("摄像头 - 多滤镜支持");
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setFocusable(true); // 使窗口能够监听键盘事件

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        final int[] filterType = new int[]{0}; // 使用数组保存滤镜类型，便于在匿名类中修改值

        // 添加键盘事件监听器
        canvas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_0:
                        filterType[0] = 0; // 无滤镜
                        System.out.println("切换到：无滤镜");
                        break;
                    case java.awt.event.KeyEvent.VK_1:
                        filterType[0] = 1; // 灰度滤镜
                        System.out.println("切换到：灰度滤镜");
                        break;
                    case java.awt.event.KeyEvent.VK_2:
                        filterType[0] = 2; // 高斯模糊
                        System.out.println("切换到：高斯模糊");
                        break;
                    case java.awt.event.KeyEvent.VK_3:
                        filterType[0] = 3; // 边缘检测
                        System.out.println("切换到：边缘检测");
                        break;
                    default:
                        System.out.println("未知按键：" + e.getKeyCode());
                        break;
                }
            }
        });

        // 主循环：捕获和显示图像
        while (canvas.isVisible()) {
            Frame frame = grabber.grab();
            if (frame == null) continue;

            Mat colorMat = converter.convert(frame); // 将帧转换为 Mat 对象
            Mat resultMat = new Mat(); // 用于存储处理后的图像

            // 根据滤镜类型进行处理
            switch (filterType[0]) {
                case 0: // 原始图像
                    resultMat = colorMat.clone();
                    break;
                case 1: // 灰度滤镜
                    opencv_imgproc.cvtColor(colorMat, resultMat, opencv_imgproc.COLOR_BGR2GRAY);
                    break;
                case 2: // 高斯模糊
                    opencv_imgproc.GaussianBlur(colorMat, resultMat, new Size(15, 15), 0);
                    break;
                case 3: // 边缘检测
                    opencv_imgproc.Canny(colorMat, resultMat, 50, 150);
                    break;
                default:
                    resultMat = colorMat.clone(); // 默认显示原始图像
                    break;
            }

            // 转换并显示图像
            Frame resultFrame = converter.convert(resultMat);
            canvas.showImage(resultFrame);
        }

        // 关闭资源
        grabber.close();
        canvas.dispose();
    }
}
