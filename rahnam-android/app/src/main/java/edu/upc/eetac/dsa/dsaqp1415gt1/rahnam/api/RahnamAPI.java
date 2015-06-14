package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Cristina on 29/05/2015.
 */
public class RahnamAPI {


    private final static String TAG = RahnamAPI.class.getName();
    private static RahnamAPI instance = null;
    private URL url;
    private RahnamRootAPI rootAPI = null;


//METODOS ESENCIALES PARA LA CONEXION DE LA APLICACION
    private RahnamAPI(Context context) throws IOException, AppException {
        super();

        AssetManager assetManager = context.getAssets();
        Properties config = new Properties();
        config.load(assetManager.open("config.properties"));
        String urlHome = config.getProperty("rahnam.home");
        url = new URL(urlHome);

        Log.d("LINKS", url.toString());
        getRootAPI();
    }
    public final static RahnamAPI getInstance(Context context) throws AppException {
        if (instance == null)
            try {
                instance = new RahnamAPI(context);
            } catch (IOException e) {
                throw new AppException(
                        "Can't load configuration file");
            }
        return instance;
    }
    private void getRootAPI() throws AppException {
        Log.d(TAG, "getRootAPI()");
        rootAPI = new RahnamRootAPI();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Beeter API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, rootAPI.getLinks());
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Beeter API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Beeter Root API");
        }

    }
    private void parseLinks(JSONArray jsonLinks, Map<String, Link> map)
            throws AppException, JSONException {
        for (int i = 0; i < jsonLinks.length(); i++) {
            Link link = null;
            try {
                link = SimpleLinkHeaderParser
                        .parseLink(jsonLinks.getString(i));
            } catch (Exception e) {
                throw new AppException(e.getMessage());
            }
            String rel = link.getParameters().get("rel");
            String rels[] = rel.split("\\s");
            for (String s : rels)
                map.put(s, link);
        }
    }

//METODOS YA PARA USAR
// Crear JSON de un Usuario
    private JSONObject createJsonUser(User user) throws JSONException {
        JSONObject jsonUser = new JSONObject();
        jsonUser.put("username", user.getUsername());
        jsonUser.put("userpass", user.getUserpass());
        jsonUser.put("name", user.getName());
        jsonUser.put("avatar",user.getAvatar());
        jsonUser.put("email", user.getEmail());
        jsonUser.put("birth", user.getBirth());
        jsonUser.put("gender", user.getGender());
        return jsonUser;
    }


//metodo para recibir un user y logearse, se usa en loginactivity
    public User checkLogin(String username, String password) throws AppException {
        Log.d(TAG, "checkLogin()");
        User user = new User();
        user.setUsername(username);
        user.setUserpass(password);

        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonUser = createJsonUser(user);
            URL urlPostUsers = new URL(rootAPI.getLinks().get("self").getTarget()+"users/login");
            urlConnection = (HttpURLConnection) urlPostUsers.openConnection();
            String mediaType = "application/vnd.rahnam.api.user+json";
            urlConnection.setRequestProperty("Accept",
                    mediaType);
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonUser.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonUser = new JSONObject(sb.toString());
            user.setLoginSuccessful(jsonUser.getBoolean("loginSuccessful"));
            user.setUsername(jsonUser.getString("username"));
            JSONArray jsonLinks = jsonUser.getJSONArray("links");
            parseLinks(jsonLinks, user.getLinks());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error parsing response");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error getting response");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return user;
    }


