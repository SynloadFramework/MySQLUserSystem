package com.synload.mySQLUserSystem.model;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.annotations.sql.*;
import com.synload.framework.sql.Model;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@SQLTable(name = "User Model", version = 1.3, description = "users table contains passwords and emails")
public class User extends Model {

    @BigIntegerColumn(length = 20, Key = true, AutoIncrement = true)
    public long id;

    @BigIntegerColumn(length = 11)
    public long created_date;

    @StringColumn(length = 128)
    public String username;

    @JsonIgnore
    @StringColumn(length = 128)
    public String email;

    @JsonIgnore
    @StringColumn(length = 128)
    public String name;

    @LongBlobColumn()
    public String flags;

    @BooleanColumn()
    public boolean admin;

    @JsonIgnore
    @StringColumn(length = 255)
    public String password;

    @HasMany(of=Session.class, key="id")
    @LongBlobColumn()
    public String sessions;
    
    // public User(){}
    /*
     * public User(ResultSet rs){ try {
     * this.setUsername(rs.getString("username")); this.flags =
     * rs.getString("flags"); this.password = rs.getString("password");
     * this.setEmail(rs.getString("email"));
     * this.setAdmin(rs.getBoolean("admin"));
     * this.setCreatedDate(rs.getLong("created_date"));
     * this.setId(rs.getLong("id")); rs.close(); } catch (SQLException e) {
     * if(SynloadFramework.debug){ e.printStackTrace(); } } }
     */
    public User(ResultSet rs) {
        super(rs);
    }

    public User(Object... data) {
        super(data);
    }

    /*
     * public User(String username, String password, String email, List<String>
     * flags, int admin){ this.setUsername(username.toLowerCase());
     * this.setPassword(password); this.setEmail(email); this.setFlags(flags);
     * try{ PreparedStatement s = SynloadFramework.sql.prepareStatement(
     * "INSERT INTO `users` ( `username`, `password`, `email`, `flags`, `created_date`,`admin`) VALUES ( ?, ?, ?, ?, UNIX_TIMESTAMP(), ? )"
     * ); s.setString(1, this.getUsername()); s.setString(2,
     * this.getPassword()); s.setString(3, this.getEmail()); s.setString(4,
     * this.getFlags().toString()); s.setInt(5, admin); s.execute(); s.close();
     * User m = User.findUser(this.getUsername()); if(m!=null){
     * this.setId(m.getId()); }else{
     * System.out.println("[ERROR] Registration error!"); } //return
     * query.setParameter("user", ).getResultList().size(); }catch(Exception e){
     * if(SynloadFramework.debug){ e.printStackTrace(); } } }
     */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getFlags() {
        return new ArrayList<String>(Arrays.asList(flags.replace("]", "")
                .replace("[", "").split(",")));
    }

    public boolean hasFlag(String flag) {
        ArrayList<String> flagsV = new ArrayList<String>(Arrays.asList(flags
                .replace("]", "").replace("[", "").split(",")));
        return flagsV.contains(flag);
    }

    public void setFlags(List<String> flags) {
        this.flags = flags.toString();
    }

    public void addFlags(String flag) {
        ArrayList<String> flagsV = new ArrayList<String>(Arrays.asList(flags
                .replace("]", "").replace("[", "").split(",")));
        flagsV.add(flag);
        flags = flagsV.toString();
    }

    public boolean isAdmin() {
        return admin;
    }

    public long getCreatedDate() {
        return created_date;
    }

    public void setCreatedDate(long createdDate) {
        this.created_date = createdDate;
    }

    public void setAdmin(boolean admin) throws IllegalArgumentException, IllegalAccessException, SQLException {
        this.admin = admin;
        this._save("admin", admin);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String Password) throws IllegalArgumentException, IllegalAccessException, SQLException, NoSuchAlgorithmException {
        String hashedPass = User.hashGenerator(Password);
        this._save("password", hashedPass);
        this.password = hashedPass;
    }

    public long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public String getSessions() {
        return sessions;
    }

    public void setSessions(String sessions) {
        this.sessions = sessions;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean passwordMatch(String Password) {
        String hashedPass = "";
        try {
            hashedPass = User.hashGenerator(Password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (hashedPass.equals(password)) {
            return true;
        }
        return false;
    }

    public static boolean existsUser(String user) {
    	return User._exists(User.class, "username=?", user);
    }

    public static List<User> all(int page, int limit) throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, ClassNotFoundException, InvocationTargetException, SQLException {
    	page = page*limit;
    	List<User> users =User._find(User.class, "").limit(limit+" OFFSET "+page).exec(User.class);
    	return users;
    }

    public static User findUser(String user) throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, ClassNotFoundException, InvocationTargetException, SQLException {
    	List<User> users = User._find(User.class, "username=?", user.toLowerCase()).exec(User.class);
    	if(users.size()>0){
    		return users.get(0);
    	}else{
    		return null;
    	}
    }

    public static User findUserSession(String uuid, String ip) throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, ClassNotFoundException, InvocationTargetException, SQLException {
    	List<Session> sessions = Session._find(Session.class, "session=? AND ip=?", uuid, ip).exec(Session.class);
    	if(sessions.size()>0){
    		return findUser(sessions.get(0).getUser());
    	}else{
    		return null;
    	}
    }

    public static User findVerifySession(String uuid) throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, ClassNotFoundException, InvocationTargetException, SQLException {
    	List<Session> sessions = Session._find(Session.class, "session=?", uuid).exec(Session.class);
    	if(sessions.size()>0){
    		return findUser(sessions.get(0).getUser());
    	}else{
    		return null;
    	}
    }

    public static User findUser(long uid) throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, ClassNotFoundException, InvocationTargetException, SQLException {
    	List<User> foundUsers = User._find(User.class, "id=?", uid).exec(User.class);
    	if(foundUsers.size()>0){
    		return foundUsers.get(0);
    	}else{
    		return null;
    	}
    }

    public void saveUserEmail(String email) throws IllegalArgumentException, IllegalAccessException, SQLException {
    	this._save("email", email);
    	this.setEmail(email);
    }

    public void deleteUserSession(String ip, String uuid) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException, NoSuchMethodException, SecurityException, ClassNotFoundException, InvocationTargetException {
    	Session s = Session._find(Session.class, "ip=? AND session=? AND user=?", ip, uuid, id).exec(Session.class).get(0);
    	s._delete();
    }

    public void saveUserSession(String ip, String uuid) throws IllegalArgumentException, IllegalAccessException, SQLException {
    	Session s = new Session();
    	s.setIp(ip);
    	s.setSession(uuid);
    	s.setUser(id);
    	s._insert();
    }

    public static String hashGenerator(String Password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(Password.getBytes());
        byte byteData[] = md.digest();
        // convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return sb.toString();
    }
}