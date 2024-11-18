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

public class AdvancedCameraFilters {
    static Map<Integer, String> filterMap;
    static Map<Integer, Dimension> resolutions;

    static {
        filterMap = Map.of(
                0, "无滤镜",
                1, "灰度滤镜",
                2, "模糊滤镜",
                3, "边缘检测",
                4, "调整亮度和对比度",
                5, "彩色滤镜",
                6, "反色",
                7, "翻转画面",
                8, "旋转画面"
        );
        // 定义常见分辨率与其大小
        resolutions = Map.of(
                0, new Dimension(640, 360),  // 16:9 标清
                1, new Dimension(1280, 720), // 16:9 高清
                2, new Dimension(1920, 1080), // 16:9 全高清
                3, new Dimension(800, 600),  // 4:3 标准
                4, new Dimension(1024, 768), // 4:3 高分辨率
                5, new Dimension(3840, 2160) // 16:9 超高清（4K）
        );
    }

    public static void main(String[] arg) throws FrameGrabber.Exception {

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        final int[] filterType = {0}; // 默认滤镜类型：0（无滤镜）

        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();
        // 获取摄像头的分辨率
        int width = grabber.getImageWidth();
        int height = grabber.getImageHeight();

        // 创建一个CanvasFrame并设置为不可见
        CanvasFrame canvas = new CanvasFrame("摄像头预览");
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setVisible(false); // 不显示默认窗口

        // 创建主窗口
        JFrame frame = new JFrame("摄像头与输出台");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); // 设置布局管理器为BorderLayout

        // 创建一个面板来显示摄像头图像
        JPanel videoPanel = new JPanel();
        videoPanel.setPreferredSize(new Dimension(width, height)); // 固定视频面板大小
        videoPanel.setLayout(new BorderLayout());
        videoPanel.add(canvas.getCanvas(), BorderLayout.CENTER); // 添加视频显示到面板中

        // 创建文本区域用于显示信息
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false); // 设置为只读模式
        textArea.setLineWrap(true); // 自动换行
        textArea.setWrapStyleWord(true); // 单词内换行
        JScrollPane scrollPane = new JScrollPane(textArea); // 添加滚动条支持
        scrollPane.setPreferredSize(new Dimension(width, 100)); // 设置文本区大小

        // 创建操作面板
        int numFilters = filterMap.size(); // 按钮的数量
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, (numFilters + 1) / 2, 5, 5)); // 2行，动态列数

        int[] customOrder = {1,2,3,4,5,6,8,7,0};
        // 使用循环创建按钮并添加到面板
        for (Integer key : customOrder) {
            String value = filterMap.get(key);
            JButton button = new JButton(value);
            button.addActionListener(e -> {
                filterType[0] = key; // 设置对应的滤镜类型
                textArea.append("当前滤镜切换为: " + value + "\n"); // 输出切换信息到文本区域
            });
            buttonPanel.add(button); // 将按钮添加到面板
        }

        // 将操作面板,视频面板和信息输出区添加到主窗口
        frame.add(buttonPanel, BorderLayout.NORTH);  // 放置在窗口顶部
        frame.add(videoPanel, BorderLayout.CENTER); // 将视频显示区域放置在中心
        frame.add(scrollPane, BorderLayout.SOUTH); // 将输出区域放置在底部

        // 显示窗口
        frame.setSize(640, 630); // 设置窗口大小
        frame.setFocusable(true);
        frame.setVisible(true);


        // 添加键盘监听器
        frame.addKeyListener(new KeyAdapter() {
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
                System.out.println("当前滤镜类型: " + filterMap.get(filterType[0]));
                // 在文本区显示日志信息
                textArea.append("当前滤镜类型: " + filterMap.get(filterType[0]) + "\n");
            }
        });

        // 持续更新视频图像
        while (frame.isVisible()) {
            Frame now = grabber.grab();
            if (now == null) break;

            Mat mat = converter.convertToMat(now);
            applyFilter(mat, filterType[0]);
            canvas.showImage(converter.convert(mat)); // 显示滤镜后的图像

            // 重新绘制窗口
            frame.repaint();
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
                opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_GRAY2BGR); // 转回三通道
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
