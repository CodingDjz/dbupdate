package person.djz.mysql.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Dao {

	/**
	 * 连接oldDB数据库
	 * 
	 * @return
	 */
	public Connection connOldDB() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/oldDB", "root", "");
		} catch (Exception e) {
			System.out.println("数据库连接失败");
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 连接newDB数据库
	 *
	 * @return
	 */
	public Connection connNewDB() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/newDB", "root", "");
		} catch (Exception e) {
			System.out.println("数据库连接失败");
			e.printStackTrace();
		}
		return conn;
	}

//	/**
//	 * 读取数据库mysql4.1内容
//	 */
//	public HashMap<String, String> getTableDataMap(Connection conn) {
//		HashMap<String, String> map = new HashMap<String, String>();
//		String selectSQL = "SELECT fieldName FROM tableName";
//		int i = 1;
//		try {
//			PreparedStatement ps = conn.prepareStatement(selectSQL);
//			ResultSet rs = ps.executeQuery();
//			while (rs.next()) {
//				String valueString = rs.getString("field");
//				map.put(valueString, ""+i++);
//			}
//		} catch (Exception e) {
//		}
//		return map;
//	}
	

	/**
	 * 关闭流
	 * @param conn
	 */

	public void closeDB(Connection conn) {
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

}
