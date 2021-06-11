import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
//import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;
import java.util.Date;

class User {
	public User() {
		id = "";
		type = new String[4];
		area = new String[4];
	}

	public void setUser(String ID, String Type, String Area, int deg, int credi) {
		id = ID;
		area[deg] = Area;
		type[deg] = Type;
		credit = credi;
	}

	public String id = "";
	public String[] type;
	public String[] area;
	public int credit;
}

class MergeUnion {
	public MergeUnion(int n) {
		f = new int[n];
		for (int i = 1; i < n; i++)
			f[i] = i;
	}

	public int find(int x) {
		if (x == f[x]) {
			return x;
		} else {
			f[x] = find(f[x]);
		}
		return f[x];
	}

	public void merge(int x, int y) {
		f[find(x)] = find(y);
	}

	public int[] f;
}

public class Main {
	final static int MAX_creditScore = 100;
	static MergeUnion mu;

	/*
	 * users(userID,creditscore,typename,areaname,degree),
	 * seats(seatID,areaname,typename)
	 */
	// users应该表示用户偏好表，同时带有信誉分
	static String fen_pei(ResultSet users, ResultSet seats, ResultSet area) throws SQLException {
		String ans = "\n";// SQL语句
		ArrayList<User>[] Users = new ArrayList[MAX_creditScore + 1];// 每个信誉分维护一个具有该信誉分的用户列表
		ArrayList[] bei_fen_pei = new ArrayList[MAX_creditScore + 1];// 考虑赋值操作开销大,采用开辟空间的方式标记用户是否被分配
		for (int i = 0; i <= MAX_creditScore; i++) {
			Users[i] = new ArrayList<User>();
			bei_fen_pei[i] = new ArrayList();
		}
		ArrayList[][] Seats = new ArrayList[10][5];// 维护每个区域,每个类型的座位列表
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 5; j++)
				Seats[i][j] = new ArrayList();
		int[][] ptr = new int[9][4];// 指针,指向每个区域每个类型第一个未分配的座位下角标
		// 初始化指针
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 4; j++)
				ptr[i][j] = 0;
		Hashtable<String, Integer> hash_area = new Hashtable();// 存储区域，为区域编号
		Hashtable<String, Integer> hash_type = new Hashtable();// 存储类型，为类型编号
		hash_type.put("单人", 1);// 类型到整数的映射
		hash_type.put("双人", 2);
		hash_type.put("多人", 3);
		hash_type.put("随意", 4);
		area.beforeFirst();// 定位到第一行
		int cnt = 1;

		// 保存area内容
		while (area.next()) {
			hash_area.put(area.getString(1), cnt);// 建立区域名到整数的映射
			cnt++;
		}
		hash_area.put("随意", cnt);

		area.beforeFirst();
		// 保存seat内容
		seats.beforeFirst();
		String tempStr;
		int tempID;
		String tempStr2;
		while (seats.next()) {
			tempStr = seats.getString(3);
			if (tempStr.charAt(0) == '单')
				tempID = 1;
			else if (tempStr.charAt(0) == '双')
				tempID = 2;
			else if (tempStr.charAt(0) == '多')
				tempID = 3;
			else
				tempID = 4;
			tempStr2 = seats.getString(1);
			int ii = 0;
			for (int i = 0; i < tempStr2.length(); i++) {
				ii *= 10;
				ii += tempStr2.charAt(i) - '0';
			}
			Seats[(int) hash_area.get(seats.getString(2))][tempID].add(ii);// 往座位表中添加座位id
		}
		// 重新组织用户信息格式
		users.beforeFirst();
		if (!users.next())
			return ans;// 空表就返回
		users.beforeFirst();
		Hashtable us_ID = new Hashtable();// 将用户ID映射到一个整数i，该用户在Users表的位置为 Users[信誉分][i];
		while (users.next()) {
			String id = users.getString(1);
			int xinyu = users.getInt(2);
			if (xinyu < 0)
				xinyu = 0;
			if (us_ID.get(id) == null)// 该用户没被插入过
			{
				bei_fen_pei[xinyu].add(0);
				User temp = new User();
				temp.setUser(id, users.getString(3), users.getString(4), users.getInt(5), xinyu);
				us_ID.put(id, Users[xinyu].size());// 插入
				Users[xinyu].add(temp);
			} else// 插入过就更新
			{
				int idx = (int) us_ID.get(id);
				User temp = Users[xinyu].get(idx);
				temp.setUser(id, users.getString(3), users.getString(4), users.getInt(5), xinyu);
				Users[xinyu].set(idx, temp);
			}
		}
		// 开始分配
		for (int i = MAX_creditScore; i >= 0; i--)// 从信誉分最高的开始
		{
			for (int k = 3; k >= 1; k--)// 从喜好度最高的开始
				for (int j = 0; j < Users[i].size(); j++) {
					if ((int) bei_fen_pei[i].get(j) == 1)// 如果被分配过了，则跳过
						continue;
					int id1, id2;
					if (Users[i].get(j).area[k] != null) {
						if (Users[i].get(j).area[k].charAt(0) != '随')
							id1 = (int) hash_area.get(Users[i].get(j).area[k]);
						else
							id1 = cnt;
					} else
						continue;
					if (Users[i].get(j).type[k] != null) {
						if (Users[i].get(j).type[k].charAt(0) != '随')
							id2 = (int) hash_type.get(Users[i].get(j).type[k]);
						else
							id2 = 4;
					} else
						continue;
					// 对区域是否为随意,类型是否为随意做分类讨论
					for (int ii = 1; ii <= 8; ii++) {
						if (id1 != cnt)// 如果区域不是随意
							ii = id1;
						for (int jj = 1; jj <= 3; jj++) {
							if (id2 != 4)
								jj = id2;
							if (ptr[ii][jj] < Seats[ii][jj].size() && (int) bei_fen_pei[i].get(j) == 0) {
								bei_fen_pei[i].set(j, 1);
								int id3 = ptr[ii][jj];
								ans += "insert into seated values('" + Users[i].get(j).id + "','"
										+ Seats[ii][jj].get(id3) + "','530'," + "'0');\n";// 开始时间为8点50分，第530分钟
								ptr[ii][jj] += 1 + virus;
							}
							if (id2 != 4)// 如果类型不是随意
								break;
						}
						if (id1 != cnt)// 如果类型不是随意
							break;
					}
				}
		}
		return ans;
	}

	// 将用户表和偏好表自然连接
	static String xin_yu_fen_he_pian_hao() {
		String ans = "select users.userID,creditscore,typename,areaname,degree\r\n" + "from users,preference\r\n"
				+ "where users.userID=preference.userID and users.userID not in\r\n" + "(select userID from seated);";
		return ans;
	}

	// 获取座位表
	static String huo_qv_zuo_wei() {
		String ans = "select*from seat\r\n" + "where seatID not in(select seatID from seated)";
		return ans;
	}

	// 获取区域表
	static String huo_qv_qv_yu() {
		String ans = "select distinct(areaname)from area";
		return ans;
	}

	// 是否预定过,避免重复预定
	static String yu_ding_guo(String userID, String type, String areaname) {
		String ans = "select userID from preference where userID='" + userID + "' and typename='" + type
				+ "' and areaname='" + areaname + "';";
		return ans;
	}

	// 预定
	static String reserve(String userID, String type, int deg, String areaname) {
		String ans;
		ans = "insert into preference values('" + userID + "','" + type + "','" + areaname + "','" + deg + "');";
		return ans;
	}

	// 放弃预定
	static String fang_qi_yu_ding(String userId) {
		String ans;
		ans = "delete from preference where userID='" + userId + "';";
		return ans;
	}

	// 查看预定结果
	static String cha_kan_yu_ding(String userId) {
		String ans;
		ans = "select seat.seatID,areaname,seated.arrived\r\n" + "from seated,seat\r\n"
				+ "where seated.seatID=seat.seatID and userID='" + userId + "';";
		return ans;
	}

	// 是否举报过该座位
	static String bei_jv_bao(String userId, String seatId, String type) {
		String ans = "select seatID from report where seatID='" + seatId + "'and userID='" + userId + "'";
		return ans;
	}

	// 被别人举报过多少次,3次则扣分
	static String bie_ren_jv_bao(String seatId, String type) {
		String ans = "select*from report where seatID='" + seatId + "' and reporttype='" + type + "';";
		return ans;
	}

	// 举报
	static String jv_bao(String userId, String seatId, String time, String type) {

		String ans = "insert into report values('" + "" + userId + "','" + seatId + "','" + time + "','" + type + "');";
		return ans;
	}

	// 查询举报信息（管理员） check_report函数更改
	static String check_report() {
		String ans = "";
		ans = "with report_times(seatId,typeName,times) as\r\n" + "(\r\n" + "   select seatID,reporttype,count(*)\r\n"
				+ "   from report\r\n" + "   group by seatID,reporttype\r\n" + ")\r\n" + "select*\r\n"
				+ "from report_times\r\n" + "order by times desc ,seatId";
		return ans;
	}

	// 扣信誉分
	static String kou_fen(String userId) {
		String ans;
		ans = "update users set creditscore=creditscore-1 where userID='" + userId + "';";
		return ans;
	}

	// 查信誉分
	static String xin_yu_fen(String userId) {
		String ans = "select creditscore from users where userID='" + userId + "';";
		return ans;
	}

	// 修改个人信息
	static String xiu_gai_xin_xi(String userId, String deptName, String mail, String phone) {
		String ans = "update users set deptname='" + deptName + "'," + "email='" + mail + "',phonenumber='" + phone
				+ "' where userID='" + userId + "';";
		return ans;
	}

	// 修改密码
	static String xiu_gai_mi_ma(String userId, String psw) {
		String ans = "update users set passwords='" + psw + "' where userID='" + userId + "';";
		return ans;
	}

	// 登录
	static String login(String userId, String pwd) {
		String ans = "select *from users where userID='" + userId + "' and passwords='" + pwd + "';";
		return ans;
	}

	// 查询空余座位
	static String kong_yu_zuo_wei(String type, String areaname) {
		String except = "";
		if (virus == 1) {
			except += "except(select seatID+1 from seated)\n";
			except += "except(select seatID-1 from seated)\n";
		}
		String ans = "with aa(ID) as((select seatID from seat where " + "areaname='" + areaname + "' and typename='"
				+ type + "')" + "except(select seatID from seated)" + except + ")select ID,areaname "
				+ "from aa,seat where ID=seatID";
		if (areaname.charAt(0) == '随') {
			if (type.charAt(0) == '随')
				ans = "with aa(ID) as((select seatID from seat)" + "except(select seatID from seated)" + except
						+ ")select ID,areaname " + "from aa,seat where ID=seatID";
			else
				ans = "with aa(ID) as((select seatID from seat where " + "typename='" + type + "')"
						+ "except(select seatID from seated)" + except + ")select ID,areaname "
						+ "from aa,seat where ID=seatID";
		} else {
			if (type.charAt(0) == '随') {
				ans = "with aa(ID) as((select seatID from seat where " + "areaname='" + areaname + "')"
						+ "except(select seatID from seated)" + except + ")select ID,areaname "
						+ "from aa,seat where ID=seatID";
			}
		}
		return ans;
	}

	// 查看某座位有无人预约/使用 checkseat seatId
	static String check_seatId(String seatId) {
		String ans = "";
		ans = "(select seatID from seat where " + "seatID='" + seatId + "')" + "except(select seatID from seated)\n";
		if (virus == 1) {
			ans += "except(select seatID+1 from seated)\n";
			ans += "except(select seatID-1 from seated)\n";
		}
		return ans;
	}

	// 使用座位
	// String seat，下面一长串是为了做新增的查询直接或间接接触的人的功能
	static String seat(String userId, String seatId, String time) {
		String ans = "insert into seated values('" + userId + "','" + seatId + "','" + time + "','1');";
		ans += "declare @id numeric(13);\r\n" + "set @id='" + userId + "';\r\n"
				+ "--delete from seated where userID=@id;\r\n" + "declare @cnt int;\r\n"
				+ "declare @area_name varchar(30);\r\n" + "declare @type_name varchar(10);\r\n" + "set @area_name=(\r\n"
				+ "  select areaname \r\n" + "  from seated,seat\r\n"
				+ "  where seated.seatID=seat.seatID and seated.userID=@id\r\n" + ")\r\n" + "set @type_name=(\r\n"
				+ "  select typename \r\n" + "  from seated,seat\r\n"
				+ "  where seated.seatID=seat.seatID and seated.userID=@id\r\n" + ")\r\n"
				+ "set @cnt=(select count(*)from user_area where userID=@id and areaname=@area_name and typename=@type_name)\r\n"
				+ "if @cnt=0\r\n" + " begin \r\n" + " insert into user_area values(@id,@area_name,@type_name);\r\n"
				+ " end";
		return ans;
	}

	// 结束当前座位使用
	static String jie_shu(String userId) {
		String ans = "delete from seated where userID='" + userId + "';";
		return ans;
	}

	// 确认入座
	static String que_ren(String userId) {
		String ans = "update seated set arrived='1' where userId='" + userId + "';";
		ans += "declare @id numeric(13);\r\n" + "set @id='" + userId + "';\r\n"
				+ "--delete from seated where userID=@id;\r\n" + "declare @cnt int;\r\n"
				+ "declare @area_name varchar(30);\r\n" + "declare @type_name varchar(10);\r\n" + "set @area_name=(\r\n"
				+ "  select areaname \r\n" + "  from seated,seat\r\n"
				+ "  where seated.seatID=seat.seatID and seated.userID=@id\r\n" + ")\r\n" + "set @type_name=(\r\n"
				+ "  select typename \r\n" + "  from seated,seat\r\n"
				+ "  where seated.seatID=seat.seatID and seated.userID=@id\r\n" + ")\r\n"
				+ "set @cnt=(select count(*)from user_area where userID=@id and areaname=@area_name and typename=@type_name)\r\n"
				+ "if @cnt=0\r\n" + " begin \r\n" + " insert into user_area values(@id,@area_name,@type_name);\r\n"
				+ " end";
		return ans;
	}

	// 清理迟到座位
	static String qing_li(String time) {
		String ans = "";
		ans = "delete from seated where arrived=0 and bookingtime+30<" + time + ";";
		return ans;
	}

	// 查看个人信息
	static String cha_xin_xi(String userId) {
		String ans = "select userID,username,deptname,email,phonenumber from users where userID='" + userId + "';";
		return ans;
	}

	// 查看目前用户是否在seated表中
	static String shi_fou_shi_yong(String userId) {
		String ans;
		ans = "select*from seated where userID='" + userId + "';";
		return ans;
	}

	// 查看区域人数
	static String qv_yu_ren_shu() {
		String ans;
		ans = "select areaname,count(areaname)\r\n" + "from seat,seated\r\n" + "where seated.seatID=seat.seatID\r\n"
				+ "group by areaname";
		return ans;
	}

	// 访问临界资源
	public static synchronized String seating(String userId, String seatId, String time, Statement stmt)
			throws SQLException {
		String Sql = shi_fou_shi_yong(userId);
		ResultSet rs = stmt.executeQuery(Sql);
		rs.beforeFirst();
		int rs_size = 0;
		while (rs.next())
			rs_size++;
		if (rs_size == 0) {
			Sql = seat(userId, seatId, time);
			stmt.executeUpdate(Sql);
			return "Accept";
		} else
			return "Refuse";
	}

	static int virus = 0;

	public static void main(String[] args) throws Exception {
		// 监听指定的端口
		int port = 55536;
		ServerSocket server = new ServerSocket(port);

		// server将一直等待连接的到来
		System.out.println("server将一直等待连接的到来");
		// 如果使用多线程,那就需要线程池,防止并发过高时创建过多线程耗尽资源
		ExecutorService threadPool = Executors.newFixedThreadPool(100);
		// 创建一个计时器线程,每1分钟查询一次占座表，将占座表中的未入座的清除
		final long timeInterval = 60000;
		Runnable runnable1 = new Runnable() {
			public void run() {
				while (true) {
					// ------- code for task to run
					// ------- 要运行的任务代码
					// System.out.println("Hello, stranger");
					// ------- ends here
					try {
						// 一系列处理 获取时间
						String time = "";
						Date date = new Date();
						DateFormat df3 = DateFormat.getTimeInstance();// 只显示出时分秒
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Calendar cal = df.getCalendar();
						int hour = cal.get(Calendar.HOUR);// 小时
						int minute = cal.get(Calendar.MINUTE);// 分
						String curr = df3.format(date);
						if (curr.charAt(0) == '下')
							hour += 12;
						minute += hour * 60;
						time = Integer.toString(minute);

						String Sql = "delete from seated where arrived = 0 and bookingtime+30<" + time + " and 1320>"
								+ time;
						// 连接数据库
						Connection connection = null;
						String driverName = "com.mysql.cj.jdbc.Driver";// SQL数据库引擎
						String dbURL = "jdbc:mysql://localhost:3306/library1?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";// 数据源
																								// ！！！注意若出现加载或者连接数据库失败一般是这里出现问题
						String Name = "root";
						String Pwd = "lwxwl11090308";

						try {

							Class.forName(driverName);
							connection = DriverManager.getConnection(dbURL, Name, Pwd);
							// System.out.println("连接数据库成功");

						} catch (Exception e) {

							e.printStackTrace();
							// System.out.println("连接失败");
						}
						Statement stmt = null;
						try {
							stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
						} catch (SQLException e) {
							//   TODO Auto-generated catch block
							e.printStackTrace();
						}
						stmt.execute(Sql);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						// sleep()：同步延迟数据，并且会阻塞线程
						Thread.sleep(timeInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		// 创建定时器
		Thread thread = new Thread(runnable1);
		// 开始执行
		thread.start();

		while (true) {
			Socket socket = server.accept();

			Runnable runnable = () -> {
				try {
					// 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
					InputStream inputStream = socket.getInputStream();
					byte[] bytes = new byte[1024];
					int len;
					StringBuilder sb = new StringBuilder();
					while ((len = inputStream.read(bytes)) != -1) {
						// 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
						sb.append(new String(bytes, 0, len, "UTF-8"));
					}
					// 处理传来的信息
					String getfromclient = "" + sb;
					String cut = " ";
					String[] newStr = getfromclient.split(cut);
					System.out.println("get message from client: " + sb + "");

					// 连接数据库
					String userId, seatId, type, time, areaname, psw, deptName, mail, phone;
					String oldpsw;
					String returnMessage = "";
					String Sql;
					String times;
					int deg;
					// Scanner scan=new Scanner(System.in);
					// 连接数据库
					Connection connection = null;
					String driverName = "com.mysql.cj.jdbc.Driver";// SQL数据库引擎
					String dbURL = "jdbc:mysql://localhost:3306/library1?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";// 数据源
					// ！！！注意若出现加载或者连接数据库失败一般是这里出现问题
					String Name = "root";
					String Pwd = "lwxwl11090308";

					try {

						Class.forName(driverName);
						connection = DriverManager.getConnection(dbURL, Name, Pwd);
						System.out.println("连接数据库成功");

					} catch (Exception e) {

						e.printStackTrace();
						System.out.println("连接失败");
					}
					Statement stmt = null;
					try {
						stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// 根据不同消息做出响应
					switch (newStr[0]) {
						case "Login":// id password
						{
							userId = newStr[1];
							psw = newStr[2];
							Sql = login(userId, psw);
							try {
								ResultSet rs = stmt.executeQuery(Sql);
								// System.out.print(rs.getNString(0));
								int rs_size = 0;
								while (rs.next())// 下一行是否不为空
									rs_size++;
								if (rs_size == 0)
									returnMessage = "Refuse";
								else if (userId.equals("100000000000"))
									returnMessage = "Admin";
								else
									returnMessage = "Accept";
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						case "Report":// id seatid type
						{
							try {
								userId = newStr[1];
								seatId = newStr[2];
								type = newStr[3];
								// 获取时间信息
								Date date = new Date();
								DateFormat df3 = DateFormat.getTimeInstance();// 只显示出时分秒
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Calendar cal = df.getCalendar();
								int hour = cal.get(Calendar.HOUR);// 小时
								int minute = cal.get(Calendar.MINUTE);// 分
								String curr = df3.format(date);
								if (curr.charAt(0) == '下')
									hour += 12;
								minute += hour * 60;
								time = Integer.toString(minute);

								Sql = bei_jv_bao(userId, seatId, type);
								ResultSet rs = stmt.executeQuery(Sql);
								// System.out.print(rs.getNString(0));
								int rs_size = 0;
								while (rs.next()) {
									rs_size++;
								}
								if (rs_size == 0) {
									rs = stmt.executeQuery("select*from seated where seatID='" + seatId + "';");
									if (!rs.next()) {
										returnMessage = "Refuse1";
									} else {
										String ab = jv_bao(userId, seatId, time, type);
										// System.out.println(ab);
										stmt.executeUpdate(ab);
										returnMessage = "Accept";
									}
								} else
									returnMessage = "Refuse";
								Sql = bie_ren_jv_bao(seatId, type);
								rs = stmt.executeQuery(Sql);
								rs.beforeFirst();
								rs.first();
								rs.absolute(0);
								rs_size = 0;
								while (rs.next()) {
									rs_size++;
								}
								rs.absolute(0);
								rs.beforeFirst();
								rs.next();
								if (rs_size >= 3) {
									Sql = "select userID from seated where seatID='" + seatId + "';";

									rs = stmt.executeQuery(Sql);
									rs.next();
									Sql = kou_fen(rs.getString(1));
									stmt.executeUpdate(Sql);
								}

							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						// 修改信息
						case "Update":// id deptname mail phone
						{
							userId = newStr[1];
							deptName = newStr[2];
							mail = newStr[3];
							phone = newStr[4];
							String sql_1 = xiu_gai_xin_xi(userId, deptName, mail, phone);
							int count_1 = 0;
							try {
								count_1 = stmt.executeUpdate(sql_1);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} // 返回值表示增删改几条数据
								// 处理结果
							if (count_1 > 0) {
								System.out.println("更新成功!");
							}
							returnMessage = "Accept";
						}
							break;
						// 查询信誉分
						case "Query":// id
						{
							userId = newStr[1];
							Sql = xin_yu_fen(userId);
							try {

								ResultSet rs = stmt.executeQuery(Sql);
								// System.out.print(rs.getNString(0));
								rs.next();
								returnMessage = rs.getString(1);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						// 修改密码
						case "Change":// id oldpassword newpassword
						{
							try {
								userId = newStr[1];
								oldpsw = newStr[2];
								psw = newStr[3];
								Sql = "select passwords from users where userID='" + userId + "';";
								ResultSet rs = stmt.executeQuery(Sql);
								rs.next();
								String temp = (String) rs.getString(1);
								boolean flag = true;
								int size1 = oldpsw.length();
								int size2 = temp.length();
								if (size1 == size2) {
									for (int i = 0; i < size1; i++)
										if (oldpsw.charAt(i) != temp.charAt(i))
											flag = false;
								} else
									flag = false;
								if (flag) {
									Sql = xiu_gai_mi_ma(userId, psw);
									stmt.executeUpdate(Sql);
									returnMessage = "Accept";
								} else
									returnMessage = "Refuse";
								// System.out.println("修改成功");

							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						// 预定
						case "Reserve":// id type areaname
						{
							try {
								userId = newStr[1];

								// seatId=newStr[2];
								deg = newStr[2].charAt(0) - '0';
								type = newStr[3];
								areaname = newStr[4];
								Sql = yu_ding_guo(userId, type, areaname);
								ResultSet rs = stmt.executeQuery(Sql);
								int rs_size = 0;

								while (rs.next()) {
									rs_size++;
								}
								if (rs_size == 0) {
									Sql = reserve(userId, type, deg, areaname);
									stmt.executeUpdate(Sql);
								} else {
									Sql = "delete from preference where userID='" + userId + "' and typename='" + type
											+ "' and areaname='" + areaname + "';";
									stmt.executeUpdate(Sql);
									Sql = reserve(userId, type, deg, areaname);
									stmt.executeUpdate(Sql);
								}
								returnMessage = "Accept";

							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;

						// 查看预定结果
						case "Check":// id
						{
							try {
								userId = newStr[1];
								Sql = cha_kan_yu_ding(userId);
								ResultSet rs = stmt.executeQuery(Sql);
								int rs_size = 0;
								while (rs.next())
									rs_size++;
								rs.beforeFirst();
								rs.next();
								if (rs_size != 0)
									returnMessage = rs.getString(2) + " " + rs.getString(1) + " " + rs.getString(3);
								else
									returnMessage = "Refuse";

							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						// 寻找空余座位
						case "Search":// id type areaname
						{
							// Random r=new Random(1);
							try {
								userId = newStr[1];
								type = newStr[2];
								areaname = newStr[3];
								Sql = kong_yu_zuo_wei(type, areaname);
								ResultSet rs = stmt.executeQuery(Sql);
								int rs_size = 0;
								while (rs.next()) {
									rs_size++;
								}
								// rs.getString(1);
								if (rs_size != 0) {
									long ran1 = System.currentTimeMillis();
									int ran = (int) (ran1 % rs_size);
									ran++;
									rs.first();

									int cnt = 1;
									while (cnt != ran) {
										cnt++;
										rs.next();
									}
									returnMessage = rs.getString(2) + " " + rs.getString(1);// +"
																							// "+Integer.toString(rs_size);
								} else
									returnMessage = "Refuse";
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;

						// 放弃预定座位
						case "Abandon":// +id
						{
							try {
								userId = newStr[1];
								Sql = fang_qi_yu_ding(userId);
								stmt.executeUpdate(Sql);
								Sql = "delete from seated where userID='" + userId + "' and arrived='0';";
								returnMessage = "Accept";
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						// 结束使用座位
						case "Finish":// +id
						{
							try {
								userId = newStr[1];
								Sql = "select seatID from seated where userID='" + userId + "' and arrived='1';";
								ResultSet rs = stmt.executeQuery(Sql);
								int rs_size = 0;
								while (rs.next())
									rs_size++;
								rs.beforeFirst();
								rs.next();
								if (rs_size == 0) {
									returnMessage = "Refuse";
								} else {
									seatId = rs.getString(1);
									Sql = "delete from report where seatID='" + seatId + "';";
									stmt.executeUpdate(Sql);
									Sql = "delete from seated where seatID='" + seatId + "';";
									stmt.executeUpdate(Sql);
									returnMessage = "Accept";
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						// 确认入座
						case "Confirm":// +id
						{
							try {
								userId = newStr[1];
								seatId = newStr[2];
								Sql = que_ren(userId);
								stmt.executeUpdate(Sql);
								// System.out.println("选座成功");
								Sql = "select*from seated where userID='" + userId + "';";
								ResultSet rs = stmt.executeQuery(Sql);
								int rs_size = 0;
								while (rs.next())
									rs_size++;
								if (rs_size == 0)
									returnMessage = "Refuse";
								else {
									Sql = "select*from seated where userID='" + userId + "' and seatId='" + seatId
											+ "';";
									rs = stmt.executeQuery(Sql);
									rs_size = 0;
									while (rs.next())
										rs_size++;
									if (rs_size == 0)
										returnMessage = "Wrong";
									else {
										Sql = "select*from seated where userID='" + userId + "' and seatId='" + seatId
												+ "' and arrived='0';";
										rs = stmt.executeQuery(Sql);
										rs_size = 0;
										while (rs.next())
											rs_size++;
										if (rs_size == 0)
											returnMessage = "Already";
										else {
											returnMessage = "Accept";
											stmt.execute("update seated set arrived=1 where userID='" + userId + "';");
										}
									}
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;

						case "GetMessage":// 返回 院系+邮箱+电话
						{
							try {
								userId = newStr[1];
								Sql = cha_xin_xi(userId);
								ResultSet rs = stmt.executeQuery(Sql);
								rs.beforeFirst();
								rs.next();
								returnMessage = rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						case "Seat":// Seat userId seatId
						{
							try {
								// 获取当前时间
								Date date = new Date();
								DateFormat df3 = DateFormat.getTimeInstance();// 只显示出时分秒
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Calendar cal = df.getCalendar();
								int hour = cal.get(Calendar.HOUR);// 小时
								int minute = cal.get(Calendar.MINUTE);// 分
								String curr = df3.format(date);
								if (curr.charAt(0) == '下')
									hour += 12;
								minute += hour * 60;
								time = Integer.toString(minute);

								userId = newStr[1];
								seatId = newStr[2];
								returnMessage = seating(userId, seatId, time, stmt);

							} catch (SQLException e) {
								e.printStackTrace();
							}
						}

							break;
						case "CheckSeat": {
							try {
								seatId = newStr[1];
								Sql = check_seatId(seatId);
								ResultSet rs = stmt.executeQuery(Sql);
								int rs_size = 0;
								while (rs.next()) {
									rs_size++;
								}
								if (rs_size == 0)
									returnMessage = "Refuse";
								else
									returnMessage = "Accept";
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						case "Distribute":// 把当前偏好表依照信誉分分配座位
						{
							try {
								Sql = xin_yu_fen_he_pian_hao();
								ResultSet rs1 = stmt.executeQuery(Sql);
								Sql = huo_qv_zuo_wei();
								Statement stmt2 = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
										ResultSet.CONCUR_READ_ONLY);
								ResultSet rs2 = stmt2.executeQuery(Sql);
								Sql = huo_qv_qv_yu();
								Statement stmt3 = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
										ResultSet.CONCUR_READ_ONLY);
								ResultSet rs3 = stmt3.executeQuery(Sql);
								Sql = fen_pei(rs1, rs2, rs3);
								Sql += "\ndelete from preference";
								stmt.executeUpdate(Sql);
								// returnMessage=Sql;
								returnMessage = "Accept";
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						case "CheckReport": {
							try {
								Sql = check_report();
								ResultSet rs = stmt.executeQuery(Sql);
								returnMessage = "";
								int count = 0;
								while (rs.next()) {
									if (count >= 50)
										break;
									count++;
									String temp = rs.getString(2);
									switch (temp) {
										case "吵闹":
											temp = "0";
											break;
										case "吃东西":
											temp = "1";
											break;
										case "打游戏/睡觉":
											temp = "2";
											break;
										case "以物占座":
											temp = "3";
											break;
										case "其他":
											temp = "4";
											break;
										default:
											temp = "5";
											break;
									}
									returnMessage += " " + rs.getString(1) + " " + temp + " " + rs.getString(3);
								}
								returnMessage = Integer.toString(count) + returnMessage;
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;
						// 以下是6.26 新开内容
						case "Area": {
							try {
								Sql = qv_yu_ren_shu();
								ResultSet rs = stmt.executeQuery(Sql);
								int count = 0;
								while (rs.next()) {
									count++;
									returnMessage += " " + rs.getString(1) + " " + rs.getString(2);
								}
								returnMessage = Integer.toString(count) + returnMessage;
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
							break;

						case "ChangeStatus":// 不需要调用函数
						{
							if (virus == 0) {
								virus = 1;
								returnMessage = "已开启防疫模式";
							} else {
								virus = 0;
								returnMessage = "已关闭防疫模式";
							}
							// returnMessage+=Integer.toString(virus);
						}
							break;
						case "Touch":// id
						{
							userId = newStr[1];
							int sz = userId.length();
							Sql = "select*from area";
							ResultSet rs = stmt.executeQuery(Sql);
							Hashtable<String, Integer> hash = new Hashtable();
							/*
							 * //也可以选择打表 hash.put("自然科学综合图书书库单人", 1);hash.put("自然科学综合图书书库双人",
							 * 2);hash.put("自然科学综合图书书库多人", 3); hash.put("工业技术专业书库单人",
							 * 4);hash.put("工业技术专业书库双人", 5);hash.put("工业技术专业书库多人", 6);
							 * hash.put("法律经济专业书库单人", 7);hash.put("法律经济专业书库双人", 8);hash.put("法律经济专业书库多人",
							 * 9); hash.put("社会科学综合书库单人", 10);hash.put("社会科学综合书库双人",
							 * 11);hash.put("社会科学综合书库多人", 12); hash.put("文学、艺术、历史专业书库单人",
							 * 13);hash.put("文学、艺术、历史专业书库双人", 14);hash.put("文学、艺术、历史专业书库多人", 15);
							 * hash.put("自然科学报刊阅览室单人", 16);hash.put("自然科学报刊阅览室双人",
							 * 17);hash.put("自然科学报刊阅览室多人", 18); hash.put("社会科学报刊阅览室单人",
							 * 19);hash.put("社会科学报刊阅览室双人", 20);hash.put("社会科学报刊阅览室多人", 21);
							 * hash.put("医学专业书库单人", 22);hash.put("医学专业书库双人", 23);hash.put("医学专业书库多人", 24);
							 */
							int cnt = 1;
							while (rs.next()) {
								hash.put(rs.getString(1) + rs.getString(2), cnt);
								cnt++;
							}
							Sql = "select*from user_area";
							rs = stmt.executeQuery(Sql);
							int rs_size = 0;
							Hashtable<String, Integer> user_hash = new Hashtable();

							int user = -1;
							boolean flag = true;
							String temp;
							while (rs.next()) {
								rs_size++;

								if (user_hash.get(rs.getString(1)) == null) {
									user_hash.put(rs.getString(1), cnt);
									// returnMessage+=rs.getString(1)+" ";
									temp = rs.getString(1);
									flag = true;
									for (int i = 0; i < temp.length() && i < sz; i++) {
										if (temp.charAt(i) != userId.charAt(i)) {
											flag = false;
											break;
										}
									}
									if (flag)
										user = cnt;
									cnt++;
								}
							}
							mu = new MergeUnion(cnt);
							rs.beforeFirst();
							while (rs.next()) {
								if (hash.get(rs.getString(2) + rs.getString(3)) != null)
									mu.merge((int) (user_hash.get(rs.getString(1))),
											(int) (hash.get(rs.getString(2) + rs.getString(3))));
							}
							rs.beforeFirst();
							Iterator iter = user_hash.entrySet().iterator();
							String key;

							// returnMessage=Integer.toString(rs_size);
							if (user != -1) {

								int us_id = user;
								int num = 0;
								while (iter.hasNext()) {
									Map.Entry entry = (Map.Entry) iter.next();
									key = (String) entry.getKey();
									flag = true;
									for (int i = 0; i < key.length() && i < userId.length(); i++) {
										if (userId.charAt(i) != key.charAt(i)) {
											flag = false;
											break;
										}
									}
									if (flag)
										continue;
									if (mu.find((int) user_hash.get(key)) == mu.find(us_id)) {
										returnMessage += " " + key;
										num++;
									}
								}
								returnMessage = Integer.toString(num) + returnMessage;
							} else
								returnMessage = "0";
						}
							break;

					}
					// 关闭
					{
						try {
							stmt.close();
							// System.out.print("正常关闭");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							connection.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					System.out.println("send back message to client: " + returnMessage + "");
					// 将StringBuilder sb拆分出 id和 password然后查询数据库，如果匹配，发回“Accept”，不匹配发货“Refuse”
					OutputStream outputStream = socket.getOutputStream();

					outputStream.write(returnMessage.getBytes("UTF-8"));
					inputStream.close();
					outputStream.close();
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
			threadPool.submit(runnable);
		}
	}
}
