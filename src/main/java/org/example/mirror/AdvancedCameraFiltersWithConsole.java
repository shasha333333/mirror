package org.example.mirror;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

public class AdvancedCameraFiltersWithConsole {
    static Map<Integer,String> map;
    static {
        map = Map.of( 0,"无滤镜",
                1,"灰度滤镜",
                2,"模糊滤镜",
                3,"边缘检测",
                4,"调整亮度和对比度",
                5,"彩色滤镜",
                6,"反色",
                7,"翻转画面",
                8,"旋转画面");
    }

    public static void main() throws FrameGrabber.Exception {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();

        // 创建主窗口
        JFrame frame = new JFrame("摄像头预览与输出台");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 创建CanvasFrame来显示摄像头视频流
        CanvasFrame canvas = new CanvasFrame("摄像头预览");
        canvas.setPreferredSize(new Dimension(640, 480));

        // 创建输出台（控制台）
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(640, 200));

        // 添加到主窗口
        frame.add(canvas, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        final int[] filterType = {0}; // 默认滤镜类型：0（无滤镜）

        // 添加键盘监听器
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_0: filterType[0] = 0; break; // 无滤镜
                    case KeyEvent.VK_1: filterType[0] = 1; break; // 灰度滤镜
                    case KeyEvent.VK_2: filterType[0] = 2; break; // 模糊滤镜
                    case KeyEvent.VK_3: filterType[0] = 3; break; // 边缘检测
                    case KeyEvent.VK_4: filterType[0] = 4; break; // 调整亮度和对比度
                    case KeyEvent.VK_5: filterType[0] = 5; break; // 彩色滤镜
                    case KeyEvent.VK_6: filterType[0] = 6; break; // 反色
                    case KeyEvent.VK_7: filterType[0] = 7; break; // 翻转画面
                    case KeyEvent.VK_8: filterType[0] = 8; break; // 旋转画面
                    default: System.out.println("未知滤镜类型"); break;
                }
                // 使用 SwingUtilities.invokeLater 来确保更新 JTextArea 在 EDT 线程中
                SwingUtilities.invokeLater(() -> {
                    outputArea.append("当前滤镜类型: " + map.get(filterType[0]) + "\n");
                });
            }
        });

        while (canvas.isVisible()) {
            Frame frame1 = grabber.grab();
            if (frame1 == null) break;

            Mat mat = converter.convertToMat(frame1);
            applyFilter(mat, filterType[0]);
            canvas.showImage(converter.convert(mat));
        }

        grabber.close();
    }

    // 应用滤镜方法
    private static void applyFilter(Mat mat, int filterType) {
        switch (filterType) {
            case 0:
                // 无滤镜
                break;
            case 1:
                // 灰度滤镜
                opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);
                opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_GRAY2BGR); // 转回三通道以保持显示一致性
                break;
            case 2:
                // 模糊滤镜
                opencv_imgproc.GaussianBlur(mat, mat, new Size(15, 15), 0);
                break;
            case 3:
                // 边缘检测
                Mat edges = new Mat();
                opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);  // 转换为灰度图
                opencv_imgproc.Canny(mat, edges, 50, 150);  // 边缘检测
                opencv_imgproc.cvtColor(edges, mat, opencv_imgproc.COLOR_GRAY2BGR); // 转回三通道
                break;
            case 4:
                // 调整亮度和对比度
                mat.convertTo(mat, -1, 1.5, 50);
                break;
            case 5:
                // 彩色滤镜
                opencv_imgproc.applyColorMap(mat, mat, opencv_imgproc.COLORMAP_JET);
                break;
            case 6:
                // 反色滤镜
                opencv_core.bitwise_not(mat, mat);
                break;
            case 7:
                // 翻转画面
                opencv_core.flip(mat, mat, 1);   // 水平翻转
                break;
            case 8:
                // 旋转画面
                opencv_core.transpose(mat, mat); // 转置
                opencv_core.flip(mat, mat, 1);   // 水平翻转
                break;
            default:
                System.out.println("未实现的滤镜类型: " + filterType);
        }
    }
}