//metodo para recibir las fotos de un usuario.
    public PhotoCollection getPhotosByUser(String username) throws AppException{
        Log.d(TAG, "getPhotosByUser()");
        PhotoCollection photos = new PhotoCollection();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("photos").getTarget()+"/user/"+username).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Rahnam API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            photos.setNewestTimestamp(jsonObject.getLong("newestTimestamp"));
            photos.setOldestTimestamp(jsonObject.getLong("oldestTimestamp"));
            JSONArray jsonPhotos = jsonObject.getJSONArray("photos");
            for (int i = 0; i < jsonPhotos.length(); i++) {
                Photo photo = new Photo();
                JSONObject jsonPhoto = jsonPhotos.getJSONObject(i);
                photo.setPhotoid(jsonPhoto.getInt("photoid"));
                photo.setUsername(jsonPhoto.getString("username"));
                photo.setTitle(jsonPhoto.getString("title"));
                photo.setDescription(jsonPhoto.getString("description"));
                photo.setLast_modified(jsonPhoto.getLong("last_modified"));
                photo.setCreationTimestamp(jsonPhoto.getLong("creationTimestamp"));
                photo.setFilename(jsonPhoto.getString("filename"));
                photo.setPhotoURL(jsonPhoto.getString("photoURL"));
                JSONArray jsonLinks = jsonPhoto.getJSONArray("links");
                parseLinks(jsonLinks, photo.getLinks());
                photos.getPhotos().add(photo);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Rahnam API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Photo Root API");
        }
        return photos;
    }

    //metodo para recibir las fotos de un usuario.
    public PhotoCollection getPhotosByTitulo(String titulo) throws AppException{
        Log.d(TAG, "getPhotosByTitulo()");
        PhotoCollection photos = new PhotoCollection();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("photos").getTarget()+"/title/"+titulo).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Rahnam API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            photos.setNewestTimestamp(jsonObject.getLong("newestTimestamp"));
            photos.setOldestTimestamp(jsonObject.getLong("oldestTimestamp"));
            JSONArray jsonPhotos = jsonObject.getJSONArray("photos");
            for (int i = 0; i < jsonPhotos.length(); i++) {
                Photo photo = new Photo();
                JSONObject jsonPhoto = jsonPhotos.getJSONObject(i);
                photo.setPhotoid(jsonPhoto.getInt("photoid"));
                photo.setUsername(jsonPhoto.getString("username"));
                photo.setTitle(jsonPhoto.getString("title"));
                photo.setDescription(jsonPhoto.getString("description"));
                photo.setLast_modified(jsonPhoto.getLong("last_modified"));
                photo.setCreationTimestamp(jsonPhoto.getLong("creationTimestamp"));
                photo.setFilename(jsonPhoto.getString("filename"));
                photo.setPhotoURL(jsonPhoto.getString("photoURL"));
                JSONArray jsonLinks = jsonPhoto.getJSONArray("links");
                parseLinks(jsonLinks, photo.getLinks());
                photos.getPhotos().add(photo);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Rahnam API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Photo Root API");
        }
        return photos;
    }

    //metodo para recibir las fotos de un usuario.
    public PhotoCollection getPhotosByCategoria(String categoria) throws AppException{
        Log.d(TAG, "getPhotosByCategoria()");
        PhotoCollection photos = new PhotoCollection();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("photos").getTarget()+"/category/"+categoria).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Rahnam API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            photos.setNewestTimestamp(jsonObject.getLong("newestTimestamp"));
            photos.setOldestTimestamp(jsonObject.getLong("oldestTimestamp"));
            JSONArray jsonPhotos = jsonObject.getJSONArray("photos");
            for (int i = 0; i < jsonPhotos.length(); i++) {
                Photo photo = new Photo();
                JSONObject jsonPhoto = jsonPhotos.getJSONObject(i);
                photo.setPhotoid(jsonPhoto.getInt("photoid"));
                photo.setUsername(jsonPhoto.getString("username"));
                photo.setTitle(jsonPhoto.getString("title"));
                photo.setDescription(jsonPhoto.getString("description"));
                photo.setLast_modified(jsonPhoto.getLong("last_modified"));
                photo.setCreationTimestamp(jsonPhoto.getLong("creationTimestamp"));
                photo.setFilename(jsonPhoto.getString("filename"));
                photo.setPhotoURL(jsonPhoto.getString("photoURL"));
                JSONArray jsonLinks = jsonPhoto.getJSONArray("links");
                parseLinks(jsonLinks, photo.getLinks());
                photos.getPhotos().add(photo);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Rahnam API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Photo Root API");
        }
        return photos;
    }

//metodo para la pagina de inicio solamente.
    public PhotoCollection getPhotos() throws AppException{
        Log.d(TAG,"getPhotos()");
        PhotoCollection photos = new PhotoCollection();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("photos").getTarget()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Rahnam API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            photos.setNewestTimestamp(jsonObject.getLong("newestTimestamp"));
            photos.setOldestTimestamp(jsonObject.getLong("oldestTimestamp"));
            JSONArray jsonPhotos = jsonObject.getJSONArray("photos");
            for (int i = 0; i < jsonPhotos.length(); i++) {
                Photo photo = new Photo();
                JSONObject jsonPhoto = jsonPhotos.getJSONObject(i);
                photo.setPhotoid(jsonPhoto.getInt("photoid"));
                photo.setUsername(jsonPhoto.getString("username"));
                photo.setTitle(jsonPhoto.getString("title"));
                photo.setDescription(jsonPhoto.getString("description"));
                photo.setLast_modified(jsonPhoto.getLong("last_modified"));
                photo.setCreationTimestamp(jsonPhoto.getLong("creationTimestamp"));
                photo.setFilename(jsonPhoto.getString("filename"));
                photo.setPhotoURL(jsonPhoto.getString("photoURL"));
                JSONArray jsonLinks = jsonPhoto.getJSONArray("links");
                parseLinks(jsonLinks, photo.getLinks());
                photos.getPhotos().add(photo);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Rahnam API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Photo Root API");
        }
        return photos;
    }


