package org.example.mirror;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.Frame;

import javax.swing.*;
import java.awt.*;

/**
 * 调用本地摄像头窗口视频，并添加信息输出台到同一界面
 * @author eguid
 * @date 2016年6月13日
 * @since  javacv1.2
 */
public class Camera {

    public static void main(String[] args) throws Exception, InterruptedException {
        // 创建OpenCVFrameGrabber来获取摄像头视频流
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0); // 默认摄像头
        grabber.start(); // 启动摄像头

        // 创建CanvasFrame，但不启动它的窗口
        CanvasFrame canvas = new CanvasFrame("摄像头视频");
        canvas.setVisible(false); // 隐藏默认窗口

        // 创建一个主窗口
        JFrame frame = new JFrame("摄像头与输出台");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); // 使用BorderLayout布局管理器

        // 创建一个面板来容纳Canvas（用于显示视频）
        JPanel videoPanel = new JPanel();
        videoPanel.setLayout(new BorderLayout());
        videoPanel.add(canvas.getCanvas(), BorderLayout.CENTER); // 将Canvas添加到面板

        // 创建一个文本区域用于显示日志信息
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false); // 设置文本区域为只读
        textArea.setLineWrap(true); // 自动换行
        textArea.setWrapStyleWord(true); // 单词内换行
        JScrollPane scrollPane = new JScrollPane(textArea); // 添加滚动条
        scrollPane.setPreferredSize(new Dimension(400, 150)); // 设置滚动区域大小

        // 将视频显示面板和信息输出区域添加到主窗口
        frame.add(videoPanel, BorderLayout.CENTER); // 视频显示放在中心
        frame.add(scrollPane, BorderLayout.SOUTH); // 输出区域放在底部

        // 设置窗口大小并显示
        frame.setSize(640, 480);
        frame.setVisible(true);

        // 持续从摄像头获取图像并显示
        while (canvas.isDisplayable()) {
            Frame frameData = grabber.grab(); // 获取视频帧
            if (frameData != null) {
                canvas.showImage(frameData); // 显示视频帧
            }

            // 在输出区域显示日志信息
            textArea.append("正在显示摄像头画面...\n");
            textArea.setCaretPosition(textArea.getDocument().getLength()); // 自动滚动到文本末尾

            // 可以添加适当的延时，防止信息刷得太快
            Thread.sleep(100);
        }

        // 关闭摄像头抓取
        grabber.close();
    }
}
