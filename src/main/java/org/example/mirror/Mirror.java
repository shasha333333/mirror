package org.example.mirror;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.IplImage;

public class Mirror {
    public static void main(String[] args) {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0); // 使用第一个摄像头
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

        try {
            grabber.start();
            System.out.println("摄像头启动");
            CanvasFrame canvas = new CanvasFrame("Mirror");
            canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

            Frame frame;
            while ((frame = grabber.grab()) != null) {
                // 转换为 IplImage
                IplImage image = converter.convert(frame);
                // 显示镜像效果
                if (image != null) {
                    opencv_core.cvFlip(image, image, 1); // 水平翻转镜像
                    Frame mirrorFrame = converter.convert(image);
                    canvas.showImage(mirrorFrame);
                }

                if (!canvas.isVisible()) {
                    break;
                }
            }

            grabber.stop();
            canvas.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
