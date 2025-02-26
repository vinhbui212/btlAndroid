package app;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ADMIN
 */
public class CONFIG {
    public class DATABASE {
        public static final String CLASSNAME = "com.mysql.cj.jdbc.Driver";
        public static final String URL = "jdbc:mysql://localhost:3306/chat";
        public static final String USERNAME = "root";
        public static final String PASSWORD = "1234";
    }
    public class SERVER_RESPONSE {
        public static final String SUCCESS = "Success";
        public static final String FAILED = "Failed";
        public static final String REGISTER_FAILED = "Account already exists!";
    }
    public static enum REQUEST_TYPE{
        MESSAGE,
        SEARCH_USERS,
        ADD_FRIEND,
        GET_FRIENDS_LIST,
        NEW_GROUP
    }
    public static enum RESPONSE_TYPE{
        DIRECT_MESSAGE,
        GROUP_MESSAGE,
        SEARCH_RESULT,
        ADD_FRIEND_REQUEST_RESULT,
        FRIENDS_LIST,
        NEW_GROUP_REQUEST_RESULT
    }
    public static final String NOTHING = "";
}
