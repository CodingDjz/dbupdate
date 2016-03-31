package person.djz.mysql.load;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import person.djz.mysql.run.update;

public class Tools {	
	private static String thisPath = update.class.getProtectionDomain().getCodeSource()
			.getLocation().getPath();
	private static File thisFile = new File(thisPath);
	
	/**
	 * ��õ�ǰ�ļ�·��
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File getFileByJarPath(String fileName) throws IOException {
		String filePath = System.getProperty("user.dir") + File.separator + fileName;;
//		String filePath2 = thisFile.getParent() + File.separator + fileName;
//		String filePath = thisFile.getParentFile().getParent() + File.separator + fileName;
		filePath = URLDecoder.decode(filePath, "gbk");
		File sqlFile = new File(filePath);
//		if ((!sqlFile.exists()) && ("ExportSQL".equals(fileName))) {
//			JOptionPane
//					.showMessageDialog(null, "û�зŵ���ȷ��Ŀ¼�£���������ԡ�", "·������", 0);
//			System.exit(-1);
//		} 
//		JOptionPane.showConfirmDialog(null, thisFile.getPath()+"--"+new File(System.getProperty("user.dir")));

		if ((!sqlFile.exists()) && (fileName.contains("ImportSQL"))) {
			sqlFile.createNewFile();
		}
		return sqlFile;
	}
	
	public static String getRootPath(){
		String path = System.getProperty("user.dir");//����������Ŀ¼
//		String path =thisFile.getParentFile().getParent();
		return path;
	}
}
