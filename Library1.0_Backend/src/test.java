import java.io.InputStream;
import java.sql.*;

public class test {
    public static void main(String[] args) {
        try {
            System.out.println("Start");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/library1?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    "root", "lwxwl11090308");
            Statement stmt = conn.createStatement();
            String sql = "select * from seat";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("seatID");
                String areaname = rs.getString("areaname");
                String typename = rs.getString("typename");
                System.out.println(id + " " + areaname + " " + typename);
            }
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException | ClassNotFoundException se) {
            se.printStackTrace();
        }
        System.out.println("end.");
    }
}