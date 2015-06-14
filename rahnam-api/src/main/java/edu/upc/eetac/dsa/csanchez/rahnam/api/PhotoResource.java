package edu.upc.eetac.dsa.csanchez.rahnam.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import edu.upc.eetac.dsa.csanchez.rahnam.api.DataSourceSPA;
import edu.upc.eetac.dsa.csanchez.rahnam.api.model.Category;
import edu.upc.eetac.dsa.csanchez.rahnam.api.model.CategoryCollection;
import edu.upc.eetac.dsa.csanchez.rahnam.api.model.Comment;
import edu.upc.eetac.dsa.csanchez.rahnam.api.model.CommentCollection;
import edu.upc.eetac.dsa.csanchez.rahnam.api.model.Photo;
import edu.upc.eetac.dsa.csanchez.rahnam.api.model.PhotoCollection;


@Path("/photos")
public class PhotoResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	@Context
	private Application app;
	
	@Context
	private SecurityContext security;
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType2.RAHNAM_API_PHOTO)
	public Photo uploadImage(@FormDataParam("username") String username,
			@FormDataParam("title") String title,
			@FormDataParam("description") String description,
			@FormDataParam("category") String category,
			@FormDataParam("image") InputStream image,
			@FormDataParam("image") FormDataContentDisposition fileDisposition) {
		
		validatePhoto(username,title,description);
		
		UUID uuid = writeAndConvertImage(image);
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		try {
			conn.setAutoCommit(false);
			
			stmt = conn.prepareStatement("insert into photos (photoid, username, title, description) "
					+ "values (?,?,?,?)");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, security.getUserPrincipal().getName() );
			//stmt.setString(2, username);
			stmt.setString(3, title);
			stmt.setString(4, description);
			stmt.executeUpdate();
			
			stmt2 = conn.prepareStatement("insert into photoscategories values (?,"
					+ "(select categoryid from categories where name = ?))");
			stmt2.setString(1, uuid.toString());
			stmt2.setString(2, category);
			stmt2.executeUpdate();
			
			conn.commit();
			
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		Photo imageData = new Photo();
		
		imageData.setPhotoid(uuid.toString());
		imageData.setUsername(security.getUserPrincipal().getName());
		//imageData.setUsername(username);
		imageData.setTitle(title);
		imageData.setDescription(description); 
		imageData.setFilename(uuid.toString() + ".png");
		imageData.setPhotoURL(app.getProperties().get("imgBaseURL")
				+ imageData.getFilename());
		imageData.setCategories(getCategories(uuid.toString()));
		
		return imageData;
	}
	
	private void validatePhoto(String username, String title, String description) {
		if ((username == null)||(username.length() > 20))
			throw new BadRequestException("username can't be null o greater than 20 characters");
		if ((title == null)||(title.length() > 20))
			throw new BadRequestException("username can't be null o greater than 20 characters");
		if ((description== null)||(description.length() > 20))
			throw new BadRequestException("description can't be null o greater than 500 characters");
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
	@Path("/photo/{photoid}")
	@Produces(MediaType2.RAHNAM_API_PHOTO)
	public Photo getPhoto(@PathParam("photoid") String photoid) {
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
			stmt = conn.prepareStatement("select * from photos where photoid = ? order by creationTimestamp");
			stmt.setString(1,photoid);
			stmt.executeQuery();

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				photo.setPhotoid(rs.getString("photoid"));
				photo.setUsername(rs.getString("username"));
				photo.setTitle(rs.getString("title"));
				photo.setDescription(rs.getString("description"));
				photo.setCreationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				photo.setLast_modified(rs.getTimestamp("last_modified").getTime());
				photo.setFilename(rs.getString("photoid") + ".png");
				photo.setPhotoURL(app.getProperties().get("imgBaseURL")+ photo.getFilename());
				
				photo.setCategories(getCategories(photoid));
				
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
	
	
	private List<Category> getCategories(String photoid) {
		List<Category> cats = new ArrayList<Category>();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("select * from categories where categoryid IN "
					+ "(select categoryid from photoscategories where photoid = ?)");
			stmt.setString(1, photoid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Category cat = new Category();
				cat.setCategoryid(rs.getInt("categoryid"));
				cat.setName(rs.getString("name"));
				cats.add(cat);
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
		return cats;

	}
	
	
	
	@GET
	@Path("/categories")
	@Produces(MediaType2.RAHNAM_API_CATEGORY_COLLECTION)
	public CategoryCollection getCategoriesCollection(String nombre){
		
		CategoryCollection categories = new CategoryCollection();
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("select * from categories");
			//stmt.setString(1, nombre);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Category category = new Category();
				category.setCategoryid(rs.getInt("categoryid"));
				category.setName(rs.getString("name"));
				categories.addCategory(category);
			}
			
		}catch (SQLException e) {
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
	return categories;
	}

	
		
	
	@GET
	@Path("/user/{username}")
	@Produces(MediaType2.RAHNAM_API_PHOTO_COLLECTION)
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
			stmt = conn.prepareStatement("select * from photos where username = ? order by creationTimestamp");
			stmt.setString(1,username);
			stmt.executeQuery();

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Photo photo = new Photo();
				photo.setPhotoid(rs.getString("photoid"));
				photo.setUsername(rs.getString("username"));
				photo.setTitle(rs.getString("title"));
				photo.setDescription(rs.getString("description"));
				photo.setCreationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				photo.setLast_modified(rs.getTimestamp("last_modified").getTime());
				photo.setFilename(rs.getString("photoid") + ".png");
				photo.setPhotoURL(app.getProperties().get("imgBaseURL")+ photo.getFilename());
				
				photo.setCategories(getCategories(photo.getPhotoid()));
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
	@Produces(MediaType2.RAHNAM_API_PHOTO_COLLECTION)
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
					+ "(select categoryid from categories where name = ?)) order by creationTimestamp");
			stmt.setString(1,category);
			stmt.executeQuery();

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Photo photo = new Photo();
				photo.setPhotoid(rs.getString("photoid"));
				photo.setUsername(rs.getString("username"));
				photo.setTitle(rs.getString("title"));
				photo.setDescription(rs.getString("description"));
				photo.setCreationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				photo.setLast_modified(rs.getTimestamp("last_modified").getTime());
				photo.setFilename(rs.getString("photoid") + ".png");
				photo.setPhotoURL(app.getProperties().get("imgBaseURL")+ photo.getFilename());
				
				photo.setCategories(getCategories(photo.getPhotoid()));
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
	@Produces(MediaType2.RAHNAM_API_PHOTO_COLLECTION)
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
			stmt = conn.prepareStatement("select * from photos where title like ? order by creationTimestamp");
			stmt.setString(1, "%" + title + "%");
			stmt.executeQuery();

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Photo photo = new Photo();
				photo.setPhotoid(rs.getString("photoid"));
				photo.setUsername(rs.getString("username"));
				photo.setTitle(rs.getString("title"));
				photo.setDescription(rs.getString("description"));
				photo.setCreationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				photo.setLast_modified(rs.getTimestamp("last_modified").getTime());
				photo.setFilename(rs.getString("photoid") + ".png");
				photo.setPhotoURL(app.getProperties().get("imgBaseURL")+ photo.getFilename());
				
				photo.setCategories(getCategories(photo.getPhotoid()));
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
		
		validateUser(photoid);
		
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
	
	
	private void validateUser(String photoid) {
	    Photo photo = getPhoto(photoid);
	    String username = photo.getUsername();
		if (!security.getUserPrincipal().getName().equals(username))
			throw new ForbiddenException("Can't touch this nanananan can't touch this photo");
	}
	
	@PUT
	@Consumes(MediaType2.RAHNAM_API_PHOTO)
	@Produces(MediaType2.RAHNAM_API_PHOTO)
	public Photo updatePhoto (@QueryParam("photoid") String photoid, Photo photo){

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		PreparedStatement stmt = null;
		PreparedStatement stmtcats = null;
		PreparedStatement stmtsicatexiste = null;
		try {
			// PRIMER MIRAR SI LA FOTO EXISTEIX!!!
			
			for (int i = 0; i< photo.getCategories().size(); i++){
				
				
				stmtsicatexiste = conn.prepareStatement("select * from photoscategories where photoid = ? "
						+ "and categoryid = (select categoryid from categories where name = ?)");
				stmtsicatexiste.setString(1, photoid);
				stmtsicatexiste.setString(2, photo.getCategories().get(i).getName());
				ResultSet rs = stmtsicatexiste.executeQuery();
				if (rs.next())
					throw new WebApplicationException("Esta foto ya tiene la categoria"
								+ photo.getCategories().get(i).getName(), Status.CONFLICT);
				rs.close();
				
				
				stmtcats = conn.prepareStatement("insert into photoscategories values "
						+ "(?,(select categoryid from categories where name = ?))");
				stmtcats.setString(1, photoid);
				stmtcats.setString(2, photo.getCategories().get(i).getName());
				stmtcats.executeUpdate();
			}
			
			stmt = conn.prepareStatement("update photos set title=ifnull(?,title), "
					+ "description=ifnull(?,description) where photoid = ?");
			
			stmt.setString(1, photo.getTitle());
			stmt.setString(2, photo.getDescription());
			stmt.setString(3, photoid);
			int rows = stmt.executeUpdate();
			

			if (rows == 1)
				photo = getPhoto(photoid);
			else {
				throw new NotFoundException("There's no photo with photoid = "
						+ photoid);
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
	@Path("/photo/{photoid}/comments")
	@Produces(MediaType2.RAHNAM_API_COMMENT_COLLECTION)
	public CommentCollection getComments(@PathParam("photoid") String photoid){
		
		CommentCollection comments = new CommentCollection();
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("select * from comments where photoid = ?  order by creationTimestamp");
			stmt.setString(1, photoid);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Comment comment = new Comment();
				comment.setCommentid(rs.getInt("commentid"));
				comment.setUsername(rs.getString("username"));
				comment.setPhotoid(rs.getString("photoid"));
				comment.setLast_modified(rs.getTimestamp("last_modified").getTime());
				comment.setCreationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				comment.setContent(rs.getString("content"));	
				comments.addComment(comment);
			}
			
		}catch (SQLException e) {
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
		return comments;
	}
	
	
	@GET
	@Produces(MediaType2.RAHNAM_API_COMMENT)
	public Comment getComment(int commentid){
		
		Comment comment = new Comment();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("select * from comments where commentid = ?");
			stmt.setInt(1, commentid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				comment.setCommentid(rs.getInt("commentid"));
				comment.setUsername(rs.getString("username"));
				comment.setPhotoid(rs.getString("photoid"));
				comment.setLast_modified(rs.getTimestamp("last_modified").getTime());
				comment.setCreationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				comment.setContent(rs.getString("content"));
			}else {
				throw new NotFoundException("There's no comment with commentid = "
						+ commentid);
					}
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
		return comment;
	} 
	
	
	
	
	@POST
	@Path("/photo/{photoid}/comments")
	@Consumes(MediaType2.RAHNAM_API_COMMENT)
	@Produces(MediaType2.RAHNAM_API_COMMENT)
	public Comment createComment(@PathParam("photoid") String photoid, Comment comment){

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("insert into comments (username, photoid, content) values (?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, security.getUserPrincipal().getName());
			//stmt.setString(1, comment.getUsername());
			stmt.setString(2, photoid);
			stmt.setString(3, comment.getContent());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int commentid = rs.getInt(1);
				comment = getComment(commentid);
			} else {
				// Something has failed...
			}
			
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
		
		return comment;
	}
	
	
	@PUT
	@Path("/photo/comments/{commentid}")
	@Consumes(MediaType2.RAHNAM_API_COMMENT)
	@Produces(MediaType2.RAHNAM_API_COMMENT)
	public Comment updateComment (@PathParam("commentid") String commentid, Comment comment){

		//validateUser(commentid);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("update comments set content=ifnull(?,content) where commentid = ?");
			stmt.setString(1, comment.getContent());
			stmt.setInt(2, Integer.valueOf(commentid));
			int rows = stmt.executeUpdate();
			if (rows == 1)
				comment = getComment(Integer.valueOf(commentid));
			else {
				;// Updating inexistent comment
			}
	 
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
		return comment;
	}
	
	@DELETE
	@Path("/photo/comments/{commentid}")
	public void deleteComment (@PathParam("commentid") String commentid){

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("delete from comments where commentid = ?");
			stmt.setInt(1, Integer.valueOf(commentid));
	 
			int rows = stmt.executeUpdate();
			if (rows == 0)
				;// Deleting inexistent comment
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
	
	
	private String GET_PHOTOS_BEFORE = "select * from photos where creationTimestamp < ifnull(?, now())  order by creationTimestamp desc limit ?";
	private String GET_PHOTOS_AFTER = "select * from photos where creationTimestamp > ? order by creationTimestamp desc";
	 
	@GET
	@Produces(MediaType2.RAHNAM_API_PHOTO_COLLECTION)
	public PhotoCollection getPhotos(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		PhotoCollection photos = new PhotoCollection();
	 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			boolean updateFromLast = after > 0;
			stmt = updateFromLast ? conn
					.prepareStatement(GET_PHOTOS_AFTER) : conn  
					.prepareStatement(GET_PHOTOS_BEFORE);  
			if (updateFromLast) { 
				stmt.setTimestamp(1, new Timestamp(after));
			} else {  
				if (before > 0)
					stmt.setTimestamp(1, new Timestamp(before));
				else 
					stmt.setTimestamp(1, null);
				length = (length <= 0) ? 12 : length;
				stmt.setInt(2, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Photo photo = new Photo();
				
				photo.setPhotoid(rs.getString("photoid"));
				photo.setUsername(rs.getString("username"));
				photo.setTitle(rs.getString("title"));
				photo.setDescription(rs.getString("description"));
				photo.setCreationTimestamp(rs.getTimestamp("creationTimestamp").getTime());
				photo.setLast_modified(rs.getTimestamp("last_modified").getTime());
				photo.setFilename(rs.getString("photoid") + ".png");
				photo.setPhotoURL(app.getProperties().get("imgBaseURL")+ photo.getFilename());
				
				photo.setCategories(getCategories(photo.getPhotoid()));
				
				oldestTimestamp = rs.getTimestamp("creationTimestamp").getTime();
				if (first) {
					first = false;
					photos.setNewestTimestamp(photo.getCreationTimestamp());
				}
				photos.addPhoto(photo);
			}
			photos.setOldestTimestamp(oldestTimestamp);
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
	 
		return photos;
	}
	
	
	
	
	
	
	
	
	
	
	
}
