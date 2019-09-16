package mil.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mil.application.ReceiveRoutine;

/**
 * 設定画面を起動する。
 * @author Satoru
 *
 */
public class SettingWindow{

	/** 実行時の背景色 */
	public static final Color RUNNING_COLOR = Color.GREEN;

	/** 停止時の背景色 */
	public static final Color STOP_COLOR = Color.LIGHT_GRAY;

	/** 画面本体 */
	private JFrame window;

	private JPanel messagePanel;
	private static JLabel messageLabel;

	/** 開始停止ボタン用パネル */
	private JPanel bottomButtonPanel;
	/** 開始ボタン */
	private static JButton startButton;
	/** 停止ボタン */
	private static JButton stopButton;

	private ReceiveRoutine receiveThread;

	/**
	 * 設定画面コンストラクタ
	 * @param title 画面タイトル
	 */
	public SettingWindow(String title) {
		window = new JFrame(title);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(320, 160);
        window.setLocationRelativeTo(null);
        window.setVisible(true);


        // 開始停止ボタン
        bottomButtonPanel = new JPanel();

        // 開始ボタン
        startButton = new JButton("Start");
        startButton.addActionListener(new StartListener());

        // 停止ボタン
        // 停止状態から開始するので最初は無効。
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new StopListener());

        bottomButtonPanel.add(startButton);
        bottomButtonPanel.add(stopButton);

        window.add(bottomButtonPanel);

        // メッセージフィールド設定
//        messagePanel = new JPanel();

        messageLabel = new JLabel("停止");
        messageLabel.setBackground(this.STOP_COLOR);
        messageLabel.setOpaque(true);

//        messagePanel.add(messageField);
        window.add(messageLabel);


        Container pane = window.getContentPane();
        pane.add(messageLabel,BorderLayout.CENTER);
        pane.add(bottomButtonPanel,BorderLayout.SOUTH);
	}

	/**
	 * エラーが発生した時、ボタンを押下せずに画面の状態を変更する。
	 * Startボタンの有効化、Stopボタンの無効化、メッセージ領域の背景色の変更を行う。
	 */
	public static void stopSetting(){
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		messageLabel.setBackground(STOP_COLOR);
        messageLabel.setText("停止");
	}

	/**
	 * メッセージ領域にメッセージを表示する。
	 * 受信スレッドから呼びたいのでstatic。
	 * @param message
	 */
	public static void setMessage(String message){
		if(messageLabel != null){
			messageLabel.setText(message);
		}
	}

	/**
	 * Startボタンが押下された時の挙動。
	 * アプリケーションを起動する。
	 * Startボタンを無効にして、Stopボタンを有効にする。
	 * @author Satoru
	 *
	 */
	private class StartListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			startButton.setEnabled(false);
			stopButton.setEnabled(true);

			//
	        messageLabel.setBackground(SettingWindow.RUNNING_COLOR);

	        messageLabel.setText("開始");

	        receiveThread = new ReceiveRoutine();
	        receiveThread.start();
		}

	}

	/**
	 * Stopボタンが押下された時の挙動。
	 * アプリケーションを停止する。
	 * Startボタンを無効にして、Stopボタンを有効にする。
	 * 次回のタイミングのメール受信を行わない。
	 * @author Satoru
	 *
	 */
	private class StopListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			stopSetting();
			receiveThread.setRunning(false);
		}

	}

}
