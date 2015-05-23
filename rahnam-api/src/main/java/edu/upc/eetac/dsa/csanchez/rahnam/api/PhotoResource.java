package edu.upc.eetac.dsa.csanchez.rahnam.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import edu.upc.eetac.dsa.csanchez.rahnam.api.DataSourceSPA;
import edu.upc.eetac.dsa.csanchez.rahnam.api.model.Photo;
import edu.upc.eetac.dsa.csanchez.rahnam.api.model.PhotoCollection;


@Path("/photos")
public class PhotoResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	@Context
	private Application app;
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Photo uploadImage(@FormDataParam("username") String username,
			@FormDataParam("title") String title,
			@FormDataParam("description") String description,
			@FormDataParam("image") InputStream image,
			@FormDataParam("image") FormDataContentDisposition fileDisposition) {
		
		UUID uuid = writeAndConvertImage(image);
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("insert into photos (photoid, username, title, description) "
					+ "values (?,?,?,?)");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, username);
			stmt.setString(3, title);
			stmt.setString(4, description);
			
			stmt.executeUpdate();
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
		Photo imageData = new Photo();

		imageData.setUsername(username);
		imageData.setTitle(title);
		imageData.setDescription(description); 
		imageData.setFilename(uuid.toString() + ".png");
		imageData.setPhotoURL(app.getProperties().get("imgBaseURL")
				+ imageData.getFilename());

		return imageData;
	}
	
	
	
	
	private UUID writeAndConvertImage(InputStream file) {

		BufferedImage image = null;
		try {
			image = ImageIO.read(file);

		} catch (IOException e) {
			throw new InternalServerErrorException(
					"Something has been wrong when reading the file.");
		}
		UUID uuid = UUID.randomUUID();
		String filename = uuid.toString() + ".png";
		try {
			ImageIO.write(image,"png",new File(app.getProperties().get("uploadFolder") + filename));
		} catch (IOException e) {
			throw new InternalServerErrorException(
					"Something has been wrong when converting the file.");
		}

		return uuid;
	}
	
	
	
	
	
	@GET
	@Path("/photo/{idphoto}")
	public Photo getPhoto(@PathParam("idphoto") String photoid) {
		Photo photo = new Photo();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("select * from photos where photoid = ?");
			stmt.setString(1,photoid);
			stmt.executeQuery();

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				photo.setPhotoid(rs.getString("photoid") + ".png");
				photo.setUsername(rs.getString("username"));
				photo.setTitle(rs.getString("title"));
				photo.setDescription(rs.getString("description"));
				photo.setCrationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				photo.setLast_modified(rs.getTimestamp("last_modified").getTime());
				photo.setFilename(photo.getPhotoid());
				photo.setPhotoURL(app.getProperties().get("imgBaseURL")+ photo.getFilename());
				
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
		return photo;
	}
	
	
	
	@GET
	@Path("/user/{username}")
	public PhotoCollection getPhotosByUser(@PathParam("username") String username) {
		PhotoCollection images = new PhotoCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("select * from photos where username = ?");
			stmt.setString(1,username);
			stmt.executeQuery();

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Photo photo = new Photo();
				photo.setPhotoid(rs.getString("photoid") + ".png");
				photo.setUsername(rs.getString("username"));
				photo.setTitle(rs.getString("title"));
				photo.setDescription(rs.getString("description"));
				photo.setCrationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				photo.setLast_modified(rs.getTimestamp("last_modified").getTime());
				photo.setFilename(photo.getPhotoid());
				photo.setPhotoURL(app.getProperties().get("imgBaseURL")+ photo.getFilename());
				images.addPhoto(photo);
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
		return images;
	}
	
	
	
	
	@GET
	@Path("/category/{category}")
	public PhotoCollection getPhotosByCategory(@PathParam("category") String category) {
		PhotoCollection images = new PhotoCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("select * from photos where photoid IN "
					+ "(select photoid from photoscategories where categoryid = "
					+ "(select categoryid from categories where name = ?))");
			stmt.setString(1,category);
			stmt.executeQuery();

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Photo photo = new Photo();
				photo.setPhotoid(rs.getString("photoid") + ".png");
				photo.setUsername(rs.getString("username"));
				photo.setTitle(rs.getString("title"));
				photo.setDescription(rs.getString("description"));
				photo.setCrationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				photo.setLast_modified(rs.getTimestamp("last_modified").getTime());
				photo.setFilename(photo.getPhotoid());
				photo.setPhotoURL(app.getProperties().get("imgBaseURL")+ photo.getFilename());
				images.addPhoto(photo);
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
		return images;
	}
	
	
	@GET
	@Path("/title/{title}")
	public PhotoCollection getPhotosByTitle(@PathParam("title") String title) {
		PhotoCollection images = new PhotoCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("select * from photos where title like ?");
			stmt.setString(1, "%" + title + "%");
			stmt.executeQuery();

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Photo photo = new Photo();
				photo.setPhotoid(rs.getString("photoid") + ".png");
				photo.setUsername(rs.getString("username"));
				photo.setTitle(rs.getString("title"));
				photo.setDescription(rs.getString("description"));
				photo.setCrationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				photo.setLast_modified(rs.getTimestamp("last_modified").getTime());
				photo.setFilename(photo.getPhotoid());
				photo.setPhotoURL(app.getProperties().get("imgBaseURL")+ photo.getFilename());
				images.addPhoto(photo);
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
		return images;
	}
	
	@DELETE
	public void deletePhoto(@QueryParam("photoid") String photoid){
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("delete from photos where photoid = ?");
			stmt.setString(1, photoid);
	 
			int rows = stmt.executeUpdate();
			if (rows == 0)
				;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
	
	
	
	
	
	
	
	
}
