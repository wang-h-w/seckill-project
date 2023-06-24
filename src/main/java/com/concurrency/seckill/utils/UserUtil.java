package com.concurrency.seckill.utils;

import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setNickname("user_" + i);
            user.setSalt("1a2b3c4d");
            user.setPassword(Md5Util.inputPassToDBPass("123456", user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }

        // 使用资源绑定器绑定属性配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("database"); // 只能找类文件夹中的属性文件，不能加.properties后缀
        String driver = bundle.getString("driver");
        String r = bundle.getString("url");
        String u = bundle.getString("user");
        String password = bundle.getString("password");
        String sql = "insert into t_user(login_count, nickname, register_date, salt, password, id) values(?, ?, ?, ?, ?, ?)";

        // 1. 注册驱动（不需要返回值，我们只想要类加载的这个动作）
        Class.forName(driver); // 调用driver的静态代码块，反射机制一定会调用
        // 2. 获取连接
        Connection conn = DriverManager.getConnection(r, u, password);
        // 3. 获取数据库操作对象
        PreparedStatement ps = conn.prepareStatement(sql);
        // 4. 执行sql语句
        for (User user : users) {
            ps.setInt(1, user.getLoginCount());
            ps.setString(2, user.getNickname());
            ps.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            ps.setString(4, user.getSalt());
            ps.setString(5, user.getPassword());
            ps.setLong(6, user.getId());
            ps.addBatch();
        }
        ps.executeBatch();

        ps.close();
        conn.close();

        // 生成userTicket的记录文件
        String urlString = "http://10.211.55.4:8080/seckill/login/doLogin";
        File file = new File("/Users/wang.h.w/Desktop/CS/java-projects/concurrency/jmeter/seckill/config.txt");
        if (file.exists()) file.delete();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (User user : users) {
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + Md5Util.inputPassToFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream input = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = input.read(buff)) >= 0) bout.write(buff, 0, len);
            input.close();
            bout.close();
            String response = bout.toString();
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
        }
        raf.close();
        System.out.println("complete");
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
