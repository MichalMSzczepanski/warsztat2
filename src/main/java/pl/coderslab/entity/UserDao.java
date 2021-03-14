package pl.coderslab.entity;

import com.mysql.cj.protocol.Resultset;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Arrays;

public class UserDao {

    private static User[] users = new User[0];

    private static final String CREATE_USER_QUERY = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";
    private static final String UPDATE_USER_DATA_QUERY = "update users set email = ?, username = ?, password = ? where id = ?;";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * from users";

    private static final String SELECT_USER_BY_EMAIL = "SELECT * FROM users where email = ?";
    private static final String SELECT_CURRENT_IDS = "SELECT id FROM users ORDER BY id ASC;";

    // hint - fetching user ID (due to its AUTO INCREMENT)
//    PreparedStatement preStmt = DBUtil.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

    // CREATE METHOD where we pass a User. This method should be in the User (master?) constructor, which will be initiated in Main (or similiar)

    public User create(User user) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.executeUpdate();
            //Pobieramy wstawiony do bazy identyfikator, a następnie ustawiamy id obiektu user.
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            System.out.println("User created.");
            findAll();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Issue with accessing database when CREATING USER");
            return null;
        }
    }

    // READ USER method - pass in the id of the user you want to print in the console

    public User read(int userId){
        try(Connection conn = DbUtil.getConnection()){
            PreparedStatement readUser = conn.prepareStatement(SELECT_USER_BY_ID);
            readUser.setString(1, (String.valueOf(userId)));
            ResultSet readUserValue = readUser.executeQuery();
            if(readUserValue.next()){
                int userID = readUserValue.getInt(1);
                String userEmail = readUserValue.getString(2);
                String userName = readUserValue.getString(3);
                String userPass = readUserValue.getString(4);
                System.out.println("Your user data is: id #" + userID + " | " + userEmail + " | " + userName + " | " + userPass);
                return new User(readUserValue.getInt(1), readUserValue.getString(2), readUserValue.getString(3), readUserValue.getString(4));
            } else {
                System.out.println("<ERROR> user with id: " + userId + " does not exist");
                return null;
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("issue with accessing database when trying to READ");
            return null;
        }
    }

    // UPDATE USER method - pass in user and grant it updated attributes

    public void update(User user, int id){
        try(Connection conn = DbUtil.getConnection()){
            PreparedStatement readUser = conn.prepareStatement(UPDATE_USER_DATA_QUERY);
            readUser.setString(1, user.getEmail());
            readUser.setString(2, user.getUserName());
            readUser.setString(3, user.getPassword());
            readUser.setString(4, (String.valueOf(id)));
            if(readUser.executeUpdate() == 1){
                System.out.println( "update SUCCESSFUL");
                System.out.println("updated data as follows:");
                System.out.println("new email is: " + user.getEmail());
                System.out.println("new username is: " + user.getUserName());
                System.out.println("new hashed password is: " + user.getPassword());
            } else {
                System.out.println("<ERROR> failed to update your data");
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("issue with accessing database when trying to UPDATE");
        }
    }

    // DELETE USER method - pass in user it to delete it

    public void delete(int userId) {
        try(Connection conn = DbUtil.getConnection()){
            PreparedStatement deleteUser = conn.prepareStatement(DELETE_USER_BY_ID);
            deleteUser.setInt(1, userId);
            if(deleteUser.executeUpdate() == 1){
                System.out.println("Deleting user with id #" + userId + " was successful");
                System.out.println("Current list of users below:");
                PreparedStatement listAllUsers = conn.prepareStatement(SELECT_ALL_USERS);
                ResultSet resultListOfUsers = listAllUsers.executeQuery();
                while(resultListOfUsers.next()){
                    int idOfUser = resultListOfUsers.getInt(1);
                    String emailOfUser = resultListOfUsers.getString(2);
                    String usernameOfUser = resultListOfUsers.getString(3);
                    String passwordOfUser = resultListOfUsers.getString(4);
                    System.out.println("#" + idOfUser + " | " + emailOfUser + " | " + usernameOfUser + " | " + passwordOfUser);
                }
            } else {
                System.out.println("<ERROR> failed to delete your user - did you provide the proper ID?");
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("issue with accessing database when trying to DELETE");
        }
    }

    // FIND ALL method - read all records in database and input them into an array of Users
    public void findAll() {
        try(Connection conn = DbUtil.getConnection()){
        PreparedStatement listAllUsers = conn.prepareStatement(SELECT_ALL_USERS);
        ResultSet resultListOfUsers = listAllUsers.executeQuery();
                while (resultListOfUsers.next()) {
                    int idOfUser = resultListOfUsers.getInt(1);
                    String emailOfUser = resultListOfUsers.getString(2);
                    String userameOfUser = resultListOfUsers.getString(3);
                    String passwordOfUser = resultListOfUsers.getString(4);
                    User addUserToArray = new User(idOfUser, emailOfUser, userameOfUser, passwordOfUser);
                    users = addToArray(addUserToArray, users);
                }
                if(users.length == 0){
                    System.out.println("There are no users in the database");
                } else {
                    System.out.println("Your database consists of the below users:");
                    for(User user : users){
                        System.out.println(("#" + user.getId() + " | " + user.getEmail() + " | " + user.getUserName()));
                    }
                }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("issue with accessing database when trying to LIST ALL USERS");
        }
    }

    // auxilary method - validate if inputed id reflect an id in the database
    public static boolean validateID (int id) {
        try (Connection connection = DbUtil.getConnection()) {
            int[] arrayOfIDs = new int[0];
            int IDcounter = 0;
            PreparedStatement listAllIDs = connection.prepareStatement(SELECT_CURRENT_IDS);
            ResultSet listOfAllIDs = listAllIDs.executeQuery();
            while(listOfAllIDs.next()){
                arrayOfIDs = Arrays.copyOf(arrayOfIDs, arrayOfIDs.length + 1);
                arrayOfIDs[IDcounter] = listOfAllIDs.getInt(1);
                IDcounter++;
            }
            for (int IDsInList : arrayOfIDs){
                if(IDsInList == id){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("issue with accessing database when trying to VALIDATE INPUTED ID");
            return true;
        }
    }

    // auxilary method - validate if email is already used in the data base
    public static boolean validateEmail(String string) {
        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement validateEmail = connection.prepareStatement(SELECT_USER_BY_EMAIL);
            validateEmail.setString(1,string);
            ResultSet validateEmailDone = validateEmail.executeQuery();
            if(validateEmailDone.next()){
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("issue with accessing database when VALIDATING EMAIL");
            return true;
        }
    }

    // auxilary method - copy table with users for findAll method
    public User[] addToArray(User u, User[] users) {
        users = Arrays.copyOf(users, users.length + 1); // Tworzymy kopię tablicy powiększoną o 1.
        users[users.length - 1] = u; // Dodajemy obiekt na ostatniej pozycji.
        return users; // Zwracamy nową tablicę.
    }

    // auxilary method - password hashing
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
