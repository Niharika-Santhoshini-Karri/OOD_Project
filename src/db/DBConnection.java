package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnection {

	private static DBConnection dbConnection;
	private Connection connection;

	private DBConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/library?createDatabaseIfNotExist=true&allowMultiQueries=true&autoReconnect=true&useSSL=false",
					"root", "Webcap@e6");
			PreparedStatement pstm = connection.prepareStatement("SHOW TABLES");
			ResultSet resultSet = pstm.executeQuery();
			if (!resultSet.next()) {
				String sql = "\n" + "CREATE TABLE `cardetail` (\n" + "  `id` int auto_increment,\n"
						+ "  `model` varchar(255) DEFAULT NULL,\n" + "  `owner` varchar(255) DEFAULT NULL,\n"
						+ "  `status` varchar(255) DEFAULT NULL,\n" + "  PRIMARY KEY (`id`)\n"
						+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;\n" + "\n"
						+ "CREATE TABLE `memberdetail` (\n" + "  `id` int auto_increment,\n"
						+ "  `email` varchar(255) DEFAULT NULL,\n" + "  `name` varchar(255) DEFAULT NULL,\n"
						+ "  `address` varchar(255) DEFAULT NULL,\n" + "  `contact` varchar(255) DEFAULT NULL,\n"
						+ "  `password` varchar(255) DEFAULT NULL,\n" + "  PRIMARY KEY (`id`)\n"
						+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n" + "\n" + "CREATE TABLE `issuetb` (\n"
						+ "  `issueId` int auto_increment,\n" + "  `date` date DEFAULT NULL,\n"
						+ "  `memberId` int DEFAULT NULL,\n" + "  `carId` int DEFAULT NULL,\n"
						+ "  PRIMARY KEY (`issueId`),\n"
						+ "  CONSTRAINT FOREIGN KEY (`memberId`) REFERENCES `memberdetail` (`id`),\n"
						+ "  CONSTRAINT FOREIGN KEY (`carId`) REFERENCES `cardetail` (`id`)\n"
						+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n" + "\n" +

						"CREATE TABLE `returndetail` (\n" + "  `id` int auto_increment,\n"
						+ "  `issuedDate` date NOT NULL,\n" + "  `returnedDate` date DEFAULT NULL,\n"
						+ "  `penalty` int(10) DEFAULT NULL,\n" + "  `issueid` int DEFAULT NULL,\n"
						+ "  PRIMARY KEY (`id`),\n"
						+ "  CONSTRAINT FOREIGN KEY (`issueid`) REFERENCES `issuetb` (`issueId`)\n"
						+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";
				pstm = connection.prepareStatement(sql);
				pstm.execute();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static DBConnection getInstance() {
		return dbConnection = ((dbConnection == null) ? new DBConnection() : dbConnection);

	}

	public Connection getConnection() {
		return connection;
	}

}
