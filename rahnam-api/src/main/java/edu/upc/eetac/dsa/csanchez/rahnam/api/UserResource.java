package edu.upc.eetac.dsa.csanchez.rahnam.api;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import edu.upc.eetac.dsa.csanchez.rahnam.api.model.User;

@Path("/users")
public class UserResource {
	
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();	
	

	@Context
	private SecurityContext security;
	

	@POST
	@Consumes(MediaType2.RAHNAM_API_USER)
	@Produces(MediaType2.RAHNAM_API_USER)
	public User createUser(User user) {
		
		validateUser(user);
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		
		PreparedStatement stmtGetUser = null;
		PreparedStatement stmtInsertUserIntoUsers = null;
		PreparedStatement stmtInsertUserIntoUserRoles = null;
				
		try {
			stmtGetUser = conn.prepareStatement("select * from users where username=?");
			stmtGetUser.setString(1, user.getUsername());
			 
			ResultSet rs = stmtGetUser.executeQuery();
			if (rs.next())
				throw new WebApplicationException(user.getUsername()
						+ " this username already exists.", Status.CONFLICT);
			rs.close();
			conn.setAutoCommit(false);
			
			stmtInsertUserIntoUsers = conn
					.prepareStatement("insert into users (username, userpass,"
			+ "name, email, birth, gender, avatar) values(?, MD5(?), ?, ?, ?, ?, NULL)");
		
			stmtInsertUserIntoUsers.setString(1, user.getUsername());
			stmtInsertUserIntoUsers.setString(2, user.getUserpass());
			stmtInsertUserIntoUsers.setString(3, user.getName());
			stmtInsertUserIntoUsers.setString(4, user.getEmail());
			stmtInsertUserIntoUsers.setDate(5, (Date) user.getBirth());
			stmtInsertUserIntoUsers.setString(6, user.getGender());
			stmtInsertUserIntoUsers.executeUpdate();
			
			stmtInsertUserIntoUserRoles = conn
					.prepareStatement("insert into user_roles values (?, 'registered')");
			
			stmtInsertUserIntoUserRoles.setString(1, user.getUsername());
			stmtInsertUserIntoUserRoles.executeUpdate();
			
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
				if (stmtGetUser != null)
					stmtGetUser.close();
				if (stmtInsertUserIntoUsers != null)
					stmtInsertUserIntoUsers.close();
				if (stmtInsertUserIntoUserRoles != null)
					stmtInsertUserIntoUserRoles.close();
				conn.setAutoCommit(true);
				conn.close();
			} catch (SQLException e) {
			}
		}
	user.setUserpass(null);
	return user;
	}
	
	private void validateUser(User user) {
		if ((user.getUsername() == null)||(user.getUsername().length() > 20))
			throw new BadRequestException("Username cannot be null or greater than 20 characters");
		if ((user.getUserpass() == null)||(user.getUserpass().length() > 80))
			throw new BadRequestException("Userpass null. Otherwise try a shorter password");
		if ((user.getName() == null)||(user.getName().length() > 20))
			throw new BadRequestException("Name cannot be null or greater than 20 characters");
		if ((user.getEmail() == null)||(user.getEmail().length() > 20))
			throw new BadRequestException("email cannot be null or greater than 20 characters");
		if ((user.getGender() != null)&&(user.getGender().length() > 20))
			throw new BadRequestException("Gender cannot be greater than 20 characters");
	}
	
	
	@Path("/login")
	@POST
	@Produces(MediaType2.RAHNAM_API_USER)
	@Consumes(MediaType2.RAHNAM_API_USER)
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
			stmt = conn.prepareStatement("select * from users where username=?");
			stmt.setString(1, username);
			
 
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setUsername(rs.getString("username"));
				if (pass)
					user.setUserpass(rs.getString("userpass"));
				user.setName(rs.getString("name"));
				user.setAvatar(rs.getInt("avatar"));
				user.setEmail(rs.getString("email"));
				user.setBirth(rs.getDate("birth"));
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
		return user;
	}
	
	//Delete user
		
		
	@DELETE
	@Path("/{username}")
	public String deleteUser(@PathParam("username") String username) {
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("delete from users where username=?");
			stmt.setString(1, username);
	 
			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no user with username = "
						+ username);
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
		return ("Deleted user!");
	}

		
//Get user
		
	@GET
	@Path("/{username}")
	@Produces(MediaType2.RAHNAM_API_USER)
	public User getUser(@PathParam("username") String username) {

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
			stmt = conn.prepareStatement("select * from users where username=?");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setUsername(rs.getString("username"));
				user.setUserpass(rs.getString("userpass"));
				user.setName(rs.getString("name"));
				user.setAvatar(rs.getInt("avatar"));
				user.setEmail(rs.getString("email"));
				user.setBirth(rs.getDate("birth"));
				user.setGender(rs.getString("gender"));					
				
			} else {
				throw new NotFoundException(
						"There's no user with username = " + username);
			}
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
		
//Edit User
		
		
	@PUT
	@Path("/{username}")
	@Consumes(MediaType2.RAHNAM_API_USER)
	@Produces(MediaType2.RAHNAM_API_USER)
	public User updateUser(@PathParam("username") String username, User user) {
		
		//validateUpdateUser(user);
		//validateUser(username);
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("update users set userpass=ifnull( MD5(?), userpass),"
					+ " name=ifnull(?, name),email=ifnull(?, email), gender=ifnull(?,gender) where username=?");
			
			stmt.setString(1, user.getUserpass());
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getGender());
			stmt.setString(5,security.getUserPrincipal().getName());
	 
			int rows = stmt.executeUpdate();
			if (rows == 1)
				user = getUser(username);
			else {
				throw new NotFoundException("There's no username such as = "
						+ username);
			}
	 
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
		
	private void validateUpdateUser(User user) {
		if (user.getUserpass().length() > 80)
			throw new BadRequestException("password can't be null or greater than 80 characters.");
		if (user.getName().length() > 20)
			throw new BadRequestException("Name can't be null or greater than 100 characters.");
		if (user.getEmail().length() > 20)
			throw new BadRequestException("email cannot be null or greater than 20 characters");
		if (user.getGender().length() > 20)
			throw new BadRequestException("Gender cannot be null or greater than 20 characters");
	}
		
}
