package pl.coderslab.production;

import pl.coderslab.entity.ANSIEscapeCode;
import pl.coderslab.entity.User;
import pl.coderslab.entity.UserDao;

import java.util.Scanner;

public class MainDao {

    public static void main(String[] args) {
        System.out.println();
        System.out.println(ANSIEscapeCode.ANSI_PURPLE + "Welcome to your admin database manager" + ANSIEscapeCode.ANSI_RESET);
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        while (!quit) {
            choseTaskToExecute();
            while(scanner.hasNextInt()){
                System.out.println("that's a number, I need a letter");
                scanner.nextLine();
                System.out.println();
                choseTaskToExecute();
            }
            String choice = scanner.nextLine().toLowerCase();
            switch (choice) {
                case "c":
                    // code for CREATE section
                    System.out.println("Input user details:");
                    System.out.println("email:");
                    while (scanner.hasNextLine()) {
                        String newEmail = scanner.nextLine();
                        if (UserDao.validateEmail(newEmail) == false) {
                            System.out.println("username:");
                            String newUsername = scanner.nextLine();
                            System.out.println("password");
                            String newPassword = scanner.nextLine();
                            User user = new User(newUsername, newEmail, newPassword);
                            UserDao userDao = new UserDao();
                            userDao.create(user);
                            System.out.println();
                            break;
                        } else {
                            System.out.println("This email is already taken, try again");
                        }
                    }
                    break;
                case "r":
                    // code for READ section
                    System.out.println("Input user id which you want more details about");
                    while (scanner.hasNextLine()) {
                        while (!scanner.hasNextInt()) {
                            System.out.println("That's not a number, try again");
                            scanner.next();
                        }
                        int UserToReadID = scanner.nextInt();
                        scanner.nextLine();
                        if (UserDao.validateID(UserToReadID) == true) {
                            UserDao userDao = new UserDao();
                            User user = userDao.read(UserToReadID);
                            System.out.println();
                            break;
                        } else {
                            System.out.println("This ID is not associated with any user, try again");
                        }
                    }
                    break;
                case "u":
                    // code for UPDATE section
                    System.out.println("Input ID of the user you want to edit");
                    while (!scanner.hasNextInt()) {
                        System.out.println("That's not a number, try again");
                        scanner.next();
                    }
                    while (scanner.hasNext()) {
                        int userToUpdate = scanner.nextInt();
                        scanner.nextLine();
                        if (UserDao.validateID(userToUpdate) == true) {
                            User user = new User();
                            System.out.println("Input new (or old) email you want to upload");
                            String email = scanner.nextLine();
                            user.setEmail(email);
                            System.out.println("Input new (or old) username you want to upload");
                            String username = scanner.nextLine();
                            user.setUserName(username);
                            System.out.println("Input new (or old) password you want to upload");
                            String password = scanner.nextLine();
                            user.setPassword(password);
                            UserDao userDao = new UserDao();
                            userDao.update(user, userToUpdate);
                            System.out.println("Double check if you wish - validate user with id: " + userToUpdate + " below");
                            userDao.findAll();
                            System.out.println();
                            break;
                        } else {
                            System.out.println("This ID is not associated with any user, try again");
                        }
                    }
                    break;
                case "d":
                    // code for DELETE section
                    System.out.println("Input user id which you want to delete");
                    while (scanner.hasNextLine()) {
                        while (!scanner.hasNextInt()) {
                            System.out.println("That's not a number, try again");
                            scanner.next();
                        }
                        int userToDeleteID = scanner.nextInt();
                        if (UserDao.validateID(userToDeleteID) == true) {
                            UserDao userDao = new UserDao();
                            userDao.delete(userToDeleteID);
                            System.out.println();
                            break;
                        } else {
                            System.out.println("This ID is not associated with any user, try again");
                        }
                    }
                    break;
                case "v":
                    UserDao userDao = new UserDao();
                    userDao.findAll();
                    System.out.println();
                    break;
                case "q":
                    quit = true;
                    System.out.println(ANSIEscapeCode.ANSI_RED + "bye!" + ANSIEscapeCode.ANSI_RESET);
                    System.out.println(" ☻/ \n" +
                            "/▌\n" +
                            "/\\\uFEFF");
                    System.out.println();
                    break;
            }
        }
    }
        private static void choseTaskToExecute(){
            System.out.println(ANSIEscapeCode.ANSI_PURPLE + "What task do you want to execute?" + ANSIEscapeCode.ANSI_RESET);
            System.out.println("Create user - type C");
            System.out.println("Read user - type R");
            System.out.println("Update user - type U");
            System.out.println("Delete user - type D");
            System.out.println("View all users - type V");
            System.out.println("To quit - type Q");
            System.out.println();
    }
}


