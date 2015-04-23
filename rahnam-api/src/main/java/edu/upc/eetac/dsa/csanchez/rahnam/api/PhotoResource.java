package edu.upc.eetac.dsa.csanchez.rahnam.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import edu.upc.eetac.dsa.csanchez.rahnam.api.DataSourceSPA;
import edu.upc.eetac.dsa.csanchez.rahnam.api.model.Photo;


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
	
	
}