//metodo para recibir una foto por id
    public Photo getPhoto(String photoid) throws AppException{
        Log.d(TAG,"getPhoto()");
        Photo photo = new Photo();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("photos").getTarget()+"/photo/"+photoid).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Rahnam API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonPhoto = new JSONObject(sb.toString());
            photo.setPhotoid(jsonPhoto.getInt("photoid"));
            photo.setFilename(jsonPhoto.getString("filename"));
            photo.setTitle(jsonPhoto.getString("title"));
            photo.setDescription(jsonPhoto.getString("description"));
            photo.setPhotoURL(jsonPhoto.getString("photoURL"));
            photo.setUsername(jsonPhoto.getString("username"));
            photo.setCreationTimestamp(jsonPhoto.getLong("creationTimestamp"));
            photo.setLast_modified(jsonPhoto.getLong("last_modified"));
            JSONArray jsonLinks = jsonPhoto.getJSONArray("links");
            parseLinks(jsonLinks, photo.getLinks());

        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Twickpic API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Photo Root API");
        }

        return photo;
    }


//metodo para recibir todos los comentarios.
    public CommentCollection getComents(String photoid) throws AppException{
        Log.d(TAG,"getPhotos()");
        CommentCollection coments = new CommentCollection();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("photos").getTarget()+"/photo/"+photoid+"/comments").openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Rahnam API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonComments = jsonObject.getJSONArray("comments");
            for (int i = 0; i < jsonComments.length(); i++) {
                Comment coment = new Comment();
                JSONObject jsonComment = jsonComments.getJSONObject(i);
                coment.setCommentid(jsonComment.getInt("commentid"));
                coment.setContent(jsonComment.getString("content"));
                coment.setLast_modified(jsonComment.getLong("last_modified"));
                coment.setCreationTimestamp(jsonComment.getLong("creationTimestamp"));
                coment.setUsername(jsonComment.getString("username"));
                coment.setPhotoid(jsonComment.getString("photoid"));
                JSONArray jsonLinks = jsonComment.getJSONArray("links");
                parseLinks(jsonLinks, coment.getLinks());
                coments.getComments().add(coment);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Rahnam API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Photo Root API");
        }
        return coments;
    }


//Crear JSON un comentario
    private JSONObject createJsonComment(Comment comment) throws JSONException {
    JSONObject jsonComment = new JSONObject();
    jsonComment.put("content", comment.getContent());
    if (comment.getUsername() != null)
        jsonComment.put("username", comment.getUsername());
    return jsonComment;
}


//metodo para recibir postear el comentario
    public Comment postComment(String username, String comment, String photoid) throws AppException {
        Log.d(TAG, "postComment()");
        Comment coment = new Comment();
        coment.setUsername(username);
        coment.setContent(comment);
        //coment.setPhotoid(photoid);

        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonComment = createJsonComment(coment);
            URL urlPostUsers = new URL(rootAPI.getLinks().get("self").getTarget()+"photos/photo/"+photoid+"/comments");
            urlConnection = (HttpURLConnection) urlPostUsers.openConnection();
            String mediaType = "application/vnd.rahnam.api.comment+json";
            urlConnection.setRequestProperty("Accept",
                    mediaType);
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonComment.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonComment = new JSONObject(sb.toString());
            coment.setContent(jsonComment.getString("content"));
            coment.setUsername(jsonComment.getString("username"));
            JSONArray jsonLinks = jsonComment.getJSONArray("links");
            parseLinks(jsonLinks, coment.getLinks());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error parsing response");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error getting response");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return coment;
    }


//metodo para eliminar un comentario
    public String deleteComment(String commentid) throws AppException{
        Log.d(TAG,"deletePhoto()");
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("self").getTarget()+"photos/photo/comments/"+commentid).openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error getting response");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
      return ("Se ha eliminado su mensaje");
    }

//metodo para crear un usuario
    public String createUser(String username, String name, String mail, String password, String gender) throws AppException {
        String respuesta = null;
        Log.d(TAG, "createUser()");
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setEmail(mail);
        user.setBirth(null);
        user.setAvatar(1);
        user.setUserpass(password);
        user.setGender(gender);

        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonUser = createJsonUser(user);
            URL urlPostUsers = new URL(rootAPI.getLinks().get("self")
                    .getTarget()+"users");
            urlConnection = (HttpURLConnection) urlPostUsers.openConnection();
            String mediaType = "application/vnd.rahnam.api.user+json"; //Esta línea no estaba en el gist
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonUser.toString());
            writer.close();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_CONFLICT) {
                respuesta = "Ya existe este username";
            }
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                respuesta = "Registrado perfectamente";
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error parsing response");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error getting response");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return respuesta;
    }

//metodo para editar un usuario


//metodo para buscar un usuario por username



}
