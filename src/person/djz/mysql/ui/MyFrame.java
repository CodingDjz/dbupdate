package person.djz.mysql.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
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
	JButton oldBtn = new JButton("���");
	JButton newBtn = new JButton("���");
	JLabel oldLabel = new JLabel("ԴDB·����");
	JLabel newLabel = new JLabel("��DB·����");
	JTextField oldtext = new JTextField(15);
	JTextField newtext = new JTextField(15);
	JLabel currInfo = new JLabel();
	JPanel btnPanel = new JPanel();
	JButton exportBtn = new JButton("��������");
	JButton processBtn = new JButton("��������");
	JButton importBtn = new JButton("��������");
	Runtime rt = Runtime.getRuntime();

	public MyFrame() {
		setTitle("DB��������");
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
	 * ����
	 */
	public void setMyFrameLayout() {
		/*
		 * ����Ϊ��λ����
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
		 * �Ϸ�ѡ��·��JP����
		 */
		pathJP.setLayout(new GridLayout(2, 1));
		pathJP.add(oldLineJP);
		pathJP.add(newLineJP);
		/*
		 * �·���ťJP����
		 */
		btnPanel.add(exportBtn);
		btnPanel.add(processBtn);
		btnPanel.add(importBtn);
		optJP.setLayout(new GridLayout(2, 1));
		optJP.add(currInfo);
		optJP.add(btnPanel);
		/*
		 * ���岼��
		 */
		jp.setLayout(new GridLayout(2, 1));
		jp.add(pathJP);
		jp.add(optJP);
		pack();
	}

	/**
	 * ��DB���������ť������
	 */
	public void oldBtnListener() {
		oldBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setApproveButtonText("ѡ��");
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
	 * ��DB���������ť������
	 */
	public void newBtnListener() {
		newBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setApproveButtonText("ѡ��");
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
	 * ������ť����
	 */
	public void exportBtnListener() {
		exportBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								currInfo.setText("���ڵ�������...");
								setBtnEnable(false);
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
								currInfo.setText("���ݵ�����ɣ�");
								setBtnEnable(true);
							}
						});

					}
				}.start();
			}
		});
	}

	/**
	 * ��������
	 */

	public void processBtnListener() {
		processBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				new Thread() {
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								currInfo.setText("���ڴ�������...");
								setBtnEnable(false);
							}
						});
						//��������
						new SQLOpt().parseExportSQL();
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								currInfo.setText("���ݴ�����ɣ�");
								setBtnEnable(true);
							}
						});

					}
				}.start();

			}
		});
	}

	/**
	 * ���밴ť����
	 */
	public void importBtnListener() {
		importBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {


				new Thread() {
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								currInfo.setText("���ڵ�������...");
								setBtnEnable(false);
							}
						});
						//�������						
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
								currInfo.setText("���ݵ�����ɣ�");
								setBtnEnable(true);
							}
						});

					}
				}.start();
			}
		});
	}

	/**
	 * ��ť����
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
	 * ��������
	 * 
	 * @param com
	 * @param text
	 */
	public void setCompText(JTextComponent com, String text) {
		com.setText(text);
	}
}