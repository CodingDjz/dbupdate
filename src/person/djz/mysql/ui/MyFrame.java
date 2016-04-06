package person.djz.mysql.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import person.djz.mysql.load.ReadConf;
import person.djz.mysql.load.Tools;
import person.djz.mysql.opt.SQLOpt;

public class MyFrame extends JFrame {
	JPanel jp = new JPanel();
	JPanel pathJP = new JPanel();
	JPanel optJP = new JPanel();
	JPanel oldLineJP = new JPanel();
	JPanel newLineJP = new JPanel();
	JFileChooser jfc = new JFileChooser();
	JDialog jd = new JDialog();
	JButton oldBtn = new JButton("浏览");
	JButton newBtn = new JButton("浏览");
	JLabel oldLabel = new JLabel("源DB路径：");
	JLabel newLabel = new JLabel("宿DB路径：");
	JTextField oldtext = new JTextField(15);
	JTextField newtext = new JTextField(15);
	JLabel currInfo = new JLabel();
	JPanel btnPanel = new JPanel();
	JButton exportBtn = new JButton("导出数据");
	JButton processBtn = new JButton("处理数据");
	JButton importBtn = new JButton("导入数据");
	Runtime rt = Runtime.getRuntime();

	public MyFrame() {
		setTitle("DB升级程序");
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMyFrameLayout();
		oldBtnListener();
		newBtnListener();
		exportBtnListener();
		processBtnListener();
		importBtnListener();
		Dimension dms = Toolkit.getDefaultToolkit().getScreenSize();
		int widthindex = (int) ((dms.getWidth() - getWidth()) / 2);
		int heightindex = (int) ((dms.getHeight() - getHeight()) / 2);
		setLocation(widthindex, heightindex);
		try {
			if (Tools.getFileByJarPath("SetPath.bat").exists()) {
				Tools.getFileByJarPath("SetPath.bat").delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 布局
	 */
	public void setMyFrameLayout() {
		/*
		 * 以行为单位布局
		 */
		oldLineJP.add(oldLabel);
		oldLineJP.add(oldtext);
		oldLineJP.add(oldBtn);
		oldtext.setEditable(false);
		oldtext.setBackground(new Color(0xffffff));
		newLineJP.add(newLabel);
		newLineJP.add(newtext);
		newLineJP.add(newBtn);
		newtext.setEditable(false);
		newtext.setBackground(new Color(0xffffff));
		add(jp);
		jd.add(jfc);
		/*
		 * 上方选择路径JP布局
		 */
		pathJP.setLayout(new GridLayout(2, 1));
		pathJP.add(oldLineJP);
		pathJP.add(newLineJP);
		/*
		 * 下方按钮JP布局
		 */
		btnPanel.add(exportBtn);
		btnPanel.add(processBtn);
		btnPanel.add(importBtn);
		optJP.setLayout(new GridLayout(2, 1));
		optJP.add(currInfo);
		optJP.add(btnPanel);
		/*
		 * 整体布局
		 */
		jp.setLayout(new GridLayout(2, 1));
		jp.add(pathJP);
		jp.add(optJP);
		pack();
	}

	/**
	 * 老DB“浏览”按钮监听器
	 */
	public void oldBtnListener() {
		oldBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setApproveButtonText("选择");
				int clickNum = jfc.showOpenDialog(null);
				if (clickNum == jfc.APPROVE_OPTION) {
					String p = jfc.getSelectedFile().getPath();
					String path = "set OLD_NMS=" + p;
					// getSelectedFile().getPath();
					new ReadConf().writePath(path);
					oldtext.setText(p);
				}
			}
		});
	}

	/**
	 * 新DB“浏览”按钮监听器
	 */
	public void newBtnListener() {
		newBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setApproveButtonText("选择");
				int clickNum = jfc.showOpenDialog(null);
				if (clickNum == jfc.APPROVE_OPTION) {
					String p = jfc.getSelectedFile().getPath();
					String path = "set NEW_NMS=" + p;
					new ReadConf().writePath(path);
					newtext.setText(p);
				}
			}
		});
	}

	/**
	 * 导出按钮监听
	 */
	public void exportBtnListener() {
		exportBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								currInfo.setText("正在导出数据...");
								setBtnEnable(false);
								setCursor(new Cursor(Cursor.WAIT_CURSOR));
							}
						});

						String path = Tools.getRootPath() + File.separator
								+ "ExportSQL.bat";
						try {
							Process process = rt.exec("\"" + path + "\"");
							process.waitFor();
						} catch (IOException | InterruptedException e1) {
							e1.printStackTrace();
						}

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								currInfo.setText("数据导出完成！");
								setBtnEnable(true);
								setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

							}
						});

					}
				}.start();
			}
		});
	}

	/**
	 * 处理数据
	 */

	public void processBtnListener() {
		processBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				new Thread() {
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								currInfo.setText("正在处理数据...");
								setBtnEnable(false);
								setCursor(new Cursor(Cursor.WAIT_CURSOR));
							}
						});
						//处理数据
						new SQLOpt().parseExportSQL();
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								currInfo.setText("数据处理完成！");
								setBtnEnable(true);
								setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							}
						});

					}
				}.start();

			}
		});
	}

	/**
	 * 导入按钮监听
	 */
	public void importBtnListener() {
		importBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {


				new Thread() {
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								currInfo.setText("正在导入数据...");
								setBtnEnable(false);
								setCursor(new Cursor(Cursor.WAIT_CURSOR));
							}
						});
						//导入操作						
						String path = Tools.getRootPath() + File.separator
								+ "ImportSQL.bat";
						try {
							Process process = rt.exec("\"" + path + "\"");
							process.waitFor();
						} catch (IOException | InterruptedException e1) {
							e1.printStackTrace();
						}
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								currInfo.setText("数据导入完成！");
								setBtnEnable(true);
								setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

							}
						});

					}
				}.start();
			}
		});
	}

	/**
	 * 按钮设置
	 * 
	 * @param isenable
	 */
	public void setBtnEnable(Boolean isenable) {
		oldBtn.setEnabled(isenable);
		newBtn.setEnabled(isenable);
		importBtn.setEnabled(isenable);
		exportBtn.setEnabled(isenable);
		processBtn.setEnabled(isenable);
	}

	/**
	 * 设置文字
	 * 
	 * @param com
	 * @param text
	 */
	public void setCompText(JTextComponent com, String text) {
		com.setText(text);
	}
}