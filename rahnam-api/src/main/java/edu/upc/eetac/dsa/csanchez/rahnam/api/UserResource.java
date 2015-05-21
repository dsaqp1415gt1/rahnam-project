package edu.upc.eetac.dsa.csanchez.rahnam.api;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import edu.upc.eetac.dsa.csanchez.rahnam.api.model.User;



@Path("/users")
public class UserResource {
	
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	
	private final static String GET_USER_BY_USERNAME_QUERY = "select * from users where username=?";
	private final static String INSERT_USER_INTO_USERS = "insert into users (username, userpass,"
			+ "name, gender, avatar) values(?, MD5(?), ?, ?, NULL)";
	private final static String INSERT_USER_INTO_USER_ROLES = "insert into user_roles values (?, 'registered')";
	
	//Crear Usario
	
	@POST
	@Consumes(MediaType.RAHNAM_API_USER)
	@Produces(MediaType.RAHNAM_API_USER)
	public User createUser(User user) {
		
		validateUser(user);
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		
		PreparedStatement stmtGetUsername = null;
		PreparedStatement stmtInsertUserIntoUsers = null;
				
		try {
			stmtGetUsername = conn.prepareStatement(GET_USER_BY_USERNAME_QUERY);
			stmtGetUsername.setString(1, user.getUsername());
			 
			ResultSet rs = stmtGetUsername.executeQuery();
			if (rs.next())
				throw new WebApplicationException(user.getUsername()
						+ " this username already exists.", Status.CONFLICT);
			rs.close();
			conn.setAutoCommit(false);
			stmtInsertUserIntoUsers = conn
					.prepareStatement(INSERT_USER_INTO_USERS);
		
			
			stmtInsertUserIntoUsers.setString(1, user.getUsername());
			stmtInsertUserIntoUsers.setString(2, user.getUserpass());
			stmtInsertUserIntoUsers.setString(3, user.getName());
			//stmtInsertUserIntoUsers.setString(4, user.getEmail());
			//stmtInsertUserIntoUsers.setDate(5, (Date) user.getBirth());
			stmtInsertUserIntoUsers.setString(4, user.getGender());
			stmtInsertUserIntoUsers.executeUpdate();
			
		
			conn.commit();	
		}
		
		catch (SQLException e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (SQLException e1) {
				}
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		finally {
			try {
				if (stmtGetUsername != null)
					stmtGetUsername.close();
				if (stmtInsertUserIntoUsers != null)
					stmtGetUsername.close();
				conn.setAutoCommit(true);
				conn.close();
			} catch (SQLException e) {
			}
		}
		
	user.setUserpass(null);
	return user;
	
			}
	
	private void validateUser(User user) {
		if (user.getUsername() == null)
			throw new BadRequestException("Username cannot be null.");
		if (user.getUserpass() == null)
			throw new BadRequestException("Password cannot be null.");
		if (user.getName() == null)
			throw new BadRequestException("Name cannot be null.");
		if (user.getGender() == null)
		throw new BadRequestException("Gender cannot be null.");
	}
	
	@Path("/login")
	@POST
	@Produces(MediaType.RAHNAM_API_USER)
	@Consumes(MediaType.RAHNAM_API_USER)
	public User login(User user) {
		if (user.getUsername() == null || user.getUserpass() == null)
			throw new BadRequestException(
					"username and password cannot be null.");
 
		String pwdDigest = DigestUtils.md5Hex(user.getUserpass());
		String storedPwd = getUserFromDatabase(user.getUsername(), true)
				.getUserpass();
 
		user.setLoginSuccessful(pwdDigest.equals(storedPwd));
		user.setUserpass(null);
		return user;
	}
	
		
		private User getUserFromDatabase(String username, boolean pass) {
			User user = new User();
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
	 
			PreparedStatement stmt = null;
			
			try {
				stmt = conn.prepareStatement(GET_USER_BY_USERNAME_QUERY);
				stmt.setString(1, username);
				
	 
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					user.setUsername(rs.getString("username"));
					if (pass)
						user.setUserpass(rs.getString("userpass"));
						user.setName(rs.getString("name"));
						user.setGender(rs.getString("gender"));
				} else
					throw new NotFoundException(username + " not found.");
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			} finally {
				try {
					if (stmt != null)
						stmt.close();
					conn.close();
				} catch (SQLException e) {
				}
			}
	 
		
		user.setUserpass(null);
		return user;
	}
	
}
